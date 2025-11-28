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
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, progressBg;
    private Color myMessageBg, otherMessageBg, inputBg;
    private List<ChatMessage> messages;
    private JPanel messagesContainer;
    private JTextArea inputField;
    private int myId = 1;
    private String projectName = "Projet Dashboard";
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public Chat() {
        this.theme = Params.theme;
        initializeColors();
        initializeDemoData();
        setLayout(new BorderLayout());
        setBackground(bgColor);

        add(createTopBar(), BorderLayout.NORTH);
        add(createMessagesArea(), BorderLayout.CENTER);
        add(createInputArea(), BorderLayout.SOUTH);
    }

    private void initializeColors() {
        if (theme == 0) {
            bgColor = new Color(245, 247, 250);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(30, 30, 30);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(59, 130, 246);
            progressBg = new Color(229, 231, 235);
            myMessageBg = new Color(99, 102, 241);
            otherMessageBg = new Color(240, 241, 245);
            inputBg = Color.WHITE;
        } else {
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(20, 20, 20);
            textPrimary = new Color(230, 230, 230);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(0, 120, 215);
            progressBg = new Color(40, 40, 40);
            myMessageBg = new Color(99, 102, 241);
            otherMessageBg = new Color(40, 40, 40);
            inputBg = new Color(30, 30, 30);
        }
    }

    private void initializeDemoData() {
        messages = new ArrayList<>();

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

                g2.setColor(theme == 0 ? new Color(229, 231, 235) : new Color(40, 40, 40));
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);

                g2.dispose();
            }
        };
        topBar.setLayout(new BorderLayout(20, 0));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        topBar.setPreferredSize(new Dimension(0, 80));

        JLabel projectLabel = new JLabel(projectName);
        projectLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        projectLabel.setForeground(textPrimary);
        topBar.add(projectLabel, BorderLayout.WEST);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        JButton callButton = createModernButton("📞", new Color(16, 185, 129), new Color(52, 211, 153));
        callButton.setPreferredSize(new Dimension(45, 45));
        callButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Appel vocal", "Appel",
                JOptionPane.INFORMATION_MESSAGE));

        buttonsPanel.add(callButton);
        topBar.add(buttonsPanel, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createMessagesArea() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);

        messagesContainer = new JPanel();
        messagesContainer.setLayout(new BoxLayout(messagesContainer, BoxLayout.Y_AXIS));
        messagesContainer.setBackground(bgColor);
        messagesContainer.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        for (ChatMessage msg : messages) {
            messagesContainer.add(createMessageBubble(msg));
            messagesContainer.add(Box.createRigidArea(new Dimension(0, 12)));
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

        if (!isMyMessage) {
            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            header.setOpaque(false);
            header.setAlignmentX(Component.LEFT_ALIGNMENT);
            header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            JPanel avatar = createAvatar(msg.prenom, msg.nom);
            header.add(avatar);

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);
            infoPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

            JLabel nameLabel = new JLabel(msg.prenom + " " + msg.nom);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nameLabel.setForeground(textPrimary);
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (!msg.type.equals("NORMAL")) {
                JLabel roleLabel = new JLabel(msg.type);
                roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
                roleLabel.setForeground(msg.type.equals("RESPONSABLE") ? new Color(139, 92, 246)
                        : new Color(245, 158, 11));
                roleLabel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(msg.type.equals("RESPONSABLE")
                                ? new Color(139, 92, 246, 150) : new Color(245, 158, 11, 150), 1, true),
                        BorderFactory.createEmptyBorder(3, 8, 3, 8)));
                roleLabel.setOpaque(false);
                infoPanel.add(roleLabel);
            }

            infoPanel.add(nameLabel);
            header.add(infoPanel);
            messagePanel.add(header);
            messagePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        JPanel bubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bubbleColor = isMyMessage ? myMessageBg : otherMessageBg;
                g2.setColor(bubbleColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                g2.dispose();
            }
        };
        bubble.setLayout(new BorderLayout(15, 10));
        bubble.setOpaque(false);
        bubble.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        bubble.setAlignmentX(isMyMessage ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
        bubble.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));

        JTextArea textArea = new JTextArea(msg.contenu);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setForeground(isMyMessage ? Color.WHITE : textPrimary);
        textArea.setBorder(null);
        textArea.setMargin(new Insets(0, 0, 0, 0));

        bubble.add(textArea, BorderLayout.CENTER);

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

    private JPanel createAvatar(String prenom, String nom) {
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(99, 102, 241),
                        getWidth(), getHeight(), new Color(139, 92, 246));
                g2.setPaint(gradient);
                g2.fillOval(0, 0, getWidth(), getHeight());

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                String initials = "" + prenom.charAt(0) + nom.charAt(0);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initials, x, y);

                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setMinimumSize(new Dimension(40, 40));
        avatar.setMaximumSize(new Dimension(40, 40));
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

                g2.setColor(theme == 0 ? new Color(229, 231, 235) : new Color(40, 40, 40));
                g2.fillRect(0, 0, getWidth(), 1);

                g2.dispose();
            }
        };
        panel.setLayout(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        panel.setPreferredSize(new Dimension(0, 90));

        JPanel inputWrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(inputBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                g2.setColor(theme == 0 ? new Color(229, 231, 235) : new Color(60, 60, 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);

                g2.dispose();
            }
        };
        inputWrapper.setLayout(new BorderLayout(10, 0));
        inputWrapper.setOpaque(false);
        inputWrapper.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        inputField = new JTextArea();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setForeground(textPrimary);
        inputField.setBackground(inputBg);
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        inputField.setBorder(null);
        inputField.setOpaque(false);
        inputField.setMargin(new Insets(5, 0, 5, 0));

        JScrollPane inputScroll = new JScrollPane(inputField);
        inputScroll.setBorder(null);
        inputScroll.setOpaque(false);
        inputScroll.getViewport().setOpaque(false);
        inputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        inputWrapper.add(inputScroll, BorderLayout.CENTER);

        JButton sendButton = createModernButton("📤", new Color(99, 102, 241), new Color(139, 92, 246));
        sendButton.setPreferredSize(new Dimension(50, 50));
        sendButton.addActionListener(e -> sendMessage());
        inputWrapper.add(sendButton, BorderLayout.EAST);

        panel.add(inputWrapper, BorderLayout.CENTER);

        return panel;
    }

    private JButton createModernButton(String icon, Color color1, Color color2) {
        JButton button = new JButton(icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, getModel().isPressed() ? color2 : color1,
                        0, getHeight(), getModel().isPressed() ? color1 : color2);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
        };
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            ChatMessage newMessage = new ChatMessage(
                    myId, "Vous", "", "NORMAL",
                    text,
                    new Date());

            addMessage(newMessage);
            inputField.setText("");
        }
    }

    private void addMessage(ChatMessage message) {
        messages.add(message);
        messagesContainer.add(createMessageBubble(message));
        messagesContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        messagesContainer.revalidate();
        messagesContainer.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = ((JScrollPane) messagesContainer.getParent().getParent()).getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private static class ChatMessage {
        int membreId;
        String prenom;
        String nom;
        String type;
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