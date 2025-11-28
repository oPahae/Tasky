package com.example.demo.interfaces;

import com.example.demo.Params;
import com.example.demo.components.Scrollbar;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.catalina.Container;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Taches extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, progressBg, progressFill;
    private List<Task> tasks;
    private List<ProjectMember> members;
    private int colorCounter = 0;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public Taches(Consumer<String> onClick) {
        this.theme = Params.theme;
        initializeColors();
        initializeDemoData();
        setLayout(new BorderLayout());
        setBackground(bgColor);

        JPanel mainPanel = createMainPanel(onClick);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void initializeColors() {
        if (theme == 0) { // Light mode
            bgColor = new Color(245, 247, 250);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(30, 30, 30);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(59, 130, 246);
            progressBg = new Color(229, 231, 235);
            progressFill = new Color(34, 197, 94);
        } else { // Dark mode
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(20, 20, 20);
            textPrimary = new Color(230, 230, 230);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(0, 120, 215);
            progressBg = new Color(30, 30, 30);
            progressFill = new Color(0, 200, 100);
        }
    }

    private void initializeDemoData() {
        members = new ArrayList<>();
        tasks = new ArrayList<>();

        // Demo members
        members.add(new ProjectMember("Alice", "Martin", "Chef de projet"));
        members.add(new ProjectMember("Bob", "Durant", "Développeur Backend"));
        members.add(new ProjectMember("Charlie", "Dubois", "Développeur Frontend"));
        members.add(new ProjectMember("Diana", "Rousseau", "QA Engineer"));

        // Demo tasks avec deadline et membres assignés
        tasks.add(new Task("Conception UI/UX", 100, "Design",
                LocalDate.now().plusDays(5),
                Arrays.asList(members.get(0))));

        tasks.add(new Task("API Backend", 75, "Backend",
                LocalDate.now().plusDays(10),
                Arrays.asList(members.get(1))));

        tasks.add(new Task("Intégration Frontend", 60, "Frontend",
                LocalDate.now().plusDays(15),
                Arrays.asList(members.get(2), members.get(0))));

        tasks.add(new Task("Tests unitaires", 40, "Testing",
                LocalDate.now().plusDays(20),
                Arrays.asList(members.get(3), members.get(1))));

        tasks.add(new Task("Documentation", 30, "Docs",
                LocalDate.now().plusDays(25),
                Arrays.asList(members.get(0))));

        tasks.add(new Task("Déploiement", 0, "DevOps",
                LocalDate.now().plusDays(30),
                Arrays.asList(members.get(1), members.get(2))));

        tasks.add(new Task("Revue de code", 85, "Quality",
                LocalDate.now().plusDays(7),
                Arrays.asList(members.get(1), members.get(3))));

        tasks.add(new Task("Optimisation performance", 50, "Backend",
                LocalDate.now().plusDays(12),
                Arrays.asList(members.get(1), members.get(2), members.get(0))));
    }

    private JPanel createMainPanel(Consumer<String> onClick) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(bgColor);

        // Tasks section
        JLabel tasksTitle = new JLabel("Toutes les tâches");
        tasksTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tasksTitle.setForeground(textPrimary);
        tasksTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(tasksTitle);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 15)));

        // Tasks list
        for (Task task : tasks) {
            JPanel taskCard = createTaskCard(task, onClick);
            taskCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentWrapper.add(taskCard);
            contentWrapper.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTaskCard(Task task, Consumer<String> onClick) {
        Color taskColor = getRandomColor();
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(taskColor);
                g2.fillRoundRect(0, 0, 5, getHeight(), 5, 5);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(20, 15));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ajout de l'écouteur de clic
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Params.tacheID = task.id;
                onClick.accept("Tache");
            }
        });

        // Left section: Checkbox + Task info
        JPanel leftSection = new JPanel(new BorderLayout(15, 0));
        leftSection.setOpaque(false);
        // Checkbox
        JCheckBox checkbox = new JCheckBox();
        checkbox.setSelected(task.progress == 100);
        checkbox.setEnabled(false);
        checkbox.setOpaque(false);
        checkbox.setPreferredSize(new Dimension(24, 24));
        leftSection.add(checkbox, BorderLayout.WEST);
        // Task info
        JPanel taskInfo = new JPanel();
        taskInfo.setLayout(new BoxLayout(taskInfo, BoxLayout.Y_AXIS));
        taskInfo.setOpaque(false);
        JLabel taskName = new JLabel(task.name);
        taskName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        taskName.setForeground(textPrimary);
        taskName.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel category = new JLabel(task.category);
        category.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        category.setForeground(textSecondary);
        category.setAlignmentX(Component.LEFT_ALIGNMENT);
        taskInfo.add(taskName);
        taskInfo.add(Box.createRigidArea(new Dimension(0, 5)));
        taskInfo.add(category);
        leftSection.add(taskInfo, BorderLayout.CENTER);
        card.add(leftSection, BorderLayout.WEST);
        // Right section: Status, Progress, Deadline, Members
        JPanel rightSection = new JPanel();
        rightSection.setLayout(new BoxLayout(rightSection, BoxLayout.Y_AXIS));
        rightSection.setOpaque(false);
        rightSection.setAlignmentX(Component.RIGHT_ALIGNMENT);
        // Status and Progress row
        JPanel statusProgressRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statusProgressRow.setOpaque(false);
        // Status badge
        JLabel statusLabel = createStatusBadge(task.getStatus());
        statusProgressRow.add(statusLabel);
        // Progress percentage
        JLabel progressLabel = new JLabel(task.progress + "%");
        progressLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        progressLabel.setForeground(textPrimary);
        statusProgressRow.add(progressLabel);
        rightSection.add(statusProgressRow);
        rightSection.add(Box.createRigidArea(new Dimension(0, 8)));
        // Progress bar
        JPanel progressBar = createProgressBar(task.progress, taskColor);
        progressBar.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightSection.add(progressBar);
        rightSection.add(Box.createRigidArea(new Dimension(0, 10)));
        // Deadline and Members row
        JPanel deadlineMembersRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        deadlineMembersRow.setOpaque(false);
        // Deadline
        JLabel deadlineLabel = new JLabel("📅 " + task.deadline.format(dateFormatter));
        deadlineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deadlineLabel.setForeground(textSecondary);
        deadlineMembersRow.add(deadlineLabel);
        // Members avatars
        JPanel membersPanel = createMembersAvatars(task.assignedMembers);
        deadlineMembersRow.add(membersPanel);
        rightSection.add(deadlineMembersRow);
        card.add(rightSection, BorderLayout.EAST);
        return card;
    }

    private JLabel createStatusBadge(String status) {
        JLabel badge = new JLabel(status) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor, fgColor;
                switch (getText()) {
                    case "Terminée":
                        bgColor = new Color(16, 185, 129, 30);
                        fgColor = new Color(16, 185, 129);
                        break;
                    case "En cours":
                        bgColor = new Color(245, 158, 11, 30);
                        fgColor = new Color(245, 158, 11);
                        break;
                    default: // En attente
                        bgColor = new Color(139, 92, 246, 30);
                        fgColor = new Color(139, 92, 246);
                        break;
                }

                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                setForeground(fgColor);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        badge.setOpaque(false);
        return badge;
    }

    private JPanel createProgressBar(int progress, Color color) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(300, 10));
        wrapper.setPreferredSize(new Dimension(300, 10));

        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                g2.setColor(progressBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Progress fill
                int fillWidth = (int) (getWidth() * progress / 100.0);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, fillWidth, getHeight(), 10, 10);

                g2.dispose();
            }
        };
        bar.setOpaque(false);
        wrapper.add(bar, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createMembersAvatars(List<ProjectMember> members) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0)); // Espacement positif
        panel.setOpaque(false);
        for (int i = 0; i < members.size(); i++) {
            ProjectMember member = members.get(i);
            Color avatarColor = getRandomColor();
            JPanel avatar = createMemberAvatar(member, avatarColor);
            avatar.setToolTipText(member.firstName + " " + member.lastName);
            panel.add(avatar);
        }
        return panel;
    }

    private JPanel createMemberAvatar(ProjectMember member, Color color) {
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Circle background with white border
                g2.setColor(cardBgColor);
                g2.fillOval(0, 0, getWidth(), getHeight());

                g2.setColor(color);
                g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);

                // Initials
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                String initials = String.valueOf(member.firstName.charAt(0)) + member.lastName.charAt(0);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);

                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setOpaque(false);
        avatar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return avatar;
    }

    private Color getRandomColor() {
        Color[] colors = {
                new Color(150, 100, 250), // violet
                new Color(255, 165, 0), // orange
                new Color(0, 200, 0), // vert
                new Color(0, 120, 255), // bleu
                new Color(255, 105, 180), // rose
        };

        if (colorCounter == colors.length)
            colorCounter = 0;
        return colors[colorCounter++];
    }

    // Inner classes
    private static class ProjectMember {
        String firstName;
        String lastName;
        String role;

        public ProjectMember(String firstName, String lastName, String role) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
        }
    }

    private static class Task {
        int id;
        String name;
        int progress;
        String category;
        LocalDate deadline;
        List<ProjectMember> assignedMembers;
        private static int nextId = 1;

        public Task(String name, int progress, String category, LocalDate deadline,
                List<ProjectMember> assignedMembers) {
            this.id = nextId++;
            this.name = name;
            this.progress = progress;
            this.category = category;
            this.deadline = deadline;
            this.assignedMembers = assignedMembers;
        }

        public String getStatus() {
            if (progress == 100)
                return "Terminée";
            if (progress > 0)
                return "En cours";
            return "En attente";
        }
    }
}