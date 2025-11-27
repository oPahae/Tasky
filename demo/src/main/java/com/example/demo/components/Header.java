package com.example.demo.components;

import com.example.demo.Params;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class Header extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, hoverColor, borderColor, shadowColor;
    private JButton themeToggleBtn;
    private String selectedElement = "Dashboard";

    public Header(List<String> elements, Consumer<String> onClick) {
        this.theme = Params.theme;
        initializeColors();

        setLayout(new BorderLayout());
        setBackground(bgColor);
        setBorder(new EmptyBorder(16, 24, 16, 24));

        // Left section with navigation buttons
        JPanel leftSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        leftSection.setBackground(bgColor);

        for (String element : elements) {
            JButton btn = createModernNavButton(element, onClick);
            leftSection.add(btn);
        }

        add(leftSection, BorderLayout.WEST);

        // Right section with theme toggle and actions
        JPanel rightSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightSection.setBackground(bgColor);

        // Notifications button with badge
        JButton notifBtn = createNotificationButton();
        rightSection.add(notifBtn);

        // Theme toggle button
        themeToggleBtn = createThemeToggleButton();
        rightSection.add(themeToggleBtn);

        add(rightSection, BorderLayout.EAST);
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

    private JButton createModernNavButton(String text, Consumer<String> onClick) {
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
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Color update logic
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
            selectedElement = text;
            onClick.accept(text);
            // Update all buttons
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

    private JButton createNotificationButton() {
        JButton btn = new JButton() {
            private final int badgeCount = 3;
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

                int size = 24;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.drawImage(bellImage, x, y, size, size, null);

                if (badgeCount > 0) {
                    int badgeSize = 12;
                    int badgeX = getWidth() - badgeSize - 2;
                    int badgeY = 2;

                    g2.setColor(new Color(239, 68, 68));
                    g2.fillOval(badgeX, badgeY, badgeSize, badgeSize);

                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    String text = badgeCount > 9 ? "9+" : String.valueOf(badgeCount);
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

        return btn;
    }

    private JButton createThemeToggleButton() {
        JButton btn = new JButton() {
            private Image bellImage;

            {
                try {
                    bellImage = ImageIO.read(getClass().getResource("/assets/" + (theme == 0 ? "moon" : "moon") + ".png"));
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

                int size = 24;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.drawImage(bellImage, x, y, size, size, null);

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

        btn.addActionListener(e -> toggleTheme());

        return btn;
    }

    private void toggleTheme() {
        // Toggle theme
        theme = (theme == 0) ? 1 : 0;
        Params.theme = theme;

        // Update colors
        initializeColors();

        // Update theme button icon
        JLabel iconLabel = (JLabel) themeToggleBtn.getComponent(0);
        iconLabel.setText(theme == 0 ? "☀" : "🌙");
        iconLabel.setForeground(textPrimary);
        themeToggleBtn.setToolTipText(theme == 0 ? "Mode sombre" : "Mode clair");

        // Refresh this panel
        updateUI();
        repaint();
    }

    // Method to update colors when theme changes externally
    public void updateTheme(int newTheme) {
        this.theme = newTheme;
        initializeColors();

        if (themeToggleBtn != null) {
            JLabel iconLabel = (JLabel) themeToggleBtn.getComponent(0);
            iconLabel.setText(theme == 0 ? "☀" : "🌙");
            iconLabel.setForeground(textPrimary);
            themeToggleBtn.setToolTipText(theme == 0 ? "Mode sombre" : "Mode clair");
        }

        setBackground(bgColor);

        // Update all child components
        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(bgColor);
            }
        }

        repaint();
    }
}