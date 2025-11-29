package com.example.demo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.concurrent.CompletableFuture;

public class Queries {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BASE_URL = "http://localhost:8080";

    public static CompletableFuture<Map<String, Object>> get(String endpoint) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET()
                .build();
        return sendRequest(request);
    }

    public static CompletableFuture<Map<String, Object>> post(String endpoint, Map<String, ?> body) {
        String requestBody = mapToJson(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBody))
                .build();
        return sendRequest(request);
    }

    public static CompletableFuture<Map<String, Object>> put(String endpoint, Map<String, ?> body) {
        String requestBody = mapToJson(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .PUT(BodyPublishers.ofString(requestBody))
                .build();
        return sendRequest(request);
    }

    public static CompletableFuture<Map<String, Object>> delete(String endpoint) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .DELETE()
                .build();
        return sendRequest(request);
    }

    private static CompletableFuture<Map<String, Object>> sendRequest(HttpRequest request) {
        return httpClient.sendAsync(request, BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(Queries::parseResponse)
                .exceptionally(e -> {
                    System.err.println("Erreur lors de la requête: " + e.getMessage());
                    return Map.of("error", e.getMessage());
                });
    }

    private static String mapToJson(Map<String, ?> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la conversion de la Map en JSON", e);
        }
    }

    private static Map<String, Object> parseResponse(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du parsing de la réponse JSON", e);
        }
    }
}