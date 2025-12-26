package com.example.demo;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Terminal extends JPanel {
    private JTextPane outputPane;
    private JTextField inputField;
    private StyledDocument doc;
    private List<String> commandHistory;
    private int historyIndex;
    private String currentInput;
    private Consumer<String> onClick;
    private boolean isUnlocked = false;

    private final Color bgColor = new Color(15, 15, 15);
    private final Color inputBgColor = new Color(25, 25, 25);
    private final Color textColor = new Color(220, 220, 220);
    private final Color promptColor = new Color(100, 200, 255);
    private final Color successColor = new Color(34, 197, 94);
    private final Color errorColor = new Color(239, 68, 68);
    private final Color infoColor = new Color(96, 165, 250);
    private final Color warningColor = new Color(251, 191, 36);

    private final String[] COMMANDS = {
        "sudo", "help", "cd", "switch", "ls", "show", "who",
        "add", "rm", "tag", "drop", "clear", "exit"
    };

    private final String[] VALID_PAGES = {
        "Dashboard", "Tâches", "Taches", "Chat", "Gestion", "Graphes",
        "Créer Projet", "Rejoindre Projet", "Mes Projets"
    };

    public Terminal(Consumer<String> onClick) {
        this.onClick = onClick;
        this.commandHistory = new ArrayList<>();
        this.historyIndex = -1;

        setLayout(new BorderLayout());
        setBackground(bgColor);

        createUI();
        printWelcomeMessage();
    }

    private void createUI() {
        outputPane = new JTextPane();
        outputPane.setEditable(false);
        outputPane.setBackground(bgColor);
        outputPane.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputPane.setForeground(textColor);
        outputPane.setCaretColor(textColor);
        doc = outputPane.getStyledDocument();

        createStyles();

        JScrollPane scrollPane = new JScrollPane(outputPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setBackground(bgColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(80, 80, 80);
                this.trackColor = new Color(30, 30, 30);
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
        });

        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(inputBgColor);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 50, 50)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel promptLabel = new JLabel("$ ");
        promptLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        promptLabel.setForeground(promptColor);

        inputField = new JTextField();
        inputField.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputField.setBackground(inputBgColor);
        inputField.setForeground(textColor);
        inputField.setCaretColor(textColor);
        inputField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        inputPanel.add(promptLabel, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setupInputHandlers();
        SwingUtilities.invokeLater(() -> inputField.requestFocusInWindow());
    }

    private void createStyles() {
        Style defaultStyle = outputPane.addStyle("default", null);
        StyleConstants.setForeground(defaultStyle, textColor);

        Style promptStyle = outputPane.addStyle("prompt", null);
        StyleConstants.setForeground(promptStyle, promptColor);
        StyleConstants.setBold(promptStyle, true);

        Style successStyle = outputPane.addStyle("success", null);
        StyleConstants.setForeground(successStyle, successColor);

        Style errorStyle = outputPane.addStyle("error", null);
        StyleConstants.setForeground(errorStyle, errorColor);
        StyleConstants.setBold(errorStyle, true);

        Style infoStyle = outputPane.addStyle("info", null);
        StyleConstants.setForeground(infoStyle, infoColor);

        Style warningStyle = outputPane.addStyle("warning", null);
        StyleConstants.setForeground(warningStyle, warningColor);

        Style commandStyle = outputPane.addStyle("command", null);
        StyleConstants.setForeground(commandStyle, new Color(156, 163, 175));
    }

    private void setupInputHandlers() {
        inputField.addActionListener(e -> {
            String command = inputField.getText().trim();
            if (!command.isEmpty()) {
                printPromptAndCommand(command);
                processCommand(command);
                commandHistory.add(command);
                historyIndex = commandHistory.size();
                currentInput = "";
            }
            inputField.setText("");
        });

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    navigateHistory(-1);
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    navigateHistory(1);
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    autoComplete();
                    e.consume();
                }
            }
        });
    }

    private void navigateHistory(int direction) {
        if (commandHistory.isEmpty()) return;

        if (historyIndex == commandHistory.size()) {
            currentInput = inputField.getText();
        }

        historyIndex += direction;
        historyIndex = Math.max(0, Math.min(historyIndex, commandHistory.size()));

        if (historyIndex < commandHistory.size()) {
            inputField.setText(commandHistory.get(historyIndex));
        } else {
            inputField.setText(currentInput);
        }
    }

    private void autoComplete() {
        String text = inputField.getText();
        if (text.isEmpty()) return;

        String[] parts = text.split(" ");
        String lastWord = parts[parts.length - 1];

        List<String> matches = new ArrayList<>();
        for (String cmd : COMMANDS) {
            if (cmd.startsWith(lastWord)) {
                matches.add(cmd);
            }
        }

        if (parts.length > 0 && parts[0].equals("cd")) {
            matches.clear();
            for (String page : VALID_PAGES) {
                if (page.toLowerCase().startsWith(lastWord.toLowerCase())) {
                    matches.add(page);
                }
            }
        }

        if (matches.size() == 1) {
            parts[parts.length - 1] = matches.get(0);
            inputField.setText(String.join(" ", parts) + " ");
        } else if (matches.size() > 1) {
            printInfo("Suggestions: " + String.join(", ", matches));
        }
    }

    private void printWelcomeMessage() {
        printSuccess("╔═══════════════════════════════════════════════════════════════╗");
        printSuccess("║          Terminal de Gestion de Projet - Version 2.0         ║");
        printSuccess("╚═══════════════════════════════════════════════════════════════╝\n");
        printInfo("Tapez 'help' pour voir la liste des commandes disponibles.\n");
    }

    private void printPromptAndCommand(String command) {
        try {
            doc.insertString(doc.getLength(), "$ ", outputPane.getStyle("prompt"));
            doc.insertString(doc.getLength(), command + "\n", outputPane.getStyle("command"));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        scrollToBottom();
    }

    private void print(String text, String style) {
        try {
            doc.insertString(doc.getLength(), text + "\n", outputPane.getStyle(style));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        scrollToBottom();
    }

    private void printSuccess(String text) { print(text, "success"); }
    private void printError(String text) { print(text, "error"); }
    private void printInfo(String text) { print(text, "info"); }
    private void printWarning(String text) { print(text, "warning"); }
    private void printDefault(String text) { print(text, "default"); }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            outputPane.setCaretPosition(doc.getLength());
        });
    }

    private void processCommand(String command) {
        if (!isUnlocked && !command.startsWith("sudo")) {
            printError("Terminal verrouillé. Utilisez 'sudo tasky -u root -p' pour débloquer.");
            return;
        }

        String[] parts = command.split("\\s+");
        String cmd = parts[0];

        try {
            switch (cmd) {
                case "sudo":
                    unlockTerminal(parts);
                    break;
                case "help":
                    showHelp();
                    break;
                case "cd":
                    changeDirectory(parts);
                    break;
                case "switch":
                    switchProject(parts);
                    break;
                case "ls":
                    listTasks(parts);
                    break;
                case "show":
                    showProject(parts);
                    break;
                case "who":
                    listMembers();
                    break;
                case "add":
                    addTask(parts);
                    break;
                case "rm":
                    removeTask(parts);
                    break;
                case "tag":
                    renameTask(parts);
                    break;
                case "drop":
                    removeMember(parts);
                    break;
                case "clear":
                    clearTerminal();
                    break;
                case "exit":
                    printInfo("Fermeture de l'application...");
                    System.exit(0);
                    break;
                default:
                    printError("Commande inconnue: " + cmd);
                    printInfo("Tapez 'help' pour voir les commandes disponibles.");
            }
        } catch (Exception e) {
            printError("Erreur: " + e.getMessage());
        }
    }

    private void unlockTerminal(String[] parts) {
        if (parts.length < 5 || !parts[1].equals("tasky") || !parts[2].equals("-u") || !parts[3].equals("root") || !parts[4].equals("-p")) {
            printError("Usage: sudo tasky -u root -p");
            return;
        }
        isUnlocked = true;
        printSuccess("Terminal débloqué avec succès !");
    }

    private void changeDirectory(String[] parts) {
        if (parts.length < 2) {
            printError("Usage: cd <page>");
            printInfo("Pages disponibles: " + String.join(", ", VALID_PAGES));
            return;
        }

        String page = parts[1];
        boolean isValid = false;
        for (String validPage : VALID_PAGES) {
            if (validPage.equalsIgnoreCase(page)) {
                page = validPage;
                isValid = true;
                break;
            }
        }

        if (isValid) {
            printSuccess("Navigation vers " + page + "...");
            onClick.accept(page);
        } else {
            printError("Page invalide: " + page);
            printInfo("Pages disponibles: " + String.join(", ", VALID_PAGES));
        }
    }

    private void switchProject(String[] parts) {
        if (parts.length < 2) {
            printError("Usage: switch <id>");
            return;
        }

        try {
            int id = Integer.parseInt(parts[1]);
            Params.projetID = id;
            printSuccess("Projet sélectionné: ID = " + id);
        } catch (NumberFormatException e) {
            printError("ID invalide: " + parts[1]);
        }
    }

    private void listTasks(String[] parts) {
        if (Params.projetID == 0) {
            printError("Aucun projet sélectionné. Utilisez 'switch <id>' pour en sélectionner un.");
            return;
        }

        String endpoint = "/api/terminal/projets/" + Params.projetID + "/taches";
        if (parts.length > 1 && parts[1].equals("-x")) {
            endpoint += "?etat=Terminé";
        }

        Queries.get(endpoint).thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                if (response.containsKey("error")) {
                    printError("Erreur: " + response.get("error"));
                    return;
                }

                List<Map<String, Object>> tasks = (List<Map<String, Object>>) response.get("data");
                if (tasks == null || tasks.isEmpty()) {
                    printWarning("Aucune tâche trouvée.");
                    return;
                }

                printSuccess("\n┌─────────────────────────── TÂCHES ───────────────────────────┐");
                for (Map<String, Object> task : tasks) {
                    printDefault(String.format("│ ID: %-4s │ %-25s │ Deadline: %-10s │ État: %-10s │",
                        task.get("id"),
                        truncate(String.valueOf(task.get("titre")), 25),
                        task.get("dateLimite"),
                        task.get("etat")));
                    if (parts.length > 1 && parts[1].equals("-l")) {
                        List<Map<String, Object>> subtasks = (List<Map<String, Object>>) task.get("sousTaches");
                        if (subtasks != null && !subtasks.isEmpty()) {
                            for (Map<String, Object> st : subtasks) {
                                printDefault(String.format("│   ├── Sous-tâche: %-20s │ Terminé: %-5s │",
                                    truncate(String.valueOf(st.get("titre")), 20),
                                    st.get("termine")));
                            }
                        }
                    }
                }
                printSuccess("└───────────────────────────────────────────────────────────────┘\n");
            });
        });
    }

    private void showProject(String[] parts) {
        if (Params.projetID == 0) {
            printError("Aucun projet sélectionné. Utilisez 'switch <id>' pour en sélectionner un.");
            return;
        }

        String endpoint = "/api/terminal/projets/" + Params.projetID;
        if (parts.length > 1 && parts[1].equals("-s")) {
            endpoint += "/details";
        }

        Queries.get(endpoint).thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                if (response.containsKey("error")) {
                    printError("Erreur: " + response.get("error"));
                    return;
                }

                Map<String, Object> project = (Map<String, Object>) response.get("data");
                if (project == null) {
                    project = response;
                }

                printSuccess("\n┌─────────────────────────── DÉTAILS DU PROJET ───────────────────────────┐");
                printDefault(String.format("│ %-20s: %-40s │", "Nom", project.get("nom")));
                printDefault(String.format("│ %-20s: %-40s │", "Description", truncate(String.valueOf(project.get("description")), 40)));
                printDefault(String.format("│ %-20s: %-40s │", "Date de début", project.get("dateDebut")));
                printDefault(String.format("│ %-20s: %-40s │", "Date de fin", project.get("dateFin")));
                printDefault(String.format("│ %-20s: %-40s │", "Statut", project.get("statut")));
                printDefault(String.format("│ %-20s: %-40s │", "Budget", project.get("budget")));
                printDefault(String.format("│ %-20s: %-40s │", "Budget consommé", project.get("budgetConsomme")));
                printSuccess("└───────────────────────────────────────────────────────────────────────────┘\n");
            });
        });
    }

    private void listMembers() {
        if (Params.projetID == 0) {
            printError("Aucun projet sélectionné. Utilisez 'switch <id>' pour en sélectionner un.");
            return;
        }

        Queries.get("/api/terminal/projets/" + Params.projetID + "/membres").thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                if (response.containsKey("error")) {
                    printError("Erreur: " + response.get("error"));
                    return;
                }

                List<Map<String, Object>> members = (List<Map<String, Object>>) response.get("data");
                if (members == null || members.isEmpty()) {
                    printWarning("Aucun membre trouvé.");
                    return;
                }

                printSuccess("\n┌────────────────────────────── MEMBRES ──────────────────────────────┐");
                printDefault(String.format("│ %-5s │ %-20s │ %-25s │ %-15s │ %-12s │ %-15s │",
                    "ID", "Nom complet", "Email", "Téléphone", "Date rejointe", "Type"));
                printDefault("├───────────────────────────────────────────────────────────────────────┤");
                for (Map<String, Object> m : members) {
                    printDefault(String.format("│ %-5s │ %-20s │ %-25s │ %-15s │ %-12s │ %-15s │",
                        m.get("id"),
                        m.get("prenom") + " " + m.get("nom"),
                        m.get("email"),
                        m.get("telephone"),
                        m.get("dateRejointe"),
                        m.get("type")));
                }
                printSuccess("└───────────────────────────────────────────────────────────────────────┘\n");
            });
        });
    }

    private void addTask(String[] parts) {
        if (Params.projetID == 0) {
            printError("Aucun projet sélectionné. Utilisez 'switch <id>' pour en sélectionner un.");
            return;
        }

        if (parts.length < 5) {
            printError("Usage: add <nom> <description> <deadline> <id_membre>");
            return;
        }

        String nom = parts[1];
        String description = parts[2];
        String deadline = parts[3];
        int membreId = Integer.parseInt(parts[4]);

        Map<String, Object> body = new HashMap<>();
        body.put("titre", nom);
        body.put("description", description);
        body.put("dateLimite", deadline);
        body.put("membreId", membreId);

        Queries.post("/api/terminal/projets/" + Params.projetID + "/taches", body).thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                if (response.containsKey("error")) {
                    printError("Erreur: " + response.get("error"));
                } else {
                    printSuccess("✓ Tâche créée avec succès! ID: " + response.get("id"));
                }
            });
        });
    }

    private void removeTask(String[] parts) {
        if (Params.projetID == 0) {
            printError("Aucun projet sélectionné. Utilisez 'switch <id>' pour en sélectionner un.");
            return;
        }

        if (parts.length < 2) {
            printError("Usage: rm <id>");
            return;
        }

        int id = Integer.parseInt(parts[1]);
        Queries.delete("/api/terminal/taches/" + id).thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                if (response.containsKey("error")) {
                    printError("Erreur: " + response.get("error"));
                } else {
                    printSuccess("✓ Tâche supprimée avec succès!");
                }
            });
        });
    }

    private void renameTask(String[] parts) {
        if (Params.projetID == 0) {
            printError("Aucun projet sélectionné. Utilisez 'switch <id>' pour en sélectionner un.");
            return;
        }

        if (parts.length < 3) {
            printError("Usage: tag <id> <nom>");
            return;
        }

        int id = Integer.parseInt(parts[1]);
        String nom = parts[2];

        Map<String, Object> body = new HashMap<>();
        body.put("titre", nom);

        Queries.put("/api/terminal/taches/" + id, body).thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                if (response.containsKey("error")) {
                    printError("Erreur: " + response.get("error"));
                } else {
                    printSuccess("✓ Tâche renommée avec succès!");
                }
            });
        });
    }

    private void removeMember(String[] parts) {
        if (Params.projetID == 0) {
            printError("Aucun projet sélectionné. Utilisez 'switch <id>' pour en sélectionner un.");
            return;
        }

        if (parts.length < 2) {
            printError("Usage: drop <id>");
            return;
        }

        int id = Integer.parseInt(parts[1]);
        Queries.delete("/api/terminal/membres/" + id).thenAccept(response -> {
            SwingUtilities.invokeLater(() -> {
                if (response.containsKey("error")) {
                    printError("Erreur: " + response.get("error"));
                } else {
                    printSuccess("✓ Membre retiré avec succès!");
                }
            });
        });
    }

    private void clearTerminal() {
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void showHelp() {
        printInfo("═══════════════════════ COMMANDES DISPONIBLES ═══════════════════════\n");

        printSuccess("NAVIGATION:");
        printDefault("  sudo tasky -u root -p          - Déverrouiller le terminal");
        printDefault("  cd <page>                     - Changer de page");
        printDefault("    Pages: Dashboard, Tâches, Chat, Gestion, Graphes, Créer Projet, Rejoindre Projet, Mes Projets");
        printDefault("  switch <id>                  - Sélectionner un projet");
        printDefault("");

        printSuccess("GESTION DES TÂCHES:");
        printDefault("  ls [ -x | -l ]                - Lister les tâches (option: -x=terminées, -l=avec sous-tâches)");
        printDefault("  add <nom> <description> <deadline> <id_membre> - Ajouter une tâche");
        printDefault("  rm <id>                       - Supprimer une tâche");
        printDefault("  tag <id> <nom>                - Renommer une tâche");
        printDefault("");

        printSuccess("GESTION DU PROJET:");
        printDefault("  show [ -s ]                   - Afficher les infos du projet (option: -s=détails)");
        printDefault("  who                           - Lister les membres");
        printDefault("  drop <id>                     - Retirer un membre");
        printDefault("");

        printSuccess("AUTRES:");
        printDefault("  clear                         - Effacer le terminal");
        printDefault("  help                          - Afficher cette aide");
        printDefault("  exit                          - Quitter");
        printDefault("");

        printInfo("═══════════════════════════════════════════════════════════════════════\n");
        printWarning("Utilisez TAB pour l'autocomplétion et les flèches ↑↓ pour l'historique\n");
    }

    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
}