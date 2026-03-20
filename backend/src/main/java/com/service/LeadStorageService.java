package com.insurancebuffer.service;

import com.insurancebuffer.model.HighTicketLead;
import com.insurancebuffer.model.LeadProfile;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * LeadStorageService - Handles File I/O for persisting leads to disk.
 * Demonstrates: File I/O (BufferedWriter, FileWriter, Path, Files),
 * exception handling, formatted output.
 * CIS200 Honors - Java Backend
 */
@Service
public class LeadStorageService {

    private static final Logger logger = Logger.getLogger(LeadStorageService.class.getName());

    // File paths for lead storage
    private static final String LEADS_DIR = "leads_data";
    private static final String ALL_LEADS_CSV = LEADS_DIR + "/all_leads.csv";
    private static final String HIGH_TICKET_CSV = LEADS_DIR + "/high_ticket_leads.csv";
    private static final String TRANSCRIPTS_DIR = LEADS_DIR + "/transcripts";

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Initialize storage directories on startup.
     * Demonstrates: File I/O with java.nio.file.Files and Path
     */
    public void initStorage() {
        try {
            Files.createDirectories(Paths.get(LEADS_DIR));
            Files.createDirectories(Paths.get(TRANSCRIPTS_DIR));

            // Create CSV headers if files don't exist
            if (!Files.exists(Paths.get(ALL_LEADS_CSV))) {
                writeCsvHeader(ALL_LEADS_CSV,
                    "session_id,name,contact,wants_referral,needs,pain_points,captured_at\n");
            }
            if (!Files.exists(Paths.get(HIGH_TICKET_CSV))) {
                writeCsvHeader(HIGH_TICKET_CSV,
                    "session_id,name,contact,ticket_score,signals,flag_reason,captured_at\n");
            }
            logger.info("Lead storage initialized at: " + Paths.get(LEADS_DIR).toAbsolutePath());
        } catch (IOException e) {
            logger.severe("Failed to initialize lead storage: " + e.getMessage());
        }
    }

    /**
     * Save a lead to the all-leads CSV file.
     * Demonstrates: BufferedWriter, FileWriter with append mode, try-with-resources.
     */
    public void saveLead(LeadProfile lead) {
        initStorage();
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(ALL_LEADS_CSV, true))) { // 'true' = append mode

            String line = String.format("%s,%s,%s,%s,\"%s\",\"%s\",%s\n",
                escapeCsv(lead.getSessionId()),
                escapeCsv(lead.getVisitorName()),
                escapeCsv(lead.getContactInfo()),
                lead.isWantsReferral() ? "YES" : "NO",
                escapeCsv(String.join("; ", lead.getInsuranceNeeds())),
                escapeCsv(String.join("; ", lead.getPainPoints())),
                lead.getCapturedAt() != null
                    ? lead.getCapturedAt().format(FORMATTER)
                    : LocalDateTime.now().format(FORMATTER)
            );

            writer.write(line);
            logger.info("Lead saved to CSV: " + lead.getSessionId());

        } catch (IOException e) {
            logger.severe("Failed to save lead to CSV: " + e.getMessage());
        }

        // Also save the summary as a text file
        saveLeadSummaryFile(lead);
    }

    /**
     * Save a high-ticket lead to the high-ticket CSV.
     * Demonstrates: polymorphism - accepting HighTicketLead (subclass) as parameter.
     */
    public void saveHighTicketLead(HighTicketLead lead) {
        saveLead(lead); // Also save to all_leads via parent method (polymorphism)
        initStorage();

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(HIGH_TICKET_CSV, true))) {

            String line = String.format("%s,%s,%s,%d,\"%s\",\"%s\",%s\n",
                escapeCsv(lead.getSessionId()),
                escapeCsv(lead.getVisitorName()),
                escapeCsv(lead.getContactInfo()),
                lead.getTicketScore(),
                escapeCsv(String.join("; ", lead.getHighValueSignals())),
                escapeCsv(lead.getFlagReason()),
                lead.getCapturedAt() != null
                    ? lead.getCapturedAt().format(FORMATTER)
                    : LocalDateTime.now().format(FORMATTER)
            );

            writer.write(line);
            logger.info("High ticket lead saved: " + lead.getSessionId() +
                        " (score: " + lead.getTicketScore() + ")");

        } catch (IOException e) {
            logger.severe("Failed to save high ticket lead: " + e.getMessage());
        }
    }

    /**
     * Save transcript to individual file.
     * Demonstrates: FileWriter, path construction, formatted file output.
     */
    public void saveTranscript(String sessionId, String transcript) {
        initStorage();
        String filename = TRANSCRIPTS_DIR + "/transcript_" + sessionId + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("=== CONVERSATION TRANSCRIPT ===\n");
            writer.write("Session: " + sessionId + "\n");
            writer.write("Captured: " + LocalDateTime.now().format(FORMATTER) + "\n");
            writer.write("================================\n\n");
            writer.write(transcript);
            logger.info("Transcript saved: " + filename);
        } catch (IOException e) {
            logger.severe("Failed to save transcript: " + e.getMessage());
        }
    }

    /**
     * Read all leads from CSV for a simple listing.
     * Demonstrates: BufferedReader, FileReader, reading line by line.
     */
    public List<String> readAllLeadLines() {
        List<String> lines = new ArrayList<>();
        initStorage();

        try (BufferedReader reader = new BufferedReader(new FileReader(ALL_LEADS_CSV))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                if (!line.isBlank()) lines.add(line);
            }
        } catch (IOException e) {
            logger.warning("Could not read leads file: " + e.getMessage());
        }
        return lines;
    }

    // --- Private helpers ---

    private void saveLeadSummaryFile(LeadProfile lead) {
        String filename = TRANSCRIPTS_DIR + "/summary_" + lead.getSessionId() + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(lead.toSummaryString());
        } catch (IOException e) {
            logger.warning("Could not save lead summary: " + e.getMessage());
        }
    }

    private void writeCsvHeader(String filepath, String header) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write(header);
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
