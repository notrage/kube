package kube.view.panels;

import kube.configuration.Config;
import kube.controller.graphical.Phase1Controller;
import kube.model.Kube;
import kube.model.ModelColor;
import kube.model.Player;
import kube.model.action.Queue;
import kube.model.action.Remove;
import kube.model.action.Swap;
import kube.model.action.Action;
import kube.model.action.Build;
import kube.view.GUI;
import kube.view.GUIColors;
import kube.view.animations.hexGlow;
import kube.view.components.Buttons;
import kube.view.components.HexIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.awt.*;
import java.sql.Array;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/*
 * This class extends JPanel and creates the GUI for the first phase of the game.
 */
public class FirstPhasePanel extends JPanel {
    // TODO : refactor this class to make it more readable
    private hexGlow animationGlow;
    private Kube k3;
    private Phase1Controller controller;
    private GUI gui;
    private JPanel constructPanel, piecesPanel, gamePanel, topPanel;
    private HashMap<ModelColor, JPanel> sidePanels;
    private JPanel[][] moutainPanels;
    private HashMap<String, JButton> buttonsMap;

    public FirstPhasePanel(GUI gui, Kube k3, Phase1Controller controller, Queue<Action> eventsToView,
            Queue<Action> eventsToModel) {
        this.gui = gui;
        this.k3 = k3;
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(GUIColors.GAME_BG.toColor());

        /* Buttons panel construction */
        JPanel buttonsPanel = initButtons();
        buttonsPanel.setBackground(GUIColors.GAME_BG.toColor());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(0, 10, 0, 10);

        add(buttonsPanel, gbc);

        /* Game panel construction */
        JPanel gamePanel = gamePanel();
        gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(20, 20, 20, 20);
        add(gamePanel, gbc);

        animationGlow = new hexGlow();
    }

    private JPanel gamePanel() {
        gamePanel = new JPanel();
        gamePanel.setLayout(new BorderLayout());

        // TOP BAR - GAME BASE
        topPanel = new JPanel();
        topPanel.setBackground(GUIColors.GAME_BG_DARK.toColor());
        JLabel baseLabel = new JLabel("Base Centrale: ");
        baseLabel.setFont(new Font("Jomhuria", Font.PLAIN, 30));
        baseLabel.setForeground(GUIColors.TEXT.toColor());
        topPanel.add(baseLabel);
        for (int i = 0; i < k3.getK3().getBaseSize(); i++) {
            topPanel.add(new HexIcon(k3.getK3().getCase(k3.getK3().getBaseSize() - 1, i), false, 1.5));
        }

        gamePanel.add(topPanel, BorderLayout.NORTH);
        // CENTER - CONSTRUCTION OF PLAYER MOUNTAIN
        initGrid();
        gamePanel.add(constructPanel);
        // SIDE BAR - PIECES AVAILABLE
        initSide();
        gamePanel.add(piecesPanel, BorderLayout.EAST);
        return gamePanel;
    }

    private JPanel initButtons() {
        buttonsMap = new HashMap<>();
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(4, 1));
        buttons.setPreferredSize(new Dimension(Config.INIT_WIDTH / 5, Config.INIT_HEIGHT / 5));
        buttons.setBackground(GUIColors.GAME_BG.toColor());

        JButton quitButton = new Buttons.GamePhaseButton("Quitter la partie");
        quitButton.setActionCommand("quit");
        quitButton.addMouseListener(controller);
        buttons.add(quitButton);
        buttonsMap.put("Quit", quitButton);

        JButton optButton = new Buttons.GamePhaseButton("Paramètres");
        optButton.setActionCommand("settings");
        optButton.addMouseListener(controller);
        buttons.add(optButton);
        buttonsMap.put("Option", optButton);

        JButton sugIaButton = new Buttons.GamePhaseButton("Suggestion IA");
        sugIaButton.setActionCommand("AI");
        sugIaButton.addMouseListener(controller);
        buttons.add(sugIaButton);
        buttonsMap.put("AI", sugIaButton);

        JButton validerButton = new Buttons.GamePhaseButton("Valider");
        validerButton.setEnabled(false);
        validerButton.setActionCommand("validate");
        validerButton.addMouseListener(controller);
        buttons.add(validerButton);
        buttonsMap.put("Validate", validerButton);

        return buttons;
    }

    public void initGrid() {
        moutainPanels = new JPanel[6][6];
        constructPanel = new JPanel();

        constructPanel.setOpaque(false);
        constructPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        for (int i = 1; i <= 6; i++) {
            JPanel lineHexa = new JPanel();
            lineHexa.setLayout(new GridLayout(1, i));
            lineHexa.setOpaque(false);
            for (int j = 0; j < i; j++) {
                JPanel hexPanel = new JPanel();
                HexIcon hex = new HexIcon(k3.getPlayerCase(k3.getCurrentPlayer(), i - 1, j), false, 2);
                hex.setPosition(new Point(i - 1, j));
                hexPanel.add(hex);
                lineHexa.add(hexPanel);
                moutainPanels[i - 1][j] = hexPanel;
            }
            gbc.gridx = 0;
            gbc.gridy = i;
            // gbc.anchor = GridBagConstraints.CENTER;
            constructPanel.add(lineHexa, gbc);
        }
        constructPanel.revalidate();
        constructPanel.repaint();
    }

    public void initSide() {
        sidePanels = new HashMap<>();
        piecesPanel = new JPanel();
        piecesPanel.setBackground(GUIColors.TEXT_HOVER.toColor());
        piecesPanel.setLayout(new GridLayout(4, 2));
        for (ModelColor c : ModelColor.getAllColoredAndJokers()) {
            JPanel mini = new JPanel();
            mini.setOpaque(false);
            int numberOfPieces = k3.getCurrentPlayer().getAvailableToBuild().get(c);
            JLabel numOfPieces = new JLabel("x" + numberOfPieces);
            numOfPieces.setFont(new Font("Jomhuria", Font.PLAIN, 40));
            boolean actionable = numberOfPieces > 0;
            mini.add(new HexIcon(c, actionable, 1.5));
            mini.add(numOfPieces);
            piecesPanel.add(mini);
            sidePanels.put(c, mini);
        }
        piecesPanel.revalidate();
        piecesPanel.repaint();
    }

    public void updateButton() {
        JButton validateButton = buttonsMap.get("Validate");
        if (k3.getCurrentPlayer().isMountainFull()) {
            validateButton.setEnabled(true);
        } else {
            validateButton.setEnabled(false);
        }
    }

    public void updateGrid(int i, int j) {
        updateGrid(new Point(i, j));
    }

    public void updateGrid(Point pos) {
        ModelColor c = k3.getPlayerCase(k3.getCurrentPlayer(), pos.x, pos.y);
        boolean actionable = c != ModelColor.EMPTY;
        HexIcon hex = new HexIcon(c, actionable, 2);
        hex.setPosition(pos);
        JPanel panel = moutainPanels[pos.x][pos.y];
        panel.removeAll();
        panel.add(hex);
        panel.revalidate();
        panel.repaint();
    }

    public void updateSide(ModelColor c) {
        JPanel mini = sidePanels.get(c);
        mini.removeAll();
        int numberOfPieces = k3.getCurrentPlayer().getAvailableToBuild().get(c);
        JLabel numOfPieces = new JLabel("x" + numberOfPieces);
        numOfPieces.setFont(new Font("Jomhuria", Font.PLAIN, 40));
        mini.add(new HexIcon(c, false, 1.5));
        mini.add(numOfPieces);
        mini.revalidate();
        mini.repaint();
    }

    public void updateAll() {
        topPanel.removeAll();
        for (int i = 0; i < k3.getK3().getBaseSize(); i++) {
            topPanel.add(new HexIcon(k3.getK3().getCase(k3.getK3().getBaseSize() - 1, i), false, 1.5));
        }
        constructPanel
                .setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                        "Au tour de " + k3.getCurrentPlayer().getName() + " de construire sa montagne",
                        TitledBorder.CENTER, TitledBorder.TOP,
                        new Font("Jomhuria", Font.PLAIN, 60), GUIColors.ACCENT.toColor()));
        buttonsMap.get("AI").setEnabled(false);
        buttonsMap.get("Validate").setEnabled(false);
        for (ModelColor c : ModelColor.getAllColoredAndJokers()) {
            updateSide(c);
        }
        for (int i = 0; i < k3.getCurrentPlayer().getMountain().getBaseSize(); i++) {
            for (int j = 0; j < i + 1; j++) {
                updateGrid(i, j);
            }
        }
        for (ModelColor c : ModelColor.getAllColoredAndJokers()) {
            updateSide(c);
        }
        buttonsMap.get("AI").setEnabled(true);
        updateButton();
        updateActionnable();
    }

    public void update(Action a) {
        switch (a.getType()) {
            case BUILD:
                Build b = (Build) a.getData();
                updateGrid(b.getPosition());
                updateSide(b.getModelColor());
                if (b.getOldColor() != null && b.getOldColor() != ModelColor.EMPTY) {
                    updateSide(b.getOldColor());
                }
                updateButton();
                break;
            case REMOVE:
                Remove r = (Remove) a.getData();
                updateGrid(r.getPosition());
                updateSide(r.getModelColor());
                updateButton();
                break;
            case SWAP:
                Swap s = (Swap) a.getData();
                updateGrid(s.getFrom());
                updateGrid(s.getTo());
                break;
            case AI_MOVE:
                updateAll();
                break;
            default:
                break;
        }
        updateActionnable();
    }

    public void setWaitingButton() {
        for (JButton b : buttonsMap.values()) {
            b.setEnabled(false);
        }
        buttonsMap.get("Validate").setText("Validation en cours");
    }

    public void resetButtonValue() {
        buttonsMap.get("Quit").setText("Quitter la partie");
        buttonsMap.get("Quit").setEnabled(true);
        buttonsMap.get("Option").setText("Paramètres");
        buttonsMap.get("Option").setEnabled(true);
        buttonsMap.get("AI").setText("Suggestion IA");
        buttonsMap.get("AI").setEnabled(true);
        buttonsMap.get("Validate").setText("Valider");
        buttonsMap.get("Validate").setEnabled(false);
    }

    public void updateDnd(Action a) {

    }

    public void updateActionnable() {
        ArrayList<HexIcon> toGlow = new ArrayList<>();
        for (JPanel pan : sidePanels.values()){
            HexIcon hex = (HexIcon) pan.getComponent(0);
            int numberOfPieces = k3.getCurrentPlayer().getAvailableToBuild().get(hex.getColor());
            if (numberOfPieces > 0){
                hex.setActionable(true);
                toGlow.add(hex);
            }
        }
        animationGlow.setToRedraw(toGlow);
    }
}
