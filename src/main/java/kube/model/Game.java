package kube.model;

import java.util.Random;

import kube.configuration.Config;
import kube.model.action.Action;
import kube.model.action.Queue;
import kube.model.action.Swap;
import kube.model.action.move.Move;
import kube.model.ai.midLevelAI;
import kube.model.ai.randomAI;
import kube.model.ai.utilsAI;

public class Game implements Runnable {

    public static final int LOCAL = 1;
    public static final int HOST = 2;
    public static final int JOIN = 3;

    public static final int PORT = 1234;

    Queue<Action> controllerToModele;
    Queue<Action> modeleToView;
    Queue<Action> eventsToNetwork;
    private int gameType;
    private final Kube k3;

    public Game(int gameType, Kube k3, Queue<Action> controllerToModele, Queue<Action> modeleToView,
            Queue<Action> eventsToNetwork) {
        this.gameType = gameType;
        this.k3 = k3;
        this.controllerToModele = controllerToModele;
        this.eventsToNetwork = eventsToNetwork;
        this.modeleToView = modeleToView;
    }

    @Override
    public void run() {
        switch (gameType) {
            case LOCAL:
                localGame(LOCAL);
                break;
            case HOST:
                localGame(HOST);
                break;
            case JOIN:
                localGame(JOIN);
                break;
            default:
                break;
        }
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public void localGame(int type) {
        Config.debug("Démarrage de la partie");
        // Initialisation
        if (type == HOST) {
            eventsToNetwork.add(new Action(Action.INIT_K3, k3.getK3()));
            eventsToNetwork.add(new Action(Action.OTHER_PLAYER, k3.getP2()));
        }
        else if (type == JOIN) {
            k3.setCurrentPlayer(k3.getP2());
        }
        // Construction phase
        while (k3.getPhase() == 1) {
            try {
                if (k3.getCurrentPlayer().isAI()) {
                    k3.getCurrentPlayer().getAI().constructionPhase();
                    modeleToView.add(new Action(Action.PRINT_VALIDATE, true));
                    k3.updatePhase();
                } else {
                    Action a = controllerToModele.remove();
                    if(a.getPlayer()!=0 && a.getPlayer()!=type){
                        switch (a.getType()) {
                            case Action.INIT_K3:
                                k3.setK3((Mountain) a.getData());
                                break;
                            case Action.OTHER_PLAYER:
                                if (a.getPlayer() == HOST) {
                                    k3.setP1((Player) a.getData());
                                } else {
                                    k3.setP2((Player) a.getData());
                                }
                                k3.updatePhase();
                                break;
                            default:
                                modeleToView.add(new Action(Action.PRINT_NOT_YOUR_TURN));
                        }
                    }
                    else if (k3.getCurrentPlayer().getId() == type || a.getPlayer() == 0) {
                        switch (a.getType()) {
                            case Action.SWAP:
                                swap((Swap) a.getData());
                                modeleToView.add(a);
                                break;
                            case Action.VALIDATE:
                                boolean isValidated;
                                if ((isValidated = k3.getCurrentPlayer().validateBuilding())) {
                                    k3.getCurrentPlayer().validate();
                                    Config.debug("Validation construction j" + k3.getCurrentPlayer().getId());
                                }
                                k3.updatePhase();
                                modeleToView.add(new Action(Action.PRINT_VALIDATE, isValidated));
                                break;
                            case Action.SHUFFLE:
                                utilsAI.randomFillMountain(k3.getCurrentPlayer(), new Random());
                                modeleToView.add(new Action(Action.SHUFFLE));
                                break;
                            default:
                                modeleToView.add(new Action(Action.PRINT_FORBIDDEN_ACTION));
                                break;
                        }
                    } else {
                        modeleToView.add(new Action(Action.PRINT_NOT_YOUR_TURN));
                    }
                }
            } catch (UnsupportedOperationException e) {
                modeleToView.add(new Action(Action.PRINT_FORBIDDEN_ACTION));
            }
        }


        Config.debug("Fin phase 1");
        k3.setCurrentPlayer(k3.getRandomPlayer());
        modeleToView.add(new Action(Action.PRINT_STATE));
        while (k3.canCurrentPlayerPlay()) {
            try {
                if (k3.getCurrentPlayer().isAI()) {
                    try {
                        Move move = k3.getCurrentPlayer().getAI().nextMove();
                        k3.playMove(move);
                        modeleToView.add(new Action(Action.PRINT_MOVE, move));
                    } catch (Exception e) {
                        System.exit(0);
                    }
                } else {

                    Action a = controllerToModele.remove();
                    if (a.getPlayer() == k3.getCurrentPlayer().getId() || a.getPlayer() == 0) {
                        switch (a.getType()) {
                            case Action.MOVE:
                                playMove(a);
                                break;
                            case Action.UNDO:
                                undo();
                                break;
                            case Action.REDO:
                                redo();
                                break;
                            default:
                                modeleToView.add(new Action(Action.PRINT_FORBIDDEN_ACTION));
                                break;
                        }
                    } else {
                        modeleToView.add(new Action(Action.PRINT_NOT_YOUR_TURN));
                    }
                }
            } catch (Exception e) {
                System.err.println(e);
                modeleToView.add(new Action(Action.PRINT_FORBIDDEN_ACTION));
            }

        }
        if (k3.getCurrentPlayer() == k3.getP1()) {
            modeleToView.add(new Action(Action.PRINT_WIN_MESSAGE, k3.getP2()));
        } else {
            modeleToView.add(new Action(Action.PRINT_WIN_MESSAGE, k3.getP1()));
        }
        Config.debug("Fin phase 2");
    }

    public void onlineGame(int whoAmI) {
        Config.debug("Démarrage de la partie en ligne en tant que " + (whoAmI == HOST ? "hôte" : "invité"));
        Player player, other;
        if (whoAmI == HOST) {

            player = k3.getP1();
            other = k3.getP2();
            eventsToNetwork.add(new Action(Action.INIT_K3, k3.getK3()));
            eventsToNetwork.add(new Action(Action.OTHER_PLAYER, k3.getP2()));
        } else {
            player = k3.getP2();
            other = k3.getP1();

        }
                        modeleToView.add(new Action(Action.PRINT_NOT_YOUR_TURN));

        // Construction phase
        eventsToNetwork.add(new Action(Action.SHOW_ALL));

        while (k3.getPhase() == 1 && player.getHasValidateBuilding() == false) {
            k3.setCurrentPlayer(player);
            try {
                Action a = controllerToModele.remove();
                switch (a.getType()) {
                    case Action.SWAP:
                        swap((Swap) a.getData());
                        modeleToView.add(a);
                        break;
                    case Action.VALIDATE:
                        boolean isValidated;
                        if ((isValidated = player.validateBuilding())) {
                            player.validate();
                            eventsToNetwork.add(new Action(Action.OTHER_PLAYER, player));
                        }
                        k3.updatePhase();
                        modeleToView.add(new Action(Action.PRINT_VALIDATE, isValidated));
                        break;

                    case Action.SHUFFLE:
                        utilsAI.randomFillMountain(player, new Random());
                        modeleToView.add(new Action(Action.PRINT_RANDOM));
                        break;
                    case Action.INIT_K3:
                        k3.setK3((Mountain) a.getData());
                        break;
                    case Action.OTHER_PLAYER:
                        if (whoAmI == HOST) {
                            k3.setP2((Player) a.getData());
                        } else {
                            k3.setP1((Player) a.getData());
                        }
                        k3.updatePhase();
                        break;

                    default:
                        modeleToView.add(new Action(Action.PRINT_FORBIDDEN_ACTION));
                        break;
                }
            } catch (UnsupportedOperationException e) {
                modeleToView.add(new Action(Action.PRINT_NOT_YOUR_TURN));
            }

        }
        modeleToView.add(new Action(Action.PRINT_NOT_YOUR_TURN));

        while (k3.getPhase() == 1 && other.getHasValidateBuilding() == false) {
            //Waiting for the other player to validate his building
            Action a = controllerToModele.remove();
            if (a.getType() == Action.OTHER_PLAYER) {
                if (whoAmI == HOST) {
                    k3.setP2((Player) a.getData());
                } else {
                    k3.setP1((Player) a.getData());
                }
                k3.updatePhase();
            } else {
                modeleToView.add(new Action(Action.PRINT_NOT_YOUR_TURN));
            }

        }
        // if (whoAmI == HOST) {
        //     eventsToNetwork.add(new Action(Action.INIT_K3, k3.getK3()));
        // }
        // END PHASE 1

        while (k3.canCurrentPlayerPlay()) {
            Action a = controllerToModele.remove();
            switch (a.getType()) {
                case Action.MOVE:
                    if (k3.getCurrentPlayer() == player) {
                        playMove(a);
                    }
                    break;
                case Action.MOVE_FROM_NETWORK:
                    if (k3.getCurrentPlayer() == other) {
                        playMove(a);
                    }
                    break;
                case Action.UNDO:
                    undo();
                    break;
                case Action.REDO:
                    redo();
                    break;
                default:
                    modeleToView.add(new Action(Action.PRINT_FORBIDDEN_ACTION));
                    break;
            }
            if (k3.getCurrentPlayer() == other) {
                modeleToView.add(new Action(Action.PRINT_NOT_YOUR_TURN));
            }
        }

        if (k3.getCurrentPlayer() == k3.getP1()) {
            modeleToView.add(new Action(Action.PRINT_WIN_MESSAGE, k3.getP2()));
        } else {
            modeleToView.add(new Action(Action.PRINT_WIN_MESSAGE, k3.getP1()));
        }

    }

    synchronized public void swap(Swap s) {
        ModelColor c = k3.getCurrentPlayer().removeFromMountainToAvailableToBuild(s.getPos1().x, s.getPos1().y);
        ModelColor c2 = k3.getCurrentPlayer().removeFromMountainToAvailableToBuild(s.getPos2().x, s.getPos2().y);
        k3.getCurrentPlayer().addToMountainFromAvailableToBuild(s.getPos1(), c2);
        k3.getCurrentPlayer().addToMountainFromAvailableToBuild(s.getPos2(), c);
    }

    public void playMove(Action a) {
        try {
            Move move = k3.moveSet().get((int) a.getData());
            if (k3.playMove(move)) {
                modeleToView.add(new Action(Action.PRINT_MOVE, move));
            } else {
                modeleToView.add(new Action(Action.PRINT_MOVE_ERROR, move));
            }
        } catch (UnsupportedOperationException e) {
            modeleToView.add(new Action(Action.PRINT_MOVE_ERROR));
        }
    }

    public void undo() {
        if (k3.unPlay()) {
            modeleToView.add(new Action(Action.PRINT_UNDO, k3.getLastMovePlayed()));
        } else {
            modeleToView.add(new Action(Action.PRINT_UNDO_ERROR));
        }
    }

    public void redo() {
        if (k3.rePlay()) {
            modeleToView.add(new Action(Action.PRINT_REDO, k3.getLastMovePlayed()));
        } else {
            modeleToView.add(new Action(Action.PRINT_REDO_ERROR));
        }
    }

}
