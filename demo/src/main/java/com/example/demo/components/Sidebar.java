package com.example.demo.components;

import com.example.demo.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class Sidebar extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, hoverColor, borderColor, shadowColor;
    private List<Map<String, Object>> projects;
    private JComboBox<String> projectSelector;
    private String currentUserFirstName = "";
    private String currentUserLastName = "";
    private String selectedElement = Params.sidebarSelectedElement;
    private Consumer<String> onClick;
    private int userID;

    public Sidebar(List<String> elements, Consumer<String> onClick) {
        this.theme = Params.theme;
        this.onClick = onClick;
        initializeColors();
        // Params.show();

        this.userID = SessionManager.getInstance().getUserId();
        this.currentUserFirstName = SessionManager.getInstance().getNom();
        this.currentUserLastName = SessionManager.getInstance().getPrenom();

        initializeProjects();
        setLayout(new BorderLayout());
        setBackground(bgColor);
        setPreferredSize(new Dimension(280, 0));

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(bgColor);
        container.setBorder(new EmptyBorder(20, 16, 20, 16));
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(bgColor);

        mainContent.add(createHeaderSection());
        mainContent.add(Box.createRigidArea(new Dimension(0, 28)));
        mainContent.add(createNavigationSection(elements));
        mainContent.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
        customizeScrollBar(scrollPane.getVerticalScrollBar());

        container.add(scrollPane, BorderLayout.CENTER);
        container.add(createProfileSection(), BorderLayout.SOUTH);

        add(container, BorderLayout.CENTER);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_T) {
                    SwingUtilities.invokeLater(() -> {
                        JFrame terminalFrame = new JFrame("Terminal");
                        terminalFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        terminalFrame.setSize(1200, 600);
                        terminalFrame.setContentPane(new Terminal(onClick));
                        terminalFrame.setLocationRelativeTo(null);
                        terminalFrame.setVisible(true);
                    });
                }
            }
        });
        setFocusable(true);
        requestFocusInWindow();
    }

    private void initializeColors() {
        if (theme == 0) { // Light mode
            bgColor = new Color(250, 251, 252);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(15, 23, 42);
            textSecondary = new Color(100, 116, 139);
            accentColor = new Color(59, 130, 246);
            hoverColor = new Color(241, 245, 249);
            borderColor = new Color(226, 232, 240);
            shadowColor = new Color(0, 0, 0, 8);
        } else { // Dark mode
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

    private void initializeProjects() {
        this.projects = new ArrayList<>();
        if (this.userID > 0)
            fetchProjectsFromBackend(this.userID);
        else
            updateProjectSelector();
    }

    private void updateProjectSelector() {
        if (projectSelector != null) {
            projectSelector.removeAllItems();
            for (Map<String, Object> project : projects) {
                int id = (int) project.get("id");
                String nom = (String) project.get("nom");
                projectSelector.addItem("[" + id + "] " + nom);
            }
        }
    }

    private void fetchProjectsFromBackend(int userID) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/projet/user/" + userID))
                .GET()
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(jsonResponse -> {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<Map<String, Object>> fetchedProjects = objectMapper.readValue(jsonResponse,
                                    new TypeReference<List<Map<String, Object>>>() {
                                    });

                            // Trier la liste pour que le projet sélectionné soit le premier
                            if (Params.projetID > 0) {
                                fetchedProjects.sort((p1, p2) -> {
                                    int id1 = (int) p1.get("id");
                                    int id2 = (int) p2.get("id");
                                    if (id1 == Params.projetID) {
                                        return -1; // p1 vient en premier
                                    } else if (id2 == Params.projetID) {
                                        return 1; // p2 vient en premier
                                    } else {
                                        return 0; // ordre inchangé
                                    }
                                });
                            }

                            this.projects.clear();
                            this.projects.addAll(fetchedProjects);
                            updateProjectSelector();
                        } catch (Exception e) {
                            System.err.println("Erreur lors du parsing des projets : " + e.getMessage());
                            updateProjectSelector();
                        }
                    });
                })
                .exceptionally(e -> {
                    System.err.println("Erreur lors de la requête : " + e.getMessage());
                    SwingUtilities.invokeLater(() -> {
                        updateProjectSelector();
                    });
                    return null;
                });
    }

    private void customizeScrollBar(JScrollBar scrollBar) {
        scrollBar.setPreferredSize(new Dimension(6, 0));
        scrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = borderColor;
                this.trackColor = bgColor;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 3, 3);
                g2.dispose();
            }
        });
    }

    private JPanel createHeaderSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Logo with modern design
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoPanel.setBackground(bgColor);
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel logoIcon = new JLabel("📊");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel appName = new JLabel(Params.appName);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appName.setForeground(textPrimary);

        logoPanel.add(logoIcon);
        logoPanel.add(appName);
        section.add(logoPanel);
        section.add(Box.createRigidArea(new Dimension(0, 20)));

        // Project selector with shadow
        section.add(createProjectSelector());

        return section;
    }

    private JPanel createProjectSelector() {
        JPanel selectorCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(shadowColor);
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 14, 14);
                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 14, 14);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 14, 14);
                g2.dispose();
            }
        };

        selectorCard.setLayout(new BoxLayout(selectorCard, BoxLayout.Y_AXIS));
        selectorCard.setOpaque(false);
        selectorCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        selectorCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        selectorCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("PROJET ACTUEL");
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(textSecondary);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectorCard.add(label);
        selectorCard.add(Box.createRigidArea(new Dimension(0, 10)));

        // Initialiser le JComboBox
        projectSelector = createModernComboBox();

        projectSelector.addActionListener(e -> {
            int selectedIndex = projectSelector.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < projects.size()) {
                String selectedItem = (String) projectSelector.getSelectedItem();
                int start = selectedItem.indexOf('[') + 1;
                int end = selectedItem.indexOf(']');
                if (start > 0 && end > start) {
                    String idStr = selectedItem.substring(start, end);
                    try {
                        if (Params.projetID != Integer.parseInt(idStr)) {
                            Params.projetID = Integer.parseInt(idStr);
                            onClick.accept("Dashboard");
                        }
                    } catch (NumberFormatException ex) {
                        System.err.println("Erreur lors de la conversion de l'ID : " + ex.getMessage());
                        Params.projetID = -1;
                    }
                } else {
                    Params.projetID = -1;
                }
            }
        });

        selectorCard.add(projectSelector);
        return selectorCard;
    }

    private JComboBox<String> createModernComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setForeground(textPrimary);
        comboBox.setBackground(bgColor);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 8)));
        comboBox.setFocusable(false);
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("▼");
                button.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                button.setForeground(textSecondary);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                button.setFocusable(false);
                return button;
            }
        });

        return comboBox;
    }

    private JPanel createNavigationSection(List<String> elements) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Section divider
        section.add(createDivider());
        section.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel navLabel = new JLabel("NAVIGATION");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navLabel.setForeground(textSecondary);
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(navLabel);
        section.add(Box.createRigidArea(new Dimension(0, 12)));

        for (String element : elements) {
            section.add(createNavButton(element));
            section.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        return section;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton() {
            private BufferedImage iconImage;
            private final int ICON_SIZE = 24;

            {
                String iconPath;
                switch (text) {
                    case "Principale":
                        iconPath = "/assets/home.png";
                        break;
                    case "Mes projets":
                        iconPath = "/assets/apps.png";
                        break;
                    case "Créer un projet":
                        iconPath = "/assets/plus.png";
                        break;
                    case "Rejoindre un projet":
                        iconPath = "/assets/link.png";
                        break;
                    default:
                        iconPath = "/assets/home.png";
                        break;
                }
                try {
                    iconImage = ImageIO.read(getClass().getResource(iconPath));
                } catch (Exception e) {
                    iconImage = null;
                    System.err.println("Erreur chargement icône: " + iconPath);
                    e.printStackTrace();
                }
                setOpaque(false);
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 14));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

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

                if (iconImage != null) {
                    BufferedImage toDraw = iconImage;
                    int x = 14;
                    int y = (getHeight() - ICON_SIZE) / 2;
                    g2.drawImage(toDraw, x, y, ICON_SIZE, ICON_SIZE, null);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setOpaque(false);

        btn.setLayout(new BorderLayout(2, 0));
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(40, 0));
        btn.add(spacer, BorderLayout.WEST);

        btn.add(textLabel, BorderLayout.CENTER);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Runnable updateColors = () -> {
            boolean isSelected = text.equals(selectedElement);
            Color textColor = isSelected ? Color.WHITE : textPrimary;
            textLabel.setForeground(textColor);
            btn.repaint();
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
            Params.sidebarSelectedElement = text;
            selectedElement = text;
            onClick.accept(text.equals("Principale") ? "Dashboard" : text);
            Container parent = btn.getParent();
            if (parent != null)
                for (Component comp : parent.getComponents())
                    if (comp instanceof JButton) {
                        JButton navBtn = (JButton) comp;
                        Component[] components = navBtn.getComponents();
                        for (Component c : components)
                            if (c instanceof JLabel) {
                                JLabel label = (JLabel) c;
                                String btnText = label.getText();
                                label.setForeground(btnText.equals(selectedElement) ? Color.WHITE : textPrimary);
                            }
                    }
            parent.repaint();
        });

        updateColors.run();
        return btn;
    }

    private JPanel createProfileSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(createDivider());
        section.add(Box.createRigidArea(new Dimension(0, 16)));

        // Profile card
        JPanel profileCard = createProfileCard();
        section.add(profileCard);
        section.add(Box.createRigidArea(new Dimension(0, 10)));

        // Logout button
        section.add(createLogoutButton());

        return section;
    }

    private JPanel createProfileCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Border
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.dispose();
            }
        };

        card.setLayout(new BorderLayout(12, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        // Avatar
        JPanel avatar = createAvatar();
        card.add(avatar, BorderLayout.WEST);

        // User info
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);

        JLabel nameLabel = new JLabel(currentUserFirstName + " " + currentUserLastName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(textPrimary);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel statusPanel = createStatusPanel();

        userInfo.add(nameLabel);
        userInfo.add(Box.createRigidArea(new Dimension(0, 4)));
        userInfo.add(statusPanel);

        card.add(userInfo, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) {
                    onClick.accept("Profile");
                }
            }
        });

        return card;
    }

    private JPanel createAvatar() {
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient
                GradientPaint gradient = new GradientPaint(
                        0, 0, accentColor,
                        getWidth(), getHeight(), new Color(139, 92, 246));
                g2.setPaint(gradient);
                g2.fillOval(0, 0, getWidth(), getHeight());

                // Initials
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String initials = "";
                if (!currentUserFirstName.isEmpty())
                    initials += currentUserFirstName.charAt(0);

                if (initials.isEmpty())
                    initials = "?";

                initials = initials.toUpperCase();

                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);

                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setOpaque(false);
        return avatar;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        statusPanel.setOpaque(false);
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel statusDot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(34, 197, 94));
                g2.fillOval(0, 0, 8, 8);
                g2.dispose();
            }
        };
        statusDot.setPreferredSize(new Dimension(8, 8));
        statusDot.setOpaque(false);

        JLabel statusLabel = new JLabel("En ligne");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(textSecondary);

        statusPanel.add(statusDot);
        statusPanel.add(statusLabel);

        return statusPanel;
    }

    private JButton createLogoutButton() {
        JButton logoutBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = getModel().isRollover()
                        ? new Color(239, 68, 68, 30)
                        : new Color(239, 68, 68, 12);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        logoutBtn.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 11));
        logoutBtn.setOpaque(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorder(null);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel logoutIcon = new JLabel("⎋");
        logoutIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        logoutIcon.setForeground(new Color(239, 68, 68));

        JLabel logoutText = new JLabel("Déconnexion");
        logoutText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logoutText.setForeground(new Color(239, 68, 68));

        logoutBtn.add(logoutIcon);
        logoutBtn.add(logoutText);

        logoutBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Êtes-vous sûr de vouloir vous déconnecter ?",
                    "Déconnexion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                String token = com.example.demo.TokenManager.loadToken();
                if (token != null) {
                    Queries.post("/auth/logout", Map.of("token", token))
                            .thenAccept(response -> {
                                SwingUtilities.invokeLater(() -> {
                                    com.example.demo.TokenManager.deleteToken();
                                    Window window = SwingUtilities.getWindowAncestor(Sidebar.this);
                                    if (window != null) {
                                        window.dispose();
                                    }
                                    new com.example.demo.Auth().setVisible(true);
                                });
                            })
                            .exceptionally(ex -> {
                                System.err.println("Erreur lors de la déconnexion: " + ex.getMessage());
                                SwingUtilities.invokeLater(() -> {
                                    com.example.demo.TokenManager.deleteToken();
                                    Window window = SwingUtilities.getWindowAncestor(Sidebar.this);
                                    if (window != null) {
                                        window.dispose();
                                    }
                                    new com.example.demo.Auth().setVisible(true);
                                });
                                return null;
                            });
                } else {
                    com.example.demo.TokenManager.deleteToken();
                    Window window = SwingUtilities.getWindowAncestor(Sidebar.this);
                    if (window != null) {
                        window.dispose();
                    }
                    new com.example.demo.Auth().setVisible(true);
                }
            }
        });

        return logoutBtn;
    }

    private JPanel createDivider() {
        JPanel dividerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(borderColor);
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        dividerPanel.setOpaque(false);
        dividerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        dividerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return dividerPanel;
    }
}

class StyledLabel extends JLabel {
    private float letterSpacing = 0;

    public StyledLabel(String text) {
        super(text);
    }

    public void setLetterSpacing(float spacing) {
        this.letterSpacing = spacing;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (letterSpacing == 0) {
            super.paintComponent(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(getFont());
        g2.setColor(getForeground());

        String text = getText();
        FontMetrics fm = g2.getFontMetrics();
        int x = 0;
        int y = fm.getAscent();

        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            g2.drawString(ch, x, y);
            x += fm.stringWidth(ch) + letterSpacing;
        }

        g2.dispose();
    }
}