package kube.view;

import javax.swing.*;

import kube.configuration.Config;
import kube.controller.graphical.DnDController;
import kube.view.panels.GlassPanel;

import java.awt.*;

/*
 * This class initializes the game frame and its layout manager : an overlay layout that contains a card layout and potential overlay elements.
 */
public class MainFrame extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JComponent glassPane;
    private JPanel framePanel;
    private JPanel overlayPanel;
    private Component overlay;

    public MainFrame() {
        setTitle("KUBE");
        // setIconImage(); for later
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(Config.getWidth(), Config.getHeight()));
        setMinimumSize(new Dimension((int) (Config.getWidth() / 1.5), Config.getHeight()));
        setLocationRelativeTo(null);
        framePanel = (JPanel) getContentPane();
        OverlayLayout overlay = new OverlayLayout(framePanel);
        framePanel.setLayout(overlay);
        framePanel.setVisible(true);
        overlayPanel = new JPanel();
        overlayPanel.setVisible(false);
        overlayPanel.setOpaque(false);
        overlayPanel.setSize(new Dimension(Config.getWidth(), Config.getHeight()));
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        framePanel.add(overlayPanel);
        framePanel.add(cardPanel);
        pack();
    }

    public void resize(){
        setSize(Config.getWidth(), Config.getHeight());
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    public void addPanel(JPanel panel, String name) {
        cardPanel.add(panel, name);
    }

    public void setFrameVisible(boolean b) {
        setVisible(true);
    }

    public void showPanel(String name) {
        Config.debug("Showing panel ", name);
        cardLayout.show(cardPanel, name);
    }

    public void createGlassPane() {
        if (glassPane != null) {
            System.err.println("Glass pane already exists.");
            return;
        }
        GlassPanel g = new GlassPanel();
        this.glassPane = g;
        super.setGlassPane(g);
    }

    public void setGlassPaneController(DnDController ma) {
        glassPane.addMouseMotionListener(ma);
        glassPane.addMouseListener(ma);
    }

    public void removeGlassPane() {
        if (glassPane == null) {
            System.err.println("No glass pane exists.");
            return;
        }
        remove(getGlassPane());
        glassPane = null;
    }
    
    public JPanel getOverlay() {
        return overlayPanel;
    }

    public JPanel getFramePanel(){
        return framePanel;
    }
    
    public void addToOverlay(Component p) {
        overlayPanel.add(p);
        overlayPanel.setVisible(true);
        framePanel.revalidate();
        framePanel.repaint();
        overlay = p;
    }

    public void removeAllFromOverlay() {
        if (overlay != null) {
            overlayPanel.removeAll();
            overlayPanel.setVisible(false);
            framePanel.revalidate();
            framePanel.repaint();
            overlay = null;
        }
    }

    public Component getOverlayComponent() {
        return overlay;
    }
}
