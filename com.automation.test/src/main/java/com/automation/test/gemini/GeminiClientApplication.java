package com.automation.test.gemini;

import com.automation.test.gemini.config.ApiKeyProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class GeminiClientApplication {

//	@Value("${google.api.key}")
	private String apiKey;

	private final OkHttpClient client;
	private final ObjectMapper objectMapper;

	public GeminiClientApplication(ApiKeyProvider apiKeyProvider) {
		this.apiKey = apiKeyProvider.getApiKey();
		this.client = new OkHttpClient();
		this.objectMapper = new ObjectMapper();
	}

	@PostMapping("/generateContent")
	public ResponseEntity<String> generateContent(@RequestBody String prompt) throws IOException {
		String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
		System.out.println("ApiKey:"+apiKey);
		// Prepare request body with user prompt
		Map<String, Object> requestBody = new HashMap<>();
		List<Map<String, Object>> contents = new ArrayList<>();
		Map<String, Object> content = new HashMap<>();
		List<Map<String, String>> parts = new ArrayList<>();
		Map<String, String> part = new HashMap<>();
		part.put("text", prompt);
		parts.add(part);
		content.put("parts", parts);
		contents.add(content);
		requestBody.put("contents", contents);

		String jsonBody = objectMapper.writeValueAsString(requestBody);

		// Create connection and send request
		MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
		okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, jsonBody);

		// Create and build the request
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.addHeader("Content-Type", "application/json")
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful() && response.body() != null) {
				return ResponseEntity.ok(response.body().string());
			} else {
				String errorMessage = response.body() != null ? response.body().string() : response.message();
				return ResponseEntity.status(response.code())
						.body(String.format("Error: %s", errorMessage));
			}
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(GeminiClientApplication.class, args);
	}
}