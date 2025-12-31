package com.example.demo;
import com.example.demo.interfaces.*;

import javax.swing.*;
import java.awt.*;

public class Auth extends JFrame {
    private JPanel mainContentPanel;
    private CardLayout contentLayout;
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, inputBorder, successColor, errorColor, inputBg, hoverColor;

    public Auth() {
        this.theme = Params.theme;
        initializeColors();
        setTitle("Authentification");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(false);
        getContentPane().setBackground(bgColor);
        setUndecorated(true);

        contentLayout = new CardLayout();
        mainContentPanel = new JPanel(contentLayout);
        mainContentPanel.setBackground(bgColor);

        Login loginPanel = new Login(this);
        Register registerPanel = new Register(this);
        Forgot forgotPanel = new Forgot(this);

        mainContentPanel.add(loginPanel, "login");
        mainContentPanel.add(registerPanel, "register");
        mainContentPanel.add(forgotPanel, "forgot");

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(bgColor);

        GridBagConstraints gbc = new GridBagConstraints();
        JPanel leftPanel = createLeftDecorativePanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(30, 40, 40, 20);
        contentPanel.add(leftPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(30, 20, 40, 40);
        contentPanel.add(mainContentPanel, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        contentLayout.show(mainContentPanel, "login");
    }

    private void initializeColors() {
        if (theme == 0) {
            bgColor = new Color(248, 250, 252);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(15, 23, 42);
            textSecondary = new Color(100, 116, 139);
            accentColor = new Color(59, 130, 246);
            inputBorder = new Color(226, 232, 240);
            inputBg = new Color(248, 250, 252);
            successColor = new Color(34, 197, 94);
            errorColor = new Color(239, 68, 68);
            hoverColor = new Color(37, 99, 235);
        } else {
            bgColor = new Color(15, 23, 42);
            cardBgColor = new Color(30, 41, 59);
            textPrimary = new Color(241, 245, 249);
            textSecondary = new Color(148, 163, 184);
            accentColor = new Color(59, 130, 246);
            inputBorder = new Color(51, 65, 85);
            inputBg = new Color(15, 23, 42);
            successColor = new Color(34, 197, 94);
            errorColor = new Color(239, 68, 68);
            hoverColor = new Color(37, 99, 235);
        }
    }

    private JPanel createLeftDecorativePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(59, 130, 246),
                    getWidth(), getHeight(), new Color(147, 51, 234)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 30));
                for (int i = 0; i < 8; i++) {
                    int size = 100 + i * 80;
                    int x = -50 + i * 40;
                    int y = -50 + i * 60;
                    g2.fillOval(x, y, size, size);
                }
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    public void switchCard(String cardName) {
        contentLayout.show(mainContentPanel, cardName);
    }

    public Color getBgColor() { return bgColor; }
    public Color getCardBgColor() { return cardBgColor; }
    public Color getTextPrimary() { return textPrimary; }
    public Color getTextSecondary() { return textSecondary; }
    public Color getAccentColor() { return accentColor; }
    public Color getInputBorder() { return inputBorder; }
    public Color getSuccessColor() { return successColor; }
    public Color getErrorColor() { return errorColor; }
    public Color getInputBg() { return inputBg; }
    public Color getHoverColor() { return hoverColor; }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new Auth().setVisible(true));
    }
}