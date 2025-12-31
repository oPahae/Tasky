package com.example.demo.interfaces;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.example.demo.Params;
import com.example.demo.SessionManager;
import com.example.demo.components.Scrollbar;
import com.example.demo.hooks.MessageDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Chat extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor;
    private Color myMessageBg, otherMessageBg, inputBg;
    private List<ChatMessage> messages;
    private JPanel messagesContainer;
    private JTextArea inputField;
    private int myId;
    private int projectId;
    private String projectName;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    // WebSocket
    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private Gson gson = new GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    private Set<Integer> loadedMessageIds = new HashSet<>();

    public Chat() {
        this.theme = Params.theme;
        initializeColors();

        // Utiliser l'ID du user connecté comme membre actuel
        this.myId = SessionManager.getInstance().getUserId();
        this.projectId = SessionManager.getInstance().getCurrentProjetId();
        this.projectName = SessionManager.getInstance().getCurrentProjetNom();

        // Vérifier que le projet est sélectionné
        if (projectId <= 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un projet avant d'ouvrir le chat.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        messages = new ArrayList<>();
        setLayout(new BorderLayout());
        setBackground(bgColor);
        add(createTopBar(), BorderLayout.NORTH);
        add(createMessagesArea(), BorderLayout.CENTER);
        add(createInputArea(), BorderLayout.SOUTH);

        // Charger l'historique et connecter WebSocket
        loadMessageHistory();
        connectWebSocket();
    }

    private void initializeColors() {
        if (theme == 0) {
            bgColor = new Color(245, 247, 250);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(30, 30, 30);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(59, 130, 246);
            myMessageBg = new Color(99, 102, 241);
            otherMessageBg = new Color(240, 241, 245);
            inputBg = Color.WHITE;
        } else {
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(20, 20, 20);
            textPrimary = new Color(230, 230, 230);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(0, 120, 215);
            myMessageBg = new Color(99, 102, 241);
            otherMessageBg = new Color(40, 40, 40);
            inputBg = new Color(30, 30, 30);
        }
    }

    private void connectWebSocket() {
        new Thread(() -> {
            try {
                List<Transport> transports = new ArrayList<>();
                transports.add(new WebSocketTransport(new StandardWebSocketClient()));

                SockJsClient sockJsClient = new SockJsClient(transports);
                stompClient = new WebSocketStompClient(sockJsClient);
                stompClient.setMessageConverter(new MappingJackson2MessageConverter());

                StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        stompSession = session;
                        System.out.println("WebSocket connecté pour projet " + projectId);

                        session.subscribe("/topic/projet/" + projectId, new StompFrameHandler() {
                            @Override
                            public Type getPayloadType(StompHeaders headers) {
                                return MessageDTO.class;
                            }

                            @Override
                            public void handleFrame(StompHeaders headers, Object payload) {
                                MessageDTO dto = (MessageDTO) payload;
                                SwingUtilities.invokeLater(() -> {
                                    if (!loadedMessageIds.contains(dto.id)) {
                                        ChatMessage msg = new ChatMessage(
                                                dto.membreId,
                                                dto.prenomMembre,
                                                dto.nomMembre,
                                                dto.typeMembre,
                                                dto.contenu,
                                                dto.dateEnvoi
                                        );
                                        msg.id = dto.id;
                                        addMessage(msg);
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void handleTransportError(StompSession session, Throwable exception) {
                        System.err.println("Erreur WebSocket : " + exception.getMessage());
                    }
                };

                String url = "http://localhost:8080/ws-chat";
                stompClient.connect(url, sessionHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadMessageHistory() {
        new Thread(() -> {
            try {
                System.out.println("Chargement de l'historique pour projet " + projectId);

                URL url = new URL("http://localhost:8080/api/messages/projet/" + projectId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                System.out.println("Code réponse HTTP: " + responseCode);

                if (responseCode != 200) {
                    System.err.println(" Erreur HTTP: " + responseCode);
                    return;
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();

                String jsonString = json.toString();

                if (jsonString.trim().isEmpty() || jsonString.equals("[]")) {
                    System.out.println("Aucun message dans l'historique");
                    return;
                }

                MessageDTO[] dtos = gson.fromJson(jsonString, MessageDTO[].class);

                if (dtos == null || dtos.length == 0) {
                    System.out.println(" Aucun message parsé");
                    return;
                }

                SwingUtilities.invokeLater(() -> {
                    messagesContainer.removeAll();
                    loadedMessageIds.clear();
                    messages.clear();

                    if (dtos == null || dtos.length == 0) {
                        // Ajouter un message par défaut si la liste est vide
                        ChatMessage defaultMessage = new ChatMessage(
                            -1, // ID spécial pour indiquer que c'est un message système
                            "Système",
                            "",
                            "SYSTÈME",
                            "Aucun message pour cette discussion",
                            new Date()
                        );
                        messages.add(defaultMessage);
                        messagesContainer.add(createMessageBubble(defaultMessage));
                        messagesContainer.add(Box.createRigidArea(new Dimension(0, 12)));
                    } else {
                        System.out.println("Ajout de " + dtos.length + " messages à l'interface");

                        for (MessageDTO dto : dtos) {
                            if (dto.id == 0) {
                                System.err.println(" Message sans ID détecté");
                                continue;
                            }

                            ChatMessage msg = new ChatMessage(
                                dto.membreId,
                                dto.prenomMembre != null ? dto.prenomMembre : "Inconnu",
                                dto.nomMembre != null ? dto.nomMembre : "",
                                dto.typeMembre != null ? dto.typeMembre : "Membre",
                                dto.contenu != null ? dto.contenu : "",
                                dto.dateEnvoi != null ? dto.dateEnvoi : new Date()
                            );
                            msg.id = dto.id;

                            loadedMessageIds.add(msg.id);
                            messages.add(msg);
                            messagesContainer.add(createMessageBubble(msg));
                            messagesContainer.add(Box.createRigidArea(new Dimension(0, 12)));
                        }
                    }

                    messagesContainer.revalidate();
                    messagesContainer.repaint();

                    SwingUtilities.invokeLater(() -> {
                        try {
                            JScrollBar vertical = ((JScrollPane) messagesContainer.getParent().getParent())
                                .getVerticalScrollBar();
                            vertical.setValue(vertical.getMaximum());
                        } catch (Exception e) {
                            System.err.println(" Erreur scroll: " + e.getMessage());
                        }
                    });
                });

                System.out.println("Historique chargé (" + dtos.length + " messages)");

            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'historique:");
                e.printStackTrace();
            }
        }).start();
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

        return topBar;
    }

    private JPanel createDefaultMessage() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setOpaque(false);
        container.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel bubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 200, 200)); // Couleur neutre pour le message par défaut
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };

        bubble.setLayout(new BorderLayout());
        bubble.setOpaque(false);
        bubble.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        bubble.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel("Aucun message pour cette discussion");
        messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        messageLabel.setForeground(textSecondary);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bubble.add(messageLabel, BorderLayout.CENTER);
        container.add(bubble);

        return container;
    }

    private JPanel createMessagesArea() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);

        messagesContainer = new JPanel();
        messagesContainer.setLayout(new BoxLayout(messagesContainer, BoxLayout.Y_AXIS));
        messagesContainer.setBackground(bgColor);
        messagesContainer.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        if (messages.isEmpty()) {
            messagesContainer.add(createDefaultMessage());
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
        boolean isMyMessage = msg.membreId == Params.membreID;

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.setOpaque(false);
        container.setAlignmentX(isMyMessage ? Component.LEFT_ALIGNMENT : Component.RIGHT_ALIGNMENT);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        if (isMyMessage) {
            container.add(Box.createHorizontalGlue());
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

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.width = Math.min(d.width, 550);
                return d;
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension d = getPreferredSize();
                d.height = super.getPreferredSize().height;
                return d;
            }
        };

        bubble.setLayout(new BorderLayout(12, 8));
        bubble.setOpaque(false);
        bubble.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        bubble.setAlignmentX(isMyMessage ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

        JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
        headerPanel.setOpaque(false);

        JPanel avatar = createAvatar(msg.prenom, msg.nom);
        headerPanel.add(avatar, BorderLayout.WEST);

        JPanel nameAndBadgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        nameAndBadgePanel.setOpaque(false);

        JLabel nameLabel = new JLabel(msg.prenom + " " + msg.nom);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(isMyMessage ? new Color(255, 255, 255, 230) : textPrimary);
        nameAndBadgePanel.add(nameLabel);

        if ("Responsable".equals(msg.type)) {
            JLabel badgeLabel = new JLabel("Responsable");
            badgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            badgeLabel.setForeground(Color.WHITE);
            badgeLabel.setBackground(new Color(255, 100, 100));
            badgeLabel.setOpaque(true);
            badgeLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            badgeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nameAndBadgePanel.add(badgeLabel);
        }

        headerPanel.add(nameAndBadgePanel, BorderLayout.CENTER);
        bubble.add(headerPanel, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(msg.contenu);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setForeground(isMyMessage ? Color.WHITE : textPrimary);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        textArea.setMargin(new Insets(0, 0, 0, 0));

        FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
        int maxWidth = 450;

        String[] words = msg.contenu.split("\\s+");
        int currentLineWidth = 0;
        int lines = 1;

        for (String word : words) {
            int wordWidth = fm.stringWidth(word + " ");
            if (currentLineWidth + wordWidth > maxWidth) {
                lines++;
                currentLineWidth = wordWidth;
            } else {
                currentLineWidth += wordWidth;
            }
        }

        int lineHeight = fm.getHeight();
        int textHeight = lines * lineHeight + 10;
        textArea.setPreferredSize(new Dimension(maxWidth, textHeight));

        bubble.add(textArea, BorderLayout.CENTER);

        JLabel timeLabel = new JLabel(timeFormat.format(msg.date));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLabel.setForeground(isMyMessage ? new Color(255, 255, 255, 180) : textSecondary);
        bubble.add(timeLabel, BorderLayout.SOUTH);

        container.add(bubble);

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
                int hue = (prenom + nom).hashCode() % 360;
                Color color1 = Color.getHSBColor(hue / 360f, 0.7f, 0.85f);
                Color color2 = Color.getHSBColor(hue / 360f, 0.8f, 0.70f);

                GradientPaint gradient = new GradientPaint(
                    0, 0, color1,
                    getWidth(), getHeight(), color2);
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
        if (!text.isEmpty() && stompSession != null && stompSession.isConnected()) {
            if (messages.isEmpty()) {
                messagesContainer.removeAll();
            }

            MessageDTO dto = new MessageDTO();
            dto.contenu = text;
            dto.dateEnvoi = new Date();
            dto.estLu = false;
            dto.membreId = Params.membreID;
            dto.projetId = Params.projetID;

            stompSession.send("/app/chat.sendMessage", dto);
            inputField.setText("");
        } else if (stompSession == null || !stompSession.isConnected()) {
            JOptionPane.showMessageDialog(this,
                "Non connecté au serveur. Veuillez réessayer.",
                "Erreur de connexion",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMessage(ChatMessage message) {
        loadedMessageIds.add(message.id);
        messages.add(message);
        messagesContainer.add(createMessageBubble(message));
        messagesContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        messagesContainer.revalidate();
        messagesContainer.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = ((JScrollPane) messagesContainer.getParent().getParent())
                .getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private static class ChatMessage {
        int id;
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