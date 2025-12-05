package com.example.demo.interfaces;

import com.example.demo.Params;
import com.example.demo.Queries;
import com.example.demo.components.Scrollbar;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;

public class Graphes extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor;
    private JPanel mainContentPanel;
    private Map<String, Object> projectData;

    public Graphes() {
        this.theme = Params.theme;
        initializeColors();
        setLayout(new BorderLayout());
        setBackground(bgColor);

        mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBackground(bgColor);

        JPanel loadingPanel = createLoadingPanel();
        mainContentPanel.add(loadingPanel);

        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(mainContentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);

        add(scrollPane, BorderLayout.CENTER);

        loadProjectData();
    }

    private void initializeColors() {
        if (theme == 0) {
            bgColor = new Color(245, 247, 250);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(30, 30, 30);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(59, 130, 246);
        } else {
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(20, 20, 20);
            textPrimary = new Color(230, 230, 230);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(0, 120, 215);
        }
    }

    private JPanel createLoadingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(100, 25, 25, 25));

        JLabel loadingLabel = new JLabel("Chargement des graphiques...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loadingLabel.setForeground(textSecondary);
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setMaximumSize(new Dimension(300, 10));
        progressBar.setBackground(bgColor);
        progressBar.setForeground(accentColor);

        panel.add(loadingLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(progressBar);

        return panel;
    }

    private void loadProjectData() {
        try {
            CompletableFuture<Map<String, Object>> projectDataFuture = Queries.get("/api/all/" + Params.projetID);

            projectDataFuture.thenAccept(data -> {
                if (data.containsKey("error")) {
                    SwingUtilities.invokeLater(() -> showError("Erreur: Impossible de récupérer les données du projet"));
                    return;
                }

                this.projectData = data;
                SwingUtilities.invokeLater(() -> displayGraphs());
            }).exceptionally(ex -> {
                SwingUtilities.invokeLater(() -> showError("Erreur: " + ex.getMessage()));
                return null;
            });

        } catch (Exception ex) {
            showError("Erreur: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        mainContentPanel.removeAll();
        JPanel errorPanel = new JPanel();
        errorPanel.setBackground(bgColor);
        errorPanel.setBorder(BorderFactory.createEmptyBorder(100, 25, 25, 25));

        JLabel errorLabel = new JLabel(message);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        errorLabel.setForeground(new Color(220, 38, 38));
        errorPanel.add(errorLabel);

        mainContentPanel.add(errorPanel);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void displayGraphs() {
        mainContentPanel.removeAll();
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titleLabel = new JLabel("Graphiques du projet");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContentPanel.add(titleLabel);
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        createGraphCard("Répartition des tâches par statut", createTaskStatusPieChart());
        createGraphCard("Progression des tâches", createTaskProgressBarChart());
        createGraphCard("Répartition des membres par rôle", createMemberRolePieChart());
        createGraphCard("Consommation du budget", createBudgetDoughnutChart());

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void createGraphCard(String title, String chartConfig) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 480));

        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(textPrimary);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionTitle);
        section.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel imageLabel = new JLabel("Chargement du graphique...", SwingConstants.CENTER);
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        imageLabel.setForeground(textSecondary);
        card.add(imageLabel, BorderLayout.CENTER);

        section.add(card);
        mainContentPanel.add(section);
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        loadQuickChartImage(chartConfig, imageLabel);
    }

    private void loadQuickChartImage(String chartConfig, JLabel imageLabel) {
        new Thread(() -> {
            try {
                String encodedChart = URLEncoder.encode(chartConfig, StandardCharsets.UTF_8.toString());
                String imageUrl = "https://quickchart.io/chart?c=" + encodedChart + "&w=700&h=350&bkg=" + 
                    (theme == 0 ? "white" : "%23141414");

                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedImage image = ImageIO.read(connection.getInputStream());
                    connection.disconnect();

                    if (image != null) {
                        // Redimensionner l'image pour qu'elle rentre dans la carte
                        int maxWidth = 750;
                        int maxHeight = 350;
                        
                        int width = image.getWidth();
                        int height = image.getHeight();
                        
                        if (width > maxWidth || height > maxHeight) {
                            double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);
                            width = (int) (width * scale);
                            height = (int) (height * scale);
                        }
                        
                        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        ImageIcon icon = new ImageIcon(scaledImage);
                        
                        SwingUtilities.invokeLater(() -> {
                            imageLabel.setIcon(icon);
                            imageLabel.setText("");
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            imageLabel.setText("Erreur lors du chargement du graphique");
                            imageLabel.setForeground(new Color(220, 38, 38));
                        });
                    }
                } else {
                    connection.disconnect();
                    SwingUtilities.invokeLater(() -> {
                        imageLabel.setText("Erreur HTTP: " + responseCode);
                        imageLabel.setForeground(new Color(220, 38, 38));
                    });
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setText("Erreur: " + e.getMessage());
                    imageLabel.setForeground(new Color(220, 38, 38));
                });
            }
        }).start();
    }

    private String createTaskStatusPieChart() {
        List<Map<String, Object>> taches = (List<Map<String, Object>>) projectData.get("taches");
        
        int enCours = 0;
        int terminees = 0;
        int enAttente = 0;

        for (Map<String, Object> tache : taches) {
            String etat = (String) tache.get("etat");
            if ("Terminé".equalsIgnoreCase(etat) || "Terminée".equalsIgnoreCase(etat)) {
                terminees++;
            } else if ("En cours".equalsIgnoreCase(etat)) {
                enCours++;
            } else {
                enAttente++;
            }
        }

        String textColor = theme == 0 ? "#1e1e1e" : "#e6e6e6";
        
        return "{type:'doughnut',data:{labels:['En cours','Terminées','En attente'],datasets:[{data:[" + 
            enCours + "," + terminees + "," + enAttente + 
            "],backgroundColor:['rgb(245,158,11)','rgb(34,197,94)','rgb(148,163,184)']}]}," +
            "options:{plugins:{legend:{position:'bottom',labels:{color:'" + textColor + "',font:{size:14}}}," +
            "title:{display:true,text:'Répartition des tâches',color:'" + textColor + "',font:{size:16,weight:'bold'}}," +
            "datalabels:{color:'white',font:{size:16,weight:'bold'},formatter:function(value,ctx){return value;}}}}}";
    }

    private String createTaskProgressBarChart() {
        List<Map<String, Object>> taches = (List<Map<String, Object>>) projectData.get("taches");
        
        StringBuilder labels = new StringBuilder();
        StringBuilder progress = new StringBuilder();
        
        boolean first = true;
        for (Map<String, Object> tache : taches) {
            if (!first) {
                labels.append(",");
                progress.append(",");
            }
            
            String titre = (String) tache.get("titre");
            labels.append("'").append(titre.replace("'", "\\'")).append("'");
            
            List<Map<String, Object>> sousTaches = (List<Map<String, Object>>) tache.get("sousTaches");
            int total = sousTaches.size();
            int completed = 0;
            for (Map<String, Object> st : sousTaches) {
                if ((Boolean) st.get("termine")) {
                    completed++;
                }
            }
            
            int progressPct = total > 0 ? (completed * 100) / total : 0;
            progress.append(progressPct);
            
            first = false;
        }

        String textColor = theme == 0 ? "#1e1e1e" : "#e6e6e6";
        String gridColor = theme == 0 ? "#e5e7eb" : "#374151";
        
        return "{type:'bar',data:{labels:[" + labels + "],datasets:[{label:'Progression (%)',data:[" + 
            progress + "],backgroundColor:'rgb(59,130,246)'}]}," +
            "options:{indexAxis:'y',plugins:{legend:{display:false}," +
            "title:{display:true,text:'Progression des tâches',color:'" + textColor + "',font:{size:16,weight:'bold'}}," +
            "datalabels:{anchor:'end',align:'end',color:'" + textColor + "',font:{size:12,weight:'bold'},formatter:function(value){return value+'%';}}}," +
            "scales:{x:{max:100,ticks:{color:'" + textColor + "'},grid:{color:'" + gridColor + "'}},y:{ticks:{color:'" + textColor + "'},grid:{display:false}}}}}";
    }

    private String createMemberRolePieChart() {
        List<Map<String, Object>> membres = (List<Map<String, Object>>) projectData.get("membres");
        
        Map<String, Integer> roleCount = new HashMap<>();
        for (Map<String, Object> membre : membres) {
            String role = (String) membre.get("role");
            roleCount.put(role, roleCount.getOrDefault(role, 0) + 1);
        }

        StringBuilder labels = new StringBuilder();
        StringBuilder data = new StringBuilder();
        
        boolean first = true;
        for (Map.Entry<String, Integer> entry : roleCount.entrySet()) {
            if (!first) {
                labels.append(",");
                data.append(",");
            }
            labels.append("'").append(entry.getKey().replace("'", "\\'")).append("'");
            data.append(entry.getValue());
            first = false;
        }

        String textColor = theme == 0 ? "#1e1e1e" : "#e6e6e6";
        
        return "{type:'pie',data:{labels:[" + labels + "],datasets:[{data:[" + data + 
            "],backgroundColor:['rgb(139,92,246)','rgb(236,72,153)','rgb(34,197,94)','rgb(59,130,246)','rgb(251,146,60)']}]}," +
            "options:{plugins:{legend:{position:'bottom',labels:{color:'" + textColor + "',font:{size:14}}}," +
            "title:{display:true,text:'Membres par rôle',color:'" + textColor + "',font:{size:16,weight:'bold'}}," +
            "datalabels:{color:'white',font:{size:14,weight:'bold'},formatter:function(value,ctx){return value;}}}}}";
    }

    private String createBudgetDoughnutChart() {
        Map<String, Object> projet = (Map<String, Object>) projectData.get("projet");
        
        Number budget = (Number) projet.get("budget");
        Number budgetConsomme = (Number) projet.get("budgetConsomme");
        
        double budgetTotal = budget != null ? budget.doubleValue() : 0;
        double budgetUtilise = budgetConsomme != null ? budgetConsomme.doubleValue() : 0;
        double budgetRestant = budgetTotal - budgetUtilise;

        String textColor = theme == 0 ? "#1e1e1e" : "#e6e6e6";
        
        return "{type:'doughnut',data:{labels:['Budget consommé','Budget restant'],datasets:[{data:[" + 
            (int)budgetUtilise + "," + (int)budgetRestant + 
            "],backgroundColor:['rgb(239,68,68)','rgb(34,197,94)']}]}," +
            "options:{plugins:{legend:{position:'bottom',labels:{color:'" + textColor + "',font:{size:14}}}," +
            "title:{display:true,text:'Budget du projet',color:'" + textColor + "',font:{size:16,weight:'bold'}}," +
            "datalabels:{color:'white',font:{size:16,weight:'bold'},formatter:function(value){return value+' DH';}}}}}";
    }
}