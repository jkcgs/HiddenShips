package com.makzk.games.hiddenships;

import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Hidden ships (submarines) game simulation
 * Created by makzk on 27-05-15.
 */
public class HiddenShips extends BasicGameState {
    // Position where the squares begin
    private int initialX = 20;
    private int initialY = 20;

    // Number of cols and rows, and square size
    private int cols = 15;
    private int rows = 11;
    private int size = 50;

    // Minimum size of each ship
    private int minLength = 2;
    private int maxLength = 5;

    // Number of ships to place in
    private int nShips = 6;

    // Total amount of ship parts placed
    private int totalSelected = 0;

    // Board
    private Board board;

    public HiddenShips() {
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        board = new Board(cols, rows, size, initialX+5, initialY+5);
        reset();
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        // :D
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        board.draw(g);

        // Information
        int xpos = initialX + cols * size + cols + 10;
        g.setColor(Color.white);
        g.drawString(String.format("Active: (%s, %s)", board.activeX+1, board.activeY+1), xpos, 30);
        g.drawString(String.format("Found: %s/%s", board.found, totalSelected), xpos, 50);
        g.drawString(String.format("Sunken: %s/%s", board.sunken, nShips), xpos, 70);
        g.drawString("Legend\nDark grey: not checked\nLight grey: nothing\nRed: sunken ship part", xpos, 110);
    }

    public void mouseMoved(int oldx, int oldy, int mouseX, int mouseY) {
        board.handleMouseMove(mouseX, mouseY);
    }

    public void mousePressed(int button, int x, int y) {
        board.handleMouseClick(button, x, y);
    }

    public void keyPressed(int key, char c) {
        // Resets everything if the R key is pressed
        if(c == 'r') {
            reset();
        }
    }

    public void reset() {
        board.reset();

        // Set special blocks
        int c = 0;
        while(c < nShips) {
            int size = minLength + (int)(Math.random() * (maxLength - minLength));
            boolean direction = Math.random() >= .5; // true: horizontal, false: vertical

            // Initial positions for ship
            int ix = (int)(Math.random() * (direction ? cols - size : cols)); // initial x
            int iy = (int)(Math.random() * (direction ? rows : rows - size)); // initial y

            Ship ship = Ship.createShip(ix, iy, direction, size);
            if(board.canBePlaced(ship)) {
                board.place(ship);
                c++;
            }
        }

        totalSelected = board.totalParts();
    }

    @Override
    public int getID() {
        return 0;
    }
}
