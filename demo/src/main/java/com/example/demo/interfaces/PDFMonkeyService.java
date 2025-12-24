package com.example.demo.interfaces;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.models.Projet;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class PDFMonkeyService {

    @Value("${pdfmonkey.api.key:QyKRWXDEa-bVxK7ynjzv}")
    private String apiKey;

    @Value("${pdfmonkey.template.id:F88D4427-D2B0-4245-8AC1-79C717ABEA78}")
    private String templateId;

    private static final String API_URL = "https://api.pdfmonkey.io/api/v1/documents";
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public PDFMonkeyService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Génère un PDF avec les données de gestion du projet
     */
    public byte[] generateGestionReport(
            Projet projet,
            List<Map<String, Object>> historique,
            List<Map<String, Object>> facturation,
            List<Map<String, Object>> documents,
            double totalDepenses) throws IOException, InterruptedException {

        // Préparer les données pour le template
        Map<String, Object> payload = preparePayload(
                projet,
                historique,
                facturation,
                documents,
                totalDepenses
        );

        // Debug - Afficher le payload
        System.out.println("=== DEBUG PAYLOAD AVANT ENVOI ===");
        System.out.println("Payload JSON: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
        System.out.println("================================");

        // Créer le document PDF
        String documentId = createPDFDocument(payload);

        // Attendre que le PDF soit généré et le télécharger
        return waitAndDownloadPDF(documentId);
    }

    /**
     * Prépare le payload JSON pour PDFMonkey
     */
    private Map<String, Object> preparePayload(
            Projet projet,
            List<Map<String, Object>> historique,
            List<Map<String, Object>> facturation,
            List<Map<String, Object>> documents,
            double totalDepenses) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd MMM yyyy", Locale.FRENCH);
        String dateGeneration = dateFormat.format(new Date());

        // Informations du projet
        String projetNom = projet.getNom() != null ? projet.getNom() : "Projet sans nom";
        String projetDescription = projet.getDescription() != null ? projet.getDescription() : "";
        String projetDateDebut = projet.getDateDebut() != null ? dateFormat.format(projet.getDateDebut()) : "N/A";
        String projetDateFin = projet.getDateFin() != null ? dateFormat.format(projet.getDateFin()) : "En cours";
        double budget = projet.getBudget();

        // Transformer l'historique
        List<Map<String, String>> historiqueFormatted = new ArrayList<>();
        System.out.println("=== TRANSFORMATION HISTORIQUE ===");
        System.out.println("Nombre d'événements reçus: " + historique.size());
        
        for (Map<String, Object> event : historique) {
            Map<String, String> eventMap = new HashMap<>();
            
            try {
                // Date
                Long dateMillis = null;
                Object dateObj = event.get("dateEnvoie");
                if (dateObj instanceof Long) {
                    dateMillis = (Long) dateObj;
                } else if (dateObj instanceof Integer) {
                    dateMillis = ((Integer) dateObj).longValue();
                } else if (dateObj != null) {
                    dateMillis = Long.parseLong(dateObj.toString());
                }
                
                String formattedDate = dateMillis != null ? 
                    dateTimeFormat.format(new Date(dateMillis)) : "Date inconnue";
                
                // Contenu
                String contenu = event.get("contenu") != null ? 
                    event.get("contenu").toString() : "Aucune description";
                
                // Membre
                String membreNom = event.get("membreNom") != null ? 
                    event.get("membreNom").toString() : "Système";
                
                eventMap.put("date", formattedDate);
                eventMap.put("description", contenu);
                eventMap.put("membre", membreNom);
                
                historiqueFormatted.add(eventMap);
                
                System.out.println("Événement ajouté: " + formattedDate + " - " + contenu + " - " + membreNom);
                
            } catch (Exception e) {
                System.err.println("Erreur lors du formatage d'un événement: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("Historique formaté: " + historiqueFormatted.size() + " événements");
        System.out.println("=================================");

        // Transformer la facturation
        List<Map<String, String>> facturationFormatted = new ArrayList<>();
        System.out.println("=== TRANSFORMATION FACTURATION ===");
        System.out.println("Nombre de dépenses reçues: " + facturation.size());
        
        for (Map<String, Object> expense : facturation) {
            Map<String, String> expenseMap = new HashMap<>();
            
            try {
                // Titre de la tâche
                String tacheTitre = expense.get("tacheTitre") != null ? 
                    expense.get("tacheTitre").toString() : "Tâche sans titre";
                expenseMap.put("tacheTitre", tacheTitre);
                
                // État
                String etat = expense.get("etat") != null ? 
                    expense.get("etat").toString() : "Non défini";
                expenseMap.put("etat", etat);
                
                // Description de la tâche
                if (expense.get("tacheDescription") != null) {
                    String description = expense.get("tacheDescription").toString();
                    if (description != null && !description.isEmpty()) {
                        expenseMap.put("tacheDescription", description);
                    }
                }
                
                // Date de fin formatée
                if (expense.containsKey("dateFin") && expense.get("dateFin") != null) {
                    Long dateMillis = null;
                    Object dateObj = expense.get("dateFin");
                    if (dateObj instanceof Long) {
                        dateMillis = (Long) dateObj;
                    } else if (dateObj instanceof Integer) {
                        dateMillis = ((Integer) dateObj).longValue();
                    } else if (dateObj != null) {
                        dateMillis = Long.parseLong(dateObj.toString());
                    }
                    
                    if (dateMillis != null) {
                        expenseMap.put("dateFin", dateFormat.format(new Date(dateMillis)));
                    }
                }
                
                // Montant de la dépense
                double depense = 0.0;
                Object depenseObj = expense.get("depense");
                if (depenseObj instanceof Number) {
                    depense = ((Number) depenseObj).doubleValue();
                } else if (depenseObj != null) {
                    depense = Double.parseDouble(depenseObj.toString());
                }
                expenseMap.put("depense", String.format("%.2f", depense));
                
                facturationFormatted.add(expenseMap);
                
                System.out.println("Dépense ajoutée: " + tacheTitre + " - " + depense + " DHS");
                
            } catch (Exception e) {
                System.err.println("Erreur lors du formatage d'une dépense: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("Facturation formatée: " + facturationFormatted.size() + " dépenses");
        System.out.println("==================================");

        // Transformer les documents
        List<Map<String, String>> documentsFormatted = new ArrayList<>();
        System.out.println("=== TRANSFORMATION DOCUMENTS ===");
        System.out.println("Nombre de documents reçus: " + documents.size());
        
        for (Map<String, Object> doc : documents) {
            Map<String, String> docMap = new HashMap<>();
            
            try {
                // Nom
                String nom = doc.get("nom") != null ? 
                    doc.get("nom").toString() : "Document sans nom";
                docMap.put("nom", nom);
                
                // Date de création
                Long dateMillis = null;
                Object dateObj = doc.get("dateCreation");
                if (dateObj instanceof Long) {
                    dateMillis = (Long) dateObj;
                } else if (dateObj instanceof Integer) {
                    dateMillis = ((Integer) dateObj).longValue();
                } else if (dateObj != null) {
                    dateMillis = Long.parseLong(dateObj.toString());
                }
                
                String dateCreation = dateMillis != null ? 
                    dateFormat.format(new Date(dateMillis)) : "Date inconnue";
                docMap.put("dateCreation", dateCreation);
                
                // Taille
                long size = 0;
                Object sizeObj = doc.get("size");
                if (sizeObj instanceof Number) {
                    size = ((Number) sizeObj).longValue();
                } else if (sizeObj != null) {
                    size = Long.parseLong(sizeObj.toString());
                }
                docMap.put("taille", formatFileSize(size));
                
                // Icône et classe
                String extension = getFileExtension(nom);
                docMap.put("icon", getDocumentIcon(extension));
                docMap.put("iconClass", getDocumentIconClass(extension));
                
                documentsFormatted.add(docMap);
                
                System.out.println("Document ajouté: " + nom + " - " + formatFileSize(size));
                
            } catch (Exception e) {
                System.err.println("Erreur lors du formatage d'un document: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("Documents formatés: " + documentsFormatted.size() + " documents");
        System.out.println("================================");

        // Calculer le dépassement
        boolean budgetDepasse = totalDepenses > budget;
        double depassement = budgetDepasse ? totalDepenses - budget : 0;

        // Construire le payload
        Map<String, Object> payload = new HashMap<>();
        
        // Informations du projet
        payload.put("projetId", String.valueOf(projet.getId()));
        payload.put("projetNom", projetNom);
        payload.put("projetDescription", projetDescription);
        payload.put("projetDateDebut", projetDateDebut);
        payload.put("projetDateFin", projetDateFin);
        
        // Autres données
        payload.put("dateGeneration", dateGeneration);
        payload.put("totalDepenses", String.format("%.2f", totalDepenses));
        payload.put("budget", String.format("%.2f", budget));
        payload.put("nombreEvenements", String.valueOf(historiqueFormatted.size()));
        
        // IMPORTANT: Toujours inclure les tableaux, même vides
        payload.put("historique", historiqueFormatted);
        payload.put("facturation", facturationFormatted);
        payload.put("documents", documentsFormatted);
        
        payload.put("budgetDepasse", budgetDepasse);
        payload.put("depassement", String.format("%.2f", depassement));

        // Debug final
        System.out.println("=== RÉSUMÉ PAYLOAD ===");
        System.out.println("Projet: " + projetNom);
        System.out.println("Historique: " + historiqueFormatted.size() + " événements");
        System.out.println("Facturation: " + facturationFormatted.size() + " dépenses");
        System.out.println("Documents: " + documentsFormatted.size() + " documents");
        System.out.println("Budget: " + budget + " DHS");
        System.out.println("Total dépenses: " + totalDepenses + " DHS");
        System.out.println("======================");

        return payload;
    }

    /**
     * Crée un document PDF via l'API PDFMonkey
     */
    private String createPDFDocument(Map<String, Object> data) throws IOException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("document", Map.of(
                "document_template_id", templateId,
                "payload", data,
                "status", "pending"
        ));

        String json = objectMapper.writeValueAsString(requestBody);
        
        System.out.println("=== REQUÊTE PDFMONKEY ===");
        System.out.println("URL: " + API_URL);
        System.out.println("Template ID: " + templateId);
        System.out.println("JSON envoyé: " + json);
        System.out.println("=========================");

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(json, MediaType.get("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            
            System.out.println("=== RÉPONSE PDFMONKEY ===");
            System.out.println("Status: " + response.code());
            System.out.println("Body: " + responseBody);
            System.out.println("=========================");
            
            if (!response.isSuccessful()) {
                throw new IOException("Erreur PDFMonkey: " + response.code() + " - " + responseBody);
            }

            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            Map<String, Object> document = (Map<String, Object>) responseMap.get("document");
            
            String documentId = (String) document.get("id");
            System.out.println("Document créé avec ID: " + documentId);
            
            return documentId;
        }
    }

    /**
     * Attend que le PDF soit généré et le télécharge
     */
    private byte[] waitAndDownloadPDF(String documentId) throws IOException, InterruptedException {
        int maxAttempts = 30;
        int attempt = 0;

        System.out.println("=== ATTENTE GÉNÉRATION PDF ===");
        
        while (attempt < maxAttempts) {
            String status = checkDocumentStatus(documentId);
            System.out.println("Tentative " + (attempt + 1) + "/" + maxAttempts + " - Status: " + status);

            if ("success".equals(status)) {
                System.out.println("PDF généré avec succès!");
                System.out.println("==============================");
                return downloadPDF(documentId);
            } else if ("failure".equals(status)) {
                System.err.println("La génération du PDF a échoué");
                System.out.println("==============================");
                throw new IOException("La génération du PDF a échoué");
            }

            Thread.sleep(1000);
            attempt++;
        }

        System.err.println("Timeout: Le PDF n'a pas pu être généré dans les temps");
        System.out.println("==============================");
        throw new IOException("Timeout: Le PDF n'a pas pu être généré dans les temps");
    }

    /**
     * Vérifie le statut du document
     */
    private String checkDocumentStatus(String documentId) throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + "/" + documentId)
                .header("Authorization", "Bearer " + apiKey)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erreur lors de la vérification du statut");
            }

            String responseBody = response.body().string();
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            Map<String, Object> document = (Map<String, Object>) responseMap.get("document");
            
            return (String) document.get("status");
        }
    }

    /**
     * Télécharge le PDF généré
     */
    private byte[] downloadPDF(String documentId) throws IOException {
        Request statusRequest = new Request.Builder()
                .url(API_URL + "/" + documentId)
                .header("Authorization", "Bearer " + apiKey)
                .get()
                .build();

        String downloadUrl;
        try (Response response = client.newCall(statusRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erreur lors de la récupération de l'URL du PDF");
            }

            String responseBody = response.body().string();
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            Map<String, Object> document = (Map<String, Object>) responseMap.get("document");
            
            downloadUrl = (String) document.get("download_url");
            System.out.println("URL de téléchargement: " + downloadUrl);
        }

        Request downloadRequest = new Request.Builder()
                .url(downloadUrl)
                .get()
                .build();

        try (Response response = client.newCall(downloadRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erreur lors du téléchargement du PDF");
            }

            byte[] pdfBytes = response.body().bytes();
            System.out.println("PDF téléchargé: " + pdfBytes.length + " bytes");
            
            return pdfBytes;
        }
    }

    // Méthodes utilitaires
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1).toLowerCase() : "";
    }

    private String getDocumentIcon(String extension) {
        switch (extension) {
            case "pdf": return "📄";
            case "docx":
            case "doc": return "📝";
            case "xlsx":
            case "xls": return "📊";
            case "fig": return "🎨";
            case "zip":
            case "rar": return "🗜";
            default: return "📎";
        }
    }

    private String getDocumentIconClass(String extension) {
        switch (extension) {
            case "pdf": return "pdf";
            case "docx":
            case "doc": return "doc";
            case "xlsx":
            case "xls": return "excel";
            default: return "other";
        }
    }
}