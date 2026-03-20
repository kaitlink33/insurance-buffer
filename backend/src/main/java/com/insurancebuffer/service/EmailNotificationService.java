package com.insurancebuffer.service;

import com.insurancebuffer.model.HighTicketLead;
import com.insurancebuffer.model.LeadProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * EmailNotificationService - Sends lead notification emails to Brett.
 * Demonstrates: service layer, configuration injection, JavaMail API.
 * CIS200 Honors - Java Backend
 */
@Service
public class EmailNotificationService {

    private static final Logger logger = Logger.getLogger(EmailNotificationService.class.getName());

    @Value("${email.from:noreply@insuranceleads.app}")
    private String fromEmail;

    @Value("${email.to:brettkendig8@gmail.com}")
    private String toEmail;

    @Value("${email.smtp.host:smtp.gmail.com}")
    private String smtpHost;

    @Value("${email.smtp.port:587}")
    private String smtpPort;

    @Value("${email.smtp.username:}")
    private String smtpUsername;

    @Value("${email.smtp.password:}")
    private String smtpPassword;

    /**
     * Send a standard lead notification email to Brett.
     */
    public void sendLeadNotification(LeadProfile lead) {
        String subject = lead.isWantsReferral()
            ? "🔔 New Lead Requesting Contact - " + (lead.getVisitorName() != null ? lead.getVisitorName() : "Unknown")
            : "📋 New Lead Profile Captured";

        String body = buildLeadEmailBody(lead, false);
        sendEmail(subject, body);
    }

    /**
     * Send a HIGH TICKET lead notification - flagged specially for Brett.
     * Demonstrates polymorphism: accepts HighTicketLead subclass.
     */
    public void sendHighTicketNotification(HighTicketLead lead) {
        String subject = "🚨 HIGH TICKET LEAD - Score: " + lead.getTicketScore() + "/100 - " +
            (lead.getVisitorName() != null ? lead.getVisitorName() : "Unknown Visitor");

        String body = buildLeadEmailBody(lead, true);
        sendEmail(subject, body);
    }

    private String buildLeadEmailBody(LeadProfile lead, boolean isHighTicket) {
        StringBuilder sb = new StringBuilder();

        if (isHighTicket && lead instanceof HighTicketLead) {
            HighTicketLead ht = (HighTicketLead) lead;
            sb.append("🚨 HIGH TICKET ALERT 🚨\n");
            sb.append("Ticket Score: ").append(ht.getTicketScore()).append("/100\n");
            sb.append("Signals: ").append(String.join(", ", ht.getHighValueSignals())).append("\n");
            sb.append("\n");
        }

        sb.append("A new potential client just finished speaking with your AI advisor.\n\n");
        sb.append("--- CLIENT DETAILS ---\n");
        sb.append("Name: ").append(lead.getVisitorName() != null ? lead.getVisitorName() : "Not provided").append("\n");
        sb.append("Contact: ").append(lead.getContactInfo() != null ? lead.getContactInfo() : "Not provided").append("\n");
        sb.append("Wants Follow-up: ").append(lead.isWantsReferral() ? "YES ✓" : "Not yet").append("\n");
        sb.append("Captured: ").append(lead.getCapturedAt()).append("\n\n");

        sb.append("--- INSURANCE NEEDS ---\n");
        if (lead.getInsuranceNeeds().isEmpty()) {
            sb.append("Not captured\n");
        } else {
            for (String need : lead.getInsuranceNeeds()) {
                sb.append("• ").append(need).append("\n");
            }
        }

        sb.append("\n--- PAIN POINTS / GAPS ---\n");
        if (lead.getPainPoints().isEmpty()) {
            sb.append("Not captured\n");
        } else {
            for (String point : lead.getPainPoints()) {
                sb.append("• ").append(point).append("\n");
            }
        }

        if (lead.getConversationSummary() != null && !lead.getConversationSummary().isBlank()) {
            sb.append("\n--- AI CONVERSATION SUMMARY ---\n");
            sb.append(lead.getConversationSummary()).append("\n");
        }

        sb.append("\n--- SESSION ---\n");
        sb.append("Session ID: ").append(lead.getSessionId()).append("\n");

        sb.append("\n\nThis lead was captured automatically by your AI advisor tool.\n");

        return sb.toString();
    }

    private void sendEmail(String subject, String body) {
        if (smtpUsername == null || smtpUsername.isBlank()) {
            // Log only if email not configured - graceful degradation
            logger.warning("Email not configured. Would have sent: " + subject);
            logger.info("Email body:\n" + body);
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            logger.info("Email sent successfully: " + subject);
        } catch (MessagingException e) {
            logger.severe("Failed to send email: " + e.getMessage());
        }
    }
}
