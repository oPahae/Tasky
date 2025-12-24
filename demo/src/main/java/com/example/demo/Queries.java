package com.example.demo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class Queries {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BASE_URL = "http://localhost:8080";

    /* =========================
       ======= JSON API ========
       ========================= */

    public static CompletableFuture<Map<String, Object>> get(String endpoint) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET()
                .build();
        return sendJsonRequest(request);
    }

    public static CompletableFuture<Map<String, Object>> post(String endpoint, Map<String, ?> body) {
        String requestBody = mapToJson(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBody))
                .build();
        return sendJsonRequest(request);
    }

    public static CompletableFuture<Map<String, Object>> put(String endpoint, Map<String, ?> body) {
        String requestBody = mapToJson(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .PUT(BodyPublishers.ofString(requestBody))
                .build();
        return sendJsonRequest(request);
    }

    public static CompletableFuture<Map<String, Object>> delete(String endpoint) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .DELETE()
                .build();
        return sendJsonRequest(request);
    }

    private static CompletableFuture<Map<String, Object>> sendJsonRequest(HttpRequest request) {
        return httpClient.sendAsync(request, BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException(
                                "Erreur HTTP " + response.statusCode() + " : " + response.body());
                    }
                    return response.body();
                })
                .thenApply(Queries::parseResponse)
                .exceptionally(e -> {
                    System.err.println("Erreur lors de la requête JSON: " + e.getMessage());
                    return Map.of("success", false, "error", e.getMessage());
                });
    }

    /* =========================
       ======= PDF / BINARY ====
       ========================= */

    public static CompletableFuture<byte[]> getBinary(String endpoint) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET()
                .build();

        return httpClient.sendAsync(request, BodyHandlers.ofByteArray())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new RuntimeException("Erreur HTTP " + response.statusCode());
                    }
                    return response.body();
                });
    }

    /* =========================
       ======= UTILITAIRES =====
       ========================= */

    private static String mapToJson(Map<String, ?> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la conversion Map → JSON", e);
        }
    }

    private static Map<String, Object> parseResponse(String json) {
        try {
            return objectMapper.readValue(json,
                    new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du parsing de la réponse JSON", e);
        }
    }
}
