package com.insurancebuffer.service;

import com.insurancebuffer.model.HighTicketLead;
import com.insurancebuffer.model.LeadProfile;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * LeadAnalysisService - Analyzes conversation summary data to score leads.
 * Demonstrates: HashMap usage, ArrayList, utility methods, keyword analysis.
 * CIS200 Honors - Java Backend
 */
@Service
public class LeadAnalysisService {

    private static final Logger logger = Logger.getLogger(LeadAnalysisService.class.getName());

    // Keyword maps for high-ticket detection
    private static final Map<String, Integer> HIGH_TICKET_KEYWORDS = new HashMap<>();
    private static final int HIGH_TICKET_THRESHOLD = 30;

    static {
        // Farm/Ag signals
        HIGH_TICKET_KEYWORDS.put("farm", 25);
        HIGH_TICKET_KEYWORDS.put("agriculture", 25);
        HIGH_TICKET_KEYWORDS.put("crop", 20);
        HIGH_TICKET_KEYWORDS.put("livestock", 20);
        HIGH_TICKET_KEYWORDS.put("ranch", 20);
        HIGH_TICKET_KEYWORDS.put("equipment", 15);

        // Business signals
        HIGH_TICKET_KEYWORDS.put("business", 20);
        HIGH_TICKET_KEYWORDS.put("commercial", 20);
        HIGH_TICKET_KEYWORDS.put("employees", 15);
        HIGH_TICKET_KEYWORDS.put("company", 15);
        HIGH_TICKET_KEYWORDS.put("llc", 20);

        // Property signals
        HIGH_TICKET_KEYWORDS.put("properties", 20);
        HIGH_TICKET_KEYWORDS.put("rental", 15);
        HIGH_TICKET_KEYWORDS.put("investment property", 20);
        HIGH_TICKET_KEYWORDS.put("multiple homes", 20);

        // Financial signals
        HIGH_TICKET_KEYWORDS.put("retirement", 15);
        HIGH_TICKET_KEYWORDS.put("annuity", 20);
        HIGH_TICKET_KEYWORDS.put("investment", 15);
        HIGH_TICKET_KEYWORDS.put("500", 15);
        HIGH_TICKET_KEYWORDS.put("million", 25);
        HIGH_TICKET_KEYWORDS.put("life insurance", 15);
        HIGH_TICKET_KEYWORDS.put("whole life", 20);
    }

    /**
     * Analyze a LeadProfile's data and determine if it should be upgraded
     * to a HighTicketLead. Returns a HighTicketLead or null.
     */
    public HighTicketLead analyzeForHighTicket(LeadProfile lead) {
        if (lead == null) return null;

        int score = 0;
        HighTicketLead htLead = new HighTicketLead(lead);

        // Analyze the conversation summary for keywords
        String textToAnalyze = buildAnalysisText(lead);

        // Iterate over keyword map and score
        for (Map.Entry<String, Integer> entry : HIGH_TICKET_KEYWORDS.entrySet()) {
            if (textToAnalyze.toLowerCase().contains(entry.getKey().toLowerCase())) {
                score += entry.getValue();
            }
        }

        // Check specific category flags
        htLead.setFarmOrAgBusiness(containsAny(textToAnalyze,
            Arrays.asList("farm", "agriculture", "crop", "livestock", "ranch")));

        htLead.setBusinessInsurance(containsAny(textToAnalyze,
            Arrays.asList("business", "commercial", "company", "llc", "employees")));

        htLead.setMultipleProperties(containsAny(textToAnalyze,
            Arrays.asList("properties", "rental", "multiple homes", "investment property")));

        htLead.setLargeLifePolicy(containsAny(textToAnalyze,
            Arrays.asList("500,000", "500000", "million", "whole life", "universal life")));

        htLead.setRetirementPlanning(containsAny(textToAnalyze,
            Arrays.asList("retirement", "annuity", "ira", "401k")));

        // Compute final score including category bonuses
        htLead.computeScore();

        // Cap at 100
        int finalScore = Math.min(100, htLead.getTicketScore() + score);
        htLead.setTicketScore(finalScore);

        if (finalScore >= HIGH_TICKET_THRESHOLD) {
            htLead.setFlagReason("Auto-detected score: " + finalScore + "/100");
            logger.info("High ticket lead detected: " + lead.getSessionId() +
                        " (score: " + finalScore + ")");
            return htLead;
        }

        return null; // Not high ticket
    }

    /**
     * Build a combined text string from all lead fields for keyword analysis.
     */
    private String buildAnalysisText(LeadProfile lead) {
        StringBuilder sb = new StringBuilder();
        if (lead.getConversationSummary() != null) sb.append(lead.getConversationSummary()).append(" ");
        if (lead.getRawTranscript() != null) sb.append(lead.getRawTranscript()).append(" ");
        if (lead.getInsuranceNeeds() != null) sb.append(String.join(" ", lead.getInsuranceNeeds())).append(" ");
        if (lead.getPainPoints() != null) sb.append(String.join(" ", lead.getPainPoints())).append(" ");
        return sb.toString();
    }

    /**
     * Check if text contains any keyword from the list.
     * Demonstrates: List iteration, String methods.
     */
    private boolean containsAny(String text, List<String> keywords) {
        String lower = text.toLowerCase();
        for (String keyword : keywords) {
            if (lower.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
