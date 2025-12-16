package com.example.demo.components;

import com.example.demo.Params;
import com.example.demo.Queries;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Header extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, hoverColor, borderColor, shadowColor;
    private JButton themeToggleBtn;
    private String selectedElement = Params.headerSelectedElement;
    private JPopupMenu notificationPopup;
    private JPopupMenu aiPopup;
    private List<Notification> notifications;

    public Header(List<String> elements, Consumer<String> onClick) {
        this.theme = Params.theme;
        initializeColors();
        notifications = new ArrayList<>();
        setLayout(new BorderLayout());
        setBackground(bgColor);
        setBorder(new EmptyBorder(16, 24, 16, 24));
        fetchNotifs();
        JPanel leftSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        leftSection.setBackground(bgColor);
        for (String element : elements) {
            JButton btn = createNavButton(element, onClick);
            leftSection.add(btn);
        }
        add(leftSection, BorderLayout.WEST);
        JPanel rightSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightSection.setBackground(bgColor);
        JButton aiBtn = createAIButton();
        rightSection.add(aiBtn);
        JButton notifBtn = createNotificationButton();
        rightSection.add(notifBtn);
        themeToggleBtn = createThemeToggleButton(onClick);
        rightSection.add(themeToggleBtn);
        add(rightSection, BorderLayout.EAST);
    }

    private void fetchNotifs() {
        CompletableFuture<Map<String, Object>> future = Queries.get(
                "/api/notif/membre/" + Params.membreID + "/projet/" + Params.projetID);
        future.thenAccept(response -> {
            // System.out.println("/api/notif/membre/" + Params.membreID + "/projet/" +
            // Params.projetID);
            if (response.containsKey("error")) {
                System.err.println("Erreur récup : " + response.get("error"));
                return;
            }
            try {
                List<Map<String, Object>> notifs = (List<Map<String, Object>>) response.get("notifications");
                notifications.clear();
                for (Map<String, Object> notif : notifs) {
                    int id = (int) notif.get("id");
                    String contenu = (String) notif.get("contenu");
                    String dateEnvoieStr = (String) notif.get("dateEnvoie");
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                    LocalDateTime dateEnvoie = LocalDateTime.parse(dateEnvoieStr, formatter);
                    boolean estLue = (boolean) notif.get("estLue");
                    notifications.add(new Notification(id, contenu, dateEnvoie, estLue));
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du parsing des notifications: " + e.getMessage());
                e.printStackTrace();
            }
        }).exceptionally(e -> {
            System.err.println("Erreur lors de la récupération des notifications: " + e.getMessage());
            return null;
        });
    }

    private void initializeColors() {
        if (theme == 0) {
            bgColor = new Color(250, 251, 252);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(15, 23, 42);
            textSecondary = new Color(100, 116, 139);
            accentColor = new Color(59, 130, 246);
            hoverColor = new Color(241, 245, 249);
            borderColor = new Color(226, 232, 240);
            shadowColor = new Color(0, 0, 0, 8);
        } else {
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(20, 20, 20);
            textPrimary = new Color(230, 230, 230);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(0, 120, 215);
            hoverColor = new Color(51, 65, 85);
            borderColor = new Color(51, 65, 85);
            shadowColor = new Color(0, 0, 0, 20);
        }
    }

    private JButton createNavButton(String text, Consumer<String> onClick) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isSelected = text.equals(selectedElement);
                boolean isHovered = getModel().isRollover();
                if (isSelected) {
                    GradientPaint gradient = new GradientPaint(
                            0, 0, accentColor,
                            getWidth(), getHeight(), new Color(139, 92, 246));
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                } else if (isHovered) {
                    g2.setColor(hoverColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setText(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false); // dik bg l3adia dial java
        btn.setFocusPainted(false); // dak porder l3adi dial java
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Runnable updateColors = () -> {
            boolean isSelected = text.equals(selectedElement);
            Color textColor = isSelected ? (theme == 0 ? Color.black : Color.WHITE) : textPrimary;
            btn.setForeground(textColor);
        };
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.repaint();
            }
        });
        btn.addActionListener(e -> {
            Params.headerSelectedElement = text;
            selectedElement = text;
            onClick.accept(text);
            Container parent = btn.getParent();
            if (parent != null) {
                for (Component comp : parent.getComponents()) {
                    comp.repaint();
                }
            }
        });
        updateColors.run();
        return btn;
    }

    private JButton createAIButton() {
        JButton btn = new JButton() {
            private Image aiImage;
            {
                try {
                    aiImage = ImageIO.read(getClass().getResource("/assets/robot.png"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (aiImage == null)
                    return;

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC); // redimension
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // radius
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY); //améliore

                int size = 34;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                g2.drawImage(aiImage, x, y, size, size, null);
                g2.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(42, 42));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("Assistant AI");
        btn.addActionListener(e -> showAIPopup(btn));

        return btn;
    }

    private void showAIPopup(JButton btn) {
        aiPopup = new JPopupMenu() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        aiPopup.setBackground(cardBgColor);
        aiPopup.setBorder(BorderFactory.createCompoundBorder( // wahd f exter wlakhor f inter
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(cardBgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(cardBgColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        JLabel titleLabel = new JLabel("Assistant AI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(textPrimary);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Chat
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(cardBgColor);

        JScrollPane scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBackground(cardBgColor);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Msg init
        JPanel initialMessagePanel = createMsgDiv(
                "Bonjour ! je suis votre assistant AI, comment puis-je vous aider aujourd'hui ?", true);
        chatPanel.add(initialMessagePanel);
        chatPanel.add(Box.createVerticalStrut(8));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Input
        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setBackground(cardBgColor);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBackground(theme == 0 ? new Color(248, 250, 252) : new Color(30, 30, 30));
        inputField.setForeground(textPrimary);
        inputField.setCaretColor(textPrimary);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JButton sendBtn = new JButton("Envoyer") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color color1 = new Color(147, 51, 234);
                Color color2 = new Color(219, 39, 119);

                if (getModel().isPressed()) {
                    color1 = color1.darker();
                    color2 = color2.darker();
                } else if (getModel().isRollover()) {
                    color1 = color1.brighter();
                    color2 = color2.brighter();
                }

                GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), 0, color2);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setPreferredSize(new Dimension(100, 38));
        sendBtn.setOpaque(false);
        sendBtn.setContentAreaFilled(false);
        sendBtn.setBorderPainted(false);
        sendBtn.setFocusPainted(false);
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ActionListener sendAction = e -> {
            String userInput = inputField.getText().trim();
            if (userInput.isEmpty()) {
                return;
            }
            inputField.setText("");
            inputField.setEnabled(false);
            sendBtn.setEnabled(false);

            // Supprimer msgs
            Component[] components = chatPanel.getComponents();
            for (Component component : components) {
                if (!(component instanceof Box.Filler)) {
                    chatPanel.remove(component);
                }
            }

            // Ajouter msg
            JPanel userMessagePanel = createMsgDiv(userInput, false);
            chatPanel.add(userMessagePanel);
            chatPanel.add(Box.createVerticalStrut(8));

            JPanel loadingPanel = createLoadingDiv();
            chatPanel.add(loadingPanel);

            chatPanel.revalidate();
            chatPanel.repaint();
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());

            try {
                CompletableFuture<Map<String, Object>> projectDataFuture = Queries.get("/api/all/" + Params.projetID);

                projectDataFuture.thenAccept(projectData -> {
                    if (projectData.containsKey("error")) {
                        SwingUtilities.invokeLater(() -> {
                            chatPanel.remove(loadingPanel);
                            JPanel errorPanel = createMsgDiv(
                                    "Erreur: Impossible de récupérer les données du projet", true);
                            chatPanel.add(errorPanel);
                            chatPanel.add(Box.createVerticalStrut(8));
                            chatPanel.revalidate();
                            chatPanel.repaint();
                            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
                            inputField.setEnabled(true);
                            sendBtn.setEnabled(true);
                            inputField.requestFocus();
                        });
                        return;
                    }

                    try {
                        String projectDataJson = convertMapToJson(projectData);
                        String encodedData = URLEncoder.encode(projectDataJson, "UTF-8");
                        String formattingType = ". REMARQUE TRES IMPORTANTE : ta réponse doit être sous forme d'un div html, pour le styling utilise uniquement l'attribut style, utilise différentes épaisseurs de textes (semi gras et mince) et couleurs (noires, grise et pistache), mais seule taille de texte. Ne fais pas de design compliqué, juste un titre coloré et gras avec du texte en noire (gris pour les infors supplémentaires s'il y en a) pour chaque paragraphe, pas de cadres ni mises en pages demandés";
                        String encodedPrompt = URLEncoder.encode(userInput + formattingType, "UTF-8");
                        String apiUrl = "https://pahae-utils.vercel.app/api/responseAI?data=" + encodedData + "&prompt="
                                + encodedPrompt;

                        HttpClient httpClient = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(apiUrl))
                                .GET()
                                .build();

                        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                .thenApply(HttpResponse::body)
                                .thenAccept(responseBody -> {
                                    SwingUtilities.invokeLater(() -> {
                                        chatPanel.remove(loadingPanel);
                                        String aiResponse;
                                        if (responseBody.contains("\"error\"")) {
                                            aiResponse = "Erreur: " + responseBody;
                                        } else {
                                            System.out.println(responseBody);
                                            aiResponse = responseBody.contains("\"response\"")
                                                    ? responseBody.split("\"response\":\"")[1]
                                                    : "Désolé, je n'ai pas pu obtenir une réponse.";
                                            aiResponse = format(aiResponse);
                                        }
                                        JPanel aiMessagePanel = createMsgDiv(aiResponse, true);
                                        chatPanel.add(aiMessagePanel);
                                        chatPanel.add(Box.createVerticalStrut(8));
                                        chatPanel.revalidate();
                                        chatPanel.repaint();
                                        scrollPane.getVerticalScrollBar()
                                                .setValue(scrollPane.getVerticalScrollBar().getMaximum());
                                        inputField.setEnabled(true);
                                        sendBtn.setEnabled(true);
                                        inputField.requestFocus();
                                    });
                                })
                                .exceptionally(ex -> {
                                    SwingUtilities.invokeLater(() -> {
                                        chatPanel.remove(loadingPanel);
                                        JPanel errorPanel = createMsgDiv("Erreur: " + ex.getMessage(), true);
                                        chatPanel.add(errorPanel);
                                        chatPanel.add(Box.createVerticalStrut(8));
                                        chatPanel.revalidate();
                                        chatPanel.repaint();
                                        scrollPane.getVerticalScrollBar()
                                                .setValue(scrollPane.getVerticalScrollBar().getMaximum());
                                        inputField.setEnabled(true);
                                        sendBtn.setEnabled(true);
                                        inputField.requestFocus();
                                    });
                                    return null;
                                });
                    } catch (UnsupportedEncodingException ex) {
                        SwingUtilities.invokeLater(() -> {
                            chatPanel.remove(loadingPanel);
                            JPanel errorPanel = createMsgDiv("Erreur d'encodage: " + ex.getMessage(), true);
                            chatPanel.add(errorPanel);
                            chatPanel.revalidate();
                            chatPanel.repaint();
                            inputField.setEnabled(true);
                            sendBtn.setEnabled(true);
                        });
                    }
                }).exceptionally(ex -> {
                    SwingUtilities.invokeLater(() -> {
                        chatPanel.remove(loadingPanel);
                        JPanel errorPanel = createMsgDiv(
                                "Erreur lors de la récupération des données: " + ex.getMessage(), true);
                        chatPanel.add(errorPanel);
                        chatPanel.add(Box.createVerticalStrut(8));
                        chatPanel.revalidate();
                        chatPanel.repaint();
                        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
                        inputField.setEnabled(true);
                        sendBtn.setEnabled(true);
                        inputField.requestFocus();
                    });
                    return null;
                });
            } catch (Exception ex) {
                chatPanel.remove(loadingPanel);
                JPanel errorPanel = createMsgDiv("Erreur: " + ex.getMessage(), true);
                chatPanel.add(errorPanel);
                chatPanel.revalidate();
                chatPanel.repaint();
                inputField.setEnabled(true);
                sendBtn.setEnabled(true);
            }
        };

        sendBtn.addActionListener(sendAction);
        inputField.addActionListener(sendAction);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        aiPopup.add(mainPanel);
        aiPopup.setPreferredSize(new Dimension(450, 380));
        aiPopup.show(btn, -410, btn.getHeight() + 5);
        inputField.requestFocus();

        AWTEventListener clickListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event instanceof MouseEvent) {
                    MouseEvent me = (MouseEvent) event;
                    if (me.getID() == MouseEvent.MOUSE_PRESSED) {
                        Component source = me.getComponent();
                        if (aiPopup != null && aiPopup.isVisible()) {
                            if (!isDescendant(aiPopup, source) && source != btn) {
                                aiPopup.setVisible(false);
                                Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                            }
                        }
                    }
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(clickListener, AWTEvent.MOUSE_EVENT_MASK);
    }

    private JPanel createMsgDiv(String message, boolean isAI) {
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.X_AXIS));
        containerPanel.setBackground(cardBgColor);
        containerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        containerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel bubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isAI) {
                    g2.setColor(theme == 0 ? new Color(241, 245, 249) : new Color(45, 45, 45));
                } else {
                    Color color1 = new Color(147, 51, 234);
                    Color color2 = new Color(219, 39, 119);
                    GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), 0, color2);
                    g2.setPaint(gradient);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension pref = getPreferredSize();
                return new Dimension(280, pref.height);
            }
        };

        bubble.setLayout(new BorderLayout());
        bubble.setOpaque(false);
        bubble.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel textLabel = new JLabel(
                "<html><div style='width: 250px; word-wrap: break-word;'>" + message + "</div></html>");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLabel.setForeground(isAI ? textPrimary : Color.WHITE);

        bubble.add(textLabel, BorderLayout.CENTER);

        if (isAI) {
            containerPanel.add(bubble);
            containerPanel.add(Box.createHorizontalGlue());
        } else {
            containerPanel.add(Box.createHorizontalGlue());
            containerPanel.add(bubble);
        }

        return containerPanel;
    }

    private JPanel createLoadingDiv() {
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.X_AXIS));
        containerPanel.setBackground(cardBgColor);
        containerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        containerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel bubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(theme == 0 ? new Color(241, 245, 249) : new Color(45, 45, 45));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension pref = getPreferredSize();
                return new Dimension(280, pref.height);
            }
        };

        bubble.setLayout(new BorderLayout());
        bubble.setOpaque(false);
        bubble.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel loadingLabel = new JLabel("En cours de réflexion...");
        loadingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        loadingLabel.setForeground(textPrimary);

        bubble.add(loadingLabel, BorderLayout.CENTER);

        containerPanel.add(bubble);
        containerPanel.add(Box.createHorizontalGlue());

        return containerPanel;
    }

    private String format(String text) {
        if (text == null || text.isEmpty())
            return text;
        text = text.replace("\\n", "");
        text = text.replaceAll("```html\\s*", "");
        text = text.replaceAll("```\\s*$", "");
        text = text.replace("\\\"", "\"");
        text = text.replace("\\", "");
        text = text.replaceAll("\\s+", " ").trim();
        return text;
    }

    private String convertMapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;

            json.append("\"").append(entry.getKey()).append("\":");
            json.append(convertObjectToJson(entry.getValue()));
        }

        json.append("}");
        return json.toString();
    }

    private String convertObjectToJson(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof String) {
            return "\"" + ((String) obj).replace("\"", "\\\"").replace("\n", "\\n") + "\"";
        } else if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        } else if (obj instanceof Map) {
            return convertMapToJson((Map<String, Object>) obj);
        } else if (obj instanceof List) {
            StringBuilder json = new StringBuilder("[");
            List<?> list = (List<?>) obj;
            for (int i = 0; i < list.size(); i++) {
                if (i > 0)
                    json.append(",");
                json.append(convertObjectToJson(list.get(i)));
            }
            json.append("]");
            return json.toString();
        } else {
            return "\"" + obj.toString().replace("\"", "\\\"") + "\"";
        }
    }

    private JButton createNotificationButton() {
        JButton btn = new JButton() {
            private Image bellImage;
            {
                try {
                    bellImage = ImageIO.read(getClass().getResource("/assets/bell.png"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bellImage == null)
                    return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int size = 30;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.drawImage(bellImage, x, y, size, size, null);
                int unreadCount = (int) notifications.stream().filter(n -> !n.estLue).count();
                if (unreadCount > 0) {
                    int badgeSize = 12;
                    int badgeX = getWidth() - badgeSize - 2;
                    int badgeY = 2;
                    g2.setColor(new Color(239, 68, 68));
                    g2.fillOval(badgeX, badgeY, badgeSize, badgeSize);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    String text = unreadCount > 9 ? "9+" : String.valueOf(unreadCount);
                    FontMetrics fm = g2.getFontMetrics();
                    int textX = badgeX + (badgeSize - fm.stringWidth(text)) / 2;
                    int textY = badgeY + ((badgeSize - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString(text, textX, textY);
                }
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(42, 42));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("Notifications");
        btn.addActionListener(e -> showNotificationPopup(btn));
        return btn;
    }

    private void showNotificationPopup(JButton btn) {
        notificationPopup = new JPopupMenu() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        notificationPopup.setBackground(cardBgColor);
        notificationPopup.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(8, 0, 8, 0)));
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(cardBgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(cardBgColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 12, 16));
        JLabel titleLabel = new JLabel("Notifications");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(textPrimary);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        int unreadCount = (int) notifications.stream().filter(n -> !n.estLue).count();
        if (unreadCount > 0) {
            JLabel countLabel = new JLabel(unreadCount + " non lue" + (unreadCount > 1 ? "s" : ""));
            countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            countLabel.setForeground(textSecondary);
            headerPanel.add(countLabel, BorderLayout.EAST);
        }
        mainPanel.add(headerPanel);
        JSeparator separator = new JSeparator();
        separator.setForeground(borderColor);
        mainPanel.add(separator);
        if (notifications.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(cardBgColor);
            emptyPanel.setBorder(BorderFactory.createEmptyBorder(32, 16, 32, 16));
            JLabel emptyLabel = new JLabel("Aucune notification");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(textSecondary);
            emptyPanel.add(emptyLabel);
            mainPanel.add(emptyPanel);
        } else {
            for (Notification notif : notifications) {
                mainPanel.add(createNotificationItem(notif));
            }
        }
        notificationPopup.add(mainPanel);
        notificationPopup.setPreferredSize(new Dimension(360, Math.min(480, notifications.size() * 90 + 80)));
        notificationPopup.show(btn, -320, btn.getHeight() + 5);
        AWTEventListener clickListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event instanceof MouseEvent) {
                    MouseEvent me = (MouseEvent) event;
                    if (me.getID() == MouseEvent.MOUSE_PRESSED) {
                        Component source = me.getComponent();
                        if (notificationPopup != null && notificationPopup.isVisible()) {
                            if (!isDescendant(notificationPopup, source) && source != btn) {
                                notificationPopup.setVisible(false);
                                Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                            }
                        }
                    }
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(clickListener, AWTEvent.MOUSE_EVENT_MASK);
    }

    private JPanel createNotificationItem(Notification notif) {
        JPanel itemPanel = new JPanel() {
            private boolean hovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovered) {
                    g2.setColor(hoverColor);
                    g2.fillRoundRect(8, 0, getWidth() - 16, getHeight(), 8, 8);
                }
                if (!notif.estLue) {
                    g2.setColor(accentColor);
                    g2.fillOval(12, getHeight() / 2 - 3, 6, 6);
                }
                g2.dispose();
            }
        };
        itemPanel.setLayout(new BorderLayout(12, 4));
        itemPanel.setBackground(cardBgColor);
        itemPanel.setBorder(BorderFactory.createEmptyBorder(12, notif.estLue ? 16 : 24, 12, 16));
        itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(cardBgColor);
        JLabel contentLabel = new JLabel("<html><body style='width: 280px'>" + notif.contenu + "</body></html>");
        contentLabel.setFont(new Font("Segoe UI", notif.estLue ? Font.PLAIN : Font.BOLD, 13));
        contentLabel.setForeground(textPrimary);
        contentPanel.add(contentLabel);
        contentPanel.add(Box.createVerticalStrut(4));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        JLabel dateLabel = new JLabel(notif.dateEnvoie.format(formatter));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(textSecondary);
        contentPanel.add(dateLabel);
        itemPanel.add(contentPanel, BorderLayout.CENTER);
        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ((JPanel) e.getSource()).putClientProperty("hovered", true);
                itemPanel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                ((JPanel) e.getSource()).putClientProperty("hovered", false);
                itemPanel.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!notif.estLue) {
                    Queries.put("/api/notif/" + notif.id + "/seen", Map.of())
                            .thenAccept(response -> {
                                if (!response.containsKey("error")) {
                                    notif.estLue = true;
                                    repaint();
                                }
                            });
                }
                notificationPopup.setVisible(false);
            }
        });
        return itemPanel;
    }

    private boolean isDescendant(Component parent, Component child) {
        if (child == null)
            return false;
        if (child == parent)
            return true;
        return isDescendant(parent, child.getParent());
    }

    private JButton createThemeToggleButton(Consumer<String> onClick) {
        JButton btn = new JButton() {
            private Image moonImage;
            private Image sunImage;
            {
                try {
                    moonImage = ImageIO.read(getClass().getResource("/assets/moon.png"));
                    sunImage = ImageIO.read(getClass().getResource("/assets/sun.png"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int size = theme == 0 ? 28 : 30;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                Image image = theme == 0 ? moonImage : sunImage;
                if (image != null) {
                    g2.drawImage(image, x, y, size, size, null);
                }
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(42, 42));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("Changer de thème");
        btn.addActionListener(e -> toggleTheme(onClick));
        return btn;
    }

    private void toggleTheme(Consumer<String> onClick) {
        Params.theme = Params.theme == 0 ? 1 : 0;
        theme = Params.theme;
        initializeColors();
        repaint();
        onClick.accept(selectedElement);
    }

    public void updateTheme(int newTheme) {
        this.theme = newTheme;
        initializeColors();
        repaint();
    }

    public void addNotification(String contenu, LocalDateTime dateEnvoie, boolean estLue) {
        notifications.add(0, new Notification(-1, contenu, dateEnvoie, estLue));
        repaint();
    }

    public static class Notification {
        int id;
        String contenu;
        LocalDateTime dateEnvoie;
        boolean estLue;

        public Notification(int id, String contenu, LocalDateTime dateEnvoie, boolean estLue) {
            this.id = id;
            this.contenu = contenu;
            this.dateEnvoie = dateEnvoie;
            this.estLue = estLue;
        }
    }
}