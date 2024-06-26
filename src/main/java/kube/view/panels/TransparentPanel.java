package kube.view.panels;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import kube.configuration.Config;
import kube.configuration.ResourceLoader;
import kube.view.GUIColors;

public class TransparentPanel extends JPanel {
    private float opacity;
    private String text;
    private Image backgroundImage, scaledBackground;
    private Font panelFont; // Font variable to store the custom font
    private Boolean noImage;

    public TransparentPanel(String text, Boolean noImage) {
        this(text);
        this.noImage = true;
    }

    public TransparentPanel(String text) {
        this.noImage = false;
        this.text = text;
        opacity = 0;
        backgroundImage = ResourceLoader.getBufferedImage("background");

        setOpaque(false);

        // Set the panelFont to the specified custom font
        panelFont = new Font("Jomhuria", Font.BOLD, (int) (Config.INIT_HEIGHT * Config.getUIScale() / 8.5f));
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
        repaint();
    }

    public void setText(String text) {
        this.text = text;
        repaint(); // Ensure the panel repaints when the text is changed
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (scaledBackground == null) {
            scaledBackground = backgroundImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        }
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        if (noImage) {
            g2d.setColor(new Color(128, 128, 128, (int) (opacity * 255 / 2))); // Set the grey color with the specified
                                                                               // opacity
            g2d.fillRect(0, 0, getWidth(), getHeight()); // Fill the component with the grey color
        } else {
            g2d.drawImage(scaledBackground, 0, 0, this);
        }
        g2d.setFont(panelFont); // Set the custom font

        // Draw the text in the center of the panel
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2d.setColor(new Color(255, 255, 255, 150)); // Set the text color
        Rectangle2D rect = fm.getStringBounds(text, g);
        g2d.fillRect(0,
                y - fm.getAscent() - 50,
                (int) getWidth(),
                (int) rect.getHeight() + 100);

        g2d.setColor(GUIColors.ACCENT.toColor()); // Set the text color
        g2d.drawString(text, x, y);
        g2d.dispose(); // Clean up graphics context
    }
}
