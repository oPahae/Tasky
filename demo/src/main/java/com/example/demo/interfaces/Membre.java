package com.example.demo.interfaces;

import com.example.demo.Params;
import com.example.demo.components.Scrollbar;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Membre extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, progressBg, progressFill;
    private Color successColor, warningColor, dangerColor;

    private Consumer<String> onClick;
    private MemberData memberData;
    private List<TaskItem> tasks;

    public Membre(Consumer<String> onClick) {
        this.onClick = onClick;
        this.theme = Params.theme;
        initializeColors();
        initializeDemoData();
        setLayout(new BorderLayout());
        setBackground(bgColor);

        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
    }

    private void initializeColors() {
        if (theme == 0) {
            bgColor = new Color(245, 247, 250);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(30, 30, 30);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(59, 130, 246);
            progressBg = new Color(229, 231, 235);
            progressFill = new Color(34, 197, 94);
            successColor = new Color(16, 185, 129);
            warningColor = new Color(245, 158, 11);
            dangerColor = new Color(239, 68, 68);
        } else {
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(20, 20, 20);
            textPrimary = new Color(230, 230, 230);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(0, 120, 215);
            progressBg = new Color(30, 30, 30);
            progressFill = new Color(0, 200, 100);
            successColor = new Color(0, 200, 100);
            warningColor = new Color(255, 165, 0);
            dangerColor = new Color(220, 38, 38);
        }
    }

    private void initializeDemoData() {
        memberData = new MemberData(
                "Sophie",
                "Dubois",
                "sophie.dubois@example.com",
                "+33 6 12 34 56 78",
                LocalDate.of(2023, 5, 15),
                "Développeuse Frontend",
                "Membre Principal");

        tasks = new ArrayList<>();
        tasks.add(new TaskItem(
                1,
                "Intégration API Backend",
                "Développer et intégrer l'API REST pour la gestion des utilisateurs",
                75,
                "en cours",
                LocalDate.of(2024, 12, 15)));
        tasks.add(new TaskItem(
                2,
                "Refonte Interface Utilisateur",
                "Moderniser le design de l'application avec Material Design",
                100,
                "terminé",
                LocalDate.of(2024, 11, 30)));
        tasks.add(new TaskItem(
                3,
                "Tests Unitaires",
                "Écrire les tests unitaires pour les composants critiques",
                30,
                "en cours",
                LocalDate.of(2024, 12, 20)));
        tasks.add(new TaskItem(
                4,
                "Documentation Technique",
                "Rédiger la documentation technique complète du projet",
                0,
                "en attente",
                LocalDate.of(2024, 12, 25)));
        tasks.add(new TaskItem(
                5,
                "Optimisation Performance",
                "Améliorer les performances de chargement de l'application",
                45,
                "en cours",
                LocalDate.of(2024, 12, 18)));
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(bgColor);

        contentWrapper.add(createMemberHeaderSection());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));

        contentWrapper.add(createMemberInfoSection());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));

        contentWrapper.add(createTasksSection());
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 20)));

        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMemberHeaderSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.X_AXIS));
        section.setBackground(bgColor);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel avatarPanel = createAvatar(memberData.prenom, memberData.nom);
        section.add(avatarPanel);
        section.add(Box.createRigidArea(new Dimension(20, 0)));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(memberData.prenom + " " + memberData.nom);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        nameLabel.setForeground(textPrimary);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel(memberData.role + " • " + memberData.type);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        roleLabel.setForeground(textSecondary);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        infoPanel.add(roleLabel);

        section.add(infoPanel);
        section.add(Box.createHorizontalGlue());

        return section;
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
                g2.setFont(new Font("Segoe UI", Font.BOLD, 42));
                String initials = "" + prenom.charAt(0) + nom.charAt(0);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initials, x, y);

                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(100, 100));
        avatar.setMinimumSize(new Dimension(100, 100));
        avatar.setMaximumSize(new Dimension(100, 100));
        return avatar;
    }

    private JPanel createMemberInfoSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        JLabel headerLabel = new JLabel("Informations du membre");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(theme == 0 ? Color.BLACK : Color.WHITE);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(headerLabel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel infoGrid = new JPanel(new GridLayout(2, 2, 20, 15));
        infoGrid.setOpaque(false);
        infoGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        infoGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoGrid.add(createInfoCard("📧 Email", memberData.email, new Color(59, 130, 246), new Color(96, 165, 250)));
        infoGrid.add(createInfoCard("📱 Téléphone", memberData.telephone, new Color(16, 185, 129),
                new Color(52, 211, 153)));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);
        long daysSinceJoined = ChronoUnit.DAYS.between(memberData.dateRejointe, LocalDate.now());
        String joinedText = memberData.dateRejointe.format(formatter) + " (" + daysSinceJoined + " jours)";

        infoGrid.add(createInfoCard("📅 Date d'adhésion", joinedText, new Color(236, 72, 153),
                new Color(244, 114, 182)));
        infoGrid.add(createInfoCard("🎯 Rôle", memberData.role, new Color(245, 158, 11), new Color(251, 191, 36)));

        card.add(infoGrid);
        section.add(card);
        return section;
    }

    private JPanel createInfoCard(String label, String value, Color primaryColor, Color secondaryColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, primaryColor,
                        getWidth(), getHeight(), secondaryColor);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 18, 18);

                g2.dispose();
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelText.setForeground(new Color(255, 255, 255, 220));
        labelText.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel("<html><body style='width: 100%;'>" + value + "</body></html>");
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(labelText);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(valueLabel);

        return card;
    }

    private JPanel createTasksSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setOpaque(false);

        JLabel sectionTitle = new JLabel("Tâches assignées");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(theme == 0 ? Color.BLACK : Color.WHITE);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel countLabel = new JLabel(getCompletedTasks() + "/" + tasks.size());
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        countLabel.setForeground(new Color(139, 92, 246));
        countLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 92, 246, 100), 2, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        countLabel.setOpaque(false);

        titlePanel.add(sectionTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        titlePanel.add(countLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        section.add(headerPanel);
        section.add(Box.createRigidArea(new Dimension(0, 18)));

        if (tasks.isEmpty()) {
            JPanel emptyCard = new JPanel();
            emptyCard.setOpaque(false);
            emptyCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            emptyCard.setLayout(new GridBagLayout());

            JLabel emptyLabel = new JLabel("Aucune tâche assignée");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            emptyLabel.setForeground(textSecondary);
            emptyCard.add(emptyLabel);

            section.add(emptyCard);
        } else {
            for (TaskItem task : tasks) {
                section.add(createTaskCard(task));
                section.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        return section;
    }

    private JPanel createTaskCard(TaskItem task) {
        JPanel card = new JPanel() {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                if (isHovered) {
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                }

                g2.setStroke(new BasicStroke(2f));
                g2.setColor(isHovered ? accentColor : new Color(progressBg.getRed(), progressBg.getGreen(),
                        progressBg.getBlue(), 100));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);

                g2.dispose();
            }
        };

        card.setLayout(new BorderLayout(20, 15));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Params.tacheID = task.id;
                onClick.accept("Tache");
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ((JPanel) evt.getSource()).putClientProperty("isHovered", true);
                evt.getComponent().repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                ((JPanel) evt.getSource()).putClientProperty("isHovered", false);
                evt.getComponent().repaint();
            }
        });

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel titleLabel = new JLabel(task.titre);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(textPrimary);

        JLabel statusLabel = createStatusBadge(task.etat);

        titleRow.add(titleLabel, BorderLayout.WEST);
        titleRow.add(statusLabel, BorderLayout.EAST);

        JLabel descLabel = new JLabel("<html><body style='width: 100%;'>" + task.description + "</body></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(textSecondary);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleRow);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(descLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel progressPanel = createProgressBar(task.progres, false);
        progressPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(progressPanel);

        card.add(contentPanel, BorderLayout.CENTER);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setOpaque(false);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH);
        JLabel dateLabel = new JLabel("⏰ " + task.dateLimite.format(formatter));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(textSecondary);
        dateLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), task.dateLimite);
        String daysText;
        Color daysColor;

        if (task.etat.equals("terminé")) {
            daysText = "Terminé";
            daysColor = successColor;
        } else if (daysRemaining < 0) {
            daysText = "En retard";
            daysColor = dangerColor;
        } else if (daysRemaining == 0) {
            daysText = "Aujourd'hui";
            daysColor = warningColor;
        } else if (daysRemaining <= 3) {
            daysText = daysRemaining + " jours";
            daysColor = warningColor;
        } else {
            daysText = daysRemaining + " jours";
            daysColor = successColor;
        }

        JLabel remainingLabel = new JLabel(daysText);
        remainingLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        remainingLabel.setForeground(daysColor);
        remainingLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        sidePanel.add(dateLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        sidePanel.add(remainingLabel);

        card.add(sidePanel, BorderLayout.EAST);

        return card;
    }

    private JLabel createStatusBadge(String etat) {
        String text;
        Color bgColor;
        Color textColor = Color.WHITE;

        switch (etat.toLowerCase()) {
            case "terminé":
                text = "✓ Terminé";
                bgColor = successColor;
                break;
            case "en cours":
                text = "⚡ En cours";
                bgColor = accentColor;
                break;
            case "en attente":
                text = "⏸ En attente";
                bgColor = warningColor;
                break;
            default:
                text = etat;
                bgColor = textSecondary;
        }

        JLabel badge = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                super.paintComponent(g);
                g2.dispose();
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(textColor);
        badge.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        badge.setOpaque(false);
        return badge;
    }

    private JPanel createProgressBar(int progress, boolean large) {
        JPanel wrapper = new JPanel(new BorderLayout(10, 0));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, large ? 50 : 30));

        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int height = large ? 14 : 10;
                int y = (getHeight() - height) / 2;

                g2.setColor(progressBg);
                g2.fillRoundRect(0, y, getWidth(), height, height, height);

                int fillWidth = (int) (getWidth() * progress / 100.0);
                g2.setColor(progressFill);
                g2.fillRoundRect(0, y, fillWidth, height, height, height);

                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, large ? 14 : 10));
        wrapper.add(bar, BorderLayout.CENTER);

        JLabel percentLabel = new JLabel(progress + "%");
        percentLabel.setFont(new Font("Segoe UI", Font.BOLD, large ? 18 : 14));
        percentLabel.setForeground(textPrimary);
        wrapper.add(percentLabel, BorderLayout.EAST);

        return wrapper;
    }

    private int getCompletedTasks() {
        return (int) tasks.stream().filter(t -> t.etat.equals("terminé")).count();
    }

    private static class MemberData {
        String nom;
        String prenom;
        String email;
        String telephone;
        LocalDate dateRejointe;
        String role;
        String type;

        public MemberData(String prenom, String nom, String email, String telephone, LocalDate dateRejointe,
                String role, String type) {
            this.prenom = prenom;
            this.nom = nom;
            this.email = email;
            this.telephone = telephone;
            this.dateRejointe = dateRejointe;
            this.role = role;
            this.type = type;
        }
    }

    private static class TaskItem {
        int id;
        String titre;
        String description;
        int progres;
        String etat;
        LocalDate dateLimite;

        public TaskItem(int id, String titre, String description, int progres, String etat, LocalDate dateLimite) {
            this.id = id;
            this.titre = titre;
            this.description = description;
            this.progres = progres;
            this.etat = etat;
            this.dateLimite = dateLimite;
        }
    }
}