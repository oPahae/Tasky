package com.example.demo.interfaces;

import com.example.demo.components.Scrollbar;
import com.example.demo.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

public class Profile extends JPanel {
    private int theme;
    private int userID;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, inputBg, borderColor;

    // Palette de couleurs premium
    private final Color primary = new Color(59, 130, 246);
    private final Color sucess = new Color(16, 185, 129);
    private final Color alert = new Color(251, 146, 60);
    private final Color danger = new Color(239, 68, 68);

    // Champs du formulaire
    private JTextField nomField, prenomField, emailField, competanceField, telephoneField;
    private JPasswordField currentPasswordField, newPasswordField, confirmPasswordField;
    private JLabel profileMessage, passwordMessage, deleteMessage;
    private JButton btnUpdateProfile, btnUpdatePassword, btnDeleteAccount;

    public Profile() {
        this.theme = Params.theme;
        this.userID = SessionManager.getInstance().getUserId();
        
        System.out.println("=== PROFILE INITIALIZATION ===");
        System.out.println("User ID: " + userID);
        System.out.println("Theme: " + theme);
        
        initializeColors();

        setLayout(new BorderLayout());
        setBackground(bgColor);
        initUI();
        loadUserData();
    }

    private void initializeColors() {
        if (theme == 0) {
            bgColor = new Color(255, 255, 255);
            cardBgColor = new Color(255, 255, 255);
            textPrimary = new Color(17, 24, 39);
            textSecondary = new Color(107, 114, 128);
            inputBg = new Color(249, 250, 251);
            borderColor = new Color(229, 231, 235);
        } else {
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(30, 41, 59);
            textPrimary = new Color(243, 244, 246);
            textSecondary = new Color(148, 163, 184);
            inputBg = new Color(51, 65, 85);
            borderColor = new Color(71, 85, 105);
        }
    }

    private void initUI() {
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(bgColor);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setOpaque(false);
        formCard.setBorder(new EmptyBorder(40, 60, 60, 60));
        formCard.setPreferredSize(new Dimension(650, 1400));

        // Titre principal
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Mon Profil");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        welcomeLabel.setForeground(textPrimary);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createVerticalStrut(12));

        JLabel subtitleLabel = new JLabel("Gérez vos informations personnelles");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        subtitleLabel.setForeground(textSecondary);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(subtitleLabel);

        formCard.add(titlePanel);
        formCard.add(Box.createVerticalStrut(48));

        // === SECTION 1: Informations Personnelles ===
        formCard.add(createSectionTitle("Informations Personnelles", primary));
        formCard.add(Box.createVerticalStrut(24));

        // Nom et Prénom
        JPanel nameRow = new JPanel(new GridLayout(1, 2, 16, 0));
        nameRow.setOpaque(false);
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        JPanel nomContainer = createFieldContainer("Nom", primary);
        nomField = createModernTextField("Votre nom", primary);
        nomContainer.add(nomField);
        nameRow.add(nomContainer);

        JPanel prenomContainer = createFieldContainer("Prénom", primary);
        prenomField = createModernTextField("Votre prénom", primary);
        prenomContainer.add(prenomField);
        nameRow.add(prenomContainer);

        formCard.add(nameRow);
        formCard.add(Box.createVerticalStrut(24));

        // Email
        JPanel emailContainer = createFieldContainer("Adresse Email", sucess);
        emailField = createModernTextField("votre@email.com", sucess);
        emailContainer.add(emailField);
        formCard.add(emailContainer);
        formCard.add(Box.createVerticalStrut(24));

        // Compétence et Téléphone
        JPanel contactRow = new JPanel(new GridLayout(1, 2, 16, 0));
        contactRow.setOpaque(false);
        contactRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        JPanel compContainer = createFieldContainer("Compétence", primary);
        competanceField = createModernTextField("Ex: Développeur", primary);
        compContainer.add(competanceField);
        contactRow.add(compContainer);

        JPanel telContainer = createFieldContainer("Téléphone", sucess);
        telephoneField = createModernTextField("+212 6 12 34 56 78", sucess);
        telContainer.add(telephoneField);
        contactRow.add(telContainer);

        formCard.add(contactRow);
        formCard.add(Box.createVerticalStrut(28));

        // Bouton de mise à jour du profil
        btnUpdateProfile = createButtonNadya("Mettre à jour le profil", primary);
        btnUpdateProfile.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUpdateProfile.addActionListener(e -> handleUpdateProfile());
        formCard.add(btnUpdateProfile);
        formCard.add(Box.createVerticalStrut(16));

        // Message de statut profil
        profileMessage = new JLabel("", SwingConstants.CENTER);
        profileMessage.setFont(new Font("Segoe UI", Font.BOLD, 15));
        profileMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(profileMessage);
        formCard.add(Box.createVerticalStrut(48));

        // Séparateur
        formCard.add(createSeparator());
        formCard.add(Box.createVerticalStrut(48));

        // === SECTION 2: Modification du Mot de Passe ===
        formCard.add(createSectionTitle("Modifier le Mot de Passe", alert));
        formCard.add(Box.createVerticalStrut(24));

        // Mot de passe actuel
        JPanel currentPasswordContainer = createFieldContainer("Mot de passe actuel", alert);
        currentPasswordField = createModernPasswordField("", alert);
        currentPasswordContainer.add(currentPasswordField);
        formCard.add(currentPasswordContainer);
        formCard.add(Box.createVerticalStrut(24));

        // Nouveau mot de passe et confirmation
        JPanel passwordRow = new JPanel(new GridLayout(1, 2, 16, 0));
        passwordRow.setOpaque(false);
        passwordRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        JPanel newPasswordContainer = createFieldContainer("Nouveau mot de passe", alert);
        newPasswordField = createModernPasswordField("", alert);
        newPasswordContainer.add(newPasswordField);
        passwordRow.add(newPasswordContainer);

        JPanel confirmPasswordContainer = createFieldContainer("Confirmer le mot de passe", alert);
        confirmPasswordField = createModernPasswordField("", alert);
        confirmPasswordContainer.add(confirmPasswordField);
        passwordRow.add(confirmPasswordContainer);

        formCard.add(passwordRow);
        formCard.add(Box.createVerticalStrut(28));

        // Bouton de mise à jour du mot de passe
        btnUpdatePassword = createButtonNadya("Modifier le mot de passe", alert);
        btnUpdatePassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUpdatePassword.addActionListener(e -> handleUpdatePassword());
        formCard.add(btnUpdatePassword);
        formCard.add(Box.createVerticalStrut(16));

        // Message de statut mot de passe
        passwordMessage = new JLabel("", SwingConstants.CENTER);
        passwordMessage.setFont(new Font("Segoe UI", Font.BOLD, 15));
        passwordMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(passwordMessage);
        formCard.add(Box.createVerticalStrut(48));

        // Séparateur
        formCard.add(createSeparator());
        formCard.add(Box.createVerticalStrut(48));

        // === SECTION 3: Suppression du Compte ===
        formCard.add(createSectionTitle("Zone Dangereuse", danger));
        formCard.add(Box.createVerticalStrut(16));

        JLabel dangerWarning = new JLabel("La suppression de votre compte est irréversible");
        dangerWarning.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        dangerWarning.setForeground(textSecondary);
        dangerWarning.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(dangerWarning);
        formCard.add(Box.createVerticalStrut(24));

        // Bouton de suppression du compte
        btnDeleteAccount = createButtonNadya("Supprimer mon compte", danger);
        btnDeleteAccount.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDeleteAccount.addActionListener(e -> handleDeleteAccount());
        formCard.add(btnDeleteAccount);
        formCard.add(Box.createVerticalStrut(16));

        // Message de statut suppression
        deleteMessage = new JLabel("", SwingConstants.CENTER);
        deleteMessage.setFont(new Font("Segoe UI", Font.BOLD, 15));
        deleteMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(deleteMessage);

        // Scrollbar
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

    private JPanel createSectionTitle(String title, Color accentColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 26));
        label.setForeground(textPrimary);

        JPanel colorBar = new JPanel();
        colorBar.setBackground(accentColor);
        colorBar.setPreferredSize(new Dimension(4, 30));
        colorBar.setMaximumSize(new Dimension(4, 30));

        panel.add(colorBar);
        panel.add(Box.createHorizontalStrut(16));
        panel.add(label);
        panel.add(Box.createHorizontalGlue());

        return panel;
    }

    private JPanel createFieldContainer(String labelText, Color accentColor) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(textPrimary);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(label);
        container.add(Box.createVerticalStrut(12));

        return container;
    }

    private JPanel createSeparator() {
        JPanel separatorContainer = new JPanel();
        separatorContainer.setLayout(new BoxLayout(separatorContainer, BoxLayout.X_AXIS));
        separatorContainer.setOpaque(false);
        separatorContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        JSeparator separator = new JSeparator();
        separator.setForeground(borderColor);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separatorContainer.add(separator);
        return separatorContainer;
    }

    private JTextField createModernTextField(String placeholder, Color accentColor) {
        JTextField field = new JTextField() {
            private boolean focused = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(inputBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                if (focused) {
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                    g2.setStroke(new BasicStroke(6f));
                    g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 16, 16);

                    g2.setColor(accentColor);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                } else {
                    g2.setColor(borderColor);
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
        field.setBackground(inputBg);
        field.setForeground(textPrimary);
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

                g2.setColor(inputBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                if (focused) {
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                    g2.setStroke(new BasicStroke(6f));
                    g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 16, 16);

                    g2.setColor(accentColor);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                } else {
                    g2.setColor(borderColor);
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
        field.setBackground(inputBg);
        field.setForeground(textPrimary);
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

                if (isEnabled()) {
                    g2.setColor(new Color(0, 0, 0, 35));
                    g2.fillRoundRect(0, 7, getWidth(), getHeight() - 7, 16, 16);
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fillRoundRect(0, 5, getWidth(), getHeight() - 5, 16, 16);
                }

                g2.setColor(baseColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() - 6, 16, 16);

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

    private void loadUserData() {
        System.out.println("=== LOADING USER DATA ===");
        System.out.println("Requesting: /api/profile/" + userID);
        
        Queries.get("/api/profile/" + userID).thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                System.out.println("Response received: " + response);
                
                if (response.containsKey("success") && Boolean.TRUE.equals(response.get("success"))) {
                    // Conversion sécurisée des valeurs
                    nomField.setText(String.valueOf(response.getOrDefault("nom", "")));
                    prenomField.setText(String.valueOf(response.getOrDefault("prenom", "")));
                    emailField.setText(String.valueOf(response.getOrDefault("email", "")));
                    competanceField.setText(String.valueOf(response.getOrDefault("competance", "")));
                    telephoneField.setText(String.valueOf(response.getOrDefault("telephone", "")));
                    
                    System.out.println("✓ Données chargées avec succès");
                } else if (response.containsKey("error")) {
                    String errorMsg = String.valueOf(response.get("error"));
                    System.err.println("✗ Erreur: " + errorMsg);
                    showMessage(profileMessage, "Erreur: " + errorMsg, danger);
                } else {
                    System.err.println("✗ Réponse invalide du serveur");
                    showMessage(profileMessage, "Erreur de chargement du profil", danger);
                }
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                System.err.println("✗ Exception: " + ex.getMessage());
                ex.printStackTrace();
                showMessage(profileMessage, "Erreur de connexion au serveur", danger);
            });
            return null;
        });
    }

    private void handleUpdateProfile() {
        System.out.println("=== UPDATE PROFILE ===");
        
        // Validation email
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            showMessage(profileMessage, "Email incorrect", danger);
            return;
        }
        
        // Validation champs obligatoires
        if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty()) {
            showMessage(profileMessage, "Veuillez remplir tous les champs obligatoires", danger);
            return;
        }

        btnUpdateProfile.setEnabled(false);
        btnUpdateProfile.setText("Mise à jour...");

        Map<String, Object> body = new HashMap<>();
        body.put("nom", nomField.getText().trim());
        body.put("prenom", prenomField.getText().trim());
        body.put("email", emailField.getText().trim());
        body.put("competance", competanceField.getText().trim());
        body.put("telephone", telephoneField.getText().trim());

        System.out.println("Sending update to: /api/profile/modifier/" + userID);
        System.out.println("Data: " + body);

        Queries.put("/api/profile/modifier/" + userID, body).thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                btnUpdateProfile.setEnabled(true);
                btnUpdateProfile.setText("Mettre à jour le profil");

                System.out.println("Update response: " + response);

                if (response.containsKey("success") && Boolean.TRUE.equals(response.get("success"))) {
                    showMessage(profileMessage, "Profil mis à jour avec succès !", sucess);
                    
                    // Mettre à jour le SessionManager
                    SessionManager.getInstance().setUserSession(
                    SessionManager.getInstance().getToken(), // Conserver le token actuel
                    prenomField.getText().trim(),
                    nomField.getText().trim(),
                    emailField.getText().trim(),
                    SessionManager.getInstance().getCompetance(), // Conserver la compétence actuelle
                    SessionManager.getInstance().getTelephone(), // Conserver le téléphone actuel
                    SessionManager.getInstance().getUserId() // Conserver l'ID utilisateur actuel
                );


                    System.out.println("✓ Profil mis à jour");
                } else if (response.containsKey("error")) {
                    String errorMsg = String.valueOf(response.get("error"));
                    System.err.println("✗ Erreur: " + errorMsg);
                    showMessage(profileMessage, "Erreur: " + errorMsg, danger);
                } else {
                    System.err.println("✗ Réponse invalide");
                    showMessage(profileMessage, "Erreur lors de la mise à jour", danger);
                }
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                btnUpdateProfile.setEnabled(true);
                btnUpdateProfile.setText("Mettre à jour le profil");
                System.err.println("✗ Exception: " + ex.getMessage());
                ex.printStackTrace();
                showMessage(profileMessage, "Erreur de connexion", danger);
            });
            return null;
        });
    }

    // Continuation de la méthode handleUpdatePassword() et handleDeleteAccount()

    private void handleUpdatePassword() {
        System.out.println("=== UPDATE PASSWORD ===");
        
        String currentPwd = new String(currentPasswordField.getPassword());
        String newPwd = new String(newPasswordField.getPassword());
        String confirmPwd = new String(confirmPasswordField.getPassword());

        // Validations
        if (currentPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
            showMessage(passwordMessage, "Veuillez remplir tous les champs", danger);
            return;
        }

        if (!newPwd.equals(confirmPwd)) {
            showMessage(passwordMessage, "Les mots de passe ne correspondent pas", danger);
            return;
        }

        if (newPwd.length() < 6) {
            showMessage(passwordMessage, "Le mot de passe doit contenir au moins 6 caractères", danger);
            return;
        }

        btnUpdatePassword.setEnabled(false);
        btnUpdatePassword.setText("Modification...");

        Map<String, Object> body = new HashMap<>();
        body.put("currentPassword", currentPwd);
        body.put("newPassword", newPwd);

        System.out.println("Sending password update to: /api/profile/password/" + userID);

        Queries.put("/api/profile/password/" + userID, body).thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                btnUpdatePassword.setEnabled(true);
                btnUpdatePassword.setText("Modifier le mot de passe");

                System.out.println("Password update response: " + response);

                if (response.containsKey("success") && Boolean.TRUE.equals(response.get("success"))) {
                    showMessage(passwordMessage, "Mot de passe modifié avec succès !", sucess);
                    currentPasswordField.setText("");
                    newPasswordField.setText("");
                    confirmPasswordField.setText("");
                    System.out.println("Mot de passe modifié");
                } else if (response.containsKey("error")) {
                    String errorMsg = String.valueOf(response.get("error"));
                    System.err.println("Erreur: " + errorMsg.split("\": \"")[1].replace("}", ""));
                    showMessage(passwordMessage, errorMsg.split("\": \"")[1].replace("\"}", ""), danger);
                } else {
                    System.err.println("Réponse invalide");
                    showMessage(passwordMessage, "Erreur lors de la modification", danger);
                }
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                btnUpdatePassword.setEnabled(true);
                btnUpdatePassword.setText("Modifier le mot de passe");
                System.err.println("✗ Exception: " + ex.getMessage());
                ex.printStackTrace();
                showMessage(passwordMessage, "Erreur de connexion", danger);
            });
            return null;
        });
    }

    private void handleDeleteAccount() {
        System.out.println("=== DELETE ACCOUNT ===");
        
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer votre compte ?\nCette action est irréversible.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            System.out.println("Suppression annulée par l'utilisateur");
            return;
        }

        btnDeleteAccount.setEnabled(false);
        btnDeleteAccount.setText("Suppression...");

        System.out.println("Sending delete request to: /api/profile/delete/" + userID);

        Queries.delete("/api/profile/delete/" + userID).thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                System.out.println("Delete response: " + response);
                
                if (response.containsKey("success") && Boolean.TRUE.equals(response.get("success"))) {
                    showMessage(deleteMessage, "Compte supprimé avec succès", sucess);
                    System.out.println("✓ Compte supprimé");
                    
                    // Déconnexion et redirection après 2 secondes
                    Timer timer = new Timer(2000, evt -> {
                        SessionManager.getInstance().logout();
                        System.out.println("Session fermée, redirection vers login...");
                        
                        // Rediriger vers la page de connexion
                        // Si vous avez une méthode pour changer de page, utilisez-la ici
                        // Exemple: parent.switchCard("login");
                        // Ou fermer la fenêtre actuelle
                        Window window = SwingUtilities.getWindowAncestor(Profile.this);
                        if (window != null) {
                            window.dispose();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else if (response.containsKey("error")) {
                    btnDeleteAccount.setEnabled(true);
                    btnDeleteAccount.setText("Supprimer mon compte");
                    String errorMsg = String.valueOf(response.get("error"));
                    System.err.println("✗ Erreur: " + errorMsg);
                    showMessage(deleteMessage, "Erreur: " + errorMsg, danger);
                } else {
                    btnDeleteAccount.setEnabled(true);
                    btnDeleteAccount.setText("Supprimer mon compte");
                    System.err.println("✗ Réponse invalide");
                    showMessage(deleteMessage, "Erreur lors de la suppression", danger);
                }
            });
        }).exceptionally(ex -> {
            SwingUtilities.invokeLater(() -> {
                btnDeleteAccount.setEnabled(true);
                btnDeleteAccount.setText("Supprimer mon compte");
                System.err.println("✗ Exception: " + ex.getMessage());
                ex.printStackTrace();
                showMessage(deleteMessage, "Erreur de connexion", danger);
            });
            return null;
        });
    }
}
