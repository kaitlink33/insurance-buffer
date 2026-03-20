package com.insurancebuffer;

import com.insurancebuffer.service.LeadStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * InsuranceBufferApplication - Spring Boot entry point.
 * CIS200 Honors - Java Backend
 */
@SpringBootApplication
public class InsuranceBufferApplication implements CommandLineRunner {

    @Autowired
    private LeadStorageService leadStorageService;

    public static void main(String[] args) {
        SpringApplication.run(InsuranceBufferApplication.class, args);
        System.out.println("==============================================");
        System.out.println("  Insurance Lead Capture API - Running");
        System.out.println("  CIS200 Honors Project - Java Backend");
        System.out.println("==============================================");
    }

    @Override
    public void run(String... args) {
        // Initialize file storage on startup
        leadStorageService.initStorage();
    }
}
