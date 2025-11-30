package com.example.demo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Auth extends JFrame {

    private static final String BASE_URL = "http://localhost:8080/auth";
    private JTabbedPane tabs;
    private int userId = -1;
    
    // Couleurs modernes
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246); // Bleu moderne
    private static final Color SECONDARY_COLOR = new Color(30, 64, 175); // Bleu foncé
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94); // Vert
    private static final Color ERROR_COLOR = new Color(239, 68, 68); // Rouge
    private static final Color BG_COLOR = new Color(249, 250, 251); // Gris très clair
    private static final Color CARD_COLOR = Color.WHITE;

    public Auth() {
        setTitle("Authentification - Gestion des Utilisateurs");
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(CARD_COLOR);
        
        tabs.addTab("  Connexion  ", loginPanel());
        tabs.addTab("  Inscription  ", registerPanel());

        add(tabs);
    }

    // ================= LOGIN PANEL ===================
    private JPanel loginPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));

        // Titre
        JLabel title = new JLabel("Bienvenue");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(SECONDARY_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        JLabel subtitle = new JLabel("Connectez-vous à votre compte");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(30));

        // Email
        JLabel emailLabel = createStyledLabel("Adresse Email");
        panel.add(emailLabel);
        panel.add(Box.createVerticalStrut(8));
        
        JTextField emailField = createStyledTextField();
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(20));

        // Password
        JLabel passwordLabel = createStyledLabel("Mot de passe");
        panel.add(passwordLabel);
        panel.add(Box.createVerticalStrut(8));
        
        JPasswordField passwordField = createStyledPasswordField();
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(25));

        // Button
        JButton btnLogin = createStyledButton("Se connecter", PRIMARY_COLOR);
        panel.add(btnLogin);
        panel.add(Box.createVerticalStrut(15));

        // Message
        JLabel message = new JLabel("", SwingConstants.CENTER);
        message.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(message);

        // Action
        btnLogin.addActionListener(e -> {
            if (emailField.getText().trim().isEmpty() || passwordField.getPassword().length == 0) {
                showMessage(message, "Veuillez remplir tous les champs", ERROR_COLOR);
                return;
            }

            btnLogin.setEnabled(false);
            btnLogin.setText("Connexion...");

            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() throws Exception {
                    String json = "{ \"email\": \"" + emailField.getText() + "\", " +
                            "\"password\": \"" + new String(passwordField.getPassword()) + "\" }";
                    return sendPOST(BASE_URL + "/login", json);
                }

                // Dans la méthode loginPanel(), dans le SwingWorker, modifiez done() :

                @Override
                protected void done() {
                    try {
                        String response = get();
                        
                        // ⭐ AJOUTEZ CES LIGNES POUR DEBUG
                        System.out.println("========================================");
                        System.out.println("RÉPONSE COMPLÈTE DE L'API:");
                        System.out.println(response);
                        System.out.println("========================================");
                        
                        if (response.contains("success") && response.contains("token")) {
                            // Extraire toutes les informations
                            String token = extractToken(response);
                            String prenom = extractValue(response, "prenom");
                            String nom = extractValue(response, "nom");
                            String email = extractValue(response, "email");
                            String competance = extractValue(response, "competance");
                            String telephone = extractValue(response, "telephone");
                            
                            // DEBUG : Afficher ce qui est extrait
                            System.out.println("--- EXTRACTION ---");
                            System.out.println("Token extrait: " + token);
                            System.out.println("Prénom extrait: " + prenom);
                            System.out.println("Nom extrait: " + nom);
                            System.out.println("Email extrait: " + email);
                            System.out.println("Compétence extraite: " + competance);
                            System.out.println("Téléphone extrait: " + telephone);
                            System.out.println("------------------");
                            
                            // Extraire l'ID utilisateur
                            String userIdStr = extractValue(response, "id");
                            try {
                                if (userIdStr != null && !userIdStr.isEmpty()) {
                                    userId = Integer.parseInt(userIdStr);
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Erreur conversion userId: " + e.getMessage());
                            }
                            
                            // Sauvegarder dans la session
                            if (token != null && !token.equals("session-active")) {
                                TokenManager.saveToken(token);
                            }
                            
                            SessionManager.getInstance().setUserSession(
                                token, prenom, nom, email, competance, telephone, userId
                            );
                            
                            // Afficher les infos
                            SessionManager.getInstance().printSessionInfo();
                            
                            showMessage(message, "✓ Connexion réussie !", SUCCESS_COLOR);
                            
                            Timer timer = new Timer(1000, evt -> {
                                dispose();
                                SwingUtilities.invokeLater(() -> {
                                    Main mainGUI = new Main(userId, prenom, nom);
                                    mainGUI.setVisible(true);
                                });
                            });
                            timer.setRepeats(false);
                            timer.start();
                        } else {
                            showMessage(message, "✗ " + extractErrorMessage(response), ERROR_COLOR);
                            btnLogin.setEnabled(true);
                            btnLogin.setText("Se connecter");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace(); // Afficher l'erreur complète
                        showMessage(message, "✗ Erreur de connexion", ERROR_COLOR);
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Se connecter");
                    }
                }
            };
            worker.execute();
        });

        mainPanel.add(panel, BorderLayout.CENTER);
        return mainPanel;
    }

    // ================= REGISTER PANEL ===================
    private JPanel registerPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Titre
        JLabel title = new JLabel("Créer un compte");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(SECONDARY_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        JLabel subtitle = new JLabel("Remplissez les informations ci-dessous");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(30));

        // Nom et Prénom (côte à côte)
        JPanel namePanel = new JPanel(new GridLayout(1, 2, 15, 0));
        namePanel.setBackground(CARD_COLOR);
        namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        JPanel nomContainer = new JPanel();
        nomContainer.setLayout(new BoxLayout(nomContainer, BoxLayout.Y_AXIS));
        nomContainer.setBackground(CARD_COLOR);
        JLabel nomLabel = createStyledLabel("Nom");
        JTextField nomField = createStyledTextField();
        nomContainer.add(nomLabel);
        nomContainer.add(Box.createVerticalStrut(8));
        nomContainer.add(nomField);
        
        JPanel prenomContainer = new JPanel();
        prenomContainer.setLayout(new BoxLayout(prenomContainer, BoxLayout.Y_AXIS));
        prenomContainer.setBackground(CARD_COLOR);
        JLabel prenomLabel = createStyledLabel("Prénom");
        JTextField prenomField = createStyledTextField();
        prenomContainer.add(prenomLabel);
        prenomContainer.add(Box.createVerticalStrut(8));
        prenomContainer.add(prenomField);
        
        namePanel.add(nomContainer);
        namePanel.add(prenomContainer);
        panel.add(namePanel);
        panel.add(Box.createVerticalStrut(20));

        // Email
        JLabel emailLabel = createStyledLabel("Adresse Email");
        panel.add(emailLabel);
        panel.add(Box.createVerticalStrut(8));
        JTextField emailField = createStyledTextField();
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(20));

        // Password
        JLabel passwordLabel = createStyledLabel("Mot de passe");
        panel.add(passwordLabel);
        panel.add(Box.createVerticalStrut(8));
        JPasswordField passwordField = createStyledPasswordField();
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(20));

        // Confirm Password
        JLabel confirmLabel = createStyledLabel("Confirmer le mot de passe");
        panel.add(confirmLabel);
        panel.add(Box.createVerticalStrut(8));
        JPasswordField confirmField = createStyledPasswordField();
        panel.add(confirmField);
        panel.add(Box.createVerticalStrut(20));

        // Compétence et Téléphone (côte à côte)
        JPanel contactPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contactPanel.setBackground(CARD_COLOR);
        contactPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        JPanel compContainer = new JPanel();
        compContainer.setLayout(new BoxLayout(compContainer, BoxLayout.Y_AXIS));
        compContainer.setBackground(CARD_COLOR);
        JLabel compLabel = createStyledLabel("Compétence");
        JTextField competanceField = createStyledTextField();
        compContainer.add(compLabel);
        compContainer.add(Box.createVerticalStrut(8));
        compContainer.add(competanceField);
        
        JPanel telContainer = new JPanel();
        telContainer.setLayout(new BoxLayout(telContainer, BoxLayout.Y_AXIS));
        telContainer.setBackground(CARD_COLOR);
        JLabel telLabel = createStyledLabel("Téléphone");
        JTextField telephoneField = createStyledTextField();
        telContainer.add(telLabel);
        telContainer.add(Box.createVerticalStrut(8));
        telContainer.add(telephoneField);
        
        contactPanel.add(compContainer);
        contactPanel.add(telContainer);
        panel.add(contactPanel);
        panel.add(Box.createVerticalStrut(20));

        // Disponibilité supprimée (par défaut true)
        panel.add(Box.createVerticalStrut(5));

        // Button
        JButton btnRegister = createStyledButton("Créer mon compte", PRIMARY_COLOR);
        panel.add(btnRegister);
        panel.add(Box.createVerticalStrut(15));

        // Message
        JLabel message = new JLabel("", SwingConstants.CENTER);
        message.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(message);

        // Action
        btnRegister.addActionListener(e -> {
            // Validation
            if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() || passwordField.getPassword().length == 0) {
                showMessage(message, "✗ Veuillez remplir tous les champs obligatoires", ERROR_COLOR);
                return;
            }

            if (!new String(passwordField.getPassword()).equals(new String(confirmField.getPassword()))) {
                showMessage(message, "✗ Les mots de passe ne correspondent pas", ERROR_COLOR);
                return;
            }

            if (passwordField.getPassword().length < 6) {
                showMessage(message, "✗ Le mot de passe doit contenir au moins 6 caractères", ERROR_COLOR);
                return;
            }

            btnRegister.setEnabled(false);
            btnRegister.setText("Création en cours...");

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
                            showMessage(message, "✓ Compte créé avec succès !", SUCCESS_COLOR);
                            
                            // Attendre 1.5 secondes puis rediriger vers login
                            Timer timer = new Timer(1500, evt -> {
                                tabs.setSelectedIndex(0); // Aller à l'onglet Login
                                clearRegisterFields(nomField, prenomField, emailField, 
                                                  passwordField, confirmField, competanceField, 
                                                  telephoneField);
                                message.setText("");
                            });
                            timer.setRepeats(false);
                            timer.start();
                        } else {
                            showMessage(message, "✗ " + extractErrorMessage(response), ERROR_COLOR);
                        }
                    } catch (Exception ex) {
                        showMessage(message, "✗ Erreur lors de la création du compte", ERROR_COLOR);
                    } finally {
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Créer mon compte");
                    }
                }
            };
            worker.execute();
        });

        scrollPane.setViewportView(panel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    // ================= UTILITAIRES UI ===================
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(55, 65, 81));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(5, 12, 5, 12)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(5, 12, 5, 12)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(0, 45));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
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
        
        // Chercher la clé
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        
        if (keyIndex == -1) {
            System.out.println("⚠️ Clé non trouvée: " + key);
            return "";
        }
        
        // Trouver le début de la valeur (après les ':')
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) {
            System.out.println("⚠️ Pas de ':' après la clé: " + key);
            return "";
        }
        
        // Ignorer les espaces après ':'
        int valueStart = colonIndex + 1;
        while (valueStart < json.length() && 
               (json.charAt(valueStart) == ' ' || json.charAt(valueStart) == '\t')) {
            valueStart++;
        }
        
        if (valueStart >= json.length()) {
            System.out.println("⚠️ Pas de valeur après ':' pour: " + key);
            return "";
        }
        
        // Vérifier si c'est une string (commence par ")
        boolean isString = json.charAt(valueStart) == '"';
        
        if (isString) {
            // Pour les strings, trouver le " de fermeture
            valueStart++; // Sauter le " d'ouverture
            int valueEnd = json.indexOf("\"", valueStart);
            
            if (valueEnd == -1) {
                System.out.println("⚠️ Guillemet de fermeture manquant pour: " + key);
                return "";
            }
            
            String value = json.substring(valueStart, valueEnd);
            System.out.println("✓ Extrait " + key + " = " + value);
            return value;
        } else {
            // Pour les nombres ou booléens, trouver la virgule ou accolade
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