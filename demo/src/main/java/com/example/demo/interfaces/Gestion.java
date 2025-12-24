package com.example.demo.interfaces;

import com.example.demo.Params;
import com.example.demo.Queries;
import com.example.demo.components.Scrollbar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.http.HttpRequest;



public class Gestion extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, successColor, warningColor,
            dangerColor;
    private List<HistoryEvent> historyEvents;
    private List<Expense> expenses;
    private List<Document> documents;
    private double budget = 4000.0;
    private JPanel mainContentPanel;
    private CardLayout contentLayout;

    public Gestion() {
        this.theme = Params.theme;
        initializeColors();

        historyEvents = new ArrayList<>();
        expenses = new ArrayList<>();
        documents = new ArrayList<>();
        loadGestionData();

        setLayout(new BorderLayout());
        setBackground(bgColor);
        contentLayout = new CardLayout();
        mainContentPanel = new JPanel(contentLayout);
        mainContentPanel.setBackground(bgColor);
        JPanel mainPanel = createMainPanel();
        mainContentPanel.add(mainPanel, "main");
        add(mainContentPanel, BorderLayout.CENTER);
        contentLayout.show(mainContentPanel, "main");
    }

    private void initializeColors() {
        if (theme == 0) {
            bgColor = new Color(245, 247, 250);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(30, 30, 30);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(59, 130, 246);
            successColor = new Color(34, 197, 94);
            warningColor = new Color(245, 158, 11);
            dangerColor = new Color(239, 68, 68);
        } else {
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(20, 20, 20);
            textPrimary = new Color(230, 230, 230);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(0, 120, 215);
            successColor = new Color(0, 200, 100);
            warningColor = new Color(255, 180, 0);
            dangerColor = new Color(255, 80, 80);
        }
    }

    private void loadGestionData() {
        Queries.get("/api/gestion/" + Params.projetID).thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                try {
                    if (response.containsKey("success") && (Boolean) response.get("success")) {
                        // Charger l'historique
                        List<Map<String, Object>> historiqueData = (List<Map<String, Object>>) response
                                .get("historique");
                        if (historiqueData != null) {
                            historyEvents.clear();
                            for (Map<String, Object> notif : historiqueData) {
                                // Gérer la date correctement
                                Long dateMillis = null;
                                Object dateObj = notif.get("dateEnvoie");
                                if (dateObj instanceof Long) {
                                    dateMillis = (Long) dateObj;
                                } else if (dateObj instanceof Integer) {
                                    dateMillis = ((Integer) dateObj).longValue();
                                } else if (dateObj instanceof String) {
                                    dateMillis = Long.parseLong((String) dateObj);
                                }

                                Date date = dateMillis != null ? new Date(dateMillis) : new Date();
                                String contenu = (String) notif.get("contenu");
                                String membreNom = notif.containsKey("membreNom") ? (String) notif.get("membreNom")
                                        : "Système";

                                // Séparer prénom et nom (simple split par espace)
                                String[] names = membreNom.split(" ", 2);
                                String firstName = names.length > 0 ? names[0] : "";
                                String lastName = names.length > 1 ? names[1] : "";

                                historyEvents.add(new HistoryEvent(date, contenu, firstName, lastName));
                            }
                        }

                        // Charger la facturation
                        List<Map<String, Object>> facturationData = (List<Map<String, Object>>) response
                                .get("facturation");
                        if (facturationData != null) {
                            expenses.clear();
                            for (Map<String, Object> expense : facturationData) {
                                String titre = (String) expense.get("titre");
                                Number depenseNum = (Number) expense.get("depense");
                                double depense = depenseNum != null ? depenseNum.doubleValue() : 0.0;
                                expenses.add(new Expense(titre, depense));
                            }
                        }

                        // Charger les documents
                        List<Map<String, Object>> documentsData = (List<Map<String, Object>>) response.get("documents");
                        if (documentsData != null) {
                            documents.clear();
                            for (Map<String, Object> doc : documentsData) {
                                String nom = (String) doc.get("nom");
                                String contenuBase64 = (String) doc.get("contenuBase64");
                                Number sizeNum = (Number) doc.get("size");
                                long size = sizeNum != null ? sizeNum.longValue() : 0;

                                // Gérer la date
                                Long dateMillis = null;
                                Object dateObj = doc.get("dateCreation");
                                if (dateObj instanceof Long) {
                                    dateMillis = (Long) dateObj;
                                } else if (dateObj instanceof Integer) {
                                    dateMillis = ((Integer) dateObj).longValue();
                                } else if (dateObj instanceof String) {
                                    dateMillis = Long.parseLong((String) dateObj);
                                }

                                Date dateCreation = dateMillis != null ? new Date(dateMillis) : new Date();

                                documents.add(new Document(nom, dateCreation, size, contenuBase64));
                            }
                        }

                        // Rafraîchir l'interface
                        refreshUI();

                    } else {
                        String error = response.containsKey("error") ? (String) response.get("error")
                                : "Erreur inconnue";
                        System.err.println("Erreur lors du chargement: " + error);
                        // Garder les données de démo si échec
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du traitement des données: " + e.getMessage());
                    e.printStackTrace();
                    // Garder les données de démo si échec
                }
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                System.err.println("Erreur de connexion: " + ex.getMessage());
                ex.printStackTrace();
                // Garder les données de démo si échec
            });
            return null;
        });
    }

    private void refreshUI() {
        // Recréer le panel principal avec les nouvelles données
        mainContentPanel.removeAll();
        JPanel mainPanel = createMainPanel();
        mainContentPanel.add(mainPanel, "main");
        contentLayout.show(mainContentPanel, "main");
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(bgColor);

        JPanel headerPanel = new JPanel(new BorderLayout());
headerPanel.setBackground(bgColor);
headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

JLabel titleLabel = new JLabel("Gestion du projet");
titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
titleLabel.setForeground(textPrimary);

JButton downloadBtn = createDownloadButton();

headerPanel.add(titleLabel, BorderLayout.WEST);
headerPanel.add(downloadBtn, BorderLayout.EAST);

contentWrapper.add(headerPanel);
contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));

        

        JPanel sectionsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        sectionsPanel.setBackground(bgColor);
        sectionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        sectionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sectionsPanel.add(createHistorySection());
        sectionsPanel.add(createBillingSection());
        sectionsPanel.add(createDocumentsSection());

        contentWrapper.add(sectionsPanel);

        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JButton createDownloadButton() {
    JButton downloadBtn = new JButton("📥 Télécharger le rapport PDF") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2.setColor(accentColor.darker());
            } else if (getModel().isRollover()) {
                g2.setColor(accentColor.brighter());
            } else {
                g2.setColor(accentColor);
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    };
    
    downloadBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    downloadBtn.setForeground(Color.WHITE);
    downloadBtn.setContentAreaFilled(false);
    downloadBtn.setBorderPainted(false);
    downloadBtn.setFocusPainted(false);
    downloadBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    downloadBtn.setPreferredSize(new Dimension(250, 45));
    downloadBtn.setMaximumSize(new Dimension(250, 45));
    
    downloadBtn.addActionListener(e -> downloadPDFReport());
    
    return downloadBtn;
}

private void downloadPDFReport() {

    JDialog progressDialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Génération du rapport",
            true
    );

    progressDialog.setLayout(new BorderLayout());
    progressDialog.setSize(400, 150);
    progressDialog.setLocationRelativeTo(this);
    progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

    JLabel label = new JLabel("Génération du rapport PDF en cours...");
    label.setAlignmentX(Component.CENTER_ALIGNMENT);

    JProgressBar bar = new JProgressBar();
    bar.setIndeterminate(true);
    bar.setAlignmentX(Component.CENTER_ALIGNMENT);

    panel.add(label);
    panel.add(Box.createRigidArea(new Dimension(0, 15)));
    panel.add(bar);

    progressDialog.add(panel);

    SwingWorker<byte[], Void> worker = new SwingWorker<>() {

        @Override
        protected byte[] doInBackground() throws Exception {
            // ✅ APPEL CORRECT (PDF = binaire)
            return Queries.getBinary(
                    "/api/gestion/" + Params.projetID + "/rapport/pdf"
            ).get();
        }

        @Override
        protected void done() {
            progressDialog.dispose();

            try {
                byte[] pdf = get();

                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(
                        new File("Rapport_Projet_" + Params.projetID + ".pdf")
                );

                if (chooser.showSaveDialog(Gestion.this)
                        == JFileChooser.APPROVE_OPTION) {

                    File file = chooser.getSelectedFile();
                    if (!file.getName().toLowerCase().endsWith(".pdf")) {
                        file = new File(file.getAbsolutePath() + ".pdf");
                    }

                    Files.write(file.toPath(), pdf);

                    JOptionPane.showMessageDialog(
                            Gestion.this,
                            "Rapport PDF téléchargé avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        Gestion.this,
                        "Erreur lors du téléchargement :\n" + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    };

    worker.execute();
    progressDialog.setVisible(true);
}




    private JPanel createHistorySection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);

        JLabel sectionTitle = new JLabel("Historique");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
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
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel timelineWrapper = new JPanel();
        timelineWrapper.setLayout(new BoxLayout(timelineWrapper, BoxLayout.Y_AXIS));
        timelineWrapper.setOpaque(false);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.FRENCH);
        for (int i = 0; i < historyEvents.size(); i++) {
            HistoryEvent event = historyEvents.get(i);
            boolean isLast = (i == historyEvents.size() - 1);
            JPanel eventPanel = createTimelineEvent(event, dateFormat, isLast);
            eventPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            timelineWrapper.add(eventPanel);
            if (!isLast) {
                timelineWrapper.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(timelineWrapper);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollPane.setBackground(cardBgColor);
        scrollPane.getViewport().setBackground(cardBgColor);
        scrollPane.setPreferredSize(new Dimension(0, 0));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        card.add(scrollPane);
        section.add(card);

        return section;
    }

    private JPanel createTimelineEvent(HistoryEvent event, SimpleDateFormat dateFormat, boolean isLast) {
        JPanel eventPanel = new JPanel(new BorderLayout(12, 0));
        eventPanel.setOpaque(false);
        eventPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel timelineIndicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int centerX = getWidth() / 2;
                g2.setColor(accentColor);
                g2.fillOval(centerX - 6, 5, 12, 12);
                if (!isLast) {
                    g2.setStroke(new BasicStroke(2));
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 80));
                    g2.drawLine(centerX, 17, centerX, getHeight());
                }
                g2.dispose();
            }
        };
        timelineIndicator.setPreferredSize(new Dimension(30, 100));
        timelineIndicator.setOpaque(false);
        eventPanel.add(timelineIndicator, BorderLayout.WEST);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel dateLabel = new JLabel(dateFormat.format(event.date));
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dateLabel.setForeground(accentColor);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel eventLabel1 = new JLabel((event.description.split(":")[0] + " :").trim());
        eventLabel1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        eventLabel1.setForeground(textPrimary);
        eventLabel1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel eventLabel2 = new JLabel((event.description.split(":")[1]).trim());
        eventLabel2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        eventLabel2.setForeground(textPrimary);
        eventLabel2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel memberLabel = new JLabel(event.firstName + " " + event.lastName);
        memberLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        memberLabel.setForeground(textSecondary);
        memberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(dateLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        contentPanel.add(eventLabel1);
        contentPanel.add(eventLabel2);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        contentPanel.add(memberLabel);

        eventPanel.add(contentPanel, BorderLayout.CENTER);
        return eventPanel;
    }

    private JPanel createBillingSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);

        JLabel sectionTitle = new JLabel("Facturation");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
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
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel expensesWrapper = new JPanel();
        expensesWrapper.setLayout(new BoxLayout(expensesWrapper, BoxLayout.Y_AXIS));
        expensesWrapper.setOpaque(false);

        double total = 0;
        for (Expense expense : expenses) {
            total += expense.amount;
            JPanel expenseRow = createExpenseRow(expense);
            expenseRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            expensesWrapper.add(expenseRow);
            expensesWrapper.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(expensesWrapper);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollPane.setBackground(cardBgColor);
        scrollPane.getViewport().setBackground(cardBgColor);
        scrollPane.setPreferredSize(new Dimension(0, 0));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        card.add(scrollPane);
        card.add(Box.createRigidArea(new Dimension(0, 15)));

        JSeparator separator = new JSeparator();
        separator.setForeground(
                new Color(textSecondary.getRed(), textSecondary.getGreen(), textSecondary.getBlue(), 30));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(separator);
        card.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel summaryPanel = createBillingSummary(total, budget);
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(summaryPanel);

        section.add(card);
        return section;
    }

    private JPanel createExpenseRow(Expense expense) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel taskLabel = new JLabel(expense.taskName);
        taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskLabel.setForeground(textPrimary);
        row.add(taskLabel, BorderLayout.CENTER);

        JLabel amountLabel = new JLabel(String.format("%.2f DHS", expense.amount));
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amountLabel.setForeground(textPrimary);
        row.add(amountLabel, BorderLayout.EAST);

        return row;
    }

    private JPanel createBillingSummary(double total, double budget) {
        JPanel summary = new JPanel();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setOpaque(false);

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel totalLabel = new JLabel("Total:");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(textPrimary);
        totalRow.add(totalLabel, BorderLayout.WEST);

        JLabel totalAmount = new JLabel(String.format("%.2f DHS", total));
        totalAmount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalAmount.setForeground(textPrimary);
        totalRow.add(totalAmount, BorderLayout.EAST);

        summary.add(totalRow);
        summary.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel budgetRow = new JPanel(new BorderLayout());
        budgetRow.setOpaque(false);
        budgetRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel budgetLabel = new JLabel("Budget:");
        budgetLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        budgetLabel.setForeground(textSecondary);
        budgetRow.add(budgetLabel, BorderLayout.WEST);

        JLabel budgetAmount = new JLabel(String.format("%.2f DHS", budget));
        budgetAmount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        budgetAmount.setForeground(textSecondary);
        budgetRow.add(budgetAmount, BorderLayout.EAST);

        summary.add(budgetRow);

        if (total > budget) {
            summary.add(Box.createRigidArea(new Dimension(0, 12)));

            JPanel alertPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(dangerColor.getRed(), dangerColor.getGreen(), dangerColor.getBlue(), 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(dangerColor);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                    g2.dispose();
                }
            };
            alertPanel.setLayout(new BorderLayout(10, 0));
            alertPanel.setOpaque(false);
            alertPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
            alertPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            JLabel alertIcon = new JLabel("⚠");
            alertIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            alertIcon.setForeground(dangerColor);
            alertPanel.add(alertIcon, BorderLayout.WEST);

            JPanel alertText = new JPanel();
            alertText.setLayout(new BoxLayout(alertText, BoxLayout.Y_AXIS));
            alertText.setOpaque(false);

            JLabel alertTitle = new JLabel("Budget dépassé!");
            alertTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            alertTitle.setForeground(dangerColor);
            alertTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel alertAmount = new JLabel(String.format("Dépassement: %.2f DHS", total - budget));
            alertAmount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            alertAmount.setForeground(dangerColor);
            alertAmount.setAlignmentX(Component.LEFT_ALIGNMENT);

            alertText.add(alertTitle);
            alertText.add(Box.createRigidArea(new Dimension(0, 2)));
            alertText.add(alertAmount);

            alertPanel.add(alertText, BorderLayout.CENTER);
            summary.add(alertPanel);
        }

        return summary;
    }

    private JPanel createDocumentsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);

        JLabel sectionTitle = new JLabel("Documents");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
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
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel documentsWrapper = new JPanel();
        documentsWrapper.setLayout(new BoxLayout(documentsWrapper, BoxLayout.Y_AXIS));
        documentsWrapper.setOpaque(false);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        for (Document doc : documents) {
            JPanel docCard = createDocumentCard(doc, dateFormat);
            docCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            documentsWrapper.add(docCard);
            documentsWrapper.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(documentsWrapper);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollPane.setBackground(cardBgColor);
        scrollPane.getViewport().setBackground(cardBgColor);
        scrollPane.setPreferredSize(new Dimension(0, 0));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        card.add(scrollPane);
        section.add(card);

        return section;
    }

    private JPanel createDocumentCard(Document doc, SimpleDateFormat dateFormat) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 20));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 10));
                } else {
                    g2.setColor(theme == 0 ? new Color(249, 250, 251) : new Color(30, 30, 30));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }

            private ButtonModel getModel() {
                Component[] comps = getComponents();
                for (Component c : comps) {
                    if (c instanceof AbstractButton) {
                        return ((AbstractButton) c).getModel();
                    }
                }
                return new DefaultButtonModel();
            }
        };
        card.setLayout(new BorderLayout(12, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        String extension = doc.name.substring(doc.name.lastIndexOf('.') + 1).toLowerCase();
        Color docColor = getDocumentColor(extension);

        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(docColor.getRed(), docColor.getGreen(), docColor.getBlue(), 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        iconPanel.setPreferredSize(new Dimension(50, 50));
        iconPanel.setOpaque(false);
        iconPanel.setLayout(new GridBagLayout());

        JLabel iconLabel = new JLabel(getDocumentIcon(extension));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(docColor);
        iconPanel.add(iconLabel);

        card.add(iconPanel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(doc.name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(textPrimary);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        metaPanel.setOpaque(false);
        metaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel("📅 " + dateFormat.format(doc.dateCreation));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(textSecondary);

        JLabel sizeLabel = new JLabel("📦 " + formatFileSize(doc.size));
        sizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sizeLabel.setForeground(textSecondary);

        metaPanel.add(dateLabel);
        metaPanel.add(sizeLabel);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(metaPanel);

        card.add(infoPanel, BorderLayout.CENTER);

        JLabel downloadIcon = new JLabel("⬇");
        downloadIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        downloadIcon.setForeground(accentColor);
        card.add(downloadIcon, BorderLayout.EAST);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                downloadDocument(doc);
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.repaint();
            }
        });

        return card;
    }

    private String getDocumentIcon(String extension) {
        switch (extension) {
            case "pdf":
                return "📄";
            case "docx":
            case "doc":
                return "📝";
            case "xlsx":
            case "xls":
                return "📊";
            case "fig":
                return "🎨";
            case "zip":
            case "rar":
                return "🗜";
            default:
                return "📎";
        }
    }

    private Color getDocumentColor(String extension) {
        switch (extension) {
            case "pdf":
                return new Color(239, 68, 68);
            case "docx":
            case "doc":
                return new Color(59, 130, 246);
            case "xlsx":
            case "xls":
                return new Color(34, 197, 94);
            case "fig":
                return new Color(139, 92, 246);
            default:
                return new Color(100, 100, 100);
        }
    }

    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }

    private void downloadDocument(Document doc) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(doc.name));
            fileChooser.setDialogTitle("Enregistrer le document");
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                byte[] decodedContent = Base64.getDecoder().decode(doc.content);
                Files.write(Paths.get(fileToSave.getAbsolutePath()), decodedContent);
                JOptionPane.showMessageDialog(this, "Document téléchargé avec succès!", "Téléchargement réussi",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du téléchargement: " + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class HistoryEvent {
        Date date;
        String description;
        String firstName;
        String lastName;

        public HistoryEvent(Date date, String description, String firstName, String lastName) {
            this.date = date;
            this.description = description;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    private static class Expense {
        String taskName;
        double amount;

        public Expense(String taskName, double amount) {
            this.taskName = taskName;
            this.amount = amount;
        }
    }

    private static class Document {
        String name;
        Date dateCreation;
        long size;
        String content;

        public Document(String name, Date dateCreation, long size, String content) {
            this.name = name;
            this.dateCreation = dateCreation;
            this.size = size;
            this.content = content;
        }
    }
}