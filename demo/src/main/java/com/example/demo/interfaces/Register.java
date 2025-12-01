package com.example.demo.interfaces;

import com.example.demo.components.Scrollbar;
import com.example.demo.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Register extends JPanel {
    private Auth parent;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, inputBorder, successColor, errorColor,
            inputBg, hoverColor;

    // Palette de couleurs premium (identique à Login.java)
    private final Color GRADIENT_START = new Color(20, 30, 48);
    private final Color GRADIENT_END = new Color(36, 59, 85);
    private final Color CARD_BG = new Color(255, 255, 255);
    private final Color PRIMARY_BLUE = new Color(59, 130, 246);
    private final Color SUCCESS_GREEN = new Color(16, 185, 129);
    private final Color ACCENT_ORANGE = new Color(251, 146, 60);
    private final Color TEXT_DARK = new Color(17, 24, 39);
    private final Color TEXT_LIGHT = new Color(107, 114, 128);
    private final Color INPUT_BG = new Color(249, 250, 251);
    private final Color BORDER_COLOR = new Color(229, 231, 235);

    public Register(Auth parent) {
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
        setLayout(new BorderLayout());
        setBackground(bgColor);
        initUI();
    }

    private void initUI() {
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(bgColor);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setOpaque(false);
        formCard.setBorder(new EmptyBorder(60, 60, 60, 60));
        formCard.setPreferredSize(new Dimension(520, 900));

        // Titre principal - centré
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Créer un compte");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        welcomeLabel.setForeground(TEXT_DARK);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createVerticalStrut(12));

        JLabel subtitleLabel = new JLabel("Rejoignez-nous en quelques étapes");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        subtitleLabel.setForeground(TEXT_LIGHT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(subtitleLabel);

        formCard.add(titlePanel);
        formCard.add(Box.createVerticalStrut(48));

        // Champs Nom et Prénom (côte à côte)
        JPanel nameRow = new JPanel(new GridLayout(1, 2, 16, 0));
        nameRow.setOpaque(false);
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        nameRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nom
        JPanel nomContainer = new JPanel();
        nomContainer.setLayout(new BoxLayout(nomContainer, BoxLayout.Y_AXIS));
        nomContainer.setOpaque(false);
        JLabel nomLabel = createLabel("Nom", PRIMARY_BLUE);
        JTextField nomField = createModernTextField("Votre nom", PRIMARY_BLUE);
        nomContainer.add(nomLabel);
        nomContainer.add(Box.createVerticalStrut(12));
        nomContainer.add(nomField);

        // Prénom
        JPanel prenomContainer = new JPanel();
        prenomContainer.setLayout(new BoxLayout(prenomContainer, BoxLayout.Y_AXIS));
        prenomContainer.setOpaque(false);
        JLabel prenomLabel = createLabel("Prénom", PRIMARY_BLUE);
        JTextField prenomField = createModernTextField("Votre prénom", PRIMARY_BLUE);
        prenomContainer.add(prenomLabel);
        prenomContainer.add(Box.createVerticalStrut(12));
        prenomContainer.add(prenomField);

        nameRow.add(nomContainer);
        nameRow.add(prenomContainer);
        formCard.add(nameRow);
        formCard.add(Box.createVerticalStrut(28));

        // Champ Email
        JPanel emailContainer = new JPanel();
        emailContainer.setLayout(new BoxLayout(emailContainer, BoxLayout.Y_AXIS));
        emailContainer.setOpaque(false);
        emailContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = createLabel("Adresse Email", SUCCESS_GREEN);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailContainer.add(emailLabel);
        emailContainer.add(Box.createVerticalStrut(12));

        JTextField emailField = createModernTextField("votre@email.com", SUCCESS_GREEN);
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailContainer.add(emailField);

        formCard.add(emailContainer);
        formCard.add(Box.createVerticalStrut(28));

        // Pass + Confirm
        JPanel psswdRow = new JPanel(new GridLayout(1, 2, 16, 0));
        psswdRow.setOpaque(false);
        psswdRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        psswdRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Champ Mot de passe
        JPanel passwordContainer = new JPanel();
        passwordContainer.setLayout(new BoxLayout(passwordContainer, BoxLayout.Y_AXIS));
        passwordContainer.setOpaque(false);
        passwordContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passwordLabel = createLabel("Mot de passe", ACCENT_ORANGE);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordContainer.add(passwordLabel);
        passwordContainer.add(Box.createVerticalStrut(12));

        JPasswordField passwordField = createModernPasswordField("", ACCENT_ORANGE);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordContainer.add(passwordField);

        // Confirm
        JPanel confirmContainer = new JPanel();
        confirmContainer.setLayout(new BoxLayout(confirmContainer, BoxLayout.Y_AXIS));
        confirmContainer.setOpaque(false);
        confirmContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel confirmLabel = createLabel("Confirmer le mot de passe", ACCENT_ORANGE);
        confirmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmContainer.add(confirmLabel);
        confirmContainer.add(Box.createVerticalStrut(12));

        JPasswordField confirmField = createModernPasswordField("", ACCENT_ORANGE);
        confirmField.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmContainer.add(confirmField);

        psswdRow.add(passwordContainer);
        psswdRow.add(confirmContainer);
        formCard.add(psswdRow);
        formCard.add(Box.createVerticalStrut(32));

        // Champs Compétence et Téléphone (côte à côte)
        JPanel contactRow = new JPanel(new GridLayout(1, 2, 16, 0));
        contactRow.setOpaque(false);
        contactRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        contactRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Compétence
        JPanel compContainer = new JPanel();
        compContainer.setLayout(new BoxLayout(compContainer, BoxLayout.Y_AXIS));
        compContainer.setOpaque(false);
        JLabel compLabel = createLabel("Compétence", PRIMARY_BLUE);
        JTextField competanceField = createModernTextField("Ex: Développeur", PRIMARY_BLUE);
        compContainer.add(compLabel);
        compContainer.add(Box.createVerticalStrut(12));
        compContainer.add(competanceField);

        // Téléphone
        JPanel telContainer = new JPanel();
        telContainer.setLayout(new BoxLayout(telContainer, BoxLayout.Y_AXIS));
        telContainer.setOpaque(false);
        JLabel telLabel = createLabel("Téléphone", SUCCESS_GREEN);
        JTextField telephoneField = createModernTextField("+212 6 12 34 56 78", SUCCESS_GREEN);
        telContainer.add(telLabel);
        telContainer.add(Box.createVerticalStrut(12));
        telContainer.add(telephoneField);

        contactRow.add(compContainer);
        contactRow.add(telContainer);
        formCard.add(contactRow);
        formCard.add(Box.createVerticalStrut(32));

        // Bouton d'inscription
        JButton btnRegister = createButtonNadya("Créer mon compte", PRIMARY_BLUE);
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(btnRegister);
        formCard.add(Box.createVerticalStrut(24));

        // Message de statut
        JLabel message = new JLabel("", SwingConstants.CENTER);
        message.setFont(new Font("Segoe UI", Font.BOLD, 15));
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(message);
        formCard.add(Box.createVerticalStrut(30));

        // Séparateur
        JPanel separatorContainer = new JPanel();
        separatorContainer.setLayout(new BoxLayout(separatorContainer, BoxLayout.X_AXIS));
        separatorContainer.setOpaque(false);
        separatorContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        JSeparator separator = new JSeparator();
        separator.setForeground(BORDER_COLOR);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separatorContainer.add(separator);
        formCard.add(separatorContainer);
        formCard.add(Box.createVerticalStrut(30));

        // Panel de switch
        JPanel switchPanel = createSwitchPanel(
                "Déjà un compte ?",
                "Se connecter",
                e -> parent.switchCard("login"));
        switchPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(switchPanel);

        btnRegister.addActionListener(e -> handleRegister(
                nomField, prenomField, emailField, passwordField, confirmField,
                competanceField, telephoneField, message, btnRegister));

        Scrollbar scroll = new Scrollbar(0);
        JScrollPane scrollPane = scroll.create(mainContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainContainer.add(formCard);
        scrollPane.setViewportView(mainContainer);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text, Color accentColor) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createModernTextField(String placeholder, Color accentColor) {
        JTextField field = new JTextField() {
            private boolean focused = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond avec dégradé subtil
                GradientPaint bgGradient = new GradientPaint(
                        0, 0, INPUT_BG,
                        0, getHeight(), new Color(246, 247, 249));
                g2.setPaint(bgGradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                // Bordure avec effet glow au focus
                if (focused) {
                    // Effet glow externe
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                    g2.setStroke(new BasicStroke(6f));
                    g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 16, 16);

                    // Bordure principale
                    g2.setColor(accentColor);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                } else {
                    g2.setColor(BORDER_COLOR);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                }

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void processFocusEvent(java.awt.event.FocusEvent e) {
                super.processFocusEvent(e);
                focused = (e.getID() == java.awt.event.FocusEvent.FOCUS_GAINED);
                repaint();
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(0, 58));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(accentColor);
        field.setBorder(new EmptyBorder(16, 22, 16, 22));
        field.setOpaque(false);
        return field;
    }

    private JPasswordField createModernPasswordField(String placeholder, Color accentColor) {
        JPasswordField field = new JPasswordField() {
            private boolean focused = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond avec dégradé subtil
                GradientPaint bgGradient = new GradientPaint(
                        0, 0, INPUT_BG,
                        0, getHeight(), new Color(246, 247, 249));
                g2.setPaint(bgGradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                // Bordure avec effet glow au focus
                if (focused) {
                    // Effet glow externe
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                    g2.setStroke(new BasicStroke(6f));
                    g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 16, 16);

                    // Bordure principale
                    g2.setColor(accentColor);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                } else {
                    g2.setColor(BORDER_COLOR);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                }

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void processFocusEvent(java.awt.event.FocusEvent e) {
                super.processFocusEvent(e);
                focused = (e.getID() == java.awt.event.FocusEvent.FOCUS_GAINED);
                repaint();
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(0, 58));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(accentColor);
        field.setBorder(new EmptyBorder(16, 22, 16, 22));
        field.setOpaque(false);
        return field;
    }

    private JButton createButtonNadya(String text, Color bgColor) {
        JButton button = new JButton(text) {
            private float hoverAlpha = 0f;
            private Timer hoverTimer;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        if (isEnabled())
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
                        hoverAlpha = Math.min(1f, hoverAlpha + 0.15f);
                    } else {
                        hoverAlpha = Math.max(0f, hoverAlpha - 0.15f);
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
                if (!isEnabled()) {
                    baseColor = new Color(156, 163, 175);
                } else if (getModel().isPressed()) {
                    baseColor = bgColor.darker();
                } else if (hoverAlpha > 0) {
                    float factor = 1 - (hoverAlpha * 0.15f);
                    int r = (int) (baseColor.getRed() * factor);
                    int g_ = (int) (baseColor.getGreen() * factor);
                    int b = (int) (baseColor.getBlue() * factor);
                    baseColor = new Color(Math.max(0, r), Math.max(0, g_), Math.max(0, b));
                }

                // Ombre du bouton progressive
                if (isEnabled()) {
                    g2.setColor(new Color(0, 0, 0, 35));
                    g2.fillRoundRect(0, 7, getWidth(), getHeight() - 7, 16, 16);
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fillRoundRect(0, 5, getWidth(), getHeight() - 5, 16, 16);
                }

                // Gradient sur le bouton
                GradientPaint gradient = new GradientPaint(
                        0, 0, baseColor,
                        0, getHeight(), new Color(
                                Math.max(0, baseColor.getRed() - 20),
                                Math.max(0, baseColor.getGreen() - 20),
                                Math.max(0, baseColor.getBlue() - 20)));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() - 6, 16, 16);

                // Highlight subtil en haut
                if (isEnabled() && !getModel().isPressed()) {
                    g2.setColor(new Color(255, 255, 255, 25));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2 - 3, 16, 16);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 17));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(0, 62));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private JPanel createSwitchPanel(String text, String linkText, java.awt.event.ActionListener action) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setForeground(TEXT_LIGHT);

        JLabel link = new JLabel(linkText);
        link.setFont(new Font("Segoe UI", Font.BOLD, 15));
        link.setForeground(PRIMARY_BLUE);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.actionPerformed(null);
            }

            public void mouseEntered(java.awt.event.MouseEvent e) {
                link.setForeground(PRIMARY_BLUE.darker());
                link.setText("<html><u>" + linkText + "</u></html>");
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                link.setForeground(PRIMARY_BLUE);
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
        Timer fadeTimer = new Timer(3500, e -> {
            Timer clearTimer = new Timer(40, evt -> {
                Color current = label.getForeground();
                int alpha = Math.max(0, current.getAlpha() - 12);
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

    private void handleRegister(JTextField nomField, JTextField prenomField, JTextField emailField,
            JPasswordField passwordField, JPasswordField confirmField,
            JTextField competanceField, JTextField telephoneField,
            JLabel message, JButton btnRegister) {
                
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            showMessage(message, "Email incorrect", new Color(239, 68, 68));
            return;
        }
        if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() || passwordField.getPassword().length == 0) {
            showMessage(message, "Veuillez remplir tous les champs obligatoires", new Color(239, 68, 68));
            return;
        }
        if (!new String(passwordField.getPassword()).equals(new String(confirmField.getPassword()))) {
            showMessage(message, "Les mots de passe ne correspondent pas", new Color(239, 68, 68));
            return;
        }
        if (passwordField.getPassword().length < 6) {
            showMessage(message, "Le mot de passe doit contenir au moins 6 caractères", new Color(239, 68, 68));
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
                return sendPOST("http://localhost:8080/auth/register", json);
            }

            @Override
            protected void done() {
                try {
                    String response = get();
                    if (response.contains("success") || response.contains("succès")) {
                        showMessage(message, "Compte créé avec succès !", SUCCESS_GREEN);
                        Timer timer = new Timer(1200, evt -> {
                            parent.switchCard("login");
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showMessage(message, extractErrorMessage(response), new Color(239, 68, 68));
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Créer mon compte");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showMessage(message, "Erreur lors de la création du compte", new Color(239, 68, 68));
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Créer mon compte");
                }
            }
        };
        worker.execute();
    }

    private String extractErrorMessage(String response) {
        if (response.contains("error")) {
            int start = response.indexOf("\"error\"");
            if (start != -1) {
                int valueStart = response.indexOf(":", start) + 1;
                int valueEnd = response.indexOf("}", valueStart);
                if (valueEnd == -1)
                    valueEnd = response.length();
                return response.substring(valueStart, valueEnd)
                        .replace("\"", "").trim();
            }
        }
        return response;
    }

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
            return response.toString();
        }
    }
}