package com.example.demo.interfaces;

import com.example.demo.Params;
import com.example.demo.components.Scrollbar;
import com.example.demo.Queries;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

public class Tache extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, progressBg, progressFill;
    private Color successColor, warningColor, dangerColor;
    private TaskData taskData;
    private List<SubTask> subTasks;
    private List<Document> documents;
    private List<Comment> comments;
    private int colorCounter = 0;
    private int tacheId;

    public Tache() {
        this.tacheId = Params.tacheID;
        this.theme = Params.theme;
        initializeColors();
        taskData = new TaskData("", "", LocalDate.now(), LocalDate.now());
        subTasks = new ArrayList<>();
        documents = new ArrayList<>();
        comments = new ArrayList<>();
        loadTacheData();
        setLayout(new BorderLayout());
        setBackground(bgColor);
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
    }

    private void initializeColors() {
        if (theme == 0) {
            bgColor = new Color(245, 247, 250);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(30, 30, 30);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(59, 130, 246);
            progressBg = new Color(229, 231, 235);
            progressFill = new Color(34, 197, 94);
            successColor = new Color(16, 185, 129);
            warningColor = new Color(245, 158, 11);
            dangerColor = new Color(239, 68, 68);
        } else {
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(20, 20, 20);
            textPrimary = new Color(230, 230, 230);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(0, 120, 215);
            progressBg = new Color(30, 30, 30);
            progressFill = new Color(0, 200, 100);
            successColor = new Color(0, 200, 100);
            warningColor = new Color(255, 165, 0);
            dangerColor = new Color(220, 38, 38);
        }
    }

    private void loadTacheData() {
        Queries.get("/api/tache/" + tacheId)
                .thenAccept(response -> {
                    SwingUtilities.invokeLater(() -> {
                        if (response.containsKey("error")) {
                            JOptionPane.showMessageDialog(this, "Erreur: " + response.get("error"), "Erreur",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            Map<String, Object> tacheData = (Map<String, Object>) response.get("tache");
                            Map<String, Object> taskMap = (Map<String, Object>) tacheData.get("tache");
                            taskData = new TaskData(
                                    (String) taskMap.get("titre"),
                                    (String) taskMap.get("description"),
                                    LocalDate.parse((String) taskMap.get("dateLimite")),
                                    LocalDate.parse((String) taskMap.get("dateCreation")));

                            List<Map<String, Object>> subTasksData = (List<Map<String, Object>>) tacheData
                                    .get("sousTaches");
                            subTasks = new ArrayList<>();
                            for (Map<String, Object> subTaskData : subTasksData) {
                                subTasks.add(new SubTask(
                                        ((Number) subTaskData.get("id")).intValue(),
                                        (String) subTaskData.get("titre"),
                                        (Boolean) subTaskData.get("termine")));
                            }

                            List<Map<String, Object>> documentsData = (List<Map<String, Object>>) tacheData
                                    .get("documents");
                            documents = new ArrayList<>();
                            for (Map<String, Object> docData : documentsData) {
                                documents.add(new Document(
                                        (String) docData.get("nom"),
                                        ((Number) docData.get("size")).intValue(),
                                        (String) docData.get("type")));
                            }

                            List<Map<String, Object>> commentsData = (List<Map<String, Object>>) tacheData
                                    .get("commentaires");
                            comments = new ArrayList<>();
                            for (Map<String, Object> commentData : commentsData) {
                                comments.add(new Comment(
                                        (String) commentData.get("author"),
                                        (String) commentData.get("contenu"),
                                        LocalDate.parse((String) commentData.get("dateCreation")),
                                        getRandomColor()));
                            }

                            System.out.println("Sous-tâches reçues : " + subTasksData);
                            refreshUI();
                        }
                    });
                });
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(bgColor);
        contentWrapper.add(createHeaderSection());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        contentWrapper.add(createOverviewSection());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        contentWrapper.add(createSubTasksSection());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        contentWrapper.add(createDocumentsSection());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        contentWrapper.add(createCommentsSection());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        contentWrapper.add(createActionsSection());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 20)));
        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHeaderSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel titleLabel = new JLabel(taskData.name);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel descLabel = new JLabel("<html><body style='width: 100%;'>" + taskData.description + "</body></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descLabel.setForeground(textSecondary);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        section.add(titleLabel);
        section.add(Box.createRigidArea(new Dimension(0, 15)));
        section.add(descLabel);
        return section;
    }

    private JPanel createOverviewSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        // En-tête avec icône
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        int progress = calculateProgress();
        JLabel progressTitle = new JLabel("Progrès global");
        progressTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        progressTitle.setForeground(theme == 0 ? Color.BLACK : Color.WHITE);
        progressTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        headerPanel.add(progressTitle);
        headerPanel.add(Box.createHorizontalGlue());
        card.add(headerPanel);
        card.add(Box.createRigidArea(new Dimension(0, 18)));
        // Barre de progression moderne
        JPanel progressBar = createProgressBar(progress, true);
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(progressBar);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        // Panneau des dates avec effet glassmorphism
        JPanel datesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        datesPanel.setOpaque(false);
        datesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        datesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        datesPanel.add(createDateCard("Date de création", taskData.dateCreation, "📅",
                new Color(139, 92, 246), new Color(167, 139, 250)));
        datesPanel.add(createDateCard("Date limite", taskData.dateEcheance, "⏰",
                new Color(236, 72, 153), new Color(244, 114, 182)));
        card.add(datesPanel);
        section.add(card);
        return section;
    }

    private JPanel createDateCard(String label, LocalDate date, String emoji, Color primaryColor,
            Color secondaryColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, primaryColor,
                        getWidth(), getHeight(), secondaryColor);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 18, 18);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(12, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillOval(0, 0, 48, 48);
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(48, 48));
        iconPanel.setLayout(new GridBagLayout());
        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconPanel.add(iconLabel);
        card.add(iconPanel, BorderLayout.WEST);
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelText.setForeground(new Color(255, 255, 255, 200));
        labelText.setAlignmentX(Component.LEFT_ALIGNMENT);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);
        JLabel dateLabel = new JLabel(date.format(formatter));
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(labelText);
        textPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        textPanel.add(dateLabel);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createSubTasksSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setOpaque(false);
        JLabel sectionTitle = new JLabel("Sous-tâches");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(theme == 0 ? Color.BLACK : Color.WHITE);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        JLabel countLabel = new JLabel(getCompletedSubTasks() + "/" + subTasks.size());
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        countLabel.setForeground(new Color(139, 92, 246));
        countLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 92, 246, 100), 2, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        countLabel.setOpaque(false);
        titlePanel.add(sectionTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        titlePanel.add(countLabel);
        JButton addButton = createModernButton("+ Ajouter", new Color(139, 92, 246), new Color(167, 139, 250));
        addButton.addActionListener(e -> addNewSubTask());
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);
        section.add(headerPanel);
        section.add(Box.createRigidArea(new Dimension(0, 18)));
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
        if (subTasks.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucune sous-tâche pour le moment");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 15));
            emptyLabel.setForeground(new Color(148, 163, 184));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(Box.createVerticalGlue());
            card.add(emptyLabel);
            card.add(Box.createVerticalGlue());
        } else {
            for (int i = 0; i < subTasks.size(); i++) {
                card.add(createSubTaskRow(subTasks.get(i)));
                if (i < subTasks.size() - 1) {
                    card.add(Box.createRigidArea(new Dimension(0, 8)));
                    card.add(createDivider());
                    card.add(Box.createRigidArea(new Dimension(0, 8)));
                }
            }
            System.out.println("Nombre de sous-tâches ajoutées : " + subTasks.size());
        }
        section.add(card);
        return section;
    }

    private JPanel createSubTaskRow(SubTask subTask) {
        JPanel row = new JPanel();
        row.setLayout(new BorderLayout(15, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));
        row.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                row.repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                row.repaint();
            }
        });
        JCheckBox checkbox = new JCheckBox() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = 22;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                if (isSelected()) {
                    GradientPaint gradient = new GradientPaint(
                            x, y, new Color(139, 92, 246),
                            x + size, y + size, new Color(167, 139, 250));
                    g2.setPaint(gradient);
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(x, y, size, size, 8, 8);
                g2.setColor(isSelected() ? new Color(139, 92, 246) : new Color(203, 213, 225));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(x, y, size, size, 8, 8);
                if (isSelected()) {
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int[] xPoints = { x + 5, x + 9, x + 17 };
                    int[] yPoints = { y + 11, y + 15, y + 7 };
                    for (int i = 0; i < xPoints.length - 1; i++) {
                        g2.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
                    }
                }
                g2.dispose();
            }
        };
        checkbox.setSelected(subTask.completed);
        checkbox.setOpaque(false);
        checkbox.setFocusPainted(false);
        checkbox.setPreferredSize(new Dimension(26, 26));
        checkbox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkbox.addActionListener(e -> {
            subTask.completed = checkbox.isSelected();
            updateSubTaskStatus(subTask);
            refreshUI();
        });
        JLabel nameLabel = new JLabel(subTask.name);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameLabel
                .setForeground(subTask.completed ? new Color(148, 163, 184) : (theme == 0 ? Color.BLACK : Color.WHITE));
        if (subTask.completed) {
            nameLabel.setText("<html><strike>" + subTask.name + "</strike></html>");
        }
        row.add(checkbox, BorderLayout.WEST);
        row.add(nameLabel, BorderLayout.CENTER);
        return row;
    }

    private void updateSubTaskStatus(SubTask subTask) {
        Map<String, Object> body = new HashMap<>();
        body.put("termine", subTask.completed);
        Queries.put("/api/tache/sous-tache/" + subTask.id, body)
                .thenAccept(response -> {
                    SwingUtilities.invokeLater(() -> {
                        if (response.containsKey("error")) {
                            JOptionPane.showMessageDialog(this, "Erreur: " + response.get("error"), "Erreur",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });
                });
    }

    private JButton createModernButton(String text, Color color1, Color color2) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, getModel().isPressed() ? color2 : color1,
                        0, getHeight(), getModel().isPressed() ? color1 : color2);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 38));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createDivider() {
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(226, 232, 240));
                g2.fillRoundRect(0, 0, getWidth(), 1, 1, 1);
                g2.dispose();
            }
        };
        divider.setOpaque(false);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        return divider;
    }

    private JPanel createDocumentsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sectionTitle = new JLabel("Documents attachés (" + documents.size() + ")");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        sectionTitle.setForeground(textPrimary);
        JButton addButton = createStyledButton("+ Ajouter", accentColor);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setPreferredSize(new Dimension(130, 42));
        addButton.addActionListener(e -> addNewDocument());
        headerPanel.add(sectionTitle, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);
        section.add(headerPanel);
        section.add(Box.createRigidArea(new Dimension(0, 20)));
        JPanel card = new JPanel(new GridLayout(0, 2, 20, 20));
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        for (Document doc : documents) {
            card.add(createDocumentCard(doc, getRandomColor()));
        }
        section.add(card);
        return section;
    }

    private JPanel createDocumentCard(Document doc, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, color,
                        getWidth(), getHeight(), new Color(
                                Math.min(255, color.getRed() + 30),
                                Math.min(255, color.getGreen() + 30),
                                Math.min(255, color.getBlue() + 30)));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 20, 20);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(15, 10));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(250, 110));
        String icon = getFileIcon(doc.type);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setForeground(new Color(255, 255, 255, 230));
        card.add(iconLabel, BorderLayout.WEST);
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        JLabel nameLabel = new JLabel(doc.name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sizeLabel = new JLabel(doc.size + " KB");
        sizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sizeLabel.setForeground(new Color(255, 255, 255, 200));
        sizeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(sizeLabel);
        card.add(infoPanel, BorderLayout.CENTER);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color originalColor = color;

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                downloadDocument(doc);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 255, 255, 150), 3, true),
                        BorderFactory.createEmptyBorder(17, 17, 17, 17)));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            }
        });
        return card;
    }

    private JPanel createCommentsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sectionTitle = new JLabel("Commentaires");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        sectionTitle.setForeground(textPrimary);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionTitle);
        section.add(Box.createRigidArea(new Dimension(0, 20)));
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, cardBgColor,
                        0, getHeight(), new Color(
                                cardBgColor.getRed(),
                                cardBgColor.getGreen(),
                                Math.max(0, cardBgColor.getBlue() - 5)));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
        for (Comment comment : comments) {
            card.add(createCommentCard(comment));
            card.add(Box.createRigidArea(new Dimension(0, 18)));
        }
        card.add(createCommentInput());
        section.add(card);
        return section;
    }

    private JPanel createCommentCard(Comment comment) {
        JPanel commentPanel = new JPanel();
        commentPanel.setLayout(new BoxLayout(commentPanel, BoxLayout.Y_AXIS));
        commentPanel.setOpaque(false);
        commentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        commentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        datePanel.setOpaque(false);
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH);
        JLabel dateLabel = new JLabel("📅 " + comment.date.format(formatter));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(textSecondary);
        datePanel.add(dateLabel);
        commentPanel.add(datePanel);
        commentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        JPanel contentBubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 15));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 4, getHeight(), 8, 8);
                g2.dispose();
            }
        };
        contentBubble.setLayout(new BorderLayout());
        contentBubble.setOpaque(false);
        contentBubble.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel contentLabel = new JLabel("<html><body style='width: 100%;'>" + comment.content + "</body></html>");
        contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentLabel.setForeground(textPrimary);
        contentBubble.add(contentLabel, BorderLayout.CENTER);
        commentPanel.add(contentBubble);
        commentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JPanel separator = new JPanel();
        separator.setBackground(new Color(progressBg.getRed(), progressBg.getGreen(), progressBg.getBlue(), 50));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        commentPanel.add(separator);
        return commentPanel;
    }

    private JPanel createCommentInput() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(false);
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        JLabel inputLabel = new JLabel("💬 Nouveau commentaire");
        inputLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        inputLabel.setForeground(textPrimary);
        inputLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(inputLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        JPanel inputRow = new JPanel(new BorderLayout(12, 0));
        inputRow.setOpaque(false);
        inputRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JTextArea commentField = new JTextArea(2, 20);
        commentField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        commentField.setForeground(textPrimary);
        commentField.setBackground(theme == 0 ? new Color(249, 250, 251) : new Color(30, 30, 30));
        commentField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(progressBg.getRed(), progressBg.getGreen(), progressBg.getBlue(), 100), 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        commentField.setLineWrap(true);
        commentField.setWrapStyleWord(true);
        JButton sendButton = createStyledButton("Envoyer", accentColor);
        sendButton.setPreferredSize(new Dimension(110, 50));
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.addActionListener(e -> {
            String text = commentField.getText().trim();
            if (!text.isEmpty()) {
                addComment(text);
                commentField.setText("");
            }
        });
        inputRow.add(commentField, BorderLayout.CENTER);
        inputRow.add(sendButton, BorderLayout.EAST);
        inputPanel.add(inputRow);
        return inputPanel;
    }

    private void addComment(String content) {
        Map<String, String> body = new HashMap<>();
        body.put("contenu", content);
        Queries.post("/api/tache/" + tacheId + "/commentaire", body)
                .thenAccept(response -> {
                    SwingUtilities.invokeLater(() -> {
                        if (response.containsKey("error")) {
                            JOptionPane.showMessageDialog(this, "Erreur: " + response.get("error"), "Erreur",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            Map<String, Object> commentData = (Map<String, Object>) response;
                            comments.add(new Comment(
                                    "Utilisateur actuel",
                                    (String) commentData.get("contenu"),
                                    LocalDate.now(),
                                    getRandomColor()));
                            JOptionPane.showMessageDialog(this, "Commentaire ajouté!", "Succès",
                                    JOptionPane.INFORMATION_MESSAGE);
                            refreshUI();
                        }
                    });
                });
    }

    private JPanel createActionsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sectionTitle = new JLabel("Actions rapides");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        sectionTitle.setForeground(textPrimary);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionTitle);
        section.add(Box.createRigidArea(new Dimension(0, 20)));
        JPanel cardsContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        cardsContainer.setOpaque(false);
        cardsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        cardsContainer.add(createActionCard("🚨 Signaler un bloquage", "Informer l'équipe d'un obstacle", dangerColor));
        cardsContainer.add(createActionCard("💰 Préciser une dépense", "Ajouter une dépense au projet", warningColor));
        section.add(cardsContainer);
        return section;
    }

    private JPanel createActionCard(String title, String description, Color color) {
        JPanel card = new JPanel() {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int alpha = isHovered ? 35 : 20;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha),
                        getWidth(), getHeight(), new Color(
                                Math.min(255, color.getRed() + 20),
                                Math.min(255, color.getGreen() + 20),
                                Math.min(255, color.getBlue() + 20),
                                alpha));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                if (isHovered) {
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                }
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(textSecondary);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(descLabel);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (title.contains("bloquage")) {
                    signalBlockage();
                } else {
                    addExpense();
                }
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                ((JPanel) e.getSource()).putClientProperty("isHovered", true);
                e.getComponent().repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                ((JPanel) e.getSource()).putClientProperty("isHovered", false);
                e.getComponent().repaint();
            }
        });
        return card;
    }

    private void addNewSubTask() {
        String name = JOptionPane.showInputDialog(this, "Nom de la sous-tâche:", "Nouvelle sous-tâche",
                JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            Map<String, String> body = new HashMap<>();
            body.put("titre", name.trim());
            Queries.post("/api/tache/" + tacheId + "/sous-tache", body)
                    .thenAccept(response -> {
                        SwingUtilities.invokeLater(() -> {
                            if (response.containsKey("error")) {
                                JOptionPane.showMessageDialog(this, "Erreur: " + response.get("error"), "Erreur",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {
                                Map<String, Object> subTaskData = (Map<String, Object>) response;
                                subTasks.add(new SubTask(
                                        ((Number) subTaskData.get("id")).intValue(),
                                        (String) subTaskData.get("titre"),
                                        (Boolean) subTaskData.get("termine")));
                                JOptionPane.showMessageDialog(this, "Sous-tâche ajoutée avec succès!", "Succès",
                                        JOptionPane.INFORMATION_MESSAGE);
                                refreshUI();
                            }
                        });
                    });
        }
    }

    private void addNewDocument() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            long sizeKB = file.length() / 1024;
            String type = getFileType(file.getName());
            Map<String, String> body = new HashMap<>();
            body.put("nom", file.getName());
            body.put("description", "Document ajouté via l'interface");
            body.put("type", type);
            Queries.post("/api/tache/" + tacheId + "/document", body)
                    .thenAccept(response -> {
                        SwingUtilities.invokeLater(() -> {
                            if (response.containsKey("error")) {
                                JOptionPane.showMessageDialog(this, "Erreur: " + response.get("error"), "Erreur",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {
                                Map<String, Object> docData = (Map<String, Object>) response;
                                documents.add(new Document(
                                        (String) docData.get("nom"),
                                        ((Number) docData.get("size")).intValue(),
                                        (String) docData.get("type")));
                                JOptionPane.showMessageDialog(this, "Document ajouté avec succès!", "Succès",
                                        JOptionPane.INFORMATION_MESSAGE);
                                refreshUI();
                            }
                        });
                    });
        }
    }

    private void signalBlockage() {
        String message = JOptionPane.showInputDialog(this, "Décrivez le bloquage:", "Signaler un bloquage",
                JOptionPane.WARNING_MESSAGE);
        if (message != null && !message.trim().isEmpty()) {
            Map<String, String> body = new HashMap<>();
            body.put("description", message.trim());
            Queries.post("/api/tache/" + tacheId + "/blocage", body)
                    .thenAccept(response -> {
                        SwingUtilities.invokeLater(() -> {
                            if (response.containsKey("error")) {
                                JOptionPane.showMessageDialog(this, "Erreur: " + response.get("error"), "Erreur",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(this, "Bloquage signalé à l'équipe!", "Succès",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        });
                    });
        }
    }

    private void addExpense() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField amountField = new JTextField();
        JTextField descField = new JTextField();
        panel.add(new JLabel("Montant (€):"));
        panel.add(amountField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Ajouter une dépense", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int montant = Integer.parseInt(amountField.getText().trim());
                Map<String, Integer> body = new HashMap<>();
                body.put("montant", montant);
                Queries.post("/api/tache/" + tacheId + "/depense", body)
                        .thenAccept(response -> {
                            SwingUtilities.invokeLater(() -> {
                                if (response.containsKey("error")) {
                                    JOptionPane.showMessageDialog(this, "Erreur: " + response.get("error"), "Erreur",
                                            JOptionPane.ERROR_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(this, "Dépense ajoutée avec succès!", "Succès",
                                            JOptionPane.INFORMATION_MESSAGE);
                                }
                            });
                        });
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un montant valide.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshUI() {
        SwingUtilities.invokeLater(() -> {
            removeAll(); // Supprime tout le contenu actuel
            setLayout(new BorderLayout());
            JPanel mainPanel = createMainPanel(); // Recrée le panel principal
            add(mainPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        });
    }

    private int calculateProgress() {
        if (subTasks.isEmpty())
            return 0;
        int completed = getCompletedSubTasks();
        return (completed * 100) / subTasks.size();
    }

    private int getCompletedSubTasks() {
        return (int) subTasks.stream().filter(st -> st.completed).count();
    }

    private String getFileIcon(String type) {
        if (type.contains("pdf"))
            return "📄";
        if (type.contains("image"))
            return "🖼️";
        if (type.contains("docx") || type.contains("doc"))
            return "📝";
        if (type.contains("xlsx") || type.contains("xls"))
            return "📊";
        return "📎";
    }

    private String getFileType(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (ext) {
            case "pdf":
                return "application/pdf";
            case "png":
            case "jpg":
            case "jpeg":
                return "image/" + ext;
            case "doc":
            case "docx":
                return "application/docx";
            default:
                return "application/octet-stream";
        }
    }

    private void downloadDocument(Document doc) {
        JOptionPane.showMessageDialog(this, "Téléchargement de: " + doc.name, "Téléchargement",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private Color getRandomColor() {
        Color[] colors = {
                new Color(150, 100, 250),
                new Color(255, 165, 0),
                new Color(0, 120, 255),
                new Color(255, 105, 180),
        };
        if (colorCounter == colors.length)
            colorCounter = 0;
        return colors[colorCounter++];
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private JPanel createProgressBar(int progress, boolean large) {
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
                g2.setColor(progressFill);
                g2.fillRoundRect(0, y, fillWidth, height, height, height);

                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, large ? 14 : 10));
        wrapper.add(bar, BorderLayout.CENTER);

        JLabel percentLabel = new JLabel(progress + "%");
        percentLabel.setFont(new Font("Segoe UI", Font.BOLD, large ? 18 : 14));
        percentLabel.setForeground(textPrimary);
        wrapper.add(percentLabel, BorderLayout.EAST);

        return wrapper;
    }

    private static class TaskData {
        String name;
        String description;
        LocalDate dateEcheance;
        LocalDate dateCreation;

        public TaskData(String name, String description, LocalDate dateEcheance, LocalDate dateCreation) {
            this.name = name;
            this.description = description;
            this.dateEcheance = dateEcheance;
            this.dateCreation = dateCreation;
        }
    }

    private static class SubTask {
        int id;
        String name;
        boolean completed;

        public SubTask(int id, String name, boolean completed) {
            this.id = id;
            this.name = name;
            this.completed = completed;
        }
    }

    private static class Document {
        String name;
        int size;
        String type;

        public Document(String name, int size, String type) {
            this.name = name;
            this.size = size;
            this.type = type;
        }
    }

    private static class Comment {
        String author;
        String content;
        LocalDate date;
        Color color;

        public Comment(String author, String content, LocalDate date, Color color) {
            this.author = author;
            this.content = content;
            this.date = date;
            this.color = color;
        }
    }
}