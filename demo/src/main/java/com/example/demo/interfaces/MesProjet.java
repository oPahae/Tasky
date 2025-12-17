package com.example.demo.interfaces;

import com.example.demo.Params;
import com.example.demo.components.Scrollbar;
import com.example.demo.Queries;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class MesProjet extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, progressBg, progressFill;
    private List<Projet> projets;
    public JPanel mainContentPanel;
    private CardLayout contentLayout;
    private int colorCounter = 0;
    private Consumer<String> onClick;
    private int userId;

    public MesProjet(Consumer<String> onClick, int userId) {
        this.theme = Params.theme;
        this.onClick = onClick;
        this.userId = userId;
        
        initializeColors();
        projets = new ArrayList<>();
        
        setLayout(new BorderLayout());
        setBackground(bgColor);
        contentLayout = new CardLayout();
        mainContentPanel = new JPanel(contentLayout);
        mainContentPanel.setBackground(bgColor);

        JPanel overviewPanel = createOverviewPanel();
        JPanel projetDetailPanel = createProjetDetailPanel(null);

        mainContentPanel.add(overviewPanel, "overview");
        mainContentPanel.add(projetDetailPanel, "detail");

        add(mainContentPanel, BorderLayout.CENTER);
        contentLayout.show(mainContentPanel, "overview");
        
        loadProjetsFromBackend();
    }

    private void loadProjetsFromBackend() {
        System.out.println("Chargement des projets du backend pour userId: " + userId);
        
        Queries.get("/api/projet/user/" + userId)
                .thenAccept(response -> {
                    System.out.println("Réponse projets: " + response);
                    projets.clear();
                    
                    if (response.containsKey("error")) {
                        System.err.println("Erreur API projets: " + response.get("error"));
                        return;
                    }
                    
                    try {
                        Object projetsObj = response;
                        if (response instanceof java.util.LinkedHashMap) {
                            if (response.containsKey("projets")) {
                                projetsObj = response.get("projets");
                            } else if (response.isEmpty()) {
                                System.out.println("Aucun projet retourné");
                                updateUI();
                                return;
                            }
                        }
                        
                        if (projetsObj instanceof List) {
                            List<?> projetsData = (List<?>) projetsObj;
                            
                            for (Object projetObj : projetsData) {
                                if (projetObj instanceof Map) {
                                    Map<String, Object> projetMap = (Map<String, Object>) projetObj;
                                    
                                    Integer id = ((Number) projetMap.getOrDefault("id", 0)).intValue();
                                    String nom = (String) projetMap.getOrDefault("nom", "Sans nom");
                                    String code = (String) projetMap.getOrDefault("code", "");
                                    String description = (String) projetMap.getOrDefault("description", "");
                                    Object budgetObj = projetMap.getOrDefault("budget", 0);
                                    Double budget = budgetObj instanceof Number ? ((Number) budgetObj).doubleValue() : 0.0;
                                    Object budgetConsommeObj = projetMap.getOrDefault("budgetConsomme", 0);
                                    Double budgetConsomme = budgetConsommeObj instanceof Number ? ((Number) budgetConsommeObj).doubleValue() : 0.0;
                                    
                                    Projet projet = new Projet(id, nom, code, description, budget, budgetConsomme);
                                    projets.add(projet);
                                    System.out.println("Projet ajouté: " + nom);
                                }
                            }
                        }
                        
                        updateUI();
                    } catch (Exception e) {
                        System.err.println("Erreur parsing projets: " + e.getMessage());
                        e.printStackTrace();
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Exception lors du chargement des projets: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                });
    }

   

    private void initializeColors() {
        if (theme == 0) { // Light mode
            bgColor = new Color(245, 247, 250);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(30, 30, 30);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(59, 130, 246);
            progressBg = new Color(229, 231, 235);
            progressFill = new Color(34, 197, 94);
        } else { // Dark mode
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(20, 20, 20);
            textPrimary = new Color(230, 230, 230);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(0, 120, 215);
            progressBg = new Color(30, 30, 30);
            progressFill = new Color(0, 200, 100);
        }
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(bgColor);

        JLabel titleLabel = new JLabel("Mes Projets");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(titleLabel);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel subtitleLabel = new JLabel("Vous avez " + projets.size() + " projet(s)");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(textSecondary);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(subtitleLabel);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel statsPanel = createStatsPanel();
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(statsPanel);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel projetsPanel = createProjetsSection();
        projetsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(projetsPanel);

        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(bgColor);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        int totalProjets = projets.size();
        int projetsCours = (int) projets.stream().filter(p -> p.progress < 100).count();
        int projetsTermines = (int) projets.stream().filter(p -> p.progress == 100).count();

        panel.add(createStatCard("Projets totaux", String.valueOf(totalProjets),
                new Color(99, 102, 241), "/assets/projects.png"));
        panel.add(createStatCard("En cours", String.valueOf(projetsCours),
                new Color(245, 158, 11), "/assets/history.png"));
        panel.add(createStatCard("Terminés", String.valueOf(projetsTermines),
                new Color(16, 185, 129), "/assets/done.png"));

        return panel;
    }

    private JPanel createStatCard(String label, String value, Color accentColor, String img) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 5, getHeight(), 5, 5);

                g2.dispose();
            }
        };

        card.setLayout(new BorderLayout(15, 10));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(img));
            Image image = icon.getImage();
            Image scaledImage = image.getScaledInstance(38, 38, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaledImage);
            JLabel iconLabel = new JLabel(icon);
            card.add(iconLabel, BorderLayout.WEST);
        } catch (Exception e) {
            System.err.println("Icon not found: " + img);
        }

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(textPrimary);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelText.setForeground(textSecondary);
        labelText.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(valueLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(labelText);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createProjetsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);

        JLabel sectionTitle = new JLabel("Vos projets");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(textPrimary);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionTitle);
        section.add(Box.createRigidArea(new Dimension(0, 15)));

        if (projets.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BorderLayout());
            emptyPanel.setBackground(cardBgColor);
            emptyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            emptyPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

            JLabel emptyLabel = new JLabel("Aucun projet pour le moment");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(textSecondary);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);

            section.add(emptyPanel);
        } else {
            for (Projet projet : projets) {
                section.add(createProjetCard(projet));
                section.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        return section;
    }

    private JPanel createProjetCard(Projet projet) {
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

        card.setLayout(new BorderLayout(20, 20));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(400, 140));

        JLabel nomLabel = new JLabel(projet.nom);
        nomLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nomLabel.setForeground(textPrimary);
        nomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(nomLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel codeLabel = new JLabel("Code: " + projet.code);
        codeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        codeLabel.setForeground(textSecondary);
        codeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(codeLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel descLabel = new JLabel(projet.description.length() > 80 ? 
            projet.description.substring(0, 80) + "..." : projet.description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(textSecondary);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(descLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        infoPanel.setOpaque(false);

        JLabel budgetLabel = new JLabel("Budget: " + String.format("%.2f €", projet.budget));
        budgetLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        budgetLabel.setForeground(accentColor);
        infoPanel.add(budgetLabel);

        JLabel usedLabel = new JLabel("Consommé: " + String.format("%.2f €", projet.budgetConsomme));
        usedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        usedLabel.setForeground(new Color(255, 107, 107));
        infoPanel.add(usedLabel);

        leftPanel.add(infoPanel);
        card.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(150, 140));

        JLabel progressLabel = new JLabel("Progression");
        progressLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progressLabel.setForeground(textPrimary);
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(progressLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel progressBar = createProgressBar((int) projet.progress, false, getRandomColor());
        rightPanel.add(progressBar);

        card.add(rightPanel, BorderLayout.EAST);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Params.projetID = projet.id;
                showProjetDetail(projet);
            }
        });

        return card;
    }

    private void showProjetDetail(Projet projet) {
        mainContentPanel.removeAll();
        mainContentPanel.add(createOverviewPanel(), "overview");
        mainContentPanel.add(createProjetDetailPanel(projet), "detail");
        contentLayout.show(mainContentPanel, "detail");
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private JPanel createProjetDetailPanel(Projet projet) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        if (projet == null) return panel;

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(bgColor);

        JButton backButton = new JButton("← Retour");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setForeground(accentColor);
        backButton.setBackground(cardBgColor);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.addActionListener(e -> contentLayout.show(mainContentPanel, "overview"));
        contentWrapper.add(backButton);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel headerCard = new JPanel() {
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
        headerCard.setLayout(new BoxLayout(headerCard, BoxLayout.Y_AXIS));
        headerCard.setOpaque(false);
        headerCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        headerCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        JLabel nameLabel = new JLabel(projet.nom);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        nameLabel.setForeground(textPrimary);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerCard.add(nameLabel);
        headerCard.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel codeLabel = new JLabel("Code: " + projet.code);
        codeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        codeLabel.setForeground(accentColor);
        codeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerCard.add(codeLabel);
        headerCard.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel descLabel = new JLabel(projet.description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(textSecondary);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerCard.add(descLabel);

        contentWrapper.add(headerCard);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel detailsTitle = new JLabel("Détails du projet");
        detailsTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        detailsTitle.setForeground(textPrimary);
        detailsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(detailsTitle);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel detailsCard = new JPanel() {
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
        detailsCard.setLayout(new GridLayout(3, 2, 30, 30));
        detailsCard.setOpaque(false);
        detailsCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        detailsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        detailsCard.add(createDetailItem("Budget total", String.format("%.2f €", projet.budget)));
        detailsCard.add(createDetailItem("Budget consommé", String.format("%.2f €", projet.budgetConsomme)));
        detailsCard.add(createDetailItem("Progression", (int)projet.progress + "%"));
        detailsCard.add(createDetailItem("Reste", String.format("%.2f €", projet.budget - projet.budgetConsomme)));

        contentWrapper.add(detailsCard);

        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDetailItem(String label, String value) {
        JPanel item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setOpaque(false);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelComponent.setForeground(textSecondary);
        labelComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.add(labelComponent);
        item.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueComponent.setForeground(textPrimary);
        valueComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.add(valueComponent);

        return item;
    }

    private JPanel createProgressBar(int progress, boolean large, Color color) {
        JPanel wrapper = new JPanel(new BorderLayout(10, 0));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, large ? 50 : 30));

        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int height = large ? 14 : 10;
                int y = (getHeight() - height) / 2;

                g2.setColor(progressBg);
                g2.fillRoundRect(0, y, getWidth(), height, height, height);

                int fillWidth = (int) (getWidth() * progress / 100.0);
                g2.setColor(color);
                g2.fillRoundRect(0, y, fillWidth, height, height, height);

                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, large ? 14 : 10));
        wrapper.add(bar, BorderLayout.CENTER);

        JLabel percentLabel = new JLabel(progress + "%");
        percentLabel.setFont(new Font("Segoe UI", Font.BOLD, large ? 16 : 13));
        percentLabel.setForeground(textPrimary);
        wrapper.add(percentLabel, BorderLayout.EAST);

        return wrapper;
    }

    private Color getRandomColor() {
        Color[] colors = {
                new Color(150, 100, 250),
                new Color(255, 165, 0),
                new Color(0, 200, 0),
                new Color(0, 120, 255),
                new Color(255, 105, 180),
        };

        if (colorCounter == colors.length)
            colorCounter = 0;
        return colors[colorCounter++];
    }

    public static class Projet {
        public int id;
        public String nom;
        public String code;
        public String description;
        public double budget;
        public double budgetConsomme;
        public double progress;

        public Projet(int id, String nom, String code, String description, double budget, double budgetConsomme) {
            this.id = id;
            this.nom = nom;
            this.code = code;
            this.description = description;
            this.budget = budget;
            this.budgetConsomme = budgetConsomme;
            this.progress = budget > 0 ? (budgetConsomme / budget) * 100 : 0;
        }
    }
}