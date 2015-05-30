package com.makzk.games.hiddenships;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

/**
 * A board that contains ships
 * Created by makzk on 29-05-15.
 */
public class Board {
    int cols = 0;
    int rows = 0;
    int blockSize = 0;
    int xPos = 0;
    int yPos = 0;
    int activeX = -1;
    int activeY = -1;

    // Ships parts found and totally sunken
    int found = 0;
    int sunken = 0;
    List<Ship> ships;
    Ship[][] board; // Points to ships
    MessageLog mlog;

    /**
     * (Supossedly) synchronized places where the board is checked for a ship part existance
     */
    boolean[][] checked;

    public Board(int cols, int rows, int blockSize, int xPos, int yPos) {
        this.cols = cols;
        this.rows = rows;
        this.blockSize = blockSize;
        this.xPos = xPos;
        this.yPos = yPos;
        board = new Ship[cols][rows];
        reset();

        mlog = new MessageLog(blockSize*cols+cols+20, blockSize*rows, 10);
    }

    /**
     * Draws the entire board and coordinate state to the passed Graphics element
     * @param g The Graphics object where to draw the board
     */
    // ACTUALLY DON'T CHECK THIS FUNCTION, YOUR MIND WILL BE BLOWN
    // I DON'T UNDERSTAND IT NEITHER, I JUST DID IT
    public void draw(Graphics g) {
        // Grid drawing!
        g.setColor(Color.white);

        // Draw main rectangle, based on margin
        int sqWidth = blockSize * cols;
        int sqHeight = blockSize * rows;
        g.drawRect(xPos, yPos, sqWidth, sqHeight);

        // Draw vertical lines
        float colWidth = sqWidth / cols;
        for(int i = 1; i < cols; i++) {
            float px = xPos + colWidth*i;
            g.drawLine(px, yPos, px, yPos + sqHeight);
        }

        // Draw horizontal lines
        float rowHeight = sqHeight / rows;
        for(int i = 1; i < rows; i++) {
            float py = yPos + rowHeight*i;
            g.drawLine(xPos, py, yPos + sqWidth, py);
        }

        // Draw colored squares
        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int px = xPos + blockSize * j + 1;
                int py = xPos + blockSize * i + 1;

                if(isChecked(j, i) && containsShip(j, i)) {
                    g.setColor(Color.red);
                } else {
                    g.setColor(isChecked(j, i) ? Color.lightGray : Color.darkGray);
                }
                g.fillRect(px, py, blockSize-1, blockSize-1);
            }
        }

        // Draw selected square
        g.setColor(new Color(200, 200, 200, 100));
        if(activeX >= 0 && activeY >= 0) {
            float x = activeX * colWidth + xPos + 1;
            float y = activeY * rowHeight + yPos + 1;
            g.fillRect(x, y, colWidth - 1, rowHeight - 1);
        }

        mlog.draw(g);
    }

    /**
     * Places a ship on the board
     * @param ship The ship to be placed
     */
    public void place(Ship ship) {
        if(!canBePlaced(ship)) {
            return;
        }

        ShipPoint firstPoint = ship.getPositions()[0];
        for(ShipPoint point : ship.getPositions()) {
            board[point.getX()][point.getY()] = ship;
        }
        ships.add(ship);
    }

    /**
     * Checks if a ship can be placed on the board
     * @param ship The ship to be placed
     * @return A boolean depending on space availability for the ship
     */
    public boolean canBePlaced(Ship ship) {
        for(ShipPoint pos : ship.getPositions()) {
            if(pos.getX() >= cols
                    || pos.getY() >= rows
                    || containsShip(pos)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a place on the board is occupied by a ship
     * @param x The x coordinate to check
     * @param y The y coordinate to check
     * @return A boolean depending on the space usage
     */
    public boolean containsShip(int x, int y) {
        if(x >= cols || y >= rows) {
            System.err.println(String.format("containsShip: Trying invalid coordinate (%s, %s)", x, y));
            return false;
        }

        return board[x][y] != null;
    }

    /**
     * Checks if a place on the board is occupied by a ship
     * @param point The place to check
     * @return A boolean depending on the space usage
     */
    public boolean containsShip(ShipPoint point) {
        return containsShip(point.getX(), point.getY());
    }

    /**
     * Returns the ship at desired coordinate
     * @param x The X position for ship
     * @param y The Y position for ship
     * @return Returns the ship on the location, or null if it's an empty place
     */
    public Ship getAt(int x, int y) {
        if(x >= cols || y >= rows) {
            System.err.println(String.format("getAt: Trying invalid coordinate (%s, %s)", x, y));
            return null;
        }

        if(!containsShip(x, y)) {
            return null;
        }

        return board[x][y];
    }

    /**
     * Counts used places on the board
     * @return The number of positions being used by ship parts
     */
    public int totalParts() {
        int n = 0;
        for(Ship ship : ships) {
            n += ship.getPositions().length;
        }

        return n;
    }

    /**
     * See if the position is already "clicked"
     * @param x X position to see
     * @param y Y position to see
     * @return A boolean depending on the checked state of the position
     */
    public boolean isChecked(int x, int y) {
        if(x >= cols || y >= rows) {
            System.err.println(String.format("isChecked: Trying invalid coordinate (%s, %s)", x, y));
            return false;
        }

        return checked[x][y];
    }

    public void setChecked(int x, int y) {
        setChecked(x, y, true);
    }

    public void setChecked(int x, int y, boolean checked) {
        if(x >= cols || y >= rows) {
            System.err.println(String.format("setChecked: Trying invalid coordinate (%s, %s)", x, y));
            return;
        }

        this.checked[x][y] = checked;
        if(board[x][y] != null) {
            board[x][y].getPosition(x, y).setSunken();
        }
    }

    public void handleMouseMove(int mouseX, int mouseY) {
        // De-set active coordinate if mouse is out of the blocks area
        if(mouseX < xPos || mouseX > xPos + cols*blockSize
                || mouseY < yPos || mouseY > yPos + rows*blockSize ) {
            activeX = -1;
            activeY = -1;
            return;
        }

        int mbX = (mouseX - xPos) / blockSize;
        int mbY = (mouseY - yPos) / blockSize;

        if(mbX < cols) activeX = mbX;
        if(mbY < rows) activeY = mbY;
    }

    public void handleMouseClick(int button, int mouseX, int mouseY) {
        // Wrong button
        if(button != 0) {
            return;
        }

        // Wrong active coordinate
        if(activeX < 0 || activeY < 0) {
            return;
        }

        if(!isChecked(activeX, activeY)) {
            setChecked(activeX, activeY);
            if(containsShip(activeX, activeY)) {
                found++;
                mlog.addMessage(String.format("found at %s, %s", activeX, activeY));
                if(getAt(activeX, activeY).isTotallySunken()) {
                    sunken++;
                    mlog.addMessage("ship sunken!");
                }
            }
        }
    }

    public void reset() {
        // Reset count
        found = 0;
        sunken = 0;

        // Reset active position
        activeX = -1;
        activeY = -1;

        ships = new ArrayList<Ship>();
        board = new Ship[cols][rows];
        checked = new boolean[cols][rows];

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                checked[i][j] = false;
            }
        }
    }
}
