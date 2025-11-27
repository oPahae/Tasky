package com.example.demo.interfaces;

import com.example.demo.Params;
import com.example.demo.components.Scrollbar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Chat extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor;
    private Color myMessageBg, otherMessageBg, inputBg;
    private List<ChatMessage> messages;
    private JPanel messagesContainer;
    private JTextField inputField;
    private int myId = 1; // ID de l'utilisateur actuel
    private String projectName = "Projet Dashboard";
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public Chat() {
        this.theme = Params.theme;
        initializeColors();
        initializeDemoData();
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // Top bar
        add(createTopBar(), BorderLayout.NORTH);

        // Messages area
        add(createMessagesArea(), BorderLayout.CENTER);

        // Input area
        add(createInputArea(), BorderLayout.SOUTH);
    }

    private void initializeColors() {
        if (theme == 0) { // Light mode
            bgColor = new Color(245, 247, 250);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(30, 30, 30);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(59, 130, 246);
            myMessageBg = new Color(167, 139, 250); // Violet clair
            otherMessageBg = Color.WHITE;
            inputBg = Color.WHITE;
        } else { // Dark mode
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(20, 20, 20);
            textPrimary = new Color(230, 230, 230);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(0, 120, 215);
            myMessageBg = new Color(139, 92, 246); // Violet pour dark mode
            otherMessageBg = new Color(20, 20, 20);
            inputBg = new Color(30, 30, 30);
        }
    }

    private void initializeDemoData() {
        messages = new ArrayList<>();
        
        // Messages de démonstration
        messages.add(new ChatMessage(2, "Alice", "Martin", "RESPONSABLE", 
            "Bonjour à tous ! Comment avance le projet ?", 
            new Date(System.currentTimeMillis() - 3600000)));
        
        messages.add(new ChatMessage(1, "Vous", "", "NORMAL", 
            "Bonjour Alice ! Tout se passe bien de mon côté.", 
            new Date(System.currentTimeMillis() - 3500000)));
        
        messages.add(new ChatMessage(3, "Bob", "Durant", "GESTIONNAIRE", 
            "J'ai terminé l'API Backend. Je vais passer aux tests.", 
            new Date(System.currentTimeMillis() - 3000000)));
        
        messages.add(new ChatMessage(2, "Alice", "Martin", "RESPONSABLE", 
            "Excellent travail Bob ! 👍", 
            new Date(System.currentTimeMillis() - 2500000)));
        
        messages.add(new ChatMessage(4, "Charlie", "Dubois", "NORMAL", 
            "Voici la maquette de la nouvelle interface :", 
            new Date(System.currentTimeMillis() - 2000000)));
        
        messages.add(new ChatMessage(1, "Vous", "", "NORMAL", 
            "Super ! J'aime beaucoup le design.", 
            new Date(System.currentTimeMillis() - 1500000)));
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBgColor);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        topBar.setLayout(new BorderLayout(20, 0));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, 
                theme == 0 ? new Color(229, 231, 235) : new Color(40, 40, 40)),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        topBar.setPreferredSize(new Dimension(0, 80));

        // Project name
        JLabel projectLabel = new JLabel(projectName);
        projectLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        projectLabel.setForeground(textPrimary);
        topBar.add(projectLabel, BorderLayout.WEST);

        // Call button
        JButton callButton = createIconButton("📞", "Appel vocal");
        callButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Fonctionnalité d'appel vocal", 
                "Appel", JOptionPane.INFORMATION_MESSAGE);
        });
        topBar.add(callButton, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createMessagesArea() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);

        messagesContainer = new JPanel();
        messagesContainer.setLayout(new BoxLayout(messagesContainer, BoxLayout.Y_AXIS));
        messagesContainer.setBackground(bgColor);
        messagesContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Ajouter tous les messages
        for (ChatMessage msg : messages) {
            messagesContainer.add(createMessageBubble(msg));
            messagesContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(messagesContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMessageBubble(ChatMessage msg) {
        boolean isMyMessage = msg.membreId == myId;
        
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setOpaque(false);
        container.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        if (isMyMessage) {
            container.add(Box.createHorizontalGlue());
        }

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setOpaque(false);

        // Header (avatar + name + role)
        if (!isMyMessage) {
            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            header.setOpaque(false);
            header.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Avatar
            JPanel avatar = createSmallAvatar(msg.prenom, msg.nom);
            header.add(avatar);

            // Name
            JLabel nameLabel = new JLabel(msg.prenom + " " + msg.nom);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nameLabel.setForeground(textPrimary);
            header.add(nameLabel);

            // Role badge (si responsable ou gestionnaire)
            if (!msg.type.equals("NORMAL")) {
                JLabel roleLabel = new JLabel(msg.type) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        Color roleColor = msg.type.equals("RESPONSABLE") ? 
                            new Color(139, 92, 246) : new Color(245, 158, 11);
                        g2.setColor(new Color(roleColor.getRed(), roleColor.getGreen(), 
                            roleColor.getBlue(), 30));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
                roleLabel.setForeground(msg.type.equals("RESPONSABLE") ? 
                    new Color(139, 92, 246) : new Color(245, 158, 11));
                roleLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
                roleLabel.setOpaque(false);
                header.add(roleLabel);
            }

            messagePanel.add(header);
            messagePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        // Message bubble
        JPanel bubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bubbleColor = isMyMessage ? myMessageBg : otherMessageBg;
                g2.setColor(bubbleColor);
                
                if (isMyMessage) {
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                    // Triangle à droite
                    int[] xPoints = {getWidth() - 1, getWidth() - 1, getWidth() - 10};
                    int[] yPoints = {getHeight() - 10, getHeight() - 1, getHeight() - 1};
                    g2.fillPolygon(xPoints, yPoints, 3);
                } else {
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                    // Triangle à gauche
                    int[] xPoints = {0, 0, 10};
                    int[] yPoints = {10, 0, 0};
                    g2.fillPolygon(xPoints, yPoints, 3);
                }
                
                g2.dispose();
            }
        };
        bubble.setLayout(new BorderLayout(10, 10));
        bubble.setOpaque(false);
        bubble.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        bubble.setAlignmentX(isMyMessage ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

        // Content - uniquement texte
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JTextArea textArea = new JTextArea(msg.contenu);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setForeground(isMyMessage ? Color.WHITE : textPrimary);
        textArea.setBorder(null);
        
        // Calculer la largeur préférée du texte
        FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
        int textWidth = fm.stringWidth(msg.contenu);
        int maxWidth = 400; // Largeur maximale du message
        int minWidth = 100; // Largeur minimale du message
        
        // Si le texte est court, adapter la largeur
        if (textWidth < maxWidth) {
            textArea.setPreferredSize(new Dimension(Math.max(textWidth + 10, minWidth), 
                textArea.getPreferredSize().height));
        } else {
            textArea.setPreferredSize(new Dimension(maxWidth, textArea.getPreferredSize().height));
        }
        
        content.add(textArea);

        bubble.add(content, BorderLayout.CENTER);

        // Time
        JLabel timeLabel = new JLabel(timeFormat.format(msg.date));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLabel.setForeground(isMyMessage ? new Color(255, 255, 255, 180) : textSecondary);
        timeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        bubble.add(timeLabel, BorderLayout.SOUTH);

        messagePanel.add(bubble);
        container.add(messagePanel);

        if (!isMyMessage) {
            container.add(Box.createHorizontalGlue());
        }

        return container;
    }

    private JPanel createSmallAvatar(String prenom, String nom) {
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Circle background
                g2.setColor(accentColor);
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                // Initials
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String initials = String.valueOf(prenom.charAt(0)) + nom.charAt(0);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
                
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(32, 32));
        avatar.setOpaque(false);
        return avatar;
    }

    private JPanel createInputArea() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(cardBgColor);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setLayout(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, 
                theme == 0 ? new Color(229, 231, 235) : new Color(40, 40, 40)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.setPreferredSize(new Dimension(0, 80));

        // Input field
        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setForeground(textPrimary);
        inputField.setBackground(inputBg);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(theme == 0 ? new Color(229, 231, 235) : new Color(60, 60, 60), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        inputField.addActionListener(e -> sendMessage());
        panel.add(inputField, BorderLayout.CENTER);

        // Send button
        JButton sendButton = createIconButton("📤", "Envoyer");
        sendButton.addActionListener(e -> sendMessage());
        panel.add(sendButton, BorderLayout.EAST);

        return panel;
    }

    private JButton createIconButton(String icon, String tooltip) {
        JButton button = new JButton(icon);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(45, 45));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setContentAreaFilled(true);
                button.setBackground(theme == 0 ? new Color(240, 240, 240) : new Color(40, 40, 40));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setContentAreaFilled(false);
            }
        });
        
        return button;
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            ChatMessage newMessage = new ChatMessage(
                myId, "Vous", "", "NORMAL",
                text,
                new Date()
            );
            
            addMessage(newMessage);
            inputField.setText("");
        }
    }

    private void addMessage(ChatMessage message) {
        messages.add(message);
        messagesContainer.add(createMessageBubble(message));
        messagesContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        messagesContainer.revalidate();
        messagesContainer.repaint();
        
        // Scroll vers le bas
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = ((JScrollPane) messagesContainer.getParent().getParent()).getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    // Classe interne simplifiée
    private static class ChatMessage {
        int membreId;
        String prenom;
        String nom;
        String type; // NORMAL, RESPONSABLE, GESTIONNAIRE
        String contenu;
        Date date;

        public ChatMessage(int membreId, String prenom, String nom, String type,
                         String contenu, Date date) {
            this.membreId = membreId;
            this.prenom = prenom;
            this.nom = nom;
            this.type = type;
            this.contenu = contenu;
            this.date = date;
        }
    }
}