package com.makzk.games.hiddenships.tests;

import com.makzk.games.hiddenships.Board;
import com.makzk.games.hiddenships.Ship;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Testing Board methods
 * Created by makzk on 30-05-2015.
 */
public class BoardTest {
    Board b = new Board(10, 10, 1, 0, 0);

    @Test
    public void testPlace() throws Exception {
        b.place(Ship.createShip(0, 0, true, 4));
        assertTrue(b.containsShip(0, 0));
        assertTrue(b.containsShip(1, 0));
        assertTrue(b.containsShip(2, 0));
        assertTrue(b.containsShip(3, 0));
    }

    @Test
    public void testCanBePlaced() throws Exception {
        b.place(Ship.createShip(0, 0, true, 4));
        assertFalse(b.canBePlaced(Ship.createShip(0, 0, true, 1)));
        assertTrue(b.canBePlaced(Ship.createShip(0, 1, true, 1)));
    }

    @Test
    public void testGetAt() throws Exception {
        Ship s = Ship.createShip(0, 0, true, 4);
        b.place(s);
        assertTrue(s.equals(b.getAt(0, 0)));
    }

    @Test
    public void testTotalParts() throws Exception {
        b.place(Ship.createShip(0, 0, true, 4));
        b.place(Ship.createShip(0, 1, true, 4));
        assertTrue(b.totalParts() == 8);

        b.place(Ship.createShip(0, 2, true, 4));
        assertTrue(b.totalParts() == 12);
    }

    @Test
    public void testReset() throws Exception {
        b.place(Ship.createShip(0, 0, true, 4));
        b.place(Ship.createShip(0, 1, true, 4));
        assertTrue(b.totalParts() == 8);
        b.reset();
        assertTrue(b.totalParts() == 0);
    }
}