package kube.controller.graphical;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import kube.model.action.Action;
import kube.model.action.ActionType;
import kube.model.action.Queue;
import kube.view.components.Buttons.ButtonIcon;

public class Phase1Controller implements ActionListener, MouseListener {
    private Queue<Action> toView;
    private Queue<Action> toModel;

    public Phase1Controller(Queue<Action> toView, Queue<Action> toModel) {
        this.toView = toView;
        this.toModel = toModel;
    }

    public void actionPerformed(ActionEvent evt) {
        switch (evt.getActionCommand()) {
            case "validate":
                toModel.add(new Action(ActionType.VALIDATE));
                break;
            case "settings": // change to opts.
                toView.add(new Action(ActionType.SETTINGS));
                break;
            case "quit":
                // TODO : ask if need to save the game
                toModel.add(new Action(ActionType.RESET));
                toView.add(new Action(ActionType.RETURN_TO_MENU));
                break;
            case "AI":
                toModel.add(new Action(ActionType.AI_MOVE));
                break;
            default:
                break;
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        Object source = e.getSource();
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (source instanceof ButtonIcon) {
                toView.add(new Action(ActionType.SET_BUTTON_PRESSED, source));
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        Object source = e.getSource();
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (source instanceof ButtonIcon) {
                toView.add(new Action(ActionType.SET_BUTTON_RELEASED, source));
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
        Object source = e.getSource();
        if (source instanceof ButtonIcon) {
            toView.add(new Action(ActionType.SET_BUTTON_HOVERED, source));
        }
    }

    public void mouseExited(MouseEvent e) {
        Object source = e.getSource();
        if (source instanceof ButtonIcon) {
            toView.add(new Action(ActionType.SET_BUTTON_DEFAULT, source));
        }
    }
}
