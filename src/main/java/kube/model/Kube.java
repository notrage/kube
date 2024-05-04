package kube.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
public class Kube {

    private History history;
    private Player p1, p2, currentPlayer;
    private Mountain k3;
    private ArrayList<Color> bag;
    private int phase;
    private boolean penality;
    private static final int nCubePerColor = 9;
    private static final int baseSize = 9;
    private static final int preparationPhase = 1;
    private static final int gamePhase = 2;

    // Constructor
    public Kube() {
        setK3(new Mountain(getBaseSize()));
        setBag(new ArrayList<Color>());
        setP1(new Player(1));
        setP2(new Player(2));
        setHistory(new History());
        setPhase(preparationPhase);
        setPenality(false);
    }

    // Getters
    public ArrayList<Color> getBag() {
        return bag;
    }

    public int getBaseSize() {
        return baseSize;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public History getHistory() {
        return history;
    }

    public Mountain getK3() {
        return k3;
    }

    public Player getP1() {
        return p1;
    }

    public Player getP2() {
        return p2;
    }

    public int getPhase() {
        return phase;
    }

    public boolean getPenality() {
        return penality;
    }

    // Setters
    public void setBag(ArrayList<Color> b) {
        bag = b;
    }

    public int setBaseSize() {
        return baseSize;
    }

    public void setCurrentPlayer(Player p) {
        currentPlayer = p;
    }

    public void setHistory(History h) {
        history = h;
    }

    public void setK3(Mountain m) {
        k3 = m;
    }

    public void setP1(Player p) {
        p1 = p;
    }

    public void setP2(Player p) {
        p2 = p;
    }

    public void setPhase(int p) {
        phase = p;
    }

    public void setPenality(boolean p) {
        penality = p;
    }

    // Methods
    public boolean isPlayable(Move move) {

        Player player = getCurrentPlayer();
        boolean cubeRemovable = false;
        boolean cubeCompatible = false;

        if (move.isFromAdditionals()) {
            // When we take the cube from the player's additional cubes, checking if the
            // move's cube color is in
            cubeRemovable = player.getAdditional().contains(move.getColor());
        } else if (move.isWhite() || move.isClassicMove()) {
            // When we take the cube from the player's mountain, checking if the cube is
            // removable
            cubeRemovable = player.getMountain().removable().contains(move.getFrom());
        } else {
            // Should never happen
            return false;
        }

        if (move.isWhite()) {
            // White cube is always compatible
            cubeCompatible = true;
        } else if (move.isFromAdditionals() || move.isClassicMove()) {
            // Checking if the cube is compatible with the base
            cubeCompatible = getK3().compatible(move.getColor()).contains(move.getTo());
        } else {
            // Should never happen
            return false;
        }

        return cubeRemovable && cubeCompatible;
    }

    public void fillBag(){
        fillBag(null);
    }
    // fill the bag with 9 times each colors, and randomize it until the base is
    // valid
    public void fillBag(Integer seed) {
        bag = new ArrayList<>();
        for (Color c : Color.getAllColored()) {
            for (int i = 0; i < nCubePerColor; i++) {
                bag.add(c);
            }
        }
        // verificate that there is 4 differents colors in the baseSize first cubes of
        // the bag
        while (new HashSet<>(bag.subList(0, 9)).size() < 4) {
            if (seed != null){
                Collections.shuffle(bag, new Random(seed));
            } else {
                Collections.shuffle(bag);
            }
        }
    }

    // fill the base with baseSize random colors
    public void fillBase() {
        for (int y = 0; y < baseSize; y++) {
            k3.setCase(baseSize - 1, y, bag.remove(0));
        }
    }

    // Set current player to the next
    public void nextPlayer() {
        if (currentPlayer == p1) {
            currentPlayer = p2;
        } else if (currentPlayer == p2) {
            currentPlayer = p1;
        } else {
            throw new UnsupportedOperationException("Current player is null");
        }
    }

    public void distributeCubesToPlayers() {
        HashMap<Color, Integer> p1Cubes = new HashMap<>();
        HashMap<Color, Integer> p2Cubes = new HashMap<>();
        p1Cubes.put(Color.WHITE, 2);
        p2Cubes.put(Color.WHITE, 2);
        p1Cubes.put(Color.NATURAL, 2);
        p2Cubes.put(Color.NATURAL, 2);
        for (Color c : Color.getAllColored()) {
            p1Cubes.put(c, 0);
            p2Cubes.put(c, 0);
        }
        Color c;
        for (int i = 0; i < 17; i++) {
            c = bag.remove(0);
            p1Cubes.put(c, p1Cubes.get(c) + 1);
            c = bag.remove(0);
            p2Cubes.put(c, p2Cubes.get(c) + 1);        }
        p1.setAvalaibleToBuild(p1Cubes);
        p2.setAvalaibleToBuild(p2Cubes);
    }

    public boolean playMove(Move move) {
        if (playMoveWithoutHistory(move)) {
            history.addMove(move);
            return true;
        }
        return false;
    }

    public boolean playMoveWithoutHistory(Move move) {

        Player player = getCurrentPlayer();
        Color color = move.getColor();
        boolean result = false;

        if (move.isWhite() && move.isFromAdditionals() && isPlayable(move)) {
            // Getting out the additional white cube from the player's additional cubes
            player.getAdditional().remove(color);

            // Adding the white cube to the player's used white cubes
            player.setWhiteUsed(player.getWhiteUsed() + 1);
            result = true;
        }
        // Catching if the move is about a colored cube or a white cube
        else if (move.isWhite() && isPlayable(move)) {
            // Getting out the white cube from the player's mountain
            player.getMountain().remove(move.getFrom().x, move.getFrom().y);

            // Adding the white cube to the player's used white cubes
            player.setWhiteUsed(player.getWhiteUsed() + 1);
            result = true;
        }
        // Checking if the move is about an additional cube
        else if (move.isFromAdditionals() && isPlayable(move)) {

            // Getting out the additional cube from the player's additional cubes
            player.getAdditional().remove(color);

            // Adding the additional cube to the player's mountain
            getK3().setCase(move.getTo().x, move.getTo().y, color);

            // Checks whether the move results in a penalty
            if (player.getMountain().isPenality(move.getTo().x, move.getTo().y, color)) {
                setPenality(true);
            }

            result = true;
        }
        // Checking if the move is a classic move and is playable
        else if (move.isClassicMove() && isPlayable(move)) {

            // Applying the move
            player.getMountain().remove(move.getFrom().x, move.getFrom().y);
            getK3().setCase(move.getTo().x, move.getTo().y, color);

            // Checks whether the move results in a penalty
            if (player.getMountain().isPenality(move.getTo().x, move.getTo().y, color)) {
                setPenality(true);
            }

            result = true;
        }

        if (result) {
            nextPlayer();
        }
        return result;
    }

    public Boolean canCurrentPlayerPlay() {
        if (getCurrentPlayer().getPlayableColors().size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    // Method that resturn the ensemble of move available for the curretn player
    public ArrayList<Move> ensMove() {
        ArrayList<Move> moves = new ArrayList<>();
        // List MM/MW moves
        for (Point start : getCurrentPlayer().getMountain().removable()) {
            Color c = getCurrentPlayer().getMountain().getCase(start);
            if (c == Color.WHITE) {
                Move mw = new MoveMW(start);
                moves.add(mw);
            } else {
                for (Point arr : getK3().compatible(c)) {
                    Move mm = new MoveMM(start, arr, c);
                    moves.add(mm);
                }
            }
        }
        // List AM/AW moves
        for (Color c : getCurrentPlayer().getAdditional()) {
            if (c == Color.WHITE) {
                Move aw = new MoveAW();
                moves.add(aw);
            } else {
                for (Point arr : getK3().compatible(c)) {
                    Move am = new MoveAM(arr, c);
                    moves.add(am);
                }
            }
        }
        return moves;
    }
}
