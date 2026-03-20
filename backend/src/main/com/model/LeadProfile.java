package com.insurancebuffer.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * LeadProfile - Base class representing a potential insurance customer.
 * Demonstrates OOP: encapsulation, constructors, getters/setters.
 * CIS200 Honors - Java Backend
 */
public class LeadProfile {

    // Encapsulated fields
    private String sessionId;
    private String visitorName;
    private String contactInfo;
    private List<String> insuranceNeeds;
    private List<String> painPoints;
    private boolean wantsReferral;
    private LocalDateTime capturedAt;
    private String conversationSummary;
    private String rawTranscript;

    // Default constructor
    public LeadProfile() {
        this.insuranceNeeds = new ArrayList<>();
        this.painPoints = new ArrayList<>();
        this.capturedAt = LocalDateTime.now();
        this.wantsReferral = false;
    }

    // Parameterized constructor
    public LeadProfile(String sessionId) {
        this();
        this.sessionId = sessionId;
    }

    // --- Getters and Setters (Encapsulation) ---

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public List<String> getInsuranceNeeds() { return insuranceNeeds; }
    public void setInsuranceNeeds(List<String> insuranceNeeds) { this.insuranceNeeds = insuranceNeeds; }

    public void addInsuranceNeed(String need) { this.insuranceNeeds.add(need); }

    public List<String> getPainPoints() { return painPoints; }
    public void setPainPoints(List<String> painPoints) { this.painPoints = painPoints; }

    public void addPainPoint(String point) { this.painPoints.add(point); }

    public boolean isWantsReferral() { return wantsReferral; }
    public void setWantsReferral(boolean wantsReferral) { this.wantsReferral = wantsReferral; }

    public LocalDateTime getCapturedAt() { return capturedAt; }
    public void setCapturedAt(LocalDateTime capturedAt) { this.capturedAt = capturedAt; }

    public String getConversationSummary() { return conversationSummary; }
    public void setConversationSummary(String conversationSummary) { this.conversationSummary = conversationSummary; }

    public String getRawTranscript() { return rawTranscript; }
    public void setRawTranscript(String rawTranscript) { this.rawTranscript = rawTranscript; }

    /**
     * Returns a formatted string representation of the lead for logging/email.
     */
    public String toSummaryString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LEAD PROFILE ===\n");
        sb.append("Session ID: ").append(sessionId).append("\n");
        sb.append("Name: ").append(visitorName != null ? visitorName : "Not provided").append("\n");
        sb.append("Contact: ").append(contactInfo != null ? contactInfo : "Not provided").append("\n");
        sb.append("Wants Referral: ").append(wantsReferral ? "YES" : "NO").append("\n");
        sb.append("Captured At: ").append(capturedAt).append("\n");
        sb.append("Insurance Needs: ").append(String.join(", ", insuranceNeeds)).append("\n");
        sb.append("Pain Points: ").append(String.join(", ", painPoints)).append("\n");
        sb.append("Summary:\n").append(conversationSummary).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "LeadProfile{sessionId='" + sessionId + "', name='" + visitorName + 
               "', wantsReferral=" + wantsReferral + "}";
    }
}
