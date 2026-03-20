package com.insurancebuffer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * RetellService - Server-side proxy for Retell AI API calls.
 * API keys NEVER leave the server. Frontend only gets a web_call_id token.
 * CIS200 Honors - Java Backend (Security: API key proxy pattern)
 */
@Service
public class RetellService {

    private static final Logger logger = Logger.getLogger(RetellService.class.getName());
    private static final String RETELL_API_BASE = "https://api.retellai.com";

    @Value("${retell.api.key}")
    private String retellApiKey;

    @Value("${retell.agent.id}")
    private String retellAgentId;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Create a web call with Retell AI.
     * Returns the web_call_id to the frontend - NO API KEY is passed to the client.
     */
    public Map<String, Object> createWebCall() {
        String url = RETELL_API_BASE + "/v2/create-web-call";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(retellApiKey); // Key stays server-side

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("agent_id", retellAgentId);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("Retell web call created successfully");
                return response.getBody();
            }
        } catch (Exception e) {
            logger.severe("Failed to create Retell web call: " + e.getMessage());
        }

        Map<String, Object> error = new HashMap<>();
        error.put("error", "Failed to initialize voice call");
        return error;
    }
}
