package com.example.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.example.demo.components.Header;
import com.example.demo.components.Sidebar;
import com.example.demo.interfaces.*;

public class Main extends JFrame {
    private JPanel centerPanel;
    private JPanel currentPage;
    private Map<String, Supplier<JPanel>> pageFactories;
    private Supplier<Header> headerFactory;
    private Supplier<Sidebar> sidebarFactory;

    public Main() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle(Params.appName);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setUndecorated(true);

        // components
        headerFactory = () -> new Header(
            new ArrayList<>(Arrays.asList("Dashboard", "Tâches", "Chat", "Gestion", "Graphes")),
            this::navigateTo
        );
        sidebarFactory = () -> new Sidebar(
            Arrays.asList("Principale", "Mes projets", "Créer un projet", "Rejoindre un projet"),
            this::navigateTo
        );

        // pages
        pageFactories = new LinkedHashMap<>();
        pageFactories.put("Dashboard", () -> new Dashboard(this::navigateTo));
        pageFactories.put("Membre", () -> new Membre(this::navigateTo));
        pageFactories.put("Tâches", () -> new Taches(this::navigateTo));
        pageFactories.put("Tache", Tache::new);
        pageFactories.put("Chat", Chat::new);
        pageFactories.put("Créer un projet", CreerProjet::new);
        pageFactories.put("Rejoindre un projet", RejoindreProjet::new);
        pageFactories.put("Gestion", Gestion::new);
        pageFactories.put("Ajouter une Tâche", () -> new AjouterTache(this::navigateTo));
        pageFactories.put("Profile", Profile::new);
        pageFactories.put("Graphes", Graphes::new);
        pageFactories.put("Mes projets", () -> new MesProjet(this::navigateTo));

        // Initialiser centerPanel AVANT d'appeler refreshSidebarAndHeader()
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        add(centerPanel, BorderLayout.CENTER);
        refreshSidebarAndHeader();

        // Page par défaut
        navigateTo("Dashboard");
    }

    private void refreshSidebarAndHeader() {
        for (java.awt.Component comp : getContentPane().getComponents()) {
            if (comp instanceof Sidebar) {
                getContentPane().remove(comp);
            }
        }
        for (java.awt.Component comp : centerPanel.getComponents()) {
            if (comp instanceof Header) {
                centerPanel.remove(comp);
            }
        }
        Sidebar sidebar = sidebarFactory.get();
        Header header = headerFactory.get();
        add(sidebar, BorderLayout.WEST);
        centerPanel.add(header, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    private void navigateTo(String pageName) {
        Supplier<JPanel> factory = pageFactories.get(pageName);
        if (factory == null) {
            System.out.println("Page inconnue : " + pageName);
            return;
        }
        refreshSidebarAndHeader();
        JPanel newPage = factory.get();
        if (currentPage != null) {
            centerPanel.remove(currentPage);
        }
        currentPage = newPage;
        centerPanel.add(currentPage, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    public static void main(String[] args) {
        SessionManager.init();
        SwingUtilities.invokeLater(Main::new);
    }
}