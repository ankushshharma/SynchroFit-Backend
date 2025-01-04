package com.automation.test.gemini;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {
    static {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("GeminiApiKey", dotenv.get("GeminiApiKey"));
    }
}
