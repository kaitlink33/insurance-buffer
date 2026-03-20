package com.insurancebuffer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * HighTicketLead - Extends LeadProfile to represent a high-value potential client.
 * Demonstrates OOP Inheritance: extends LeadProfile, adds specialized fields,
 * overrides toString() and toSummaryString() (polymorphism).
 * CIS200 Honors - Java Backend
 */
public class HighTicketLead extends LeadProfile {

    // Additional fields specific to high-ticket leads
    private List<String> highValueSignals;
    private int ticketScore;         // 0-100 scoring
    private String flagReason;
    private boolean farmOrAgBusiness;
    private boolean multipleProperties;
    private boolean businessInsurance;
    private boolean largeLifePolicy;  // $500k+ face value
    private boolean retirementPlanning; // $500k+ assets

    // Constructor chains up to parent
    public HighTicketLead(String sessionId) {
        super(sessionId); // Call parent constructor
        this.highValueSignals = new ArrayList<>();
        this.ticketScore = 0;
    }

    /**
     * Copy constructor - promote a LeadProfile to HighTicketLead
     * Demonstrates constructor chaining and inheritance
     */
    public HighTicketLead(LeadProfile base) {
        super(base.getSessionId());
        this.setVisitorName(base.getVisitorName());
        this.setContactInfo(base.getContactInfo());
        this.setInsuranceNeeds(base.getInsuranceNeeds());
        this.setPainPoints(base.getPainPoints());
        this.setWantsReferral(base.isWantsReferral());
        this.setConversationSummary(base.getConversationSummary());
        this.setRawTranscript(base.getRawTranscript());
        this.highValueSignals = new ArrayList<>();
        this.ticketScore = 0;
    }

    /**
     * Evaluate and compute ticket score based on signals.
     * Each high-value indicator adds to the score.
     */
    public void computeScore() {
        ticketScore = 0;
        if (farmOrAgBusiness)     { ticketScore += 25; highValueSignals.add("Farm/Ag Business"); }
        if (multipleProperties)   { ticketScore += 20; highValueSignals.add("Multiple Properties"); }
        if (businessInsurance)    { ticketScore += 20; highValueSignals.add("Business Insurance Need"); }
        if (largeLifePolicy)      { ticketScore += 20; highValueSignals.add("Large Life Policy ($500k+)"); }
        if (retirementPlanning)   { ticketScore += 15; highValueSignals.add("Retirement Planning ($500k+)"); }
    }

    /**
     * Override parent's toSummaryString() - polymorphism
     */
    @Override
    public String toSummaryString() {
        StringBuilder sb = new StringBuilder();
        sb.append("🚨 === HIGH TICKET LEAD === 🚨\n");
        sb.append(super.toSummaryString()); // Reuse parent logic
        sb.append("\n--- HIGH TICKET DETAILS ---\n");
        sb.append("Ticket Score: ").append(ticketScore).append("/100\n");
        sb.append("Flag Reason: ").append(flagReason != null ? flagReason : "Auto-detected").append("\n");
        sb.append("High Value Signals:\n");
        for (String signal : highValueSignals) {
            sb.append("  ✓ ").append(signal).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "HighTicketLead{sessionId='" + getSessionId() + 
               "', score=" + ticketScore + ", signals=" + highValueSignals + "}";
    }

    // Getters and Setters
    public List<String> getHighValueSignals() { return highValueSignals; }
    public int getTicketScore() { return ticketScore; }
    public void setTicketScore(int ticketScore) { this.ticketScore = ticketScore; }
    public String getFlagReason() { return flagReason; }
    public void setFlagReason(String flagReason) { this.flagReason = flagReason; }
    public boolean isFarmOrAgBusiness() { return farmOrAgBusiness; }
    public void setFarmOrAgBusiness(boolean farmOrAgBusiness) { this.farmOrAgBusiness = farmOrAgBusiness; }
    public boolean isMultipleProperties() { return multipleProperties; }
    public void setMultipleProperties(boolean multipleProperties) { this.multipleProperties = multipleProperties; }
    public boolean isBusinessInsurance() { return businessInsurance; }
    public void setBusinessInsurance(boolean businessInsurance) { this.businessInsurance = businessInsurance; }
    public boolean isLargeLifePolicy() { return largeLifePolicy; }
    public void setLargeLifePolicy(boolean largeLifePolicy) { this.largeLifePolicy = largeLifePolicy; }
    public boolean isRetirementPlanning() { return retirementPlanning; }
    public void setRetirementPlanning(boolean retirementPlanning) { this.retirementPlanning = retirementPlanning; }
}
