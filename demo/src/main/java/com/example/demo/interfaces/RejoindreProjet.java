package com.example.demo.interfaces;

import com.example.demo.Params;
import com.example.demo.Queries;
import com.example.demo.SessionManager;
import com.example.demo.components.Scrollbar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RejoindreProjet extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, progressBg;
    private Color successColor, dangerColor, warningColor;
    private Color primaryGradient1, primaryGradient2;

    private JTextField codeField;
    private JButton verifyButton;
    private JPanel messagePanel;
    private ArrayList<String> validCodes;
    private int userID;

    public RejoindreProjet() {
        this.theme = Params.theme;
        this.userID = SessionManager.getInstance().getUserId();
        initializeColors();
        initializeValidCodes();
        setLayout(new BorderLayout());
        setBackground(bgColor);

        add(createMainPanel(), BorderLayout.CENTER);
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

    private void initializeValidCodes() {
        validCodes = new ArrayList<>();
        validCodes.add("8F1A7BC2");
        validCodes.add("A42DE5F9");
        validCodes.add("C7B9E341");
        validCodes.add("DEMO1234");
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(bgColor);

        contentWrapper.add(Box.createVerticalGlue());
        contentWrapper.add(createHeaderSection());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 40)));
        contentWrapper.add(createInputSection());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 30)));
        contentWrapper.add(createMessagePanel());
        contentWrapper.add(Box.createVerticalGlue());

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
        section.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel titleLabel = new JLabel("Rejoindre un projet");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("Entrez le code d'accès pour rejoindre un projet existant");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(textSecondary);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel subtitleLabel = new JLabel("Le code vous a été fourni par le créateur du projet");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        section.add(titleLabel);
        section.add(Box.createRigidArea(new Dimension(0, 12)));
        section.add(descLabel);
        section.add(Box.createRigidArea(new Dimension(0, 8)));
        section.add(subtitleLabel);

        return section;
    }

    private JPanel createInputSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.setMaximumSize(new Dimension(500, 250));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(500, 250));

        JLabel codeLabel = new JLabel("Code d'accès");
        codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        codeLabel.setForeground(textPrimary);
        codeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(codeLabel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        codeField = createModernTextField();
        codeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        codeField.setPreferredSize(new Dimension(0, 56));
        codeField.setHorizontalAlignment(JTextField.CENTER);
        codeField.setFont(new Font("Courier New", Font.BOLD, 24));

        codeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    verifyCode();
                }
            }
        });

        card.add(codeField);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        verifyButton = createModernButton("Vérifier le code",
                new Color(25, 200, 25), new Color(75, 200, 75));
        verifyButton.setPreferredSize(new Dimension(300, 54));
        verifyButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        verifyButton.setMaximumSize(new Dimension(300, 54));
        verifyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        verifyButton.addActionListener(e -> verifyCode());

        card.add(verifyButton);

        section.add(card);
        return section;
    }

    private JPanel createMessagePanel() {
        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setOpaque(false);
        messagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messagePanel.setMaximumSize(new Dimension(500, 200));

        return messagePanel;
    }

    private void verifyCode() {
        String code = codeField.getText().trim().toUpperCase();
        if (code.isEmpty()) {
            showMessage("Veuillez entrer un code", false, true);
            return;
        }
        if (code.length() < 6) {
            showMessage("Le code doit contenir au moins 6 caractères", false, true);
            return;
        }

        Queries.get("/api/projet/join/" + code + "/" + userID)
                .thenAccept(response -> {
                    SwingUtilities.invokeLater(() -> {
                        if (response.containsKey("error")) {
                            showMessage("Erreur réseau : " + response.get("error"), false, false);
                        } else {
                            boolean success = (boolean) response.getOrDefault("success", false);
                            String message = (String) response.getOrDefault("message", "Erreur inconnue");
                            if (success) {
                                showMessage(message, true, false);
                                codeField.setEnabled(false);
                                verifyButton.setEnabled(false);
                            } else {
                                showMessage(message, false, false);
                                codeField.selectAll();
                                codeField.requestFocus();
                            }
                        }
                    });
                })
                .exceptionally(e -> {
                    SwingUtilities.invokeLater(() -> {
                        showMessage("Erreur réseau : " + e.getMessage(), false, false);
                    });
                    return null;
                });
    }

    private void showMessage(String message, boolean success, boolean warning) {
        messagePanel.removeAll();

        JPanel messageContent = new JPanel(new BorderLayout(20, 20));
        messageContent.setOpaque(false);
        messageContent.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        messageContent.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        Color borderColor = success ? successColor : warning ? warningColor : dangerColor;
        String icon = success ? "✓" : warning ? "!" : "X";

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        iconLabel.setForeground(borderColor);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(success ? "Succès" : warning ? "Attention" : "Erreur");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        titleLabel.setForeground(borderColor);

        JLabel messageLabel = new JLabel("<html>" + message + "</html>");
        messageLabel.setFont(new Font("Dialog", Font.PLAIN, 13));
        messageLabel.setForeground(textPrimary);

        textPanel.add(titleLabel);
        textPanel.add(messageLabel);

        messageContent.add(iconLabel, BorderLayout.WEST);
        messageContent.add(textPanel, BorderLayout.CENTER);

        messageContent.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        messagePanel.add(messageContent);
        messagePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        if (success) {
            JPanel detailsPanel = createSuccessDetails();
            messagePanel.add(detailsPanel);
            messagePanel.add(Box.createRigidArea(new Dimension(0, 10)));

            JButton actionButton = new JButton("Accéder au projet");
            actionButton.setFont(new Font("Dialog", Font.BOLD, 13));
            actionButton.setPreferredSize(new Dimension(200, 40));
            actionButton.setBackground(primaryGradient1);
            actionButton.setForeground(Color.WHITE);
            actionButton.setFocusPainted(false);
            actionButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

            actionButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(this,
                        new JLabel("Redirection vers le projet..."),
                        "Accès", JOptionPane.PLAIN_MESSAGE);
            });

            messagePanel.add(actionButton);
        }

        messagePanel.revalidate();
        messagePanel.repaint();
    }

    private JPanel createSuccessDetails() {
        JPanel details = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(successColor.getRed(), successColor.getGreen(), successColor.getBlue(), 10));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(new Color(successColor.getRed(), successColor.getGreen(), successColor.getBlue(), 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.dispose();
            }
        };

        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setOpaque(false);
        details.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        details.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        details.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Détails du projet");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy - HH:mm", Locale.FRENCH));

        JLabel infoLabel = new JLabel("Date d'accès : " + timestamp);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setForeground(textSecondary);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel("Rôle : Membre");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleLabel.setForeground(textSecondary);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        details.add(titleLabel);
        details.add(Box.createRigidArea(new Dimension(0, 10)));
        details.add(infoLabel);
        details.add(Box.createRigidArea(new Dimension(0, 5)));
        details.add(roleLabel);

        return details;
    }

    private JPanel createMessageComponent(String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel icon = new JLabel("⏳");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        panel.add(icon);
        panel.add(textLabel);

        return panel;
    }

    private JTextField createModernTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(theme == 0 ? new Color(249, 250, 251) : new Color(30, 30, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                if (hasFocus()) {
                    g2.setColor(accentColor);
                    g2.setStroke(new BasicStroke(3f));
                } else {
                    g2.setColor(new Color(progressBg.getRed(), progressBg.getGreen(), progressBg.getBlue(), 100));
                    g2.setStroke(new BasicStroke(2f));
                }
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(textPrimary);
        field.setBackground(new Color(0, 0, 0, 0));
        field.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        field.setCaretColor(accentColor);
        field.setOpaque(false);

        return field;
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                if (getModel().isRollover() && isEnabled()) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
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
}