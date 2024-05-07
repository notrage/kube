package kube.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.awt.Point;

public class Player {

    int id, whiteUsed;
    String name;
    Mountain mountain;
    ArrayList<Color> additionals;
    HashMap<Color, Integer> avalaibleToBuild;

    // Constructor
    public Player(int id) {
        setId(id);
        setWhiteUsed(0);
        setMountain(new Mountain(6));
        clearMountain();
        setAdditionals(new ArrayList<Color>());
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setWhiteUsed(int whiteUsed) {
        this.whiteUsed = whiteUsed;
    }

    public void setMountain(Mountain mountain) {
        this.mountain = mountain;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdditionals(ArrayList<Color> additionals) {
        this.additionals = additionals;
    }

    public void setAvalaibleToBuild(HashMap<Color, Integer> avalaibleToBuild) {
        this.avalaibleToBuild = avalaibleToBuild;
    }

    // Getters
    public int getId() {
        return this.id;
    }

    public int getWhiteUsed() {
        return this.whiteUsed;
    }

    public Mountain getMountain() {
        return this.mountain;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Color> getAdditionals() {
        return this.additionals;
    }

    public HashMap<Color, Integer> getAvalaibleToBuild() {
        return this.avalaibleToBuild;
    }

    // Methods
    public void addToAdditionals(Color color) {
        getAdditionals().add(color);
    }

    public Color removeFromAdditionals(int pos) {
        return getAdditionals().remove(pos);
    }

    public boolean addToMountain(Point point, Color color) {
        return addToMountain(point.x, point.y, color);
    }

    public boolean addToMountain(int l, int c, Color color) {
        
        if (l < 0 || c < 0 || l < c || l >= getMountain().getBaseSize()) {
            return false;
        }
        
        int n;
        Color colb = getMountain().getCase(l, c);
        if ((n = getAvalaibleToBuild().get(color)) > 0) {
            getMountain().setCase(l, c, color);
            getAvalaibleToBuild().put(color, n);
            if (colb != Color.EMPTY) {
                getAvalaibleToBuild().put(colb, getAvalaibleToBuild().get(colb) + 1);
            }
            return true;
        }
        return false;
    }

    public boolean isAvailableToBuild(Color c){
        return getAvalaibleToBuild().get(c) > 0;
    }

    public Color removeFromMountain(Point point) {
        return removeFromMountain(point.x, point.y);
    }

    public Color removeFromMountain(int l, int c) {
        Color col = getMountain().getCase(l, c);
        getMountain().remove(l, c);
        return col;
    }

    public void removeFromAvailableToBuild(Point p) {
        removeFromAvailableToBuild(p.x, p.y);
    }

    public void removeFromAvailableToBuild(int l, int c) {
        Color color;
        if ((color = getMountain().getCase(l, c)) != Color.WHITE){
            getMountain().remove(l, c);
            getAvalaibleToBuild().put(color, getAvalaibleToBuild().get(color) + 1);
        }
    }

    public void clearMountain() {
        getMountain().clear();
    }

    public boolean isMountainFull() {
        return getMountain().isFull();
    }

    public boolean isMountainEmpty() {
        return getMountain().isEmpty();
    }

    public String forSave() {
        String s = "{";
        s += getId() + "\n {";
        s += getMountain().forSave() + "}";
        s += "{";
        for (Color c : getAdditionals()) {
            s += c.toString() + ",";
        }
        if (getAdditionals().size() > 0)
            s = s.substring(0, s.length() - 1);
        s += "}";
        return s;
    }

    public String toString() {
        String s = "Player " + getId();
        s += "\nMountain: \n" + getMountain().toString();
        s += "\nAdditional: ";
        for (Color c : getAdditionals()) {
            s += c.toString() + " ";
        }
        return s;
    }

    public HashSet<Color> getPlayableColors() {

        HashSet<Color> playable = new HashSet<>();
        HashSet<Color> toTest = new HashSet<>();
        ArrayList<Point> removable = getMountain().removable();

        for (Point p : removable) {

            toTest.add(getMountain().getCase(p.x, p.y));
        }

        toTest.addAll(getAdditionals());

        for (Color c : toTest) {

            if (getMountain().compatible(c).size() >= 1) {

                playable.add(c);
            }
        }
        return playable;
    }
}
