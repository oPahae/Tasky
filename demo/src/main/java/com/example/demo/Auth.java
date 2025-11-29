package com.example.demo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class Auth extends JPanel {
    // Login fields
    private JTextField loginEmail;
    private JPasswordField loginPassword;

    // Register fields
    private JTextField regNom;
    private JTextField regPrenom;
    private JTextField regEmail;
    private JPasswordField regPassword;
    private JPasswordField regConfirmPassword;
    private JTextField regCompetance;
    private JTextField regTelephone;
   //private JCheckBox regDisponibilite;

    // HTTP client & base url (adapte si nécessaire)
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String baseUrl = "http://localhost:8080"; // <- adapte si besoin

    public Auth() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JLabel title = new JLabel("Authentification");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // Tabs : Login / Register
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Login", createLoginPanel());
        tabs.addTab("Register", createRegisterPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = commonConstraints();

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color fieldBackground = new Color(255, 255, 255);
        int fieldHeight = 40;

        // Email
        JLabel lblEmail = new JLabel("Email :");
        lblEmail.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblEmail, gbc);

        loginEmail = new JTextField(20);
        loginEmail.setFont(fieldFont);
        loginEmail.setBackground(fieldBackground);
        loginEmail.setPreferredSize(new Dimension(0, fieldHeight));
        gbc.gridx = 1;
        panel.add(loginEmail, gbc);

        // Password
        JLabel lblPass = new JLabel("Mot de passe :");
        lblPass.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblPass, gbc);

        loginPassword = new JPasswordField(20);
        loginPassword.setFont(fieldFont);
        loginPassword.setBackground(fieldBackground);
        loginPassword.setPreferredSize(new Dimension(0, fieldHeight));
        gbc.gridx = 1;
        panel.add(loginPassword, gbc);

        // Login button
        JButton btnLogin = new JButton("Connexion");
        styleButton(btnLogin);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> doLogin());

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = commonConstraints();

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color fieldBackground = new Color(255, 255, 255);
        int fieldHeight = 36;

        // Nom
        JLabel lblNom = new JLabel("Nom :");
        lblNom.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblNom, gbc);

        regNom = new JTextField(15);
        regNom.setFont(fieldFont);
        regNom.setBackground(fieldBackground);
        regNom.setPreferredSize(new Dimension(0, fieldHeight));
        gbc.gridx = 1;
        panel.add(regNom, gbc);

        // Prenom
        JLabel lblPrenom = new JLabel("Prénom :");
        lblPrenom.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblPrenom, gbc);

        regPrenom = new JTextField(15);
        regPrenom.setFont(fieldFont);
        regPrenom.setBackground(fieldBackground);
        regPrenom.setPreferredSize(new Dimension(0, fieldHeight));
        gbc.gridx = 1;
        panel.add(regPrenom, gbc);

        // Email
        JLabel lblEmail = new JLabel("Email :");
        lblEmail.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblEmail, gbc);

        regEmail = new JTextField(15);
        regEmail.setFont(fieldFont);
        regEmail.setBackground(fieldBackground);
        regEmail.setPreferredSize(new Dimension(0, fieldHeight));
        gbc.gridx = 1;
        panel.add(regEmail, gbc);

        // Password
        JLabel lblPass = new JLabel("Mot de passe :");
        lblPass.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblPass, gbc);

        regPassword = new JPasswordField(15);
        regPassword.setFont(fieldFont);
        regPassword.setBackground(fieldBackground);
        regPassword.setPreferredSize(new Dimension(0, fieldHeight));
        gbc.gridx = 1;
        panel.add(regPassword, gbc);

        // Confirm password
        JLabel lblConfirm = new JLabel("Confirmer mot de passe :");
        lblConfirm.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(lblConfirm, gbc);

        regConfirmPassword = new JPasswordField(15);
        regConfirmPassword.setFont(fieldFont);
        regConfirmPassword.setBackground(fieldBackground);
        regConfirmPassword.setPreferredSize(new Dimension(0, fieldHeight));
        gbc.gridx = 1;
        panel.add(regConfirmPassword, gbc);

        // Competance
        JLabel lblCompet = new JLabel("Compétance :");
        lblCompet.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(lblCompet, gbc);

        regCompetance = new JTextField(15);
        regCompetance.setFont(fieldFont);
        regCompetance.setBackground(fieldBackground);
        regCompetance.setPreferredSize(new Dimension(0, fieldHeight));
        gbc.gridx = 1;
        panel.add(regCompetance, gbc);

        // Telephone
        JLabel lblTelephone = new JLabel("Téléphone :");
        lblTelephone.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(lblTelephone, gbc);

        regTelephone = new JTextField(15);
        regTelephone.setFont(fieldFont);
        regTelephone.setBackground(fieldBackground);
        regTelephone.setPreferredSize(new Dimension(0, fieldHeight));
        gbc.gridx = 1;
        panel.add(regTelephone, gbc);

        // Disponibilité
        // JLabel lblDisp = new JLabel("Disponible :");
        // lblDisp.setFont(labelFont);
        // gbc.gridx = 0;
        // gbc.gridy = 7;
        // panel.add(lblDisp, gbc);

        // regDisponibilite = new JCheckBox();
        // regDisponibilite.setOpaque(false);
        // gbc.gridx = 1;
        // panel.add(regDisponibilite, gbc);

        // Register button
        JButton btnRegister = new JButton("S'inscrire");
        styleButton(btnRegister);
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnRegister, gbc);

        btnRegister.addActionListener(e -> doRegister());

        return panel;
    }

    private GridBagConstraints commonConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(255, 255, 255));
        btn.setBackground(new Color(0, 120, 212));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ---- HTTP helpers ----
    private CompletableFuture<HttpResponse<String>> sendPostAsync(String url, String json) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPost(String url, String json) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    }

    // ---- Actions ----
    private void doRegister() {
        String nom = regNom.getText().trim();
        String prenom = regPrenom.getText().trim();
        String email = regEmail.getText().trim();
        String password = new String(regPassword.getPassword());
        String confirm = new String(regConfirmPassword.getPassword());
        String competance = regCompetance.getText().trim();
        String telStr = regTelephone.getText().trim();
        //boolean dispon = regDisponibilite.isSelected();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int telephone = 0;
        try {
            if (!telStr.isEmpty()) telephone = Integer.parseInt(telStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Le numéro de téléphone doit être un nombre.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Construire JSON (adapte les noms si ton DTO est différent)
        String json = "{"
                + "\"nom\":\"" + escapeJson(nom) + "\","
                + "\"prenom\":\"" + escapeJson(prenom) + "\","
                + "\"email\":\"" + escapeJson(email) + "\","
                + "\"password\":\"" + escapeJson(password) + "\","
                + "\"confirmPassword\":\"" + escapeJson(confirm) + "\","
                + "\"competance\":\"" + escapeJson(competance) + "\","
                + "\"telephone\":" + telephone //+ ","
               // + "\"disponibilite\":" + dispon
                + "}";

        String url = baseUrl + "/auth/register";

        // Envoi synchrone (facile à déboguer). Tu peux utiliser sendPostAsync si tu veux.
        try {
            HttpResponse<String> resp = sendPost(url, json);
            int code = resp.statusCode();
            String body = resp.body();

            if (code >= 200 && code < 300) {
                JOptionPane.showMessageDialog(this, "Inscription réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Affiche le message renvoyé par le backend si présent
                JOptionPane.showMessageDialog(this, "Erreur inscription : " + body, "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur réseau : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doLogin() {
        String email = loginEmail.getText().trim();
        String password = new String(loginPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir l'email et le mot de passe.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String json = "{"
                + "\"email\":\"" + escapeJson(email) + "\","
                + "\"password\":\"" + escapeJson(password) + "\""
                + "}";

        String url = baseUrl + "/auth/login";

        // Envoi synchrone
        try {
            HttpResponse<String> resp = sendPost(url, json);
            int code = resp.statusCode();
            String body = resp.body();

            if (code >= 200 && code < 300) {
                // On attend un JSON du type {"token":"..."}
                String token = extractTokenFromBody(body);
                JOptionPane.showMessageDialog(this, "Connexion réussie ! Token: " + (token != null ? token.substring(0, Math.min(30, token.length())) + "..." : ""), "Succès", JOptionPane.INFORMATION_MESSAGE);

                // fermer la fenetre actuelle et ouvrir Main (tu peux modifier Main pour accepter token)
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(Auth.this);
                currentFrame.dispose();

                SwingUtilities.invokeLater(() -> {
                    // si ta classe Main accepte un token, utilise: new Main(token);
                    Main mainGUI = new Main();
                    mainGUI.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this, "Erreur login : " + body, "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur réseau : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // simple extraction du token depuis {"token":"..."} sans dépendance externe
    private String extractTokenFromBody(String body) {
        if (body == null) return null;
        body = body.trim();
        // recherche naive "token":"value"
        String key = "\"token\"";
        int idx = body.indexOf(key);
        if (idx == -1) return null;
        int colon = body.indexOf(":", idx);
        if (colon == -1) return null;
        int firstQuote = body.indexOf("\"", colon);
        if (firstQuote == -1) return null;
        int secondQuote = body.indexOf("\"", firstQuote + 1);
        if (secondQuote == -1) return null;
        return body.substring(firstQuote + 1, secondQuote);
    }

    // échappe quelques caractères pour éviter de casser le JSON (très simple)
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    // main pour lancer l'UI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Login / Register");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Auth());
            frame.setSize(800, 520);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
