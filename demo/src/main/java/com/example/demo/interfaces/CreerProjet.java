package com.example.demo.interfaces;

import com.example.demo.Params;
import com.example.demo.Queries;
import com.example.demo.components.Scrollbar;
import com.example.demo.hooks.ProjetDTO;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Timer;

import javax.imageio.ImageIO;

public class CreerProjet extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, progressBg;
    private Color successColor, dangerColor, warningColor;
    private Color primaryGradient1, primaryGradient2, accentGradient1, accentGradient2;
    private Color shadowColor, glowColor;

    private JTextField nomField, budgetField;
    private JTextArea descriptionField;
    private JButton dateButton;
    private LocalDate selectedDate;
    private JPanel formPanel;
    private JPanel successPanel;

    private Timer animationTimer;
    private float animationProgress = 0f;

    public CreerProjet() {
        this.theme = Params.theme;
        initializeColors();
        setLayout(new BorderLayout());
        setBackground(bgColor);

        formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
    }

    private void initializeColors() {
        if (theme == 0) {
            bgColor = new Color(248, 250, 252);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(15, 23, 42);
            textSecondary = new Color(100, 116, 139);
            accentColor = new Color(59, 130, 246);
            progressBg = new Color(226, 232, 240);
            successColor = new Color(34, 197, 94);
            dangerColor = new Color(239, 68, 68);
            warningColor = new Color(251, 146, 60);
            primaryGradient1 = new Color(99, 102, 241);
            primaryGradient2 = new Color(139, 92, 246);
            accentGradient1 = new Color(14, 165, 233);
            accentGradient2 = new Color(59, 130, 246);
            shadowColor = new Color(15, 23, 42, 15);
            glowColor = new Color(99, 102, 241, 40);
        } else {
            bgColor = new Color(15, 23, 42);
            cardBgColor = new Color(30, 41, 59);
            textPrimary = new Color(248, 250, 252);
            textSecondary = new Color(148, 163, 184);
            accentColor = new Color(96, 165, 250);
            progressBg = new Color(51, 65, 85);
            successColor = new Color(52, 211, 153);
            dangerColor = new Color(248, 113, 113);
            warningColor = new Color(251, 146, 60);
            primaryGradient1 = new Color(124, 58, 237);
            primaryGradient2 = new Color(167, 139, 250);
            accentGradient1 = new Color(6, 182, 212);
            accentGradient2 = new Color(59, 130, 246);
            shadowColor = new Color(0, 0, 0, 30);
            glowColor = new Color(139, 92, 246, 50);
        }
    }

    private JPanel createFormPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(bgColor);

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(theme == 0 ? Color.WHITE : Color.BLACK);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        contentWrapper.add(createHeader());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 35)));
        contentWrapper.add(createFormCard());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 30)));

        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("Créer un nouveau projet");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(Box.createRigidArea(new Dimension(0, 15)));
        header.add(titleLabel);
        header.add(Box.createRigidArea(new Dimension(0, 10)));

        return header;
    }

    private JPanel createFormCard() {
        JPanel cardWrapper = new JPanel();
        cardWrapper.setLayout(new BoxLayout(cardWrapper, BoxLayout.Y_AXIS));
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(BorderFactory.createEmptyBorder(40, 45, 40, 45));
        cardWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardWrapper.add(createPremiumField("Nom du projet", "Ex: Application mobile innovante", "🎯",
                nomField = createPremiumTextField()));
        cardWrapper.add(Box.createRigidArea(new Dimension(0, 25)));

        cardWrapper.add(createPremiumDescriptionField());
        cardWrapper.add(Box.createRigidArea(new Dimension(0, 25)));

        cardWrapper.add(createPremiumDateField());
        cardWrapper.add(Box.createRigidArea(new Dimension(0, 25)));

        cardWrapper.add(createPremiumField("Budget estimé", "Ex: 50000", "💎",
                budgetField = createPremiumTextField()));
        cardWrapper.add(Box.createRigidArea(new Dimension(0, 35)));

        cardWrapper.add(createSubmitButton());

        return cardWrapper;
    }

    private JPanel createPremiumField(String label, String placeholder, String emoji, JTextField field) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        labelPanel.setOpaque(false);
        labelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        labelText.setForeground(textPrimary);

        JLabel requiredBadge = new JLabel(" * obligatoire");
        requiredBadge.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        requiredBadge.setForeground(dangerColor);

        labelPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        labelPanel.add(labelText);
        labelPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        labelPanel.add(requiredBadge);
        labelPanel.add(Box.createHorizontalGlue());

        fieldPanel.add(labelPanel);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        fieldPanel.add(field);

        return fieldPanel;
    }

    private JTextField createPremiumTextField() {
        JTextField field = new JTextField() {
            private boolean isFocused = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color borderColor = isFocused ? primaryGradient1 : progressBg;
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setForeground(textPrimary);
        field.setBackground(new Color(0, 0, 0, 0));
        field.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        field.setPreferredSize(new Dimension(0, 54));
        field.setCaretColor(primaryGradient1);
        field.setOpaque(false);

        return field;
    }

    private JPanel createPremiumDescriptionField() {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        labelPanel.setOpaque(false);
        labelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelText = new JLabel("Description détaillée");
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        labelText.setForeground(textPrimary);

        JLabel requiredBadge = new JLabel(" * obligatoire");
        requiredBadge.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        requiredBadge.setForeground(dangerColor);

        labelPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        labelPanel.add(labelText);
        labelPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        labelPanel.add(requiredBadge);
        labelPanel.add(Box.createHorizontalGlue());

        fieldPanel.add(labelPanel);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel textAreaWrapper = new JPanel() {
            private boolean isFocused = false;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color borderColor = isFocused ? primaryGradient1 : progressBg;
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);

                g2.dispose();
            }
        };
        textAreaWrapper.setLayout(new BorderLayout());
        textAreaWrapper.setOpaque(false);
        textAreaWrapper.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        textAreaWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        descriptionField = new JTextArea();
        descriptionField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descriptionField.setForeground(textPrimary);
        descriptionField.setBackground(new Color(0, 0, 0, 0));
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        descriptionField.setBorder(null);
        descriptionField.setCaretColor(primaryGradient1);
        descriptionField.setOpaque(false);
        descriptionField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textAreaWrapper.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                textAreaWrapper.repaint();
            }
        });

        JScrollPane scrollPane = new JScrollPane(descriptionField);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        textAreaWrapper.add(scrollPane, BorderLayout.CENTER);
        fieldPanel.add(textAreaWrapper);

        return fieldPanel;
    }

    private JPanel createPremiumDateField() {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        labelPanel.setOpaque(false);
        labelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelText = new JLabel("Date limite du projet");
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        labelText.setForeground(textPrimary);

        JLabel requiredBadge = new JLabel(" * obligatoire");
        requiredBadge.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        requiredBadge.setForeground(dangerColor);

        labelPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        labelPanel.add(labelText);
        labelPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        labelPanel.add(requiredBadge);
        labelPanel.add(Box.createHorizontalGlue());

        fieldPanel.add(labelPanel);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        dateButton = new JButton("Sélectionner une date") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(progressBg);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);

                g2.setColor(selectedDate != null ? textPrimary : textSecondary);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = 18;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);

                String calendarIcon = "📆";
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
                int iconX = getWidth() - 45;
                int iconY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(calendarIcon, iconX, iconY);

                g2.dispose();
            }
        };
        dateButton.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        dateButton.setOpaque(false);
        dateButton.setContentAreaFilled(false);
        dateButton.setBorderPainted(false);
        dateButton.setFocusPainted(false);
        dateButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        dateButton.setPreferredSize(new Dimension(0, 54));
        dateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dateButton.setHorizontalAlignment(SwingConstants.LEFT);
        dateButton.addActionListener(e -> showDatePicker());

        fieldPanel.add(dateButton);
        return fieldPanel;
    }

    private void showDatePicker() {
        JDateChooser dateChooser = new JDateChooser();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(dateChooser, BorderLayout.CENTER);

        int option = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Sélectionner une date",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION && dateChooser.getDate() != null) {
            java.util.Date utilDate = dateChooser.getDate();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            selectedDate = sqlDate.toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);
            dateButton.setText(selectedDate.format(formatter));
            dateButton.repaint();
        }
    }

    private JPanel createSubmitButton() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JButton createButton = new JButton("Créer le projet") {
            private boolean isPressed = false;
            private float hoverAnimation = 0f;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        isPressed = true;
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        isPressed = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int shadowOffset = isPressed ? 2 : 4;
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(shadowOffset, shadowOffset, getWidth() - shadowOffset,
                        getHeight() - shadowOffset, 16, 16);

                int glowSize = (int) (hoverAnimation * 15);
                if (glowSize > 0) {
                    g2.setColor(new Color(primaryGradient1.getRed(), primaryGradient1.getGreen(),
                            primaryGradient1.getBlue(), (int) (hoverAnimation * 100)));
                    g2.fillRoundRect(-glowSize, -glowSize, getWidth() + glowSize * 2,
                            getHeight() + glowSize * 2, 20, 20);
                }

                GradientPaint gradient = new GradientPaint(
                        0, 0, primaryGradient1,
                        getWidth(), getHeight(), primaryGradient2);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                if (hoverAnimation > 0) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                            hoverAnimation * 0.2f));
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        createButton.setFont(new Font("Segoe UI", Font.BOLD, 17));
        createButton.setForeground(Color.WHITE);
        createButton.setBorderPainted(false);
        createButton.setContentAreaFilled(false);
        createButton.setFocusPainted(false);
        createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createButton.setOpaque(false);
        createButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        createButton.setPreferredSize(new Dimension(0, 58));
        createButton.addActionListener(e -> createProject());

        buttonPanel.add(createButton);
        return buttonPanel;
    }

    private void createProject() {
        String nom = nomField.getText().trim();
        String description = descriptionField.getText().trim();
        String budgetStr = budgetField.getText().trim();

        if (nom.isEmpty() || description.isEmpty() || selectedDate == null || budgetStr.isEmpty()) {
            showModernError("Veuillez remplir tous les champs obligatoires");
            return;
        }

        try {
            double budget = Double.parseDouble(budgetStr);
            if (budget <= 0) {
                showModernError("Le budget doit être supérieur à 0");
                return;
            }
        } catch (NumberFormatException e) {
            showModernError("Le budget doit être un nombre valide");
            return;
        }

        // Générer le code du projet
        String projectCode = generateProjectCode(nom);

        // Créer un objet ProjetDTO avec les données du formulaire
        ProjetDTO projetDTO = new ProjetDTO();
        projetDTO.nom = nom;
        projetDTO.description = description;
        projetDTO.budget = (float) Double.parseDouble(budgetStr);
        projetDTO.budgetConsomme = 0; // Par défaut
        projetDTO.code = projectCode;
        projetDTO.deadline = java.sql.Date.valueOf(selectedDate);
        projetDTO.dateDebut = new java.sql.Date(System.currentTimeMillis()); // Date actuelle
        projetDTO.statut = "En cours"; // Par défaut

        // Convertir l'objet en Map pour l'envoyer via Queries
        Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("nom", projetDTO.nom);
        requestBody.put("description", projetDTO.description);
        requestBody.put("budget", projetDTO.budget);
        requestBody.put("budgetConsomme", projetDTO.budgetConsomme);
        requestBody.put("code", projetDTO.code);
        requestBody.put("deadline", projetDTO.deadline);
        requestBody.put("dateDebut", projetDTO.dateDebut);
        requestBody.put("statut", projetDTO.statut);

        // Envoyer la requête au backend
        Queries.post("/api/projet/creer", requestBody)
                .thenAccept(response -> {
                    SwingUtilities.invokeLater(() -> {
                        if (response.containsKey("error")) {
                            showModernError("Erreur lors de la création du projet : " + response.get("error"));
                        } else {
                            showSuccessPanel(nom, projectCode);
                        }
                    });
                })
                .exceptionally(e -> {
                    SwingUtilities.invokeLater(() -> {
                        showModernError("Erreur réseau : " + e.getMessage());
                    });
                    return null;
                });
    }

    private void showModernError(String message) {
        JPanel errorPanel = new JPanel();
        errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.X_AXIS));
        errorPanel.setOpaque(false);
        errorPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel iconLabel = new JLabel("⚠️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        messageLabel.setForeground(dangerColor);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        errorPanel.add(iconLabel);
        errorPanel.add(messageLabel);

        JOptionPane.showMessageDialog(this, errorPanel, "Erreur", JOptionPane.PLAIN_MESSAGE);
    }

    private String generateProjectCode(String projectName) {
        String nameHash = Integer.toHexString(projectName.hashCode()).toUpperCase();
        long timestamp = System.currentTimeMillis();
        String timeHash = Long.toHexString(timestamp).toUpperCase();

        StringBuilder code = new StringBuilder();
        code.append(nameHash.charAt(0));
        code.append(timeHash.substring(Math.max(0, timeHash.length() - 4)));
        code.append(nameHash.substring(nameHash.length() - 3));
        code.append(String.format("%04X", new Random().nextInt(65536)));

        return code.toString();
    }

    private void showSuccessPanel(String projectName, String projectCode) {
        animationProgress = 0f;

        removeAll();

        successPanel = new JPanel(new BorderLayout(0, 0));
        successPanel.setBackground(bgColor);

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(theme == 0 ? Color.WHITE : Color.BLACK);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        contentWrapper.add(Box.createRigidArea(new Dimension(0, 20)));
        contentWrapper.add(createSuccessHeader(projectName));
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 45)));
        contentWrapper.add(createPremiumCodeCard(projectCode));
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 40)));
        contentWrapper.add(createPremiumQRSection(projectCode));
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 45)));
        contentWrapper.add(createPremiumActionButtons(projectCode));
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 30)));

        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);

        successPanel.add(scrollPane, BorderLayout.CENTER);

        add(successPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createSuccessHeader(String projectName) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel successTitle = new JLabel("Projet créé avec succès !");
        successTitle.setFont(new Font("Segoe UI", Font.BOLD, 38));
        successTitle.setForeground(successColor);
        successTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel projectNameLabel = new JLabel(projectName);
        projectNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        projectNameLabel.setForeground(textSecondary);
        projectNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(Box.createRigidArea(new Dimension(0, 25)));
        header.add(successTitle);
        header.add(Box.createRigidArea(new Dimension(0, 15)));
        header.add(projectNameLabel);

        return header;
    }

    private JPanel createPremiumCodeCard(String projectCode) {
        JPanel cardWrapper = new JPanel();
        cardWrapper.setLayout(new BoxLayout(cardWrapper, BoxLayout.Y_AXIS));
        cardWrapper.setOpaque(false);
        cardWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardWrapper.setMaximumSize(new Dimension(600, 200));

        JLabel codeTitle = new JLabel("Code d'accès unique");
        codeTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        codeTitle.setForeground(textPrimary);
        codeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel codeSubtitle = new JLabel("Partagez ce code pour donner accès au projet");
        codeSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        codeSubtitle.setForeground(textSecondary);
        codeSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardWrapper.add(codeTitle);
        cardWrapper.add(Box.createRigidArea(new Dimension(0, 8)));
        cardWrapper.add(codeSubtitle);
        cardWrapper.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel codeCard = new JPanel() {
            private float pulse = 0f;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int glowSize = 8 + (int) (Math.sin(pulse) * 4);
                g2.setColor(new Color(primaryGradient1.getRed(), primaryGradient1.getGreen(),
                        primaryGradient1.getBlue(), 30));
                g2.fillRoundRect(-glowSize, -glowSize, getWidth() + glowSize * 2,
                        getHeight() + glowSize * 2, 24, 24);

                GradientPaint gradient = new GradientPaint(
                        0, 0, primaryGradient1,
                        getWidth(), getHeight(), primaryGradient2);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                GradientPaint shine = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 150),
                        0, getHeight() / 2, new Color(255, 255, 255, 0));
                g2.setPaint(shine);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 20, 20);

                g2.dispose();
            }
        };
        codeCard.setLayout(new GridBagLayout());
        codeCard.setOpaque(false);
        codeCard.setMaximumSize(new Dimension(600, 110));
        codeCard.setPreferredSize(new Dimension(600, 110));
        codeCard.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel codeLabel = new JLabel(projectCode);
        codeLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 32));
        codeLabel.setForeground(Color.WHITE);
        codeCard.add(codeLabel);

        cardWrapper.add(codeCard);
        return cardWrapper;
    }

    private JPanel createPremiumQRSection(String projectCode) {
        JPanel qrSection = new JPanel();
        qrSection.setLayout(new BoxLayout(qrSection, BoxLayout.Y_AXIS));
        qrSection.setOpaque(false);
        qrSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrSection.setMaximumSize(new Dimension(450, 480));

        JLabel qrTitle = new JLabel("Code QR");
        qrTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        qrTitle.setForeground(textPrimary);
        qrTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel qrSubtitle = new JLabel("Scannez pour un accès rapide");
        qrSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        qrSubtitle.setForeground(textSecondary);
        qrSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        qrSection.add(qrTitle);
        qrSection.add(Box.createRigidArea(new Dimension(0, 8)));
        qrSection.add(qrSubtitle);
        qrSection.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel qrContainer = new JPanel();
        qrContainer.setLayout(new GridBagLayout());
        qrContainer.setOpaque(false);
        qrContainer.setPreferredSize(new Dimension(380, 380));
        qrContainer.setMaximumSize(new Dimension(380, 380));

        JLabel qrLabel = new JLabel("⏳");
        qrLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        qrLabel.setForeground(textSecondary);
        qrContainer.add(qrLabel);

        qrSection.add(qrContainer);

        loadQRCode(projectCode, qrLabel);

        return qrSection;
    }

    private void loadQRCode(String code, JLabel qrLabel) {
        new Thread(() -> {
            try {
                String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=320x320&data=" + code;
                URL url = new URL(qrUrl);
                BufferedImage qrImage = ImageIO.read(url);

                if (qrImage != null) {
                    ImageIcon icon = new ImageIcon(qrImage);
                    SwingUtilities.invokeLater(() -> {
                        qrLabel.setIcon(icon);
                        qrLabel.setText("");
                    });
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    qrLabel.setText("❌");
                    qrLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
                    qrLabel.setForeground(dangerColor);
                });
            }
        }).start();
    }

    private JPanel createPremiumActionButtons(String projectCode) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonsPanel.setMaximumSize(new Dimension(700, 65));

        JButton copyButton = createButton("Copier le code",
                accentGradient1, accentGradient2);
        copyButton.setPreferredSize(new Dimension(220, 58));
        copyButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        copyButton.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(projectCode);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            showModernSuccess("Code copié dans le presse-papiers !");
        });

        JButton resetButton = createButton("Nouveau projet",
                primaryGradient1, primaryGradient2);
        resetButton.setPreferredSize(new Dimension(220, 58));
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resetButton.addActionListener(e -> resetForm());

        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(copyButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonsPanel.add(resetButton);
        buttonsPanel.add(Box.createHorizontalGlue());

        return buttonsPanel;
    }

    private JButton createButton(String text, Color color1, Color color2) {
        JButton button = new JButton(text) {
            private boolean isPressed = false;
            private float hoverAnimation = 0f;

            {
                addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {
                        isPressed = true;
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        isPressed = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int shadowOffset = isPressed ? 2 : 4;
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(shadowOffset, shadowOffset, getWidth() - shadowOffset,
                        getHeight() - shadowOffset, 14, 14);

                GradientPaint gradient = new GradientPaint(
                        0, 0, color1,
                        getWidth(), getHeight(), color2);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                if (hoverAnimation > 0) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                            hoverAnimation * 0.25f));
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                }

                int translateY = isPressed ? 2 : 0;
                g2.translate(0, translateY);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        return button;
    }

    private void showModernSuccess(String message) {
        JPanel successPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(240, 253, 244));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(new Color(34, 197, 94, 50));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

                g2.dispose();
            }
        };
        successPanel.setLayout(new BoxLayout(successPanel, BoxLayout.X_AXIS));
        successPanel.setOpaque(false);
        successPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel icon = new JLabel("✅");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel text = new JLabel(message);
        text.setFont(new Font("Segoe UI", Font.BOLD, 15));
        text.setForeground(successColor);
        text.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        successPanel.add(icon);
        successPanel.add(text);

        JOptionPane.showMessageDialog(this, successPanel, "Succès", JOptionPane.PLAIN_MESSAGE);
    }

    private void resetForm() {
        nomField.setText("");
        descriptionField.setText("");
        budgetField.setText("");
        selectedDate = null;
        dateButton.setText("Sélectionner une date");
        dateButton.repaint();

        removeAll();
        formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    static class JDateChooser extends JPanel {
        private JSpinner dateSpinner;

        public JDateChooser() {
            setLayout(new BorderLayout());
            dateSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
            dateSpinner.setEditor(editor);
            dateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            add(dateSpinner, BorderLayout.CENTER);
            setPreferredSize(new Dimension(400, 50));
        }

        public java.util.Date getDate() {
            return (java.util.Date) dateSpinner.getValue();
        }
    }
}