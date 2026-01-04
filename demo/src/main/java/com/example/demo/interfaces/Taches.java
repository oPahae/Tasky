package com.example.demo.interfaces;

import com.example.demo.Params;
import com.example.demo.components.Scrollbar;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.Queries;
import javax.swing.*;

import org.springframework.scheduling.config.Task;
import org.springframework.web.servlet.tags.Param;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Taches extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, progressBg, progressFill, dangerColor,
            borderColor;
    private List<Task> tasks;
    private List<ProjectMember> members;
    private int colorCounter = 0;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private Consumer<String> onClickHandler;
    private int projectId;
    private JPanel mainPanel;

    public Taches(Consumer<String> onClick) {
        this.theme = Params.theme;
        this.onClickHandler = onClick;
        this.projectId = Params.projetID;

        initializeColors();
        tasks = new ArrayList<>();
        members = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(bgColor);

        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        loadDataFromBackend();
    }

    private void loadDataFromBackend() {
        System.out.println("Chargement des tâches pour projectId: " + projectId);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/dashboard/projet/" + projectId + "/taches"))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        List<Map<String, Object>> tachesData = mapper.readValue(response.body(),
                                new com.fasterxml.jackson.core.type.TypeReference<>() {
                                });

                        tasks.clear();

                        for (Map<String, Object> tacheMap : tachesData) {
                            List<Integer> membreIds = ((List<?>) tacheMap.get("membreIds"))
                                    .stream()
                                    .map(id -> ((Number) id).intValue())
                                    .toList();
                            if (!membreIds.contains(Params.membreID)) continue;
                            
                            int id = ((Number) tacheMap.get("id")).intValue();
                            String titre = (String) tacheMap.getOrDefault("titre", "Sans titre");
                            String description = (String) tacheMap.getOrDefault("description", "");
                            String etat = (String) tacheMap.getOrDefault("etat", "En attente");
                            int prog = (int) tacheMap.getOrDefault("progres", 0);
                            String dateLimite = (String) tacheMap.getOrDefault("dateLimite", 0);

                            Task task = new Task(
                                    id,
                                    titre,
                                    prog,
                                    description,
                                    dateLimite,
                                    new ArrayList<>());
                            task.setState(etat);
                            tasks.add(task);

                            System.out.println("Tâche chargée: " + titre);
                        }

                        loadMembersFromBackend();

                    } catch (Exception e) {
                        System.err.println("Erreur parsing tâches: " + e.getMessage());
                        e.printStackTrace();
                        showErrorPanel("Erreur lors du chargement des tâches");
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Erreur HTTP tâches: " + e.getMessage());
                    showErrorPanel("Erreur serveur");
                    return null;
                });
    }

    private void loadMembersFromBackend() {
        System.out.println("Chargement des membres pour projectId: " + projectId);

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
                                new com.fasterxml.jackson.core.type.TypeReference<>() {
                                });

                        members.clear();

                        for (Map<String, Object> membreMap : membresData) {
                            String nom = (String) membreMap.getOrDefault("nom", "Inconnu");
                            String role = (String) membreMap.getOrDefault("role", "Membre");

                            String[] split = nom.split(" ", 2);
                            String firstName = split[0];
                            String lastName = split.length > 1 ? split[1] : "";

                            ProjectMember member = new ProjectMember(firstName, lastName, role);
                            members.add(member);
                        }

                        SwingUtilities.invokeLater(() -> {
                            System.out.println("Mise à jour UI avec " + tasks.size() + " tâches");
                            mainPanel.removeAll();
                            mainPanel.add(createMainPanel(onClickHandler), BorderLayout.CENTER);
                            mainPanel.revalidate();
                            mainPanel.repaint();
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

    private void showErrorPanel(String message) {
        SwingUtilities.invokeLater(() -> {
            mainPanel.removeAll();
            JPanel errorPanel = new JPanel();
            errorPanel.setBackground(bgColor);
            JLabel errorLabel = new JLabel(message);
            errorLabel.setForeground(dangerColor);
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            errorPanel.add(errorLabel);
            mainPanel.add(errorPanel, BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }

    private void initializeColors() {
        if (theme == 0) {
            bgColor = new Color(248, 250, 252);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(15, 23, 42);
            textSecondary = new Color(100, 116, 139);
            accentColor = new Color(59, 130, 246);
            progressBg = new Color(226, 232, 240);
            progressFill = new Color(34, 197, 94);
            dangerColor = new Color(239, 68, 68);
            borderColor = new Color(226, 232, 240);
        } else {
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(15, 15, 15);
            textPrimary = new Color(241, 245, 249);
            textSecondary = new Color(148, 163, 184);
            accentColor = new Color(59, 130, 246);
            progressBg = new Color(30, 41, 59);
            progressFill = new Color(34, 197, 94);
            dangerColor = new Color(239, 68, 68);
            borderColor = new Color(30, 41, 59);
        }
    }

    private JPanel createMainPanel(Consumer<String> onClick) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(bgColor);

        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        headerWrapper.setBackground(bgColor);
        headerWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(cardBgColor);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setBackground(cardBgColor);

        JLabel tasksTitle = new JLabel("Gestion des tâches");
        tasksTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        tasksTitle.setForeground(textPrimary);
        tasksTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(tasks.size() + " tâches actives");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(textSecondary);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleSection.add(tasksTitle);
        titleSection.add(Box.createRigidArea(new Dimension(0, 5)));
        titleSection.add(subtitle);

        header.add(titleSection, BorderLayout.WEST);

        if (Params.role.equals("R")) {
            JButton addBtn = new JButton("+ Nouvelle tâche") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gradient = new GradientPaint(
                            0, 0, accentColor,
                            getWidth(), getHeight(), new Color(139, 92, 246));
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            addBtn.setFocusPainted(false);
            addBtn.setBorderPainted(false);
            addBtn.setContentAreaFilled(false);
            addBtn.setForeground(Color.WHITE);
            addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            addBtn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
            addBtn.addActionListener(e -> onClick.accept("Ajouter une Tâche"));
            header.add(addBtn, BorderLayout.EAST);
        }

        headerWrapper.add(header);

        contentWrapper.add(headerWrapper);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));

        if (tasks.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucune tâche disponible");
            emptyLabel.setForeground(textSecondary);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentWrapper.add(emptyLabel);
        } else {
            for (Task task : tasks) {
                JPanel taskCard = createTaskCard(task, onClick);
                taskCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentWrapper.add(taskCard);
                contentWrapper.add(Box.createRigidArea(new Dimension(0, 15)));
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

    private JPanel createTaskCard(Task task, Consumer<String> onClick) {
        Color taskColor = getRandomColor();
        JPanel card = new JPanel() {
            private boolean hovered = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, hovered ? 35 : 20));
                g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, 20, 20);
                g2.setColor(cardBgColor);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);
                g2.setStroke(new BasicStroke(hovered ? 1.6f : 1f));
                g2.setColor(hovered ? taskColor.brighter() : borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
                GradientPaint gp = new GradientPaint(
                        0, 0, taskColor,
                        0, getHeight(), taskColor.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, 6, getHeight() - 4, 20, 20);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        card.setOpaque(false);
        card.setLayout(new BorderLayout(25, 20));
        card.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel left = new JPanel(new BorderLayout(15, 0));
        left.setOpaque(false);

        JCheckBox checkbox = new JCheckBox();
        checkbox.setSelected("Terminée".equals(task.getStatus()));
        checkbox.setEnabled(false);
        checkbox.setOpaque(false);
        checkbox.setPreferredSize(new Dimension(24, 24));
        left.add(checkbox, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(task.name);
        name.setFont(new Font("Segoe UI", Font.BOLD, 18));
        name.setForeground(textPrimary);

        JLabel desc = new JLabel(task.description);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(textSecondary);

        info.add(name);
        info.add(Box.createVerticalStrut(6));
        info.add(desc);

        left.add(info, BorderLayout.CENTER);
        card.add(left, BorderLayout.WEST);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        topRow.setOpaque(false);

        JLabel status = createStatusBadge(task.getStatus());
        topRow.add(status);

        JLabel percent = new JLabel(task.progress + "%");
        percent.setFont(new Font("Segoe UI", Font.BOLD, 16));
        percent.setForeground(textPrimary);
        topRow.add(percent);

        if (Params.role.equals("R")) {
            ImageIcon icon = new ImageIcon(getClass().getResource("/assets/bin.png"));
            Image scaled = icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);

            JButton deleteBtn = new JButton(new ImageIcon(scaled));
            deleteBtn.setBorderPainted(false);
            deleteBtn.setContentAreaFilled(false);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteBtn.setToolTipText("Supprimer la tâche");
            deleteBtn.addActionListener(e -> deleteTacheFromBackend(task));

            topRow.add(deleteBtn);
        }

        right.add(topRow);
        right.add(Box.createVerticalStrut(12));

        JPanel progressBar = createProgressBar(task.progress, taskColor);
        right.add(progressBar);
        right.add(Box.createVerticalStrut(14));

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomRow.setOpaque(false);

        JLabel deadline = new JLabel(task.deadline);
        deadline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deadline.setForeground(textSecondary);
        bottomRow.add(deadline);

        JPanel members = createMembersAvatars(task.assignedMembers);
        bottomRow.add(members);

        right.add(bottomRow);
        card.add(right, BorderLayout.EAST);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!(e.getSource() instanceof JButton)) {
                    Params.tacheID = task.id;
                    onClick.accept("Tache");
                }
            }
        });

        return card;
    }

    private void deleteTacheFromBackend(Task task) {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous vraiment supprimer la tâche \"" + task.name + "\" ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            System.out.println("suppression...");
            Queries.delete("/api/tache/delete/" + task.id)
                    .thenAccept(response -> {
                        System.out.println("Tâche supprimée: " + response);
                        SwingUtilities.invokeLater(this::loadDataFromBackend);
                    })
                    .exceptionally(e -> {
                        System.err.println("Erreur suppression: " + e.getMessage());
                        JOptionPane.showMessageDialog(this, "Erreur lors de la suppression", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        return null;
                    });
        }
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
                        bgColor = new Color(16, 185, 129, 40);
                        fgColor = new Color(5, 150, 105);
                        break;
                    case "En cours":
                        bgColor = new Color(245, 158, 11, 40);
                        fgColor = new Color(217, 119, 6);
                        break;
                    default:
                        bgColor = new Color(139, 92, 246, 40);
                        fgColor = new Color(124, 58, 237);
                        break;
                }

                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                setForeground(fgColor);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        badge.setOpaque(false);
        return badge;
    }

    private JPanel createProgressBar(int progress, Color color) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(320, 12));
        wrapper.setPreferredSize(new Dimension(320, 12));

        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(progressBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                int fillWidth = (int) (getWidth() * progress / 100.0);
                if (fillWidth > 0) {
                    GradientPaint gradient = new GradientPaint(
                            0, 0, color,
                            fillWidth, 0, color.brighter());
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, fillWidth, getHeight(), 12, 12);
                }

                g2.dispose();
            }
        };
        bar.setOpaque(false);
        wrapper.add(bar, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createMembersAvatars(List<ProjectMember> members) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.add(Box.createHorizontalGlue());

        int maxDisplay = Math.min(members.size(), 3);
        for (int i = 0; i < maxDisplay; i++) {
            ProjectMember member = members.get(i);
            Color avatarColor = getRandomColor();
            JPanel avatar = createMemberAvatar(member, avatarColor);
            avatar.setToolTipText(member.firstName + " " + member.lastName + " - " + member.role);
            panel.add(avatar);
        }

        if (members.size() > 3) {
            JPanel moreAvatar = createMoreAvatar(members.size() - 3);
            moreAvatar.setToolTipText("+" + (members.size() - 3) + " autres membres");
            panel.add(moreAvatar);
        }

        return panel;
    }

    private JPanel createMemberAvatar(ProjectMember member, Color color) {
        JPanel avatar = new JPanel() {
            private boolean hovered = false;

            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        hovered = true;
                        repaint();
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (hovered) {
                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.fillOval(2, 2, getWidth(), getHeight());
                }

                g2.setColor(cardBgColor);
                g2.fillOval(0, 0, getWidth(), getHeight());

                g2.setColor(hovered ? color.brighter() : color);
                g2.fillOval(3, 3, getWidth() - 6, getHeight() - 6);

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
        avatar.setPreferredSize(new Dimension(42, 42));
        avatar.setOpaque(false);
        avatar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return avatar;
    }

    private JPanel createMoreAvatar(int count) {
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(cardBgColor);
                g2.fillOval(0, 0, getWidth(), getHeight());

                g2.setColor(progressBg);
                g2.fillOval(3, 3, getWidth() - 6, getHeight() - 6);

                g2.setColor(textSecondary);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                String text = "+" + count;
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(text, x, y);

                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(42, 42));
        avatar.setOpaque(false);
        avatar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return avatar;
    }

    private Color getRandomColor() {
        Color[] colors = {
                new Color(139, 92, 246),
                new Color(249, 115, 22),
                new Color(34, 197, 94),
                new Color(59, 130, 246),
                new Color(236, 72, 153),
                new Color(20, 184, 166),
        };

        if (colorCounter >= colors.length)
            colorCounter = 0;
        return colors[colorCounter++];
    }

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
        String description;
        String deadline;
        List<ProjectMember> assignedMembers;
        String state;

        public Task(int id, String name, int progress, String description, String deadline,
                List<ProjectMember> assignedMembers) {
            this.id = id;
            this.name = name;
            this.progress = progress;
            this.description = description;
            this.deadline = deadline;
            this.assignedMembers = assignedMembers;
            this.state = "En attente";
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getStatus() {
            if (progress == 100) {
                return "Terminée";
            } else if (progress == 0) {
                return "En attente";
            } else {
                return "En cours";
            }
        }
    }
}
