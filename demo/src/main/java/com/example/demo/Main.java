package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import com.example.demo.components.Header;
import com.example.demo.components.Sidebar;
import com.example.demo.interfaces.*;

public class Main extends JFrame {
    private JPanel container;
    private CardLayout cardLayout;

    public Main() {
        setTitle(Params.appName);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Pages disponibles
        Map<String, JPanel> pages = new LinkedHashMap<>();
        pages.put("Dashboard", new Dashboard());
        pages.put("Taches", new Taches());
        pages.put("Chat", new Chat());

        // Éléments du header et sidebar
        ArrayList<String> headerElements = new ArrayList<>(Arrays.asList("Dashboard", "Taches", "Chat", "Gestion"));
        ArrayList<String> sidebarElements = new ArrayList<>(Arrays.asList("Principale", "Mes projets", "Créer un projet", "Rejoindre un projet"));

        // Sidebar
        Sidebar sidebar = new Sidebar(sidebarElements, page -> cardLayout.show(container, page));
        add(sidebar, BorderLayout.WEST);

        // centerPanel = Header + Contenu
        JPanel centerPanel = new JPanel(new BorderLayout());
        Header header = new Header(headerElements, page -> cardLayout.show(container, page));
        centerPanel.add(header, BorderLayout.NORTH);
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        pages.forEach((name, panel) -> container.add(panel, name));
        centerPanel.add(container, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Page par défaut
        cardLayout.show(container, "Home");
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}