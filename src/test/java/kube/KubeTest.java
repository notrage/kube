package kube;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import kube.model.Color;
import kube.model.Kube;
import kube.model.Mountain;
import kube.model.move.*;

public class KubeTest {

    @Test
    public void kubeTest() {

        Kube kube = new Kube();
        // Verifying the game has been correctly initialized
        assertEquals(6, kube.getP1().getMountain().getBaseSize());
        assertEquals(6, kube.getP2().getMountain().getBaseSize());
        assertEquals(9, kube.getK3().getBaseSize());
        assertEquals(0, kube.getHistory().getDone().size());
        assertEquals(0, kube.getHistory().getUndone().size());
        assertEquals(0, kube.getHistory().getFirstPlayer());
        assertEquals(1, kube.getPhase());
    }

    @Test
    public void fillBagTest() {

        Kube kube = new Kube();
        // Filling the bag
        kube.fillBag();
        ArrayList<Color> bag = kube.getBag();
        // Verifying the bag has been filled with the right number of cubes
        assertEquals(45, bag.size());
        int[] colors = new int[8];
        for (Color c : bag) {
            switch (c) {
                case RED:
                    colors[3]++;
                    break;
                case GREEN:
                    colors[4]++;
                    break;
                case BLUE:
                    colors[5]++;
                    break;
                case YELLOW:
                    colors[6]++;
                    break;
                case BLACK:
                    colors[7]++;
                    break;
                default:
                    assertFalse(true);
            }
        }
        // Verifiying the bag has at least 9 cubes of each color
        for (int i = 3; i < 8; i++) {
            assertEquals(colors[i], 9);
        }
    }

    @Test
    public void fillBaseTest() {

        Kube kube = new Kube();
        // Filling the bag
        kube.fillBag();
        // Filling the base
        kube.fillBase();
        // Verifying the base has been filled with the right number of cubes
        assertEquals(36, kube.getBag().size());

        // Counting the number of cubes of each color in the base
        Color[] base = new Color[9];
        for (int i = 0; i < 9; i++) {
            base[i] = kube.getK3().getCase(8, i);
        }
        ArrayList<Color> colors = new ArrayList<>();
        for (Color c : base) {
            assertFalse(c == Color.EMPTY);
            if (!colors.contains(c)) {
                colors.add(c);
            }
        }
        // Verifying the base has at least 4 different colors
        assertTrue(colors.size() >= 4);
    }

    @Test
    public void isPlayableWorkingTest() {

        Kube kube = new Kube();
        initPlayMove(kube);

        // MoveMM
        assertTrue(kube.isPlayable(new MoveMM(1, 0, 7, 0, Color.BLUE)));
        assertTrue(kube.isPlayable(new MoveMM(1, 0, 7, 6, Color.BLUE)));
        assertTrue(kube.isPlayable(new MoveMM(1, 0, 7, 7, Color.BLUE)));

        // MoveMW
        assertTrue(kube.isPlayable(new MoveMW(1, 1)));

        // MoveAM
        assertTrue(kube.isPlayable(new MoveAM(7, 0, Color.BLUE)));
        assertTrue(kube.isPlayable(new MoveAM(7, 6, Color.BLUE)));
        assertTrue(kube.isPlayable(new MoveAM(7, 7, Color.BLUE)));
        assertTrue(kube.isPlayable(new MoveAM(7, 0, Color.RED)));
        assertTrue(kube.isPlayable(new MoveAM(7, 1, Color.RED)));
        assertTrue(kube.isPlayable(new MoveAM(7, 0, Color.NATURAL)));
        assertTrue(kube.isPlayable(new MoveAM(7, 1, Color.NATURAL)));
        assertTrue(kube.isPlayable(new MoveAM(7, 2, Color.NATURAL)));
        assertTrue(kube.isPlayable(new MoveAM(7, 3, Color.NATURAL)));
        assertTrue(kube.isPlayable(new MoveAM(7, 4, Color.NATURAL)));
        assertTrue(kube.isPlayable(new MoveAM(7, 5, Color.NATURAL)));
        assertTrue(kube.isPlayable(new MoveAM(7, 6, Color.NATURAL)));
        assertTrue(kube.isPlayable(new MoveAM(7, 7, Color.NATURAL)));

        // MoveAW
        assertTrue(kube.isPlayable(new MoveAW()));

        // MoveMA
        kube.setPenality(true);
        assertTrue(kube.isPlayable(new MoveMA(1, 0, Color.RED)));
        assertTrue(kube.isPlayable(new MoveMA(1, 1, Color.GREEN)));

        // MoveAA
        assertTrue(kube.isPlayable(new MoveAA(Color.GREEN)));
        assertTrue(kube.isPlayable(new MoveAA(Color.YELLOW)));
        assertTrue(kube.isPlayable(new MoveAA(Color.BLACK)));
        assertTrue(kube.isPlayable(new MoveAA(Color.RED)));
        assertTrue(kube.isPlayable(new MoveAA(Color.NATURAL)));
        assertTrue(kube.isPlayable(new MoveAA(Color.WHITE)));
    }

    @Test
    public void isPlayableNotWorkingTest() {

        Kube kube = new Kube();
        initPlayMove(kube);

        // MoveMM not removable
        assertFalse(kube.isPlayable(new MoveMM(0, 0, 7, 0, Color.BLUE)));
        assertFalse(kube.isPlayable(new MoveMM(2, 2, 7, 0, Color.BLUE)));

        // MoveMM not compatible
        assertFalse(kube.isPlayable(new MoveMM(1, 0, 7, 1, Color.BLUE)));
        assertFalse(kube.isPlayable(new MoveMM(1, 0, 7, 2, Color.BLUE)));

        // MoveMW not removable
        assertFalse(kube.isPlayable(new MoveMW(0, 0)));
        assertFalse(kube.isPlayable(new MoveMW(2, 1)));

        // MoveAM not removable
        assertFalse(kube.isPlayable(new MoveAM(7, 1, Color.GREEN)));
        assertFalse(kube.isPlayable(new MoveAM(7, 2, Color.YELLOW)));

        // MoveAM not compatible
        assertFalse(kube.isPlayable(new MoveAM(7, 4, Color.BLUE)));
        assertFalse(kube.isPlayable(new MoveAM(7, 7, Color.RED)));

        // MoveAW not removable
        kube.getP1().getAdditionals().remove(Color.WHITE);
        assertFalse(kube.isPlayable(new MoveAW()));

        // MoveMA not removable
        kube.setPenality(true);
        assertFalse(kube.isPlayable(new MoveMA(2, 0, Color.YELLOW)));
        assertFalse(kube.isPlayable(new MoveMA(2, 1, Color.WHITE)));
        assertFalse(kube.isPlayable(new MoveMA(2, 2, Color.BLUE)));
        kube.setPenality(false);

        // MoveMA not compatible
        assertFalse(kube.isPlayable(new MoveMA(1, 0, Color.RED)));
        assertFalse(kube.isPlayable(new MoveMA(1, 1, Color.GREEN)));

        // MoveAA not removable
        kube.setPenality(true);
        assertFalse(kube.isPlayable(new MoveAA(Color.BLUE)));
        kube.setPenality(false);

        // MoveAA not compatible
        assertFalse(kube.isPlayable(new MoveAA(Color.GREEN)));
        assertFalse(kube.isPlayable(new MoveAA(Color.YELLOW)));
        assertFalse(kube.isPlayable(new MoveAA(Color.BLACK)));
        assertFalse(kube.isPlayable(new MoveAA(Color.RED)));
        assertFalse(kube.isPlayable(new MoveAA(Color.NATURAL)));
    }

    @Test
    public void distributeCubesToPlayersTest() {

        Kube kube = new Kube();
        // Filling the bag
        kube.fillBag();
        // Distributing the cubes to players 
        kube.distributeCubesToPlayers();

        // Verifying the players have the right number of cubes
        int sum = 0;
        for (int n : kube.getP1().getAvalaibleToBuild().values()) {
            sum += n;
        }
        assertEquals(21, sum);

        sum = 0;
        for (int n : kube.getP2().getAvalaibleToBuild().values()) {
            sum += n;
        }
        assertEquals(21, sum);
        // Verifying the bag having at the end 11 cubes
        assertEquals(11, kube.getBag().size());
    }

    @Test
    public void playMoveWorkingTest() {

        Kube kube = new Kube();

        // MoveMW
        // Initialisation of the cube
        initPlayMove(kube);
        // Creation of the move we want to test
        MoveMW mw = new MoveMW(1, 1);
        // Playing the move
        assertTrue(kube.playMove(mw));
        // Verifying the cube has been coorecly moved
        assertEquals(Color.EMPTY, kube.getP1().getMountain().getCase(1, 1));
        int lastElementIndex = kube.getHistory().getDone().size() - 1;
        // Verifying the move has been added to the history
        assertEquals(mw, kube.getHistory().getDone().get(lastElementIndex));
        // Verifying the penality state
        assertFalse(kube.getPenality());

        // MoveMM
        initPlayMove(kube);
        MoveMM mm = new MoveMM(1, 0, 7, 0, Color.BLUE);
        assertTrue(kube.playMove(mm));
        assertEquals(Color.EMPTY, kube.getP1().getMountain().getCase(1, 0));
        assertEquals(Color.BLUE, kube.getK3().getCase(7, 0));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(mm, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        mm = new MoveMM(1, 0, 7, 5, Color.BLUE);
        assertTrue(kube.playMove(mm));
        assertEquals(Color.EMPTY, kube.getP1().getMountain().getCase(1, 0));
        assertEquals(Color.BLUE, kube.getK3().getCase(7, 5));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(mm, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        mm = new MoveMM(1, 0, 7, 6, Color.BLUE);
        assertTrue(kube.playMove(mm));
        assertEquals(Color.EMPTY, kube.getP1().getMountain().getCase(1, 0));
        assertEquals(Color.BLUE, kube.getK3().getCase(7, 6));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(mm, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        mm = new MoveMM(1, 0, 7, 7, Color.BLUE);
        assertTrue(kube.playMove(mm));
        assertEquals(Color.EMPTY, kube.getP1().getMountain().getCase(1, 0));
        assertEquals(Color.BLUE, kube.getK3().getCase(7, 7));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(mm, kube.getHistory().getDone().get(lastElementIndex));
        assertTrue(kube.getPenality());

        // MoveAM
        initPlayMove(kube);
        MoveAM am = new MoveAM(7, 0, Color.BLUE);
        assertTrue(kube.playMove(am));
        assertEquals(Color.BLUE, kube.getK3().getCase(7, 0));
        assertEquals(0, Collections.frequency(kube.getP1().getAdditionals(), Color.BLUE));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(am, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        am = new MoveAM(7, 5, Color.BLUE);
        assertTrue(kube.playMove(am));
        assertEquals(Color.BLUE, kube.getK3().getCase(7, 5));
        assertFalse(kube.getP1().getAdditionals().contains(Color.BLUE));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(am, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        am = new MoveAM(7, 6, Color.BLUE);
        assertTrue(kube.playMove(am));
        assertEquals(Color.BLUE, kube.getK3().getCase(7, 6));
        assertFalse(kube.getP1().getAdditionals().contains(Color.BLUE));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(am, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        am = new MoveAM(7, 7, Color.BLUE);
        assertTrue(kube.playMove(am));
        assertEquals(Color.BLUE, kube.getK3().getCase(7, 7));
        assertFalse(kube.getP1().getAdditionals().contains(Color.BLUE));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(am, kube.getHistory().getDone().get(lastElementIndex));
        assertTrue(kube.getPenality());

        initPlayMove(kube);
        am = new MoveAM(7, 0, Color.RED);
        assertTrue(kube.playMove(am));
        assertEquals(Color.RED, kube.getK3().getCase(7, 0));
        assertFalse(kube.getP1().getAdditionals().contains(Color.RED));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(am, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        am = new MoveAM(7, 1, Color.RED);
        assertTrue(kube.playMove(am));
        assertEquals(Color.RED, kube.getK3().getCase(7, 1));
        assertFalse(kube.getP1().getAdditionals().contains(Color.RED));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(am, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        am = new MoveAM(7, 5, Color.RED);
        assertTrue(kube.playMove(am));
        assertEquals(Color.RED, kube.getK3().getCase(7, 5));
        assertFalse(kube.getP1().getAdditionals().contains(Color.RED));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(am, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        am = new MoveAM(7, 6, Color.RED);
        assertTrue(kube.playMove(am));
        assertEquals(Color.RED, kube.getK3().getCase(7, 6));
        assertFalse(kube.getP1().getAdditionals().contains(Color.RED));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(am, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        for (int i = 0; i < 7; i++) {
            initPlayMove(kube);
            am = new MoveAM(7, i, Color.NATURAL);
            assertTrue(kube.playMove(am));
            assertEquals(Color.NATURAL, kube.getK3().getCase(7, i));
            assertFalse(kube.getP1().getAdditionals().contains(Color.NATURAL));
            lastElementIndex = kube.getHistory().getDone().size() - 1;
            assertEquals(am, kube.getHistory().getDone().get(lastElementIndex));
            if (i == 4 || i == 7) {
                assertTrue(kube.getPenality());
            }
            else {
                assertFalse(kube.getPenality());
            }
        }

        // MoveAW
        initPlayMove(kube);
        MoveAW aw = new MoveAW();
        assertTrue(kube.playMove(aw));
        assertFalse(kube.getP1().getAdditionals().contains(Color.WHITE));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(aw, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        // MoveMA
        initPlayMove(kube);
        kube.setPenality(true);
        MoveMA ma = new MoveMA(1, 0, Color.RED);
        assertTrue(kube.playMove(ma));
        assertEquals(Color.EMPTY, kube.getP2().getMountain().getCase(1, 0));
        assertEquals(2, Collections.frequency(kube.getP1().getAdditionals(), Color.RED));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(ma, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        kube.setPenality(true);
        ma = new MoveMA(1, 1, Color.GREEN);
        assertTrue(kube.playMove(ma));
        assertEquals(Color.EMPTY, kube.getP2().getMountain().getCase(1, 1));
        assertEquals(1, Collections.frequency(kube.getP2().getAdditionals(), Color.GREEN));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(ma, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        // MoveAA
        initPlayMove(kube);
        kube.setPenality(true);
        MoveAA aa = new MoveAA(Color.GREEN);
        assertTrue(kube.playMove(aa));
        assertEquals(0, Collections.frequency(kube.getP2().getAdditionals(), Color.GREEN));
        assertEquals(1, Collections.frequency(kube.getP1().getAdditionals(), Color.GREEN));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(aa, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        kube.setPenality(true);
        aa = new MoveAA(Color.YELLOW);
        assertTrue(kube.playMove(aa));
        assertEquals(0, Collections.frequency(kube.getP2().getAdditionals(), Color.YELLOW));
        assertEquals(1, Collections.frequency(kube.getP1().getAdditionals(), Color.YELLOW));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(aa, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        kube.setPenality(true);
        aa = new MoveAA(Color.BLACK);
        assertTrue(kube.playMove(aa));
        assertEquals(0, Collections.frequency(kube.getP2().getAdditionals(), Color.BLACK));
        assertEquals(1, Collections.frequency(kube.getP1().getAdditionals(), Color.BLACK));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(aa, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        kube.setPenality(true);
        aa = new MoveAA(Color.RED);
        assertTrue(kube.playMove(aa));
        assertEquals(0, Collections.frequency(kube.getP2().getAdditionals(), Color.RED));
        assertEquals(2, Collections.frequency(kube.getP1().getAdditionals(), Color.RED));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(aa, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        kube.setPenality(true);
        aa = new MoveAA(Color.NATURAL);
        assertTrue(kube.playMove(aa));
        assertEquals(0, Collections.frequency(kube.getP2().getAdditionals(), Color.NATURAL));
        assertEquals(2, Collections.frequency(kube.getP1().getAdditionals(), Color.NATURAL));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(aa, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());

        initPlayMove(kube);
        kube.setPenality(true);
        aa = new MoveAA(Color.WHITE);
        assertTrue(kube.playMove(aa));
        assertEquals(0, Collections.frequency(kube.getP2().getAdditionals(), Color.WHITE));
        assertEquals(2, Collections.frequency(kube.getP1().getAdditionals(), Color.WHITE));
        lastElementIndex = kube.getHistory().getDone().size() - 1;
        assertEquals(aa, kube.getHistory().getDone().get(lastElementIndex));
        assertFalse(kube.getPenality());
    }
    
    @Test
    public void playMoveNotWorkingTest() {

        Kube kube = new Kube();
        initPlayMove(kube);
        kube.getP1().getMountain().setCase(0, 0, Color.BLUE);

        // Move to a place there is already a cube
        boolean res = kube.playMove(new MoveMM(0, 0, 8, 0, kube.getP1().getMountain().getCase(0, 0)));
        assertFalse(res);
        assertEquals(0, kube.getHistory().getDone().size());
        assertEquals(0, kube.getHistory().getUndone().size());
        assertEquals(1, kube.getCurrentPlayer().getId());
        assertEquals(Color.BLUE, kube.getP1().getMountain().getCase(0, 0));

        // Move a cube where it should not be
        res = kube.playMove(new MoveMM(0, 0, 7, 8, kube.getP1().getMountain().getCase(0, 0)));
        assertFalse(res);
        assertEquals(0, kube.getHistory().getDone().size());
        assertEquals(0, kube.getHistory().getUndone().size());
        assertEquals(1, kube.getCurrentPlayer().getId());
        assertEquals(Color.BLUE, kube.getP1().getMountain().getCase(0, 0));

        // Move a cube on the wrong mountain's cubes
        res = kube.playMove(new MoveMM(0, 0, 7, 2, kube.getP1().getMountain().getCase(0, 0)));
        assertFalse(res);
        assertEquals(0, kube.getHistory().getDone().size());
        assertEquals(0, kube.getHistory().getUndone().size());
        assertEquals(1, kube.getCurrentPlayer().getId());
        assertEquals(Color.BLUE, kube.getP1().getMountain().getCase(0, 0));

        // Move a cube with the wrong color
        res = kube.playMove(new MoveMM(0, 0, 7, 0, Color.RED));
        assertFalse(res);
        assertEquals(0, kube.getHistory().getDone().size());
        assertEquals(0, kube.getHistory().getUndone().size());
        assertEquals(1, kube.getCurrentPlayer().getId());
        assertEquals(Color.BLUE, kube.getP1().getMountain().getCase(0, 0));
    }

    @Test
    public void unPlayTest() {

        Kube kube = new Kube();
        
        // MoveMW
        // Initialisation
        initPlayMove(kube);
        // Creation of the move we want to test
        Move move = new MoveMW(1, 1);
        // Cloning the global kube state
        Mountain k3 = kube.getK3().clone();
        Mountain p1 = kube.getP1().getMountain().clone();
        Mountain p2 = kube.getP2().getMountain().clone();
        int p = kube.getCurrentPlayer().getId();
        ArrayList<Color> p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        ArrayList<Color> p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        // Playing and unplaying the move
        kube.playMove(move);
        kube.unPlay();

        // Verifying the global kube state has not changed
        ArrayList<Color> p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        ArrayList<Color> p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertFalse(kube.getPenality());

        // MoveMM
        // MoveMM(MoveMM(1, 0, 7, 0, Color.BLUE))
        initPlayMove(kube);
        move = new MoveMM(1, 0, 7, 0, Color.BLUE);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertFalse(kube.getPenality());

        // MoveMM(1, 0, 7, 5, Color.BLUE)
        initPlayMove(kube);
        move = new MoveMM(1, 0, 7, 5, Color.BLUE);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertFalse(kube.getPenality());

        // MoveMM(1, 0, 7, 6, Color.BLUE)
        initPlayMove(kube);
        move = new MoveMM(1, 0, 7, 6, Color.BLUE);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertFalse(kube.getPenality());

        // MoveMM(1, 0, 7, 7, Color.BLUE)
        initPlayMove(kube);
        move = new MoveMM(1, 0, 7, 7, Color.BLUE);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertFalse(kube.getPenality());

        // MoveAM
        // MoveAM(7, 0, Color.BLUE)
        initPlayMove(kube);
        move = new MoveAM(7, 0, Color.BLUE);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertFalse(kube.getPenality());

        // MoveAM(7, 5, Color.BLUE)
        initPlayMove(kube);
        move = new MoveAM(7, 5, Color.BLUE);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertFalse(kube.getPenality());

        // MoveAM(7, 6, Color.BLUE)
        initPlayMove(kube);
        move = new MoveAM(7, 6, Color.BLUE);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertFalse(kube.getPenality());

        // MoveAM(7, 7, Color.BLUE)
        initPlayMove(kube);
        move = new MoveAM(7, 7, Color.BLUE);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertFalse(kube.getPenality());

        // MoveAM(7, 0, Color.RED)
        initPlayMove(kube);
        move = new MoveAM(7, 0, Color.RED);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertFalse(kube.getPenality());

        // MoveAM(7, 1, Color.RED)
        initPlayMove(kube);
        move = new MoveAM(7, 1, Color.RED);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertFalse(kube.getPenality());

        // MoveAM(7, 5, Color.RED)
        initPlayMove(kube);
        move = new MoveAM(7, 5, Color.RED);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertFalse(kube.getPenality());

        // MoveAM(7, 6, Color.RED)
        initPlayMove(kube);
        move = new MoveAM(7, 6, Color.RED);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertFalse(kube.getPenality());

        // MoveAM(7, i, Color.NATURAL)
        for (int i = 0; i < 7; i++) {

            initPlayMove(kube);
            move = new MoveAM(7, i, Color.NATURAL);
            k3 = kube.getK3().clone();
            p1 = kube.getP1().getMountain().clone();
            p2 = kube.getP2().getMountain().clone();
            p = kube.getCurrentPlayer().getId();
            p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
            p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

            p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
            p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

            kube.playMove(move);
            kube.unPlay();

            p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
            p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

            assertTrue(areSameMountain(k3, kube.getK3()));
            assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
            assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

            p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
            p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

            assertEquals(p1Additional, p1Additional2);
            assertEquals(p2Additional, p2Additional2);
            assertEquals(p, kube.getCurrentPlayer().getId());

            assertFalse(kube.getHistory().getDone().contains(move));
            assertFalse(kube.getPenality());
        }

        // MoveAW
        initPlayMove(kube);
        move = new MoveAW();
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertFalse(kube.getPenality());

        // MoveMA
        // MoveMA(1, 0, Color.RED)
        initPlayMove(kube);
        kube.setPenality(true);
        move = new MoveMA(1, 0, Color.RED);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertTrue(kube.getPenality());

        // MoveMA(1, 1, Color.GREEN)
        initPlayMove(kube);
        kube.setPenality(true);
        move = new MoveMA(1, 1, Color.GREEN);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertTrue(kube.getPenality());

        // MoveAA
        // MoveAA(Color.GREEN)
        initPlayMove(kube);
        kube.setPenality(true);
        move = new MoveAA(Color.GREEN);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertTrue(kube.getPenality());

        // MoveAA(Color.YELLOW)
        initPlayMove(kube);
        kube.setPenality(true);
        move = new MoveAA(Color.YELLOW);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertTrue(kube.getPenality());

        // MoveAA(Color.BLACK)
        initPlayMove(kube);
        kube.setPenality(true);
        move = new MoveAA(Color.BLACK);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertTrue(kube.getPenality());

        // MoveAA(Color.RED)
        initPlayMove(kube);
        kube.setPenality(true);
        move = new MoveAA(Color.RED);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertTrue(kube.getPenality());

        // MoveAA(Color.NATURAL)
        initPlayMove(kube);
        kube.setPenality(true);
        move = new MoveAA(Color.NATURAL);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertTrue(kube.getPenality());

        // MoveAA(Color.WHITE)
        initPlayMove(kube);
        kube.setPenality(true);
        move = new MoveAA(Color.WHITE);
        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p = kube.getCurrentPlayer().getId();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(move);
        kube.unPlay();

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);
        assertEquals(p, kube.getCurrentPlayer().getId());

        assertFalse(kube.getHistory().getDone().contains(move));
        assertTrue(kube.getPenality());
    }

    @Test
    public void rePlayTest() {
    
        Kube kube = new Kube();

        // MoveMW
        // Initialisation
        initPlayMove(kube);
        // Creation of the move we want to test
        MoveMW mw = new MoveMW(1, 1);
        // Cloning all the kube state
        Mountain k3 = kube.getK3().clone();
        Mountain p1 = kube.getP1().getMountain().clone();
        Mountain p2 = kube.getP2().getMountain().clone();
        int nbWhite = kube.getP1().getWhiteUsed();
        ArrayList<Color> p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        ArrayList<Color> p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        // Playing, unplaying and replaying the move
        kube.playMove(mw);
        kube.unPlay();
        kube.rePlay();

        // Changing to the expected state
        p1.remove(1, 1);
        nbWhite ++;

        // Verifying that the state is the expected one
        ArrayList<Color> p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        ArrayList<Color> p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);

        assertTrue(kube.getHistory().getDone().contains(mw));
        assertFalse(kube.getHistory().getUndone().contains(mw));
        assertEquals(nbWhite, kube.getP1().getWhiteUsed());

        // MoveMM
        initPlayMove(kube);
        MoveMM mm = new MoveMM(1, 0, 7, 0, Color.BLUE);

        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(mm);
        kube.unPlay();
        kube.rePlay();

        p1.remove(1, 0);
        k3.setCase(7, 0, Color.BLUE);

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);

        assertTrue(kube.getHistory().getDone().contains(mm));
        assertFalse(kube.getHistory().getUndone().contains(mm));

        // MoveAM
        initPlayMove(kube);
        MoveAM am = new MoveAM(7, 0, Color.BLUE);

        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(am);
        kube.unPlay();
        kube.rePlay();

        p1Additional.remove(Color.BLUE);
        k3.setCase(7, 0, Color.BLUE);

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);

        assertTrue(kube.getHistory().getDone().contains(am));
        assertFalse(kube.getHistory().getUndone().contains(am));

        // MoveAW
        initPlayMove(kube);
        MoveAW aw = new MoveAW();

        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        nbWhite = kube.getP1().getWhiteUsed();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(aw);
        kube.unPlay();
        kube.rePlay();

        p1Additional.remove(Color.WHITE);
        nbWhite ++;

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);

        assertTrue(kube.getHistory().getDone().contains(aw));
        assertFalse(kube.getHistory().getUndone().contains(aw));
        assertEquals(nbWhite, kube.getP1().getWhiteUsed());

        // MoveMA
        initPlayMove(kube);
        kube.setPenality(true);
        MoveMA ma = new MoveMA(1, 0, Color.RED);

        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        nbWhite = kube.getP1().getWhiteUsed();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(ma);
        kube.unPlay();
        kube.rePlay();

        p2.remove(1, 0);
        p1Additional.add(Color.RED);

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);

        assertTrue(kube.getHistory().getDone().contains(ma));
        assertFalse(kube.getHistory().getUndone().contains(ma));

        // MoveAA
        initPlayMove(kube);
        kube.setPenality(true);
        MoveAA aa = new MoveAA(Color.GREEN);

        k3 = kube.getK3().clone();
        p1 = kube.getP1().getMountain().clone();
        p2 = kube.getP2().getMountain().clone();
        p1Additional = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional = new ArrayList<>(kube.getP2().getAdditionals());

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        kube.playMove(aa);
        kube.unPlay();
        kube.rePlay();

        p2Additional.remove(Color.GREEN);
        p1Additional.add(Color.GREEN);

        p1Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        p1Additional2 = new ArrayList<>(kube.getP1().getAdditionals());
        p2Additional2 = new ArrayList<>(kube.getP2().getAdditionals());

        assertTrue(areSameMountain(k3, kube.getK3()));
        assertTrue(areSameMountain(p1, kube.getP1().getMountain()));
        assertTrue(areSameMountain(p2, kube.getP2().getMountain()));

        p1Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());
        p2Additional2.sort((c1, c2) -> c1.getColorCode() - c2.getColorCode());

        assertEquals(p1Additional, p1Additional2);
        assertEquals(p2Additional, p2Additional2);

        assertTrue(kube.getHistory().getDone().contains(aa));
        assertFalse(kube.getHistory().getUndone().contains(aa));
    }

    @Test
    public void testSeededBag() {

        Kube kube = new Kube();
        // Filling first kube's the bag
        kube.fillBag(1);
        Kube kube2 = new Kube();
        // Filling the second kube's bag with the same seed
        kube2.fillBag(1);
        // Verifying that the two bags are the same
        for (int i = 0; i < kube.getBag().size(); i++) {
            assertEquals(kube.getBag().get(i), kube2.getBag().get(i));
        }
    }

    @Test
    public void moveSetTest() {

        Kube kube = new Kube();
        // Filling the bag
        kube.fillBag(1);
        // Filling the base
        kube.fillBase();
        // Distributing the cubes to the players
        kube.distributeCubesToPlayers();
        // Setting the current player state
        Color[][] mountainP1 = new Color[][] {
            { Color.EMPTY, Color.EMPTY, Color.EMPTY, Color.EMPTY, Color.EMPTY, Color.EMPTY },
            { Color.EMPTY, Color.EMPTY, Color.EMPTY, Color.EMPTY, Color.EMPTY, Color.EMPTY },
            { Color.EMPTY, Color.BLACK, Color.EMPTY, Color.EMPTY, Color.EMPTY, Color.EMPTY },
            { Color.RED, Color.BLUE, Color.GREEN, Color.EMPTY, Color.EMPTY, Color.EMPTY },
            { Color.RED, Color.BLUE, Color.GREEN, Color.WHITE, Color.EMPTY, Color.EMPTY },
            { Color.RED, Color.BLUE, Color.GREEN, Color.NATURAL, Color.YELLOW, Color.WHITE }
        };
        kube.getP1().getMountain().setMountain(mountainP1);
        kube.getP1().addToAdditionals(Color.NATURAL);
        kube.getP1().addToAdditionals(Color.WHITE);
        kube.getP1().addToAdditionals(Color.YELLOW);
        kube.setCurrentPlayer(kube.getP1());

        // Verifying the outgoing move set
        kube.setPhase(2);
        ArrayList<Move> moves = kube.moveSet();

        assertTrue(moves.contains(new MoveMM(2, 1, 7, 0, Color.BLACK)));
        assertTrue(moves.contains(new MoveMM(2, 1, 7, 2, Color.BLACK)));
        assertTrue(moves.contains(new MoveMM(2, 1, 7, 3, Color.BLACK)));
        assertTrue(moves.contains(new MoveMM(2, 1, 7, 4, Color.BLACK)));
        assertTrue(moves.contains(new MoveMM(2, 1, 7, 5, Color.BLACK)));
        assertTrue(moves.contains(new MoveMM(2, 1, 7, 5, Color.BLACK)));
        assertTrue(moves.contains(new MoveMW(5, 5)));
        assertTrue(moves.contains(new MoveAM(7, 0, Color.NATURAL)));
        assertTrue(moves.contains(new MoveAM(7, 1, Color.NATURAL)));
        assertTrue(moves.contains(new MoveAM(7, 2, Color.NATURAL)));
        assertTrue(moves.contains(new MoveAM(7, 3, Color.NATURAL)));
        assertTrue(moves.contains(new MoveAM(7, 4, Color.NATURAL)));
        assertTrue(moves.contains(new MoveAM(7, 5, Color.NATURAL)));
        assertTrue(moves.contains(new MoveAM(7, 6, Color.NATURAL)));
        assertTrue(moves.contains(new MoveAM(7, 7, Color.NATURAL)));
        assertTrue(moves.contains(new MoveAW()));
        assertTrue(moves.contains(new MoveAM(7, 6, Color.YELLOW)));
        assertTrue(moves.contains(new MoveAM(7, 7, Color.YELLOW)));
    }

    private void initPlayMove(Kube k) {

        k.getK3().clear();

        // Seeting the kube's base
        k.getK3().setCase(8, 0, Color.BLUE);
        k.getK3().setCase(8, 1, Color.RED);
        k.getK3().setCase(8, 2, Color.GREEN);
        k.getK3().setCase(8, 3, Color.YELLOW);
        k.getK3().setCase(8, 4, Color.BLACK);
        k.getK3().setCase(8, 5, Color.BLACK);
        k.getK3().setCase(8, 6, Color.NATURAL);
        k.getK3().setCase(8, 7, Color.BLUE);
        k.getK3().setCase(8, 8, Color.BLUE);

        // Setting the first player's mountain
        Mountain m = new Mountain(3);
        k.getP1().getAdditionals().clear();

        m.setCase(1, 0, Color.BLUE);
        m.setCase(1, 1, Color.WHITE);
        m.setCase(2, 0, Color.YELLOW);
        m.setCase(2, 1, Color.WHITE);
        m.setCase(2, 2, Color.BLUE);

        k.getP1().setMountain(m);

        k.getP1().getAdditionals().add(Color.BLUE);
        k.getP1().getAdditionals().add(Color.RED);
        k.getP1().getAdditionals().add(Color.NATURAL);
        k.getP1().getAdditionals().add(Color.WHITE);

        // Setting the second player's mountain
        m = new Mountain(3);
        k.getP2().getAdditionals().clear();

        m.setCase(1, 0, Color.RED);
        m.setCase(1, 1, Color.GREEN);
        m.setCase(2, 0, Color.BLACK);
        m.setCase(2, 1, Color.GREEN);
        m.setCase(2, 2, Color.RED);

        k.getP2().setMountain(m);

        k.getP2().getAdditionals().add(Color.GREEN);
        k.getP2().getAdditionals().add(Color.YELLOW);
        k.getP2().getAdditionals().add(Color.BLACK);
        k.getP2().getAdditionals().add(Color.RED);
        k.getP2().getAdditionals().add(Color.NATURAL);
        k.getP2().getAdditionals().add(Color.WHITE);

        // Other settings for the kube instance
        k.getHistory().clear();
        k.setPhase(2);
        k.setCurrentPlayer(k.getP1());
        k.setPenality(false);
    }

    private boolean areSameMountain(Mountain m1, Mountain m2) {
        if (m1.getBaseSize() != m2.getBaseSize()) {
            return false;
        }
        for (int i = 0; i < m1.getBaseSize(); i++) {
            for (int j = 0; j < m1.getBaseSize(); j++) {
                if (m1.getCase(i, j) != m2.getCase(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }
}