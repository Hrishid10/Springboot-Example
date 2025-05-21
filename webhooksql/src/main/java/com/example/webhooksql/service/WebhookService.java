package com.example.webhooksql.service;

import jakarta.annotation.PostConstruct;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String regNo = "REG12347";

    @PostConstruct
    public void startProcess() {
        try {
            String postUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            String body = String.format("""
                {
                    "name": "John Doe",
                    "regNo": "%s",
                    "email": "john@example.com"
                }
            """, regNo);

            HttpEntity<String> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(postUrl, HttpMethod.POST, request, Map.class);

            System.out.println("Response Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String webhook = (String) response.getBody().get("webhook");
                String accessToken = (String) response.getBody().get("accessToken");

                System.out.println("Webhook URL: " + webhook);
                System.out.println("Access Token: " + accessToken);

                if (webhook == null || accessToken == null || webhook.isBlank() || accessToken.isBlank()) {
                    System.out.println("Webhook or accessToken is null or blank.");
                    return;
                }

                String questionUrl = regNo.endsWith("7")
                        ? "https://drive.google.com/file/d/1IeSI6l6KoSQAFfRihIT9tEDICtoz-G/view?usp=sharing"
                        : "https://drive.google.com/file/d/143MR5cLFrlNEuHzzWJ5RHnEWuijuM9X/view?usp=sharing";

                String finalQuery = "SELECT * FROM your_table WHERE your_condition;";

                submitSolution(webhook, accessToken, finalQuery);
            } else {
                System.out.println("Failed to generate webhook: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.out.println("Exception in startProcess():");
            e.printStackTrace();
        }
    }

    private void submitSolution(String webhook, String token, String query) {
    try {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(token);

        String body = String.format("""
            {
                "solution": "%s"
            }
        """, query.replace("\"", "\\\""));

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(webhook, HttpMethod.POST, request, String.class);

        System.out.println("Submission Status Code: " + response.getStatusCode());
        System.out.println("Submission Response: " + response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Submission successful!");
        } else {
            System.out.println("Submission failed: " + response.getStatusCode() + " - " + response.getBody());
        }

    } catch (Exception e) {
        System.out.println("Exception in submitSolution():");
        e.printStackTrace();
    }
}
}