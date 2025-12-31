package com.example.demo.interfaces;

import com.example.demo.Params;
import com.example.demo.components.Scrollbar;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.Queries;

import javax.swing.*;

import org.springframework.scheduling.config.Task;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class Dashboard extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, progressBg, progressFill;
    private List<ProjectMember> members;
    private List<Task> tasks;
    private JPanel mainContentPanel;
    private CardLayout contentLayout;
    private int colorCounter = 0;
    private Consumer<String> onClick;
    private int projectId;

    public Dashboard(Consumer<String> onClick) {
        this.theme = Params.theme;
        this.onClick = onClick;
        this.projectId = Params.projetID; // Assurez-vous que projectId est défini dans Params

        initializeColors();
        members = new ArrayList<>();
        tasks = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(bgColor);
        contentLayout = new CardLayout();
        mainContentPanel = new JPanel(contentLayout);
        mainContentPanel.setBackground(bgColor);

        JPanel overviewPanel = createOverviewPanel();
        JPanel memberDetailPanel = createMemberDetailPanel(null, getRandomColor());

        mainContentPanel.add(overviewPanel, "overview");
        mainContentPanel.add(memberDetailPanel, "detail");

        add(mainContentPanel, BorderLayout.CENTER);
        contentLayout.show(mainContentPanel, "overview");

        loadDataFromBackend();
    }

    private void loadDataFromBackend() {
        System.out.println("Chargement des données du backend pour projectId: " + projectId);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/dashboard/projet/" + projectId + "/taches"))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        List<Map<String, Object>> tachesData = mapper.readValue(response.body(), new TypeReference<>() {
                        });

                        tasks.clear();

                        for (Map<String, Object> tacheMap : tachesData) {
                            String nom = (String) tacheMap.getOrDefault("titre", "Sans nom");
                            String etat = (String) tacheMap.getOrDefault("etat", "À faire");
                            int prog = (int) tacheMap.getOrDefault("progres", 0);

                            Task task = new Task(nom, prog, etat);
                            tasks.add(task);
                            System.out.println("Tâche ajoutée: " + nom);
                        }

                        loadMembersFromBackend();

                    } catch (Exception e) {
                        System.err.println("Erreur parsing tâches: " + e.getMessage());
                        e.printStackTrace();
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Erreur HTTP tâches: " + e.getMessage());
                    return null;
                });
    }

    private void loadMembersFromBackend() {
        System.out.println("Chargement des membres du backend");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/dashboard/projet/" + projectId + "/Membre"))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();

                        List<Map<String, Object>> membresData = mapper.readValue(response.body(),
                                new TypeReference<>() {
                                });

                        members.clear();

                        for (Map<String, Object> membreMap : membresData) {
                            Integer id = ((Number) membreMap.get("id")).intValue();
                            String nom = (String) membreMap.getOrDefault("nom", "Inconnu");
                            String email = (String) membreMap.getOrDefault("email", "");
                            String role = (String) membreMap.getOrDefault("role", "Membre");

                            String[] nomSplit = nom.split(" ", 2);
                            String firstName = nomSplit[0];
                            String lastName = nomSplit.length > 1 ? nomSplit[1] : "";

                            List<String> assignedTasks = getAssignedTasksForMember(email);

                            ProjectMember member = new ProjectMember(
                                    id, firstName, lastName, role, assignedTasks);
                            members.add(member);

                            System.out.println("Membre ajouté: " + firstName + " " + lastName);
                        }

                        SwingUtilities.invokeLater(() -> {
                            mainContentPanel.removeAll();
                            mainContentPanel.add(createOverviewPanel(), "overview");
                            mainContentPanel.add(createMemberDetailPanel(null, getRandomColor()), "detail");
                            contentLayout.show(mainContentPanel, "overview");
                            mainContentPanel.revalidate();
                            mainContentPanel.repaint();
                        });

                    } catch (Exception e) {
                        System.err.println("Erreur parsing membres: " + e.getMessage());
                        e.printStackTrace();
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Erreur HTTP membres: " + e.getMessage());
                    return null;
                });
    }

    private List<String> getAssignedTasksForMember(String email) {
        List<String> assigned = new ArrayList<>();
        for (Task task : tasks) {
            assigned.add(task.name);
        }
        return assigned;
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

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(bgColor);

        JLabel titleLabel = new JLabel("Tableau de bord du projet");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(titleLabel);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel statsPanel = createStatsPanel();
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(statsPanel);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel progressPanel = createProgressSection();
        progressPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(progressPanel);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel membersPanel = createMembersSection();
        membersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(membersPanel);

        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setBackground(bgColor);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream().filter(t -> t.progress == 100).count();
        int inProgressTasks = totalTasks - completedTasks;
        int totalMembers = members.size();

        panel.add(createStatCard("Tâches totales", String.valueOf(totalTasks),
                new Color(99, 102, 241), "/assets/tasks.png"));
        panel.add(createStatCard("En cours", String.valueOf(inProgressTasks),
                new Color(245, 158, 11), "/assets/history.png"));
        panel.add(createStatCard("Terminées", String.valueOf(completedTasks),
                new Color(16, 185, 129), "/assets/done.png"));
        panel.add(createStatCard("Membres", String.valueOf(totalMembers),
                new Color(139, 92, 246), "/assets/users.png"));

        return panel;
    }

    private JPanel createStatCard(String label, String value, Color accentColor, String img) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 5, getHeight(), 5, 5);

                g2.dispose();
            }
        };

        card.setLayout(new BorderLayout(15, 10));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon icon = new ImageIcon(getClass().getResource(img));
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(38, 38, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);
        JLabel iconLabel = new JLabel(icon);
        card.add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(textPrimary);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelText.setForeground(textSecondary);
        labelText.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(valueLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(labelText);

        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createProgressSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));

        JLabel sectionTitle = new JLabel("Progrès du projet");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(textPrimary);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionTitle);
        section.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        double overallProgress = tasks.stream().mapToInt(t -> t.progress).average().orElse(0);
        JLabel overallLabel = new JLabel("Progrès global");
        overallLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        overallLabel.setForeground(textPrimary);
        overallLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(overallLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel overallBar = createProgressBar((int) overallProgress, true, getRandomColor());
        overallBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(overallBar);
        card.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel tasksLabel = new JLabel("Tâches individuelles");
        tasksLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tasksLabel.setForeground(textPrimary);
        tasksLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(tasksLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));

        for (Task task : tasks) {
            JPanel taskRow = new JPanel(new BorderLayout(15, 0));
            taskRow.setOpaque(false);
            taskRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            taskRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel taskInfo = new JPanel();
            taskInfo.setLayout(new BoxLayout(taskInfo, BoxLayout.Y_AXIS));
            taskInfo.setOpaque(false);

            JLabel taskName = new JLabel(task.name);
            taskName.setFont(new Font("Segoe UI", Font.BOLD, 14));
            taskName.setForeground(textPrimary);
            taskName.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel taskCategory = new JLabel(task.category);
            taskCategory.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            taskCategory.setForeground(textSecondary);
            taskCategory.setAlignmentX(Component.LEFT_ALIGNMENT);

            taskInfo.add(taskName);
            taskInfo.add(taskCategory);
            taskRow.add(taskInfo, BorderLayout.WEST);

            JPanel barWrapper = new JPanel(new BorderLayout());
            barWrapper.setOpaque(false);
            barWrapper.setPreferredSize(new Dimension(300, 40));
            JPanel taskBar = createProgressBar(task.progress, false, getRandomColor());
            barWrapper.add(taskBar, BorderLayout.CENTER);
            taskRow.add(barWrapper, BorderLayout.EAST);

            card.add(taskRow);
            card.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        section.add(card);
        return section;
    }

    private JPanel createProgressBar(int progress, boolean large, Color color) {
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
                g2.setColor(color);
                g2.fillRoundRect(0, y, fillWidth, height, height, height);

                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, large ? 14 : 10));
        wrapper.add(bar, BorderLayout.CENTER);

        JLabel percentLabel = new JLabel(progress + "%");
        percentLabel.setFont(new Font("Segoe UI", Font.BOLD, large ? 16 : 13));
        percentLabel.setForeground(textPrimary);
        wrapper.add(percentLabel, BorderLayout.EAST);

        return wrapper;
    }

    private JPanel createMembersSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(bgColor);

        JLabel sectionTitle = new JLabel("Membres du projet");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(textPrimary);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionTitle);
        section.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel membersGrid = new JPanel(new GridLayout(0, 2, 20, 20));
        membersGrid.setBackground(bgColor);
        membersGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        for (ProjectMember member : members) {
            membersGrid.add(createMemberCard(member, getRandomColor()));
        }

        section.add(membersGrid);
        return section;
    }

    private JPanel createMemberCard(ProjectMember member, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(15, 15));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setOpaque(false);

        JPanel avatar = createAvatar(member, color);
        header.add(avatar, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel nameLabel = new JLabel(member.firstName + " " + member.lastName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(textPrimary);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel(member.role);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleLabel.setForeground(textSecondary);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(nameLabel);
        info.add(Box.createRigidArea(new Dimension(0, 3)));
        info.add(roleLabel);

        header.add(info, BorderLayout.CENTER);
        card.add(header, BorderLayout.NORTH);

        JPanel tasksPanel = new JPanel();
        tasksPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        tasksPanel.setOpaque(false);

        List<String> sublist = member.assignedTasks.size() > 1 ? member.assignedTasks.subList(0, 2) : member.assignedTasks;
        sublist.add("...");

        for (String task : sublist) {
            JLabel taskLabel = new JLabel(task) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(color.getRed(), color.getGreen(),
                            color.getBlue(), 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            taskLabel.setForeground(color);
            taskLabel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            taskLabel.setOpaque(false);
            tasksPanel.add(taskLabel);
        }

        card.add(tasksPanel, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.out.println(member.id);
                Params.membreID = member.id;
                onClick.accept("Membre");
            }
        });

        return card;
    }

    private JPanel createAvatar(ProjectMember member, Color color) {
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(color);
                g2.fillOval(0, 0, getWidth(), getHeight());

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 20));

                // Check if firstName and lastName are not empty
                String firstInitial = (member.firstName != null && !member.firstName.isEmpty())
                        ? String.valueOf(member.firstName.charAt(0))
                        : "";
                String lastInitial = (member.lastName != null && !member.lastName.isEmpty())
                        ? String.valueOf(member.lastName.charAt(0))
                        : "";

                String initials = firstInitial + lastInitial;

                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);

                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(50, 50));
        avatar.setOpaque(false);
        return avatar;
    }

    private JPanel createMemberDetailPanel(ProjectMember member, Color color) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        if (member == null)
            return panel;

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(bgColor);

        JButton backButton = new JButton("← Retour");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setForeground(accentColor);
        backButton.setBackground(cardBgColor);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.addActionListener(e -> {
            contentLayout.show(mainContentPanel, "overview");
        });
        contentWrapper.add(backButton);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel headerCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        headerCard.setLayout(new BorderLayout(20, 0));
        headerCard.setOpaque(false);
        headerCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        headerCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel largeAvatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 40));
                String initials = String.valueOf(member.firstName.charAt(0)) + member.lastName.charAt(0);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
        largeAvatar.setPreferredSize(new Dimension(100, 100));
        largeAvatar.setOpaque(false);
        headerCard.add(largeAvatar, BorderLayout.WEST);

        JPanel memberInfo = new JPanel();
        memberInfo.setLayout(new BoxLayout(memberInfo, BoxLayout.Y_AXIS));
        memberInfo.setOpaque(false);

        JLabel nameLabel = new JLabel(member.firstName + " " + member.lastName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        nameLabel.setForeground(textPrimary);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel(member.role);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        roleLabel.setForeground(textSecondary);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel taskCountLabel = new JLabel(member.assignedTasks.size() + " tâches assignées");
        taskCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskCountLabel.setForeground(color);
        taskCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        memberInfo.add(nameLabel);
        memberInfo.add(Box.createRigidArea(new Dimension(0, 8)));
        memberInfo.add(roleLabel);
        memberInfo.add(Box.createRigidArea(new Dimension(0, 8)));
        memberInfo.add(taskCountLabel);

        headerCard.add(memberInfo, BorderLayout.CENTER);
        contentWrapper.add(headerCard);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel tasksTitle = new JLabel("Tâches assignées");
        tasksTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tasksTitle.setForeground(textPrimary);
        tasksTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(tasksTitle);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 15)));

        for (String taskName : member.assignedTasks) {
            Task task = tasks.stream().filter(t -> t.name.equals(taskName)).findFirst().orElse(null);
            if (task != null) {
                JPanel taskCard = createDetailTaskCard(task, color);
                taskCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentWrapper.add(taskCard);
                contentWrapper.add(Box.createRigidArea(new Dimension(0, 12)));
            }
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

    private JPanel createDetailTaskCard(Task task, Color memberColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(memberColor);
                g2.fillRoundRect(0, 0, 5, getHeight(), 5, 5);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(15, 10));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel taskName = new JLabel(task.name);
        taskName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        taskName.setForeground(textPrimary);
        taskName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel category = new JLabel(task.category);
        category.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        category.setForeground(textSecondary);
        category.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(taskName);
        textPanel.add(Box.createRigidArea(getPreferredSize()));
        textPanel.add(category);
        card.add(textPanel, BorderLayout.WEST);

        JPanel progressWrapper = new JPanel(new BorderLayout());
        progressWrapper.setOpaque(false);
        progressWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        JPanel progressBar = createProgressBar(task.progress, false, getRandomColor());
        progressWrapper.add(progressBar, BorderLayout.CENTER);
        card.add(progressWrapper, BorderLayout.EAST);

        return card;
    }

    private Color getRandomColor() {
        Color[] colors = {
                new Color(150, 100, 250),
                new Color(255, 165, 0),
                new Color(0, 200, 0),
                new Color(0, 120, 255),
                new Color(255, 105, 180),
        };

        if (colorCounter == colors.length)
            colorCounter = 0;
        return colors[colorCounter++];
    }

    private static class ProjectMember {
        int id;
        String firstName;
        String lastName;
        String role;
        List<String> assignedTasks;

        public ProjectMember(int id, String firstName, String lastName, String role, List<String> assignedTasks) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
            this.assignedTasks = assignedTasks;
        }
    }

    private static class Task {
        String name;
        int progress;
        String category;

        public Task(String name, int progress, String category) {
            this.name = name;
            this.progress = progress;
            this.category = category;
        }
    }
}