package com.example.demo.interfaces;

import com.example.demo.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class Forgot extends JPanel {
    private Auth parent;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, inputBorder, successColor, errorColor,
            inputBg, hoverColor;
    private String userEmail = "";
    private int generatedCode = 0;
    private JPanel dynamicPanel;
    private CardLayout cardLayout;

    public Forgot(Auth parent) {
        this.parent = parent;
        this.bgColor = parent.getBgColor();
        this.cardBgColor = parent.getCardBgColor();
        this.textPrimary = parent.getTextPrimary();
        this.textSecondary = parent.getTextSecondary();
        this.accentColor = parent.getAccentColor();
        this.inputBorder = parent.getInputBorder();
        this.successColor = parent.getSuccessColor();
        this.errorColor = parent.getErrorColor();
        this.inputBg = parent.getInputBg();
        this.hoverColor = parent.getHoverColor();
        cardLayout = new CardLayout();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(bgColor);
        setBorder(new EmptyBorder(40, 40, 40, 40));
        initUI();
    }

    private void initUI() {
        JPanel formCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, 24, 24);
                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 24, 24);
                g2.dispose();
            }
        };
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setOpaque(false);
        formCard.setBorder(new EmptyBorder(50, 45, 50, 45));

        JLabel titleLabel = new JLabel("Mot de passe oublié ?");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(titleLabel);
        formCard.add(Box.createVerticalStrut(8));

        JLabel subtitleLabel = new JLabel("Entrez votre email pour recevoir un code de vérification");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(textSecondary);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(subtitleLabel);
        formCard.add(Box.createVerticalStrut(40));

        dynamicPanel = new JPanel(cardLayout);
        dynamicPanel.setOpaque(false);

        // Panel pour l'email
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setOpaque(false);
        JLabel emailLabel = createModernLabel("Adresse Email");
        emailPanel.add(emailLabel);
        emailPanel.add(Box.createVerticalStrut(10));
        JTextField emailField = createModernTextField("votre@email.com");
        emailPanel.add(emailField);
        emailPanel.add(Box.createVerticalStrut(32));
        JButton btnSendCode = createModernButton("Envoyer le code", accentColor);
        emailPanel.add(btnSendCode);
        emailPanel.add(Box.createVerticalStrut(20));
        JLabel message = new JLabel("", SwingConstants.CENTER);
        message.setFont(new Font("Segoe UI", Font.BOLD, 14));
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailPanel.add(message);

        // Panel pour le code
        JPanel codePanel = new JPanel();
        codePanel.setLayout(new BoxLayout(codePanel, BoxLayout.Y_AXIS));
        codePanel.setOpaque(false);
        JLabel codeLabel = createModernLabel("Code de vérification");
        codePanel.add(codeLabel);
        codePanel.add(Box.createVerticalStrut(10));
        JTextField codeField = createModernTextField("123456");
        codePanel.add(codeField);
        codePanel.add(Box.createVerticalStrut(32));
        JButton btnVerifyCode = createModernButton("Vérifier le code", accentColor);
        codePanel.add(btnVerifyCode);
        codePanel.add(Box.createVerticalStrut(20));
        JLabel codeMessage = new JLabel("", SwingConstants.CENTER);
        codeMessage.setFont(new Font("Segoe UI", Font.BOLD, 14));
        codeMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        codePanel.add(codeMessage);

        // Panel pour le nouveau mot de passe
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setOpaque(false);
        JLabel passwordLabel = createModernLabel("Nouveau mot de passe");
        passwordPanel.add(passwordLabel);
        passwordPanel.add(Box.createVerticalStrut(10));
        JPasswordField passwordField = createModernPasswordField("Nouveau mot de passe");
        passwordPanel.add(passwordField);
        passwordPanel.add(Box.createVerticalStrut(32));
        JButton btnResetPassword = createModernButton("Réinitialiser", accentColor);
        passwordPanel.add(btnResetPassword);
        passwordPanel.add(Box.createVerticalStrut(20));
        JLabel passwordMessage = new JLabel("", SwingConstants.CENTER);
        passwordMessage.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordPanel.add(passwordMessage);

        dynamicPanel.add(emailPanel, "email");
        dynamicPanel.add(codePanel, "code");
        dynamicPanel.add(passwordPanel, "password");
        cardLayout.show(dynamicPanel, "email");

        formCard.add(dynamicPanel);
        formCard.add(Box.createVerticalStrut(25));

        JPanel switchPanel = createSwitchPanel(
                "Vous vous souvenez de votre mot de passe ?",
                "Se connecter",
                e -> parent.switchCard("login"));
        formCard.add(switchPanel);

        btnSendCode.addActionListener(e -> handleSendCode(emailField, message, btnSendCode));
        btnVerifyCode.addActionListener(e -> handleVerifyCode(codeField, codeMessage, btnVerifyCode));
        btnResetPassword.addActionListener(e -> handleResetPassword(passwordField, passwordMessage, btnResetPassword));

        add(formCard);
    }

    private JLabel createModernLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(textPrimary);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(inputBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (isFocusOwner()) {
                    g2.setColor(accentColor);
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(inputBorder);
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setPreferredSize(new Dimension(0, 50));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        field.setBackground(inputBg);
        field.setForeground(textPrimary);
        field.setCaretColor(textPrimary);
        field.setBorder(new EmptyBorder(12, 18, 12, 18));
        field.setOpaque(false);
        return field;
    }

    private JPasswordField createModernPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(inputBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (isFocusOwner()) {
                    g2.setColor(accentColor);
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(inputBorder);
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setPreferredSize(new Dimension(0, 50));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        field.setBackground(inputBg);
        field.setForeground(textPrimary);
        field.setCaretColor(textPrimary);
        field.setBorder(new EmptyBorder(12, 18, 12, 18));
        field.setOpaque(false);
        return field;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            private float hoverAlpha = 0f;
            private Timer hoverTimer;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        animateHover(true);
                    }

                    public void mouseExited(java.awt.event.MouseEvent e) {
                        animateHover(false);
                    }
                });
            }

            private void animateHover(boolean enter) {
                if (hoverTimer != null)
                    hoverTimer.stop();
                hoverTimer = new Timer(10, e -> {
                    if (enter) {
                        hoverAlpha = Math.min(1f, hoverAlpha + 0.1f);
                    } else {
                        hoverAlpha = Math.max(0f, hoverAlpha - 0.1f);
                    }
                    repaint();
                    if ((enter && hoverAlpha >= 1f) || (!enter && hoverAlpha <= 0f)) {
                        hoverTimer.stop();
                    }
                });
                hoverTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color baseColor = bgColor;
                if (getModel().isPressed()) {
                    baseColor = bgColor.darker().darker();
                } else if (hoverAlpha > 0) {
                    int r = (int) (baseColor.getRed() * (1 - hoverAlpha * 0.2));
                    int g_ = (int) (baseColor.getGreen() * (1 - hoverAlpha * 0.2));
                    int b = (int) (baseColor.getBlue() * (1 - hoverAlpha * 0.2));
                    baseColor = new Color(r, g_, b);
                }
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(2, 4, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.setColor(baseColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() - 4, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(0, 54));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private JPanel createSwitchPanel(String text, String linkText, java.awt.event.ActionListener action) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(textSecondary);
        JLabel link = new JLabel(linkText);
        link.setFont(new Font("Segoe UI", Font.BOLD, 14));
        link.setForeground(accentColor);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.actionPerformed(null);
            }

            public void mouseEntered(java.awt.event.MouseEvent e) {
                link.setForeground(hoverColor);
                link.setText("<html><u>" + linkText + "</u></html>");
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                link.setForeground(accentColor);
                link.setText(linkText);
            }
        });
        panel.add(label);
        panel.add(link);
        return panel;
    }

    private void showMessage(JLabel label, String text, Color color) {
        label.setText(text);
        label.setForeground(color);
        Timer fadeTimer = new Timer(3000, e -> {
            Timer clearTimer = new Timer(50, evt -> {
                Color current = label.getForeground();
                int alpha = Math.max(0, current.getAlpha() - 15);
                label.setForeground(new Color(current.getRed(), current.getGreen(), current.getBlue(), alpha));
                if (alpha == 0) {
                    label.setText("");
                    ((Timer) evt.getSource()).stop();
                }
            });
            clearTimer.start();
        });
        fadeTimer.setRepeats(false);
        fadeTimer.start();
    }

    private void handleSendCode(JTextField emailField, JLabel message, JButton btnSendCode) {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showMessage(message, "⚠ Veuillez entrer votre adresse email", errorColor);
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showMessage(message, "✗ Adresse email invalide", errorColor);
            return;
        }
        btnSendCode.setEnabled(false);
        btnSendCode.setText("Envoi en cours...");
        message.setText("");
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                Random random = new Random();
                generatedCode = 100000 + random.nextInt(900000);
                Params.verifCode = generatedCode;
                String urlString = "https://pahae-utils.vercel.app/api/sendMail?email="
                        + URLEncoder.encode(email, "UTF-8") +
                        "&code=" + generatedCode;
                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(10000);
                con.setReadTimeout(10000);
                int responseCode = con.getResponseCode();
                InputStreamReader streamReader;
                if (responseCode >= 200 && responseCode < 400) {
                    streamReader = new InputStreamReader(con.getInputStream(), "UTF-8");
                } else {
                    streamReader = new InputStreamReader(con.getErrorStream(), "UTF-8");
                }
                try (BufferedReader in = new BufferedReader(streamReader)) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    return response.toString();
                }
            }

            @Override
            protected void done() {
                try {
                    String response = get();
                    if (response.contains("Email code sent!")) {
                        userEmail = emailField.getText().trim();
                        showMessage(message, "✓ Code envoyé avec succès !", successColor);
                        Timer timer = new Timer(1500, evt -> {
                            cardLayout.show(dynamicPanel, "code");
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showMessage(message, "✗ Erreur lors de l'envoi du code", errorColor);
                        btnSendCode.setEnabled(true);
                        btnSendCode.setText("Envoyer le code");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessage(message, "✗ Erreur de connexion au serveur", errorColor);
                    btnSendCode.setEnabled(true);
                    btnSendCode.setText("Envoyer le code");
                }
            }
        };
        worker.execute();
    }

    private void handleVerifyCode(JTextField codeField, JLabel message, JButton btnVerifyCode) {
        String codeStr = codeField.getText().trim();
        if (codeStr.isEmpty()) {
            showMessage(message, "⚠ Veuillez entrer le code", errorColor);
            return;
        }
        try {
            int code = Integer.parseInt(codeStr);
            if (code == Params.verifCode) {
                showMessage(message, "✓ Code correct !", successColor);
                Timer timer = new Timer(1500, evt -> {
                    cardLayout.show(dynamicPanel, "password");
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                showMessage(message, "✗ Code incorrect", errorColor);
            }
        } catch (NumberFormatException e) {
            showMessage(message, "✗ Code invalide", errorColor);
        }
    }

    private void handleResetPassword(JPasswordField passwordField, JLabel message, JButton btnResetPassword) {
        String newPassword = new String(passwordField.getPassword()).trim();
        if (newPassword.isEmpty()) {
            showMessage(message, "⚠ Veuillez entrer un nouveau mot de passe", errorColor);
            return;
        }

        btnResetPassword.setEnabled(false);
        btnResetPassword.setText("Mise à jour en cours...");

        Map<String, String> body = new HashMap<>();
        body.put("email", userEmail);
        body.put("newPassword", newPassword);

        CompletableFuture<Map<String, Object>> future = Queries.post("/auth/reset-password", body);
        future.whenComplete((response, throwable) -> {
            SwingUtilities.invokeLater(() -> {
                if (throwable != null) {
                    showMessage(message, "✗ Erreur de connexion au serveur", errorColor);
                    btnResetPassword.setEnabled(true);
                    btnResetPassword.setText("Réinitialiser");
                    return;
                }

                if (response.containsKey("success")) {
                    showMessage(message, "✓ Mot de passe mis à jour avec succès !", successColor);
                    Timer timer = new Timer(2000, evt -> {
                        parent.switchCard("login");
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    String errorMsg = response.containsKey("error") ? response.get("error").toString()
                            : "Erreur inconnue";
                    System.out.println(response);
                    showMessage(message, "✗ " + errorMsg, errorColor);
                    btnResetPassword.setEnabled(true);
                    btnResetPassword.setText("Réinitialiser");
                }
            });
        });
    }
}
