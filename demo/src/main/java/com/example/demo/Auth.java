package com.example.demo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Auth extends JPanel {
    private JTextField username;
    private JPasswordField password;

    public Auth() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Style des labels
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Color labelColor = new Color(50, 50, 50);

        // Style des champs de texte
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color fieldBackground = new Color(255, 255, 255);
        int fieldHeight = 40;

        // Style du bouton
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Color buttonBackground = new Color(0, 120, 212);
        Color buttonForeground = new Color(255, 255, 255);

        // Label et champ pour le nom d'utilisateur
        JLabel lblUser = new JLabel("Nom d'utilisateur :");
        lblUser.setFont(labelFont);
        lblUser.setForeground(labelColor);

        username = new JTextField(15);
        username.setFont(fieldFont);
        username.setBackground(fieldBackground);
        username.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        username.setPreferredSize(new Dimension(0, fieldHeight));

        // Label et champ pour le mot de passe
        JLabel lblPass = new JLabel("Mot de passe :");
        lblPass.setFont(labelFont);
        lblPass.setForeground(labelColor);

        password = new JPasswordField(15);
        password.setFont(fieldFont);
        password.setBackground(fieldBackground);
        password.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        password.setPreferredSize(new Dimension(0, fieldHeight));

        // Bouton de connexion
        JButton btnLogin = new JButton("Connexion");
        btnLogin.setFont(buttonFont);
        btnLogin.setForeground(buttonForeground);
        btnLogin.setBackground(buttonBackground);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.addActionListener(e -> {
            // Ferme la fenêtre actuelle (optionnel)
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(Auth.this);
            currentFrame.dispose();

            // Ouvre la nouvelle fenêtre Main
            SwingUtilities.invokeLater(() -> {
                Main mainGUI = new Main(); // Supposons que Main est une classe qui étend JFrame ou JPanel
                mainGUI.setVisible(true);
            });
        });

        // Ajout des composants
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblUser, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(username, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblPass, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(password, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(btnLogin, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Login");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Auth());
            frame.setSize(Params.width, Params.height);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}