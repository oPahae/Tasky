package com.example.demo.components;

import javax.swing.*;
import java.awt.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class Scrollbar {
    private int theme;
    private Color bgColor;
    private Color thumbColor;
    private Color trackColor;

    public Scrollbar(int theme) {
        this.theme = theme;
        applyTheme();
    }

    private void applyTheme() {
        if (theme == 0) {
            bgColor = new Color(245, 247, 250);
            thumbColor = new Color(180, 180, 180);
            trackColor = new Color(230, 230, 230);
        } else {
            bgColor = new Color(32, 32, 35);
            thumbColor = new Color(90, 90, 90);
            trackColor = new Color(55, 55, 55);
        }
    }

    public JScrollPane create(JComponent content) {
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(bgColor);
        scroll.setBackground(bgColor);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        scroll.getVerticalScrollBar().setUI(new ScrollUI(thumbColor, trackColor));
        scroll.getHorizontalScrollBar().setUI(new ScrollUI(thumbColor, trackColor));

        return scroll;
    }

    class ScrollUI extends BasicScrollBarUI {
        private final Color thumbColor;
        private final Color trackColor;

        ScrollUI(Color thumbColor, Color trackColor) {
            this.thumbColor = thumbColor;
            this.trackColor = trackColor;
        }

        @Override // dik l3aypa
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 12, 12);
            g2.dispose();
        }

        @Override // l3aypa li ktmshi fiha l3aypa
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }
    }
}