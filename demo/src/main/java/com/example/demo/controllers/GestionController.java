package com.example.demo.controllers;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.models.Document;
import com.example.demo.models.Notification;
import com.example.demo.models.Projet;
import com.example.demo.models.Tache;
import com.example.demo.repositories.DocumentRepository;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.repositories.ProjetRepository;
import com.example.demo.repositories.TacheRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/gestion")
@CrossOrigin(origins = "*")
public class GestionController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ProjetRepository projetRepository;

    @GetMapping("/{projetId}")
    
    public ResponseEntity<Map<String, Object>> getGestionData(@PathVariable int projetId) {
        try {
            System.out.println("get gestion data");
            System.out.println("Projet ID: " + projetId);

            Map<String, Object> response = new HashMap<>();

            // recuperer les infos dprojet
            Optional<Projet> projetOpt = projetRepository.findById((Integer) projetId);
            if (projetOpt.isPresent()) {
                Projet projet = projetOpt.get();
                Map<String, Object> projetInfo = new HashMap<>();
                projetInfo.put("nom", projet.getNom());
                projetInfo.put("description", projet.getDescription());
                projetInfo.put("dateDebut", projet.getDateDebut() != null ? projet.getDateDebut().getTime() : null);
                projetInfo.put("dateFin", projet.getDateFin() != null ? projet.getDateFin().getTime() : null);
                projetInfo.put("budget", projet.getBudget());
                response.put("projet", projetInfo);
                System.out.println("Projet trouve: " + projet.getNom());
            } else {
                System.out.println("Projet non trouve");
            }

            // historique hia somme dyal notification dyal dak projet
            List<Notification> notifications = notificationRepository.findByProjetId(projetId);
            System.out.println("Notifications trouve: " + notifications.size());

            List<Map<String, Object>> historiqueDTO = notifications.stream()
                    .map(n -> {
                        Map<String, Object> notifMap = new HashMap<>();
                        notifMap.put("id", n.getId());
                        notifMap.put("contenu", n.getContenu());
                        notifMap.put("dateEnvoie", n.getDateEnvoie().getTime());
                        notifMap.put("estLue", n.isEstLue());
                        if (n.getMembre() != null) {
                            notifMap.put("membreNom", n.getMembre().getNom() + " " +  n.getMembre().getPrenom());
                        } else {
                            notifMap.put("membreNom", "Système");
                        }
                        return notifMap;
                    })
                    //ordre desc
                    .sorted((n1, n2) -> ((Long) n2.get("dateEnvoie")).compareTo((Long) n1.get("dateEnvoie")))
                    .collect(Collectors.toList());

            // facturation hiya somme dyal depense kol tache f dak projet
            List<Tache> taches = tacheRepository.findByProjetId(projetId);
            System.out.println("Taches trouvee: " + taches.size());

            List<Map<String, Object>> facturation = taches.stream()
                    .filter(t -> t.getDepense() > 0)
                    .map(t -> {
                        Map<String, Object> expense = new HashMap<>();
                        expense.put("id", t.getId());
                        expense.put("titre", t.getTitre());
                        expense.put("depense", t.getDepense());
                        System.out.println("Tache: " + t.getTitre() + " " + t.getDepense() + " DHS");
                        return expense;
                    })
                    .collect(Collectors.toList());

            double totalDepenses = taches.stream()
                    .mapToDouble(Tache::getDepense)
                    .sum();
            System.out.println("Total depenses: " + totalDepenses);

            // liste dyal doc li kaynin fwahed projet
            List<Document> documents = documentRepository.findAllByProjetId(projetId);
            System.out.println("Documents trouves: " + documents.size());
          
            List<Map<String, Object>> documentsDTO = documents.stream()
                    .map(d -> {
                        Map<String, Object> docMap = new HashMap<>();
                        docMap.put("id", d.getId());
                        docMap.put("nom", d.getNom());
                        docMap.put("description", d.getDescription());
                        docMap.put("contenuBase64",
                        //transformer contenu binaire lString 
                                d.getContenu() != null ? Base64.getEncoder().encodeToString(d.getContenu()) : "");
                        
                            if (d.getDateCreation() != null) {
                            docMap.put("dateCreation",
                                    d.getDateCreation().atStartOfDay()
                                            .atZone(java.time.ZoneId.systemDefault())
                                            .toInstant()
                                            .toEpochMilli());
                        }
                        docMap.put("size", d.getContenu() != null ? d.getContenu().length : 0);
                        System.out.println("Document: " + d.getNom());
                        return docMap;
                    })
                    .sorted((d1, d2) -> ((Long) d2.get("dateCreation")).compareTo((Long) d1.get("dateCreation")))
                    .collect(Collectors.toList());

            response.put("historique", historiqueDTO);
            response.put("facturation", facturation);
            response.put("totalDepenses", totalDepenses);
            response.put("documents", documentsDTO);
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("erreur dans getGestionData: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/{projetId}/rapport/pdf")
    //renvoie PDF
    public ResponseEntity<byte[]> downloadRapportPDF(@PathVariable int projetId) {

        try {
            Projet projet = projetRepository.findById(projetId);

            // infos dyal historique
            List<Map<String, Object>> historiqueDTO = notificationRepository.findByProjetId(projetId)
                    .stream()
                    .map(n -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("contenu", n.getContenu());
                        m.put("dateEnvoie", n.getDateEnvoie().getTime());
                        m.put("membreNom", n.getMembre() != null ? n.getMembre().getNom() : "Système");
                        return m;
                    })
                    .toList();

            // infos dyal facturation
            List<Tache> taches = tacheRepository.findByProjetId(projetId);

            List<Map<String, Object>> facturation = taches.stream()
                    .filter(t -> t.getDepense() > 0)
                    .map(t -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("tacheTitre", t.getTitre());
                        m.put("tacheDescription", t.getDescription());
                        m.put("etat", t.getEtat());
                        m.put("depense", t.getDepense());
                        if (t.getDateFin() != null) {
                            m.put("dateFin", t.getDateFin()
                                    .atStartOfDay()
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli());
                        }
                        return m;
                    })
                    .toList();

            double totalDepenses = taches.stream().mapToDouble(Tache::getDepense).sum();

            // infos dyal docs
            List<Map<String, Object>> documentsDTO = documentRepository.findAllByProjetId(projetId)
                    .stream()
                    .map(d -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("nom", d.getNom());
                        m.put("size", d.getContenu() != null ? d.getContenu().length : 0);
                        if (d.getDateCreation() != null) {
                            m.put("dateCreation", d.getDateCreation()
                                    .atStartOfDay()
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli());
                        }
                        return m;
                    })
                    .toList();

            // appel method li katgenerer PDF 
            byte[] pdfBytes = generatePDF(
                    Map.of(
                            "projet", Map.of(
                                    "nom", projet.getNom(),
                                    "description", projet.getDescription(),
                                    "budget", projet.getBudget(),
                                    "budgetConsomme", projet.getBudgetConsomme()),
                            "historique", historiqueDTO,
                            "facturation", facturation,
                            "documents", documentsDTO,
                            "totalDepenses", totalDepenses));

            String filename = "Rapport_" + projet.getNom().replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public byte[] generatePDF(Map<String, Object> data) throws Exception {
        StringBuilder html = buildHTML(data);//appel lmethod li katcreer code html

        //appel dyal api pdf bach transformi html l pdf 
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(
                mapper.writeValueAsString(Map.of("html", html.toString())),
                headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                "https://pahae-utils.vercel.app/api/generatePDF",
                HttpMethod.POST,
                request,
                byte[].class);

        return response.getBody();
    }

    //code html li ghan siftuh l api bach iweli PDF
    public StringBuilder buildHTML(Map<String, Object> data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Map<String, Object> projet = (Map<String, Object>) data.get("projet");
        List<Map<String, Object>> historique = (List<Map<String, Object>>) data.get("historique");
        List<Map<String, Object>> facturation = (List<Map<String, Object>>) data.get("facturation");
        List<Map<String, Object>> documents = (List<Map<String, Object>>) data.get("documents");
        double totalDepenses = (double) data.get("totalDepenses");

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang='fr'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>Rapport de Projet</title>");
        html.append("<style>");

        html.append("body{font-family:'Segoe UI',Arial,sans-serif;background:#f1f5f9;padding:40px;color:#1f2937;}");
        html.append(
                ".container{background:#fff;padding:50px;border-radius:14px;box-shadow:0 10px 30px rgba(0,0,0,.08);}");
        html.append(
                ".header{display:flex;align-items:center;gap:20px;border-bottom:2px solid #e5e7eb;padding-bottom:20px;margin-bottom:40px;}");
        html.append(".logo{height:60px;}");
        html.append(".app-title{font-size:28px;font-weight:700;color:#1e3a8a;}");
        html.append(".subtitle{color:#6b7280;font-size:14px;margin-top:4px;}");

        html.append(".section{margin-top:35px;padding:25px;background:#f8fafc;border-radius:12px;}");
        html.append(".section h2{margin-top:0;border-left:5px solid #2563eb;padding-left:12px;color:#1e293b;}");

        html.append(
                "table{width:100%;border-collapse:collapse;margin-top:15px;background:#fff;border-radius:8px;overflow:hidden;}");
        html.append("th,td{border-bottom:1px solid #e5e7eb;padding:12px;text-align:left;}");
        html.append("th{background:#f1f5f9;font-weight:600;color:#374151;}");
        html.append("tr:last-child td{border-bottom:none;}");

        html.append(
                ".badge{padding:5px 12px;border-radius:999px;background:#dbeafe;color:#1e40af;font-size:12px;font-weight:600;}");

        html.append(".footer{text-align:center;margin-top:50px;font-size:12px;color:#9ca3af;}");

        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");

        /* HEADER*/
        html.append("<div class='header'>");
        html.append("<img class='logo' src='https://taskyx.vercel.app/logo.png' />");
        html.append("<div>");
        html.append("<div class='app-title'>Tasky – Rapport de Projet</div>");
        html.append("<div class='subtitle'>Généré le ")
                .append(sdf.format(new Date()))
                .append("</div>");
        html.append("</div>");
        html.append("</div>");   

        /* INFOS PROJET */
        html.append("<div class='section'>");
        html.append("<h2>1. Informations générales</h2>");
        html.append("<p><b>Nom du projet :</b> ").append(projet.get("nom")).append("</p>");
        html.append("<p><b>Description :</b> ").append(projet.get("description")).append("</p>");
        html.append("<p><b>Budget :</b> ").append(projet.get("budget")).append(" MAD</p>");
        html.append("</div>");

        /*HISTORIQUE */
        html.append("<div class='section'>");
        html.append("<h2>2. Historique</h2>");
        html.append("<table><tr><th>Date</th><th>Membre</th><th>Contenu</th></tr>");

        for (Map<String, Object> h : historique) {
            html.append("<tr>");
            html.append("<td>").append(sdf.format(new Date((Long) h.get("dateEnvoie")))).append("</td>");
            html.append("<td>").append(h.get("membreNom")).append("</td>");
            html.append("<td>").append(h.get("contenu")).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("</div>");

        /*  FACTURATION  */
        html.append("<div class='section'>");
        html.append("<h2>3. Facturation</h2>");
        html.append("<table><tr><th>Tâche</th><th>Description</th><th>État</th><th>Dépense (MAD)</th></tr>");

        for (Map<String, Object> f : facturation) {
            html.append("<tr>");
            html.append("<td>").append(f.get("tacheTitre")).append("</td>");
            html.append("<td>").append(f.get("tacheDescription")).append("</td>");
            html.append("<td><span class='badge'>").append(f.get("etat")).append("</span></td>");
            html.append("<td>").append(f.get("depense")).append("</td>");
            html.append("</tr>");
        }

        html.append("<tr>");
        html.append("<th colspan='3'>Total</th>");
        html.append("<th>").append(totalDepenses).append("</th>");
        html.append("</tr>");

        html.append("</table>");
        html.append("</div>");

        /*  DOCUMENTS */
        html.append("<div class='section'>");
        html.append("<h2>4. Documents</h2>");
        html.append("<table><tr><th>Nom</th><th>Taille</th><th>Date</th></tr>");

        for (Map<String, Object> d : documents) {
            html.append("<tr>");
            html.append("<td>").append(d.get("nom")).append("</td>");
            html.append("<td>").append(d.get("size")).append(" octets</td>");
            html.append("<td>");
            if (d.get("dateCreation") != null) {
                html.append(sdf.format(new Date((Long) d.get("dateCreation"))));
            } else {
                html.append("-");
            }
            html.append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("</div>");

        /*  FOOTER  */
        html.append("<div class='footer'>");
        html.append("© ").append(new Date().getYear() + 1900)
                .append(" Tasky • Rapport généré automatiquement");
        html.append("</div>");

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html;
    }
}