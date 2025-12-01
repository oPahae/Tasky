package com.example.demo;

import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Params {
    public static int theme = 0;
    public static String appName = "TASKY";

    public static int projetID = 1;
    public static int membreID = 1;
    public static int tacheID = 1;
    public static int verifCode = 0;

    public static String headerSelectedElement = "Dashboard";
    public static String sidebarSelectedElement = "Principale";

    public static void toggleTheme(Component comp) {
        Params.theme = Params.theme == 0 ? 1 : 0;
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(comp);
        currentFrame.dispose();
        SwingUtilities.invokeLater(() -> {
            Main mainGUI = new Main();
            mainGUI.setVisible(true);
        });
    }
}