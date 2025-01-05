package com.automation.test.gemini.config;


import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.cdimascio.dotenv.Dotenv;

@Component
public class ApiKeyProvider {
    private static final Logger logger = LoggerFactory.getLogger(ApiKeyProvider.class);
    private final String apiKey;

    public ApiKeyProvider() {
        // First try system environment variables
        String key = System.getenv("GeminiApiKey");

        // If not found, try .env file
        if (key == null || key.trim().isEmpty()) {
            try {
                logger.info("Attempting to load API key from .env file");
                Dotenv dotenv = Dotenv.configure()
                        .directory(".")
                        .load();
                key = dotenv.get("GeminiApiKey");
            } catch (Exception e) {
                logger.warn("Failed to load .env file: {}", e.getMessage());
            }
        }

        if (key == null || key.trim().isEmpty()) {
            logger.error("GeminiApiKey not found in environment variables or .env file");
            throw new IllegalStateException("GeminiApiKey is required but not configured");
        }

        this.apiKey = key;
        logger.info("API Key loaded successfully");
    }

    public String getApiKey() {
        return apiKey;
    }
}