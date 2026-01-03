package com.example.demo.interfaces;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.example.demo.Params;
import com.example.demo.components.Scrollbar;

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
        this.onClick = onClick;
        initialiseCouleur();
        initialiseMembre();
        memberCheckboxes = new HashMap<>();
        setLayout(new BorderLayout());
        setBackground(bgColor);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(bgColor);
        content.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        JLabel titleLabel = new JLabel("Ajouter une nouvelle tâche");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textPrimary);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(titleLabel);
        content.add(Box.createRigidArea(new Dimension(0, 25)));
        JPanel mainCard = createMainCard();
        mainCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(mainCard);    
        Scrollbar scroll = new Scrollbar(theme);
        JScrollPane scrollPane = scroll.create(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initialiseCouleur() {
        if (theme == 0) {
            bgColor = new Color(245, 247, 250);
            cardBgColor = Color.WHITE;
            textPrimary = new Color(30, 30, 30);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(59, 130, 246);
            inputBg = new Color(249, 250, 251);
            borderColor = new Color(229, 231, 235);
        } else {
            bgColor = new Color(0, 0, 0);
            cardBgColor = new Color(20, 20, 20);
            textPrimary = new Color(230, 230, 230);
            textSecondary = new Color(100, 100, 100);
            accentColor = new Color(0, 120, 215);
            inputBg = new Color(30, 30, 30);
            borderColor = new Color(45, 45, 45);
        }
    }

    //recuperer les memebres li mparticipin fhad projet
    private void initialiseMembre() {
        members = new ArrayList<>();

        try {
            URL url = new URL("http://localhost:8080/api/projets/" + Params.projetID + "/membres");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                json.append(line);
            reader.close();

            org.json.JSONArray array = new org.json.JSONArray(json.toString());
            for (int i = 0; i < array.length(); i++) {
                org.json.JSONObject obj = array.getJSONObject(i);
                ProjectMember member = new ProjectMember(
                        obj.getInt("id"),
                        obj.getString("prenom"),
                        obj.getString("nom"),
                        obj.getString("role")
                );
                members.add(member);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        descLabel.setForeground(textPrimary);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(descLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        JPanel descPanel = createTextArea();
        descPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(descPanel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
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
        JLabel membersLabel = new JLabel("Affecter aux membres");
        membersLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        membersLabel.setForeground(textPrimary);
        membersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(membersLabel);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        JPanel membersPanel = createMembreCheckBoxP();
        membersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(membersPanel);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        JButton addButton = createButton();
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
        field.setCaretColor(textPrimary); // cursor dial lktpa
        field.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        field.setPreferredSize(new Dimension(0, 48));
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

    private JPanel createTextArea() {
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
        descriptionArea.setWrapStyleWord(true); // kit9t3 tal lkhra dial lklma mashi lwst
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

    private JPanel createMembreCheckBoxP() {
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
            JPanel memberRow = createMembreChexBoxR(member);
            memberRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(memberRow);
            if (members.indexOf(member) < members.size() - 1)
                panel.add(Box.createRigidArea(new Dimension(0, 12)));
        }
        return panel;
    }

    private JPanel createMembreChexBoxR(ProjectMember member) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
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
                FontMetrics fm = g2.getFontMetrics(); // text mcentri
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(45, 45));
        avatar.setOpaque(false);
        row.add(avatar, BorderLayout.WEST);
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
        JCheckBox checkbox = new JCheckBox();
        checkbox.setOpaque(false);
        checkbox.setFocusPainted(false);
        checkbox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkbox.setPreferredSize(new Dimension(24, 24));
        memberCheckboxes.put(member.id, checkbox);
        row.add(checkbox, BorderLayout.EAST);
        return row;
    }

    private JButton createButton() {
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
                            Math.min(255, accentColor.getBlue() + 20)));
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

    //ajouter task avec la selectin dyal membre li yakhud had task
    private void addTask() {
        String title = titleField.getText();
        String description = descriptionArea.getText();
        String dateLimit = dateLimitField.getText();
        if (title.equals("Entrez le titre de la tâche") || title.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer un titre pour la tâche",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (description.equals("Décrivez la tâche en détail...") || description.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer une description pour la tâche",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (dateLimit.equals("JJ/MM/AAAA") || dateLimit.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer une date limite",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Integer> selectedMemberIds = new ArrayList<>();
        for (ProjectMember member : members) {
            JCheckBox checkbox = memberCheckboxes.get(member.id);
            if (checkbox != null && checkbox.isSelected())
                selectedMemberIds.add(member.id);
        }
        sendTaskToBackend(title, description, dateLimit, selectedMemberIds);
    }

    //ajout du task dans bdd
    private void sendTaskToBackend(
            String titre,
            String description,
            String dateLimite,
            List<Integer> membreIds) {
        try {
            URL url = new URL("http://localhost:8080/api/ajouterTache");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true); // ecriture
            org.json.JSONObject jsonObject = new org.json.JSONObject();
            jsonObject.put("titre", titre);
            jsonObject.put("description", description);
            jsonObject.put("dateLimite", dateLimite);
            jsonObject.put("projetId", Params.projetID);
            jsonObject.put("membreIds", new org.json.JSONArray(membreIds));
            String json = jsonObject.toString();
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }
            int responseCode = con.getResponseCode();
            System.out.println("HTTP Response Code: " + responseCode);
            if (responseCode == 200) {
                System.out.println("Tâche ajoutée avec succès!");
                onClick.accept("Tâches");
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null)
                    response.append(responseLine.trim());
                System.err.println("Erreur: " + response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout de la tâche: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

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