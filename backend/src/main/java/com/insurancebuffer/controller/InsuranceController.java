package com.insurancebuffer.controller;

import com.insurancebuffer.model.HighTicketLead;
import com.insurancebuffer.model.LeadProfile;
import com.insurancebuffer.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.logging.Logger;

/**
 * InsuranceController - Main REST API controller.
 * Exposes secure endpoints for the frontend.
 * Demonstrates: Spring REST, @RequestBody, @PostMapping, ResponseEntity.
 * CIS200 Honors - Java Backend
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Configure to your domain in production
public class InsuranceController {

    private static final Logger logger = Logger.getLogger(InsuranceController.class.getName());

    @Autowired
    private RetellService retellService;

    @Autowired
    private LeadStorageService leadStorageService;

    @Autowired
    private LeadAnalysisService leadAnalysisService;

    @Autowired
    private EmailNotificationService emailService;

    /**
     * POST /api/start-call
     * Creates a Retell web call session server-side.
     * Returns only the web_call_id to the frontend — API key never exposed.
     */
    @PostMapping("/start-call")
    public ResponseEntity<Map<String, Object>> startCall() {
        logger.info("POST /api/start-call - Creating new Retell session");
        Map<String, Object> result = retellService.createWebCall();

        if (result.containsKey("error")) {
            return ResponseEntity.internalServerError().body(result);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/submit-lead
     * Receives the conversation summary/lead data after call ends.
     * Analyzes, stores, and emails the lead to Brett.
     *
     * Expected JSON body:
     * {
     *   "sessionId": "...",
     *   "visitorName": "...",
     *   "contactInfo": "...",
     *   "wantsReferral": true,
     *   "insuranceNeeds": [...],
     *   "painPoints": [...],
     *   "conversationSummary": "...",
     *   "rawTranscript": "..."
     * }
     */
    @PostMapping("/submit-lead")
    public ResponseEntity<Map<String, Object>> submitLead(@RequestBody Map<String, Object> body) {
        logger.info("POST /api/submit-lead - Processing lead submission");

        // Build LeadProfile from request body
        LeadProfile lead = new LeadProfile();
        lead.setSessionId((String) body.getOrDefault("sessionId", UUID.randomUUID().toString()));
        lead.setVisitorName((String) body.get("visitorName"));
        lead.setContactInfo((String) body.get("contactInfo"));
        lead.setWantsReferral(Boolean.TRUE.equals(body.get("wantsReferral")));
        lead.setConversationSummary((String) body.get("conversationSummary"));
        lead.setRawTranscript((String) body.get("rawTranscript"));

        // Handle lists
        Object needsObj = body.get("insuranceNeeds");
        if (needsObj instanceof List) {
            for (Object need : (List<?>) needsObj) {
                lead.addInsuranceNeed(need.toString());
            }
        }

        Object painObj = body.get("painPoints");
        if (painObj instanceof List) {
            for (Object point : (List<?>) painObj) {
                lead.addPainPoint(point.toString());
            }
        }

        // Save transcript
        if (lead.getRawTranscript() != null) {
            leadStorageService.saveTranscript(lead.getSessionId(), lead.getRawTranscript());
        }

        // Analyze for high-ticket
        HighTicketLead highTicketLead = leadAnalysisService.analyzeForHighTicket(lead);

        Map<String, Object> response = new HashMap<>();

        if (highTicketLead != null) {
            // HIGH TICKET path
            leadStorageService.saveHighTicketLead(highTicketLead);
            emailService.sendHighTicketNotification(highTicketLead);
            response.put("status", "success");
            response.put("leadType", "HIGH_TICKET");
            response.put("score", highTicketLead.getTicketScore());
            logger.info("High ticket lead processed: " + highTicketLead.getSessionId());
        } else {
            // Standard lead path
            leadStorageService.saveLead(lead);
            emailService.sendLeadNotification(lead);
            response.put("status", "success");
            response.put("leadType", "STANDARD");
            logger.info("Standard lead processed: " + lead.getSessionId());
        }

        // Return referral URL if they want it
        if (lead.isWantsReferral()) {
            response.put("referralUrl", "https://www.fbfs.com/find-an-agent/brettkendig");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/health
     * Simple health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "ok");
        status.put("service", "Insurance Lead Capture API");
        return ResponseEntity.ok(status);
    }
}
