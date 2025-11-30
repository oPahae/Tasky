package com.example.demo;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.example.demo.components.Header;
import com.example.demo.components.Sidebar;
import com.example.demo.interfaces.Chat;
import com.example.demo.interfaces.CreerProjet;
import com.example.demo.interfaces.Dashboard;
import com.example.demo.interfaces.Gestion;
import com.example.demo.interfaces.Membre;
import com.example.demo.interfaces.RejoindreProjet;
import com.example.demo.interfaces.Tache;
import com.example.demo.interfaces.Taches;

public class Main extends JFrame {
    private JPanel container;
    private CardLayout cardLayout;
    private String prenom;
    private String nom;
    private int userID;

    public Main(int userID, String prenom, String nom) {
        this.userID = userID;
        this.prenom = prenom;
        this.nom = nom;
        initializeUI();
    }

    public Main() {
        this.prenom = "Utilisateur";
        this.nom = "";
        initializeUI();
    }

    private void initializeUI() {
        setTitle(Params.appName);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Sidebar sidebar = new Sidebar(
            Arrays.asList("Principale", "Mes projets", "Créer un projet", "Rejoindre un projet"),
            element -> {
                cardLayout.show(container, element);
            },
            userID,
            prenom,
            nom
        );
        
        add(sidebar, BorderLayout.WEST);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel, BorderLayout.CENTER);

        Map<String, JPanel> pages = new LinkedHashMap<>();
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        
        pages.put("Dashboard", new Dashboard(page -> cardLayout.show(container, page)));
        pages.put("Membre", new Membre(page -> cardLayout.show(container, page)));
        pages.put("Tâches", new Taches(page -> cardLayout.show(container, page)));
        pages.put("Tache", new Tache());
        pages.put("Chat", new Chat());
        pages.put("Créer un projet", new CreerProjet());
        pages.put("Rejoindre un projet", new RejoindreProjet());
        pages.put("Gestion", new Gestion());

        // centerPanel = Header + Contenu
        JPanel centerPanel = new JPanel(new BorderLayout());
        ArrayList<String> headerElements = new ArrayList<>(Arrays.asList("Dashboard", "Tâches", "Chat", "Gestion"));
        Header header = new Header(headerElements, page -> cardLayout.show(container, page));
        centerPanel.add(header, BorderLayout.NORTH);
        
        pages.forEach((name, panel) -> container.add(panel, name));
        centerPanel.add(container, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // par défaut
        cardLayout.show(container, "Dashboard");
    }

    public static void main(String[] args) {
        SessionManager.init();
        SwingUtilities.invokeLater(Main::new);
    }
}