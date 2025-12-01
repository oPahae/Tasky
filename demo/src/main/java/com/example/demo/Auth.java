package com.example.demo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Auth extends JFrame {

    private static final String BASE_URL = "http://localhost:8080/auth";
    private JPanel mainContentPanel;
    private CardLayout contentLayout;
    private int userId = -1;
    private int theme;
    
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, inputBorder, successColor, errorColor;

    public Auth() {
        this.theme = Params.theme;
        initializeColors();
        
        setTitle("Authentification - Gestion des Utilisateurs");
        setSize(550, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);

        contentLayout = new CardLayout();
        mainContentPanel = new JPanel(contentLayout);
        mainContentPanel.setBackground(bgColor);

        JPanel loginPanel = createLoginPanel();
        JPanel registerPanel = createRegisterPanel();

        mainContentPanel.add(loginPanel, "login");
        mainContentPanel.add(registerPanel, "register");

        add(mainContentPanel);
        contentLayout.show(mainContentPanel, "login");
    }

    private void initializeColors() {
        if (theme == 0) {
            bgColor = new Color(245, 247, 250);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(30, 30, 30);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(59, 130, 246);
            inputBorder = new Color(209, 213, 219);
            successColor = new Color(34, 197, 94);
            errorColor = new Color(239, 68, 68);
        } else {
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(20, 20, 20);
            textPrimary = new Color(230, 230, 230);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(0, 120, 215);
            inputBorder = new Color(50, 50, 50);
            successColor = new Color(0, 200, 100);
            errorColor = new Color(255, 80, 80);
        }
    }

    // ================= LOGIN PANEL ===================
    private JPanel createLoginPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

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
        card.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header avec icône
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconLabel = new JLabel("🔐");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(15));

        JLabel title = new JLabel("Bienvenue");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(textPrimary);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(8));

        JLabel subtitle = new JLabel("Connectez-vous à votre compte");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(textSecondary);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitle);

        card.add(headerPanel);
        card.add(Box.createVerticalStrut(35));

        // Email
        JLabel emailLabel = createStyledLabel("Adresse Email");
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(8));
        
        JTextField emailField = createStyledTextField();
        emailField.setText("pahae");
        card.add(emailField);
        card.add(Box.createVerticalStrut(20));

        // Password
        JLabel passwordLabel = createStyledLabel("Mot de passe");
        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(8));
        
        JPasswordField passwordField = createStyledPasswordField();
        passwordField.setText("111111");
        card.add(passwordField);
        card.add(Box.createVerticalStrut(30));

        // Button
        JButton btnLogin = createStyledButton("Se connecter", accentColor);
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(20));

        // Message
        JLabel message = new JLabel("", SwingConstants.CENTER);
        message.setFont(new Font("Segoe UI", Font.BOLD, 13));
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(message);
        card.add(Box.createVerticalStrut(20));

        // Switch to register
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        switchPanel.setOpaque(false);
        switchPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel switchText = new JLabel("Pas de compte ?");
        switchText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        switchText.setForeground(textSecondary);
        
        JLabel switchLink = new JLabel("S'inscrire");
        switchLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        switchLink.setForeground(accentColor);
        switchLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                contentLayout.show(mainContentPanel, "register");
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                switchLink.setForeground(accentColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                switchLink.setForeground(accentColor);
            }
        });
        
        switchPanel.add(switchText);
        switchPanel.add(switchLink);
        card.add(switchPanel);

        // Action
        btnLogin.addActionListener(e -> {
            if (emailField.getText().trim().isEmpty() || passwordField.getPassword().length == 0) {
                showMessage(message, "⚠ Veuillez remplir tous les champs", errorColor);
                return;
            }

            btnLogin.setEnabled(false);
            btnLogin.setText("Connexion en cours...");
            message.setText("");

            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() throws Exception {
                    String json = "{ \"email\": \"" + emailField.getText() + "\", " +
                            "\"password\": \"" + new String(passwordField.getPassword()) + "\" }";
                    return sendPOST(BASE_URL + "/login", json);
                }

                @Override
                protected void done() {
                    try {
                        String response = get();
                        
                        System.out.println("========================================");
                        System.out.println("RÉPONSE COMPLÈTE DE L'API:");
                        System.out.println(response);
                        System.out.println("========================================");
                        
                        if (response.contains("success") && response.contains("token")) {
                            String token = extractToken(response);
                            String prenom = extractValue(response, "prenom");
                            String nom = extractValue(response, "nom");
                            String email = extractValue(response, "email");
                            String competance = extractValue(response, "competance");
                            String telephone = extractValue(response, "telephone");
                            
                            System.out.println("--- EXTRACTION ---");
                            System.out.println("Token extrait: " + token);
                            System.out.println("Prénom extrait: " + prenom);
                            System.out.println("Nom extrait: " + nom);
                            System.out.println("Email extrait: " + email);
                            System.out.println("Compétence extraite: " + competance);
                            System.out.println("Téléphone extrait: " + telephone);
                            System.out.println("------------------");
                            
                            String userIdStr = extractValue(response, "id");
                            try {
                                if (userIdStr != null && !userIdStr.isEmpty()) {
                                    userId = Integer.parseInt(userIdStr);
                                }
                            } catch (NumberFormatException ex) {
                                System.err.println("Erreur conversion userId: " + ex.getMessage());
                            }
                            
                            if (token != null && !token.equals("session-active")) {
                                TokenManager.saveToken(token);
                            }
                            
                            SessionManager.getInstance().setUserSession(
                                token, prenom, nom, email, competance, telephone, userId
                            );
                            
                            SessionManager.getInstance().printSessionInfo();
                            
                            showMessage(message, "✓ Connexion réussie !", successColor);
                            
                            Timer timer = new Timer(1000, evt -> {
                                dispose();
                                SwingUtilities.invokeLater(() -> {
                                    Main mainGUI = new Main();
                                    mainGUI.setVisible(true);
                                });
                            });
                            timer.setRepeats(false);
                            timer.start();
                        } else {
                            showMessage(message, "✗ " + extractErrorMessage(response), errorColor);
                            btnLogin.setEnabled(true);
                            btnLogin.setText("Se connecter");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showMessage(message, "✗ Erreur de connexion", errorColor);
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Se connecter");
                    }
                }
            };
            worker.execute();
        });

        mainPanel.add(card, BorderLayout.CENTER);
        return mainPanel;
    }

    // ================= REGISTER PANEL ===================
    private JPanel createRegisterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(bgColor);

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
        card.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconLabel = new JLabel("✨");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(15));

        JLabel title = new JLabel("Créer un compte");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(textPrimary);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(8));

        JLabel subtitle = new JLabel("Remplissez les informations ci-dessous");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(textSecondary);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitle);

        card.add(headerPanel);
        card.add(Box.createVerticalStrut(30));

        // Nom et Prénom
        JPanel namePanel = new JPanel(new GridLayout(1, 2, 15, 0));
        namePanel.setBackground(Color.WHITE);
        namePanel.setOpaque(false);
        namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        JPanel nomContainer = new JPanel();
        nomContainer.setLayout(new BoxLayout(nomContainer, BoxLayout.Y_AXIS));
        nomContainer.setOpaque(false);
        JLabel nomLabel = createStyledLabel("Nom");
        JTextField nomField = createStyledTextField();
        nomContainer.add(nomLabel);
        nomContainer.add(Box.createVerticalStrut(8));
        nomContainer.add(nomField);
        
        JPanel prenomContainer = new JPanel();
        prenomContainer.setLayout(new BoxLayout(prenomContainer, BoxLayout.Y_AXIS));
        prenomContainer.setOpaque(false);
        JLabel prenomLabel = createStyledLabel("Prénom");
        JTextField prenomField = createStyledTextField();
        prenomContainer.add(prenomLabel);
        prenomContainer.add(Box.createVerticalStrut(8));
        prenomContainer.add(prenomField);
        
        namePanel.add(nomContainer);
        namePanel.add(prenomContainer);
        card.add(namePanel);
        card.add(Box.createVerticalStrut(18));

        // Email
        JLabel emailLabel = createStyledLabel("Adresse Email");
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(8));
        JTextField emailField = createStyledTextField();
        card.add(emailField);
        card.add(Box.createVerticalStrut(18));

        // Password
        JLabel passwordLabel = createStyledLabel("Mot de passe");
        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(8));
        JPasswordField passwordField = createStyledPasswordField();
        card.add(passwordField);
        card.add(Box.createVerticalStrut(18));

        // Confirm Password
        JLabel confirmLabel = createStyledLabel("Confirmer le mot de passe");
        card.add(confirmLabel);
        card.add(Box.createVerticalStrut(8));
        JPasswordField confirmField = createStyledPasswordField();
        card.add(confirmField);
        card.add(Box.createVerticalStrut(18));

        // Compétence et Téléphone
        JPanel contactPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contactPanel.setOpaque(false);
        contactPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        JPanel compContainer = new JPanel();
        compContainer.setLayout(new BoxLayout(compContainer, BoxLayout.Y_AXIS));
        compContainer.setOpaque(false);
        JLabel compLabel = createStyledLabel("Compétence");
        JTextField competanceField = createStyledTextField();
        compContainer.add(compLabel);
        compContainer.add(Box.createVerticalStrut(8));
        compContainer.add(competanceField);
        
        JPanel telContainer = new JPanel();
        telContainer.setLayout(new BoxLayout(telContainer, BoxLayout.Y_AXIS));
        telContainer.setOpaque(false);
        JLabel telLabel = createStyledLabel("Téléphone");
        JTextField telephoneField = createStyledTextField();
        telContainer.add(telLabel);
        telContainer.add(Box.createVerticalStrut(8));
        telContainer.add(telephoneField);
        
        contactPanel.add(compContainer);
        contactPanel.add(telContainer);
        card.add(contactPanel);
        card.add(Box.createVerticalStrut(25));

        // Button
        JButton btnRegister = createStyledButton("Créer mon compte", accentColor);
        card.add(btnRegister);
        card.add(Box.createVerticalStrut(18));

        // Message
        JLabel message = new JLabel("", SwingConstants.CENTER);
        message.setFont(new Font("Segoe UI", Font.BOLD, 13));
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(message);
        card.add(Box.createVerticalStrut(15));

        // Switch to login
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        switchPanel.setOpaque(false);
        switchPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel switchText = new JLabel("Déjà un compte ?");
        switchText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        switchText.setForeground(textSecondary);
        
        JLabel switchLink = new JLabel("Se connecter");
        switchLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        switchLink.setForeground(accentColor);
        switchLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                contentLayout.show(mainContentPanel, "login");
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                switchLink.setForeground(accentColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                switchLink.setForeground(accentColor);
            }
        });
        
        switchPanel.add(switchText);
        switchPanel.add(switchLink);
        card.add(switchPanel);

        // Action
        btnRegister.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() || passwordField.getPassword().length == 0) {
                showMessage(message, "⚠ Veuillez remplir tous les champs obligatoires", errorColor);
                return;
            }

            if (!new String(passwordField.getPassword()).equals(new String(confirmField.getPassword()))) {
                showMessage(message, "✗ Les mots de passe ne correspondent pas", errorColor);
                return;
            }

            if (passwordField.getPassword().length < 6) {
                showMessage(message, "✗ Le mot de passe doit contenir au moins 6 caractères", errorColor);
                return;
            }

            btnRegister.setEnabled(false);
            btnRegister.setText("Création en cours...");
            message.setText("");

            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() throws Exception {
                    String json = "{"
                            + "\"nom\": \"" + nomField.getText() + "\","
                            + "\"prenom\": \"" + prenomField.getText() + "\","
                            + "\"email\": \"" + emailField.getText() + "\","
                            + "\"password\": \"" + new String(passwordField.getPassword()) + "\","
                            + "\"competance\": \"" + competanceField.getText() + "\","
                            + "\"telephone\": \"" + telephoneField.getText() + "\","
                            + "\"disponibilite\": true"
                            + "}";
                    return sendPOST(BASE_URL + "/register", json);
                }

                @Override
                protected void done() {
                    try {
                        String response = get();
                        if (response.contains("success") || response.contains("succès")) {
                            showMessage(message, "✓ Compte créé avec succès !", successColor);
                            
                            Timer timer = new Timer(1500, evt -> {
                                contentLayout.show(mainContentPanel, "login");
                                clearRegisterFields(nomField, prenomField, emailField, 
                                                  passwordField, confirmField, competanceField, 
                                                  telephoneField);
                                message.setText("");
                            });
                            timer.setRepeats(false);
                            timer.start();
                        } else {
                            showMessage(message, "✗ " + extractErrorMessage(response), errorColor);
                        }
                    } catch (Exception ex) {
                        showMessage(message, "✗ Erreur lors de la création du compte", errorColor);
                    } finally {
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Créer mon compte");
                    }
                }
            };
            worker.execute();
        });

        contentWrapper.add(card);

        JScrollPane scrollPane = new JScrollPane(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    // ================= UTILITAIRES UI ===================
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(textPrimary);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 42));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setBackground(cardBgColor);
        field.setForeground(textPrimary);
        field.setCaretColor(textPrimary);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(inputBorder, 1),
            new EmptyBorder(8, 14, 8, 14)
        ));
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 2),
                    new EmptyBorder(8, 14, 8, 14)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(inputBorder, 1),
                    new EmptyBorder(8, 14, 8, 14)
                ));
            }
        });
        
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 42));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setBackground(cardBgColor);
        field.setForeground(textPrimary);
        field.setCaretColor(textPrimary);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(inputBorder, 1),
            new EmptyBorder(8, 14, 8, 14)
        ));
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 2),
                    new EmptyBorder(8, 14, 8, 14)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(inputBorder, 1),
                    new EmptyBorder(8, 14, 8, 14)
                ));
            }
        });
        
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker().darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.darker());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(0, 48));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        return button;
    }

    private void showMessage(JLabel label, String text, Color color) {
        label.setText(text);
        label.setForeground(color);
    }

    private String extractErrorMessage(String response) {
        if (response.contains("error")) {
            int start = response.indexOf("\"error\"");
            if (start != -1) {
                int valueStart = response.indexOf(":", start) + 1;
                int valueEnd = response.indexOf("}", valueStart);
                if (valueEnd == -1) valueEnd = response.length();
                return response.substring(valueStart, valueEnd)
                    .replace("\"", "").trim();
            }
        }
        return response;
    }

    private String extractToken(String response) {
        if (response.contains("token")) {
            int start = response.indexOf("\"token\"");
            if (start != -1) {
                int valueStart = response.indexOf(":", start) + 2;
                int valueEnd = response.indexOf("\"", valueStart);
                if (valueEnd != -1) {
                    return response.substring(valueStart, valueEnd);
                }
            }
        }
        return null;
    }
    
    private String extractValue(String json, String key) {
        try {
            if (json == null || json.isEmpty()) {
                System.out.println("⚠️ JSON vide pour clé: " + key);
                return "";
            }
            
            String searchKey = "\"" + key + "\"";
            int keyIndex = json.indexOf(searchKey);
            
            if (keyIndex == -1) {
                System.out.println("⚠️ Clé non trouvée: " + key);
                return "";
            }
            
            int colonIndex = json.indexOf(":", keyIndex);
            if (colonIndex == -1) {
                System.out.println("⚠️ Pas de ':' après la clé: " + key);
                return "";
            }
            
            int valueStart = colonIndex + 1;
            while (valueStart < json.length() && 
                   (json.charAt(valueStart) == ' ' || json.charAt(valueStart) == '\t')) {
                valueStart++;
            }
            
            if (valueStart >= json.length()) {
                System.out.println("⚠️ Pas de valeur après ':' pour: " + key);
                return "";
            }
            
            boolean isString = json.charAt(valueStart) == '"';
            
            if (isString) {
                valueStart++;
                int valueEnd = json.indexOf("\"", valueStart);
                
                if (valueEnd == -1) {
                    System.out.println("⚠️ Guillemet de fermeture manquant pour: " + key);
                    return "";
                }
                
                String value = json.substring(valueStart, valueEnd);
                System.out.println("✓ Extrait " + key + " = " + value);
                return value;
            } else {
                int valueEnd = json.indexOf(",", valueStart);
                if (valueEnd == -1) {
                    valueEnd = json.indexOf("}", valueStart);
                }
                
                if (valueEnd == -1) {
                    System.out.println("⚠️ Fin de valeur non trouvée pour: " + key);
                    return "";
                }
                
                String value = json.substring(valueStart, valueEnd).trim();
                System.out.println("✓ Extrait " + key + " = " + value);
                return value;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur extraction de '" + key + "': " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    private void clearRegisterFields(JTextField nom, JTextField prenom, JTextField email,
                                     JPasswordField password, JPasswordField confirm,
                                     JTextField comp, JTextField tel) {
        nom.setText("");
        prenom.setText("");
        email.setText("");
        password.setText("");
        confirm.setText("");
        comp.setText("");
        tel.setText("");
    }

    // ========== MÉTHODE HTTP POST ==========
    private String sendPOST(String url, String jsonInput) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setDoOutput(true);
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        try (OutputStream os = con.getOutputStream()) {
            os.write(jsonInput.getBytes("UTF-8"));
            os.flush();
        }

        int responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        InputStreamReader streamReader;
        if (responseCode >= 200 && responseCode < 400) {
            streamReader = new InputStreamReader(con.getInputStream(), "UTF-8");
        } else {
            streamReader = new InputStreamReader(con.getErrorStream(), "UTF-8");
        }

        try (BufferedReader in = new BufferedReader(streamReader)) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            String result = response.toString();
            System.out.println("Response: " + result);
            return result;
        }
    }

    // Lancement
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new Auth().setVisible(true));
    }
}