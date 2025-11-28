package com.example.demo.interfaces;

import com.example.demo.Params;
import com.example.demo.components.Scrollbar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.imageio.ImageIO;

public class CreerProjet extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, progressBg;
    private Color successColor, dangerColor, warningColor;
    private Color primaryGradient1, primaryGradient2;

    private JTextField nomField, budgetField;
    private JTextArea descriptionField;
    private JButton dateButton;
    private LocalDate selectedDate;
    private JPanel formPanel;
    private JPanel successPanel;

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
            bgColor = new Color(245, 247, 250);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(30, 30, 30);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(59, 130, 246);
            progressBg = new Color(229, 231, 235);
            successColor = new Color(16, 185, 129);
            dangerColor = new Color(239, 68, 68);
            warningColor = new Color(245, 158, 11);
            primaryGradient1 = new Color(139, 92, 246);
            primaryGradient2 = new Color(167, 139, 250);
        } else {
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(20, 20, 20);
            textPrimary = new Color(230, 230, 230);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(0, 120, 215);
            progressBg = new Color(30, 30, 30);
            successColor = new Color(0, 200, 100);
            dangerColor = new Color(220, 38, 38);
            warningColor = new Color(255, 165, 0);
            primaryGradient1 = new Color(139, 92, 246);
            primaryGradient2 = new Color(167, 139, 250);
        }
    }

    private JPanel createFormPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(bgColor);

        contentWrapper.add(createHeaderSection());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        contentWrapper.add(createFormFieldsSection());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        contentWrapper.add(createSubmitSection());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 20)));

        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createHeaderSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("Créer un nouveau projet");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("Remplissez les informations pour créer votre projet");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descLabel.setForeground(textSecondary);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        section.add(titleLabel);
        section.add(Box.createRigidArea(new Dimension(0, 15)));
        section.add(descLabel);

        return section;
    }

    private JPanel createFormFieldsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 800));

        card.add(createFormField("Nom du projet", "Entrez le nom du projet", "📋",
                nomField = createModernTextField()));
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        card.add(createDescriptionField());
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        card.add(createDateFieldModern());
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        card.add(createFormField("Budget", "Entrez le budget en euros", "💰",
                budgetField = createModernTextField()));

        section.add(card);
        return section;
    }

    private JPanel createFormField(String label, String placeholder, String emoji, JTextField field) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        labelPanel.setOpaque(false);
        labelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        labelText.setForeground(textPrimary);

        JLabel requiredLabel = new JLabel("*");
        requiredLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        requiredLabel.setForeground(dangerColor);

        labelPanel.add(emojiLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        labelPanel.add(labelText);
        labelPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        labelPanel.add(requiredLabel);

        fieldPanel.add(labelPanel);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        fieldPanel.add(field);

        return fieldPanel;
    }

    private JTextField createModernTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(theme == 0 ? new Color(249, 250, 251) : new Color(30, 30, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                if (hasFocus()) {
                    g2.setColor(accentColor);
                    g2.setStroke(new BasicStroke(2.5f));
                } else {
                    g2.setColor(progressBg);
                    g2.setStroke(new BasicStroke(2f));
                }
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setForeground(textPrimary);
        field.setBackground(new Color(0, 0, 0, 0));
        field.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        field.setPreferredSize(new Dimension(0, 48));
        field.setCaretColor(accentColor);
        field.setOpaque(false);

        return field;
    }

    private JPanel createDescriptionField() {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        labelPanel.setOpaque(false);
        labelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emojiLabel = new JLabel("📝");
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel labelText = new JLabel("Description");
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        labelText.setForeground(textPrimary);

        JLabel requiredLabel = new JLabel("*");
        requiredLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        requiredLabel.setForeground(dangerColor);

        labelPanel.add(emojiLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        labelPanel.add(labelText);
        labelPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        labelPanel.add(requiredLabel);

        fieldPanel.add(labelPanel);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel textAreaWrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(theme == 0 ? new Color(249, 250, 251) : new Color(30, 30, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(progressBg);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

                g2.dispose();
            }
        };
        textAreaWrapper.setLayout(new BorderLayout());
        textAreaWrapper.setOpaque(false);
        textAreaWrapper.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        textAreaWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        descriptionField = new JTextArea();
        descriptionField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descriptionField.setForeground(textPrimary);
        descriptionField.setBackground(new Color(0, 0, 0, 0));
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        descriptionField.setBorder(null);
        descriptionField.setCaretColor(accentColor);
        descriptionField.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(descriptionField);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        textAreaWrapper.add(scrollPane, BorderLayout.CENTER);
        fieldPanel.add(textAreaWrapper);

        return fieldPanel;
    }

    private JPanel createDateFieldModern() {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        labelPanel.setOpaque(false);
        labelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emojiLabel = new JLabel("📅");
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel labelText = new JLabel("Date limite");
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        labelText.setForeground(textPrimary);

        JLabel requiredLabel = new JLabel("*");
        requiredLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        requiredLabel.setForeground(dangerColor);

        labelPanel.add(emojiLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        labelPanel.add(labelText);
        labelPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        labelPanel.add(requiredLabel);

        fieldPanel.add(labelPanel);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        dateButton = new JButton("Sélectionner une date") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(theme == 0 ? new Color(249, 250, 251) : new Color(30, 30, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(progressBg);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

                g2.setColor(selectedDate != null ? textPrimary : textSecondary);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = 16;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);

                String calendarIcon = "📆";
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                int iconX = getWidth() - 40;
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
        dateButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        dateButton.setPreferredSize(new Dimension(0, 48));
        dateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dateButton.setHorizontalAlignment(SwingConstants.LEFT);
        dateButton.addActionListener(e -> showDatePicker());

        fieldPanel.add(dateButton);
        return fieldPanel;
    }

    private void showDatePicker() {
        JDateChooser dateChooser = new JDateChooser();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
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

    private JPanel createSubmitSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.X_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JButton createButton = createModernButton("🚀 Créer le projet",
                new Color(16, 185, 129), new Color(52, 211, 153));
        createButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        createButton.setPreferredSize(new Dimension(250, 52));
        createButton.addActionListener(e -> createProject());

        section.add(createButton);
        section.add(Box.createHorizontalGlue());

        return section;
    }

    private void createProject() {
        String nom = nomField.getText().trim();
        String description = descriptionField.getText().trim();
        String budgetStr = budgetField.getText().trim();

        if (nom.isEmpty() || description.isEmpty() || selectedDate == null || budgetStr.isEmpty()) {
            showErrorDialog("Veuillez remplir tous les champs obligatoires");
            return;
        }

        try {
            double budget = Double.parseDouble(budgetStr);
            if (budget <= 0) {
                showErrorDialog("Le budget doit être supérieur à 0");
                return;
            }
        } catch (NumberFormatException e) {
            showErrorDialog("Le budget doit être un nombre valide");
            return;
        }

        String projectCode = generateProjectCode(nom);
        showSuccessPanel(nom, projectCode);
    }

    private void showErrorDialog(String message) {
        JPanel errorPanel = new JPanel();
        errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.X_AXIS));
        errorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel("⚠️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        errorPanel.add(iconLabel);
        errorPanel.add(messageLabel);

        JOptionPane.showMessageDialog(this, errorPanel, "Erreur", JOptionPane.ERROR_MESSAGE);
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
        removeAll();

        successPanel = new JPanel(new BorderLayout(20, 20));
        successPanel.setBackground(bgColor);
        successPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(bgColor);

        contentWrapper.add(Box.createRigidArea(new Dimension(0, 30)));
        contentWrapper.add(createSuccessHeader(projectName));
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 40)));
        contentWrapper.add(createCodeCard(projectCode));
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 35)));
        contentWrapper.add(createQRSection(projectCode));
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 40)));
        contentWrapper.add(createActionButtons(projectCode));
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 20)));

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
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JLabel successIcon = new JLabel("✅");
        successIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        successIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel successTitle = new JLabel("Projet créé avec succès !");
        successTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        successTitle.setForeground(successColor);
        successTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel projectNameLabel = new JLabel(projectName);
        projectNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        projectNameLabel.setForeground(textSecondary);
        projectNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(successIcon);
        header.add(Box.createRigidArea(new Dimension(0, 20)));
        header.add(successTitle);
        header.add(Box.createRigidArea(new Dimension(0, 12)));
        header.add(projectNameLabel);

        return header;
    }

    private JPanel createCodeCard(String projectCode) {
        JPanel cardWrapper = new JPanel();
        cardWrapper.setLayout(new BoxLayout(cardWrapper, BoxLayout.Y_AXIS));
        cardWrapper.setOpaque(false);
        cardWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardWrapper.setMaximumSize(new Dimension(500, 160));

        JLabel codeTitle = new JLabel("Code d'accès unique");
        codeTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        codeTitle.setForeground(textPrimary);
        codeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardWrapper.add(codeTitle);
        cardWrapper.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel codeCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, primaryGradient1,
                        getWidth(), getHeight(), primaryGradient2);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 20, 20);

                g2.dispose();
            }
        };
        codeCard.setLayout(new GridBagLayout());
        codeCard.setOpaque(false);
        codeCard.setMaximumSize(new Dimension(500, 90));
        codeCard.setPreferredSize(new Dimension(500, 90));
        codeCard.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel codeLabel = new JLabel(projectCode);
        codeLabel.setFont(new Font("Courier New", Font.BOLD, 28));
        codeLabel.setForeground(Color.WHITE);
        codeCard.add(codeLabel);

        cardWrapper.add(codeCard);
        return cardWrapper;
    }

    private JPanel createQRSection(String projectCode) {
        JPanel qrSection = new JPanel();
        qrSection.setLayout(new BoxLayout(qrSection, BoxLayout.Y_AXIS));
        qrSection.setOpaque(false);
        qrSection.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrSection.setMaximumSize(new Dimension(400, 420));

        JLabel qrTitle = new JLabel("Code QR");
        qrTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        qrTitle.setForeground(textPrimary);
        qrTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        qrSection.add(qrTitle);
        qrSection.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel qrContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.setColor(new Color(progressBg.getRed(), progressBg.getGreen(), progressBg.getBlue(), 100));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);

                g2.dispose();
            }
        };
        qrContainer.setLayout(new GridBagLayout());
        qrContainer.setOpaque(false);
        qrContainer.setPreferredSize(new Dimension(350, 350));
        qrContainer.setMaximumSize(new Dimension(350, 350));

        JLabel qrLabel = new JLabel("⏳ Chargement...");
        qrLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        qrLabel.setForeground(textSecondary);
        qrContainer.add(qrLabel);

        qrSection.add(qrContainer);

        loadQRCode(projectCode, qrLabel);

        return qrSection;
    }

    private void loadQRCode(String code, JLabel qrLabel) {
        new Thread(() -> {
            try {
                String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" + code;
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
                    qrLabel.setText("❌ Erreur de chargement");
                    qrLabel.setForeground(dangerColor);
                });
            }
        }).start();
    }

    private JPanel createActionButtons(String projectCode) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonsPanel.setMaximumSize(new Dimension(600, 55));

        JButton copyButton = createModernButton("📋 Copier le code",
                accentColor, new Color(96, 165, 250));
        copyButton.setPreferredSize(new Dimension(200, 50));
        copyButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        copyButton.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(projectCode);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

            JOptionPane.showMessageDialog(this,
                    createSuccessMessage("Code copié dans le presse-papiers !"),
                    "Succès",
                    JOptionPane.PLAIN_MESSAGE);
        });

        JButton resetButton = createModernButton("🔄 Créer un autre projet",
                new Color(139, 92, 246), new Color(167, 139, 250));
        resetButton.setPreferredSize(new Dimension(240, 50));
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        resetButton.addActionListener(e -> resetForm());

        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(copyButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        buttonsPanel.add(resetButton);
        buttonsPanel.add(Box.createHorizontalGlue());

        return buttonsPanel;
    }

    private JPanel createSuccessMessage(String message) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel icon = new JLabel("✅");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel text = new JLabel(message);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        text.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        panel.add(icon);
        panel.add(text);

        return panel;
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

                if (getModel().isRollover()) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        return button;
    }

    static class JDateChooser extends JPanel {
        private JSpinner dateSpinner;

        public JDateChooser() {
            setLayout(new BorderLayout());
            dateSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
            dateSpinner.setEditor(editor);
            dateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            add(dateSpinner, BorderLayout.CENTER);
            setPreferredSize(new Dimension(350, 45));
        }

        public java.util.Date getDate() {
            return (java.util.Date) dateSpinner.getValue();
        }
    }
}