package com.example.demo.interfaces;

import com.example.demo.Params;
import com.example.demo.components.Scrollbar;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class AjouterTache extends JPanel {
    private int theme;
    private Color bgColor, cardBgColor, textPrimary, textSecondary, accentColor, inputBg, borderColor;
    private List<ProjectMember> members;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField dateLimitField;
    private Map<Integer, JCheckBox> memberCheckboxes;
    private Consumer<String> onClick;

    public AjouterTache(Consumer<String> onClick) {
        this.theme = Params.theme;
        initializeColors();
        initializeDemoMembers();
        memberCheckboxes = new HashMap<>();
        
        setLayout(new BorderLayout());
        setBackground(bgColor);
        
        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(bgColor);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // Title
        JLabel titleLabel = new JLabel("Ajouter une nouvelle tâche");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(titleLabel);
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Main card
        JPanel mainCard = createMainCard();
        mainCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.add(mainCard);
        
        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initializeColors() {
        if (theme == 0) { // Light mode
            bgColor = new Color(245, 247, 250);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(30, 30, 30);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(59, 130, 246);
            inputBg = new Color(249, 250, 251);
            borderColor = new Color(229, 231, 235);
        } else { // Dark mode
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(20, 20, 20);
            textPrimary = new Color(230, 230, 230);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(0, 120, 215);
            inputBg = new Color(30, 30, 30);
            borderColor = new Color(45, 45, 45);
        }
    }

    private void initializeDemoMembers() {
        members = new ArrayList<>();
        members.add(new ProjectMember(1, "Alice", "Martin", "Chef de projet"));
        members.add(new ProjectMember(2, "Bob", "Durant", "Développeur Backend"));
        members.add(new ProjectMember(3, "Charlie", "Dubois", "Développeur Frontend"));
        members.add(new ProjectMember(4, "Diana", "Rousseau", "QA Engineer"));
    }

    private JPanel createMainCard() {
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
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Title field
        JLabel titleLabelField = new JLabel("Titre de la tâche");
        titleLabelField.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabelField.setForeground(textPrimary);
        titleLabelField.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabelField);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        titleField = createStyledTextField("Entrez le titre de la tâche");
        titleField.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleField);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Description field
        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        descLabel.setForeground(textPrimary);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(descLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel descPanel = createStyledTextArea();
        descPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(descPanel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Date limit field
        JLabel dateLabel = new JLabel("Date limite");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dateLabel.setForeground(textPrimary);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(dateLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        dateLimitField = createStyledTextField("JJ/MM/AAAA");
        dateLimitField.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(dateLimitField);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        // Members section
        JLabel membersLabel = new JLabel("Affecter aux membres");
        membersLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        membersLabel.setForeground(textPrimary);
        membersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(membersLabel);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel membersPanel = createMembersCheckboxPanel();
        membersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(membersPanel);
        card.add(Box.createRigidArea(new Dimension(0, 30)));

        // Add button
        JButton addButton = createStyledButton();
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(addButton);

        return card;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(inputBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(textPrimary);
        field.setCaretColor(textPrimary);
        field.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        field.setPreferredSize(new Dimension(0, 48));
        
        // Placeholder effect
        field.setText(placeholder);
        field.setForeground(textSecondary);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(textPrimary);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(textSecondary);
                }
            }
        });
        
        return field;
    }

    private JPanel createStyledTextArea() {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(inputBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        wrapper.setPreferredSize(new Dimension(0, 120));
        wrapper.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        descriptionArea = new JTextArea();
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setForeground(textPrimary);
        descriptionArea.setCaretColor(textPrimary);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setOpaque(false);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        
        String placeholder = "Décrivez la tâche en détail...";
        descriptionArea.setText(placeholder);
        descriptionArea.setForeground(textSecondary);
        descriptionArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (descriptionArea.getText().equals(placeholder)) {
                    descriptionArea.setText("");
                    descriptionArea.setForeground(textPrimary);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (descriptionArea.getText().isEmpty()) {
                    descriptionArea.setText(placeholder);
                    descriptionArea.setForeground(textSecondary);
                }
            }
        });

        wrapper.add(descriptionArea, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createMembersCheckboxPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(inputBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        for (ProjectMember member : members) {
            JPanel memberRow = createMemberCheckboxRow(member);
            memberRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(memberRow);
            if (members.indexOf(member) < members.size() - 1) {
                panel.add(Box.createRigidArea(new Dimension(0, 12)));
            }
        }

        return panel;
    }

    private JPanel createMemberCheckboxRow(ProjectMember member) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Avatar
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentColor);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String initials = String.valueOf(member.firstName.charAt(0)) + member.lastName.charAt(0);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(45, 45));
        avatar.setOpaque(false);
        row.add(avatar, BorderLayout.WEST);

        // Member info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel nameLabel = new JLabel(member.firstName + " " + member.lastName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(textPrimary);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel(member.role);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(textSecondary);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(nameLabel);
        info.add(Box.createRigidArea(new Dimension(0, 2)));
        info.add(roleLabel);
        row.add(info, BorderLayout.CENTER);

        // Checkbox
        JCheckBox checkbox = new JCheckBox();
        checkbox.setOpaque(false);
        checkbox.setFocusPainted(false);
        checkbox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkbox.setPreferredSize(new Dimension(24, 24));
        memberCheckboxes.put(member.id, checkbox);
        row.add(checkbox, BorderLayout.EAST);

        return row;
    }

    private JButton createStyledButton() {
        JButton button = new JButton("Ajouter la tâche") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(accentColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(
                        Math.min(255, accentColor.getRed() + 20),
                        Math.min(255, accentColor.getGreen() + 20),
                        Math.min(255, accentColor.getBlue() + 20)
                    ));
                } else {
                    g2.setColor(accentColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(14, 30, 14, 30));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setPreferredSize(new Dimension(0, 50));
        
        button.addActionListener(e -> addTask());
        
        return button;
    }

    private void addTask() {
        String title = titleField.getText();
        String description = descriptionArea.getText();
        String dateLimit = dateLimitField.getText();
        
        List<String> selectedMembers = new ArrayList<>();
        for (ProjectMember member : members) {
            JCheckBox checkbox = memberCheckboxes.get(member.id);
            if (checkbox.isSelected()) {
                selectedMembers.add(member.firstName + " " + member.lastName);
            }
        }
        
        System.out.println("=== Nouvelle Tâche ===");
        System.out.println("Titre: " + title);
        System.out.println("Description: " + description);
        System.out.println("Date Limite: " + dateLimit);
        System.out.println("Membres affectés: " + selectedMembers);
        System.out.println("Nombre de membres: " + selectedMembers.size());
        System.out.println("====================");

        onClick.accept("Tâches");
    }

    // Classe interne pour les membres
    private static class ProjectMember {
        int id;
        String firstName;
        String lastName;
        String role;

        public ProjectMember(int id, String firstName, String lastName, String role) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
        }
    }
}