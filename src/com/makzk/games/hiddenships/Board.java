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

    // Set if boxes are checkable on mouse click
    private boolean canCheck = true;

    // Ships parts found and totally sunken
    int found = 0;
    int sunken = 0;
    List<Ship> ships;
    Ship[][] board; // Points to ships
    final MessageLog mlog;

    /**
     * (Supossedly) synchronized places where the board is checked
     */
    boolean[][] checked;

    private Runnable clickEvent;

    public Board(int cols, int rows, int blockSize, int xPos, int yPos) {
        this.cols = cols;
        this.rows = rows;
        this.blockSize = blockSize;
        this.xPos = xPos;
        this.yPos = yPos;
        board = new Ship[cols][rows];

        mlog = new MessageLog(blockSize*cols+cols+20, blockSize*rows, 10);
        clickEvent = null;

        reset();
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

        // Draw coordinates numbers
        g.setColor(Color.white);
        for (int i = 0; i < cols; i++) {
            g.drawString((i+1)+"", xPos + i*blockSize + 10, yPos - 20);
        }
        for (int i = 0; i < rows; i++) {
            g.drawString((i+1)+"", xPos - 20, yPos + i*blockSize + 10);
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
     * Removes an entire ship at a specific location
     * @param ship The ship to be removed from the board
     */
    public void removeShip(Ship ship) {
        if(ship != null) {
            for(ShipPoint pos : ship.getPositions()) {
                setChecked(pos.getX(), pos.getY(), false);
                board[pos.getX()][pos.getY()] = null;
            }

            if(ship.isTotallySunken()) {
                sunken--;
            }

            found -= ship.totalSunkenParts();
            ships.remove(ship);
        }
    }


    /**
     * Removes an entire ship at a specific location
     * @param x The X coordinate where the ship is located
     * @param y The Y coordinate where the ship is located
     */
    public void removeShip(int x, int y) {
        removeShip(getAt(x, y));
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
        if(x >= cols || y >= rows || x < 0 || y < 0) {
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
     * Checks if the active place on the board is occupied by a ship
     * @return A boolean depending on the space usage
     */
    public boolean containsShip() {
        return (activeX >= 0 && activeY >= 0) && containsShip(activeX, activeY);
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
     * Returns the ship at the actual active coordinate
     * @return Returns the ship on the location, or null if it's an empty place
     */
    public Ship getActive() {
        return (activeX >= 0 && activeY >= 0) ? getAt(activeX, activeY) : null;
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

    /**
     * Set a box as is checked. If a ship part was there, the ShipPoint is marked as sunken.
     * @param x The X coordinate to check
     * @param y The Y coordinate to check
     */
    public void setChecked(int x, int y) {
        setChecked(x, y, true);
    }

    /**
     * Sets a box as checked. If a ship part was there, the ShipPoint is marked as sunken.
     * @param x The X coordinate to check
     * @param y The Y coordinate to check
     * @param checked Set if checked or not
     */
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

    /**
     * Sets all the board positions as unchecked. Also sets a ship in place as not sunken.
     */
    public void uncheckAll() {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                checked[i][j] = false;
                if(containsShip(i, j)) {
                    getAt(i, j).getPosition(i, j).setSunken(false);
                }
            }
        }
    }

    /**
     * Sets the runnable event to trigger on clicking a valid box
     * @param clickEvent The event to be trigged
     */
    public void setClickEvent(Runnable clickEvent) {
        this.clickEvent = clickEvent;
    }

    /**
     * Sets if board boxes can be checked
     * @param canCheck A boolean value
     */
    public void setCanCheck(boolean canCheck) {
        this.canCheck = canCheck;
    }

    /**
     * Handles mouse moving activity (mouse hovering, focusing on boxes on board)
     * @param mouseX The X mouse position moved to
     * @param mouseY The Y mouse position moved to
     */
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

    /**
     * Handles the mouse click activity (checking boxes on board)
     * @param button The button clicked
     * @param mouseX The X mouse position while clicking
     * @param mouseY The Y mouse position while clicking
     */
    public void handleMouseClick(int button, int mouseX, int mouseY) {
        // Wrong button
        if(button != 0) {
            return;
        }

        // Wrong active coordinate
        if(activeX < 0 || activeY < 0) {
            return;
        }

        // Process click over active location
        if(canCheck) {
            boolean isChecked = isChecked(activeX, activeY);
            processCheck(activeX, activeY);
            boolean wasChecked = isChecked(activeX, activeY);

            // Process click event when a valid click was executed
            if(clickEvent != null && !isChecked && wasChecked) {
                clickEvent.run();
            }
        }
    }

    /**
     * Processes verbosely action over a box
     * @param x The X coordinate of the box
     * @param y The Y coordinate of the box
     */
    public void processCheck(int x, int y) {
        if(!isChecked(x, y)) {
            setChecked(x, y);
            if(containsShip(x, y)) {
                found++;
                mlog.addMessage(String.format("found at %s, %s", x+1, y+1));
                if(getAt(x, y).isTotallySunken()) {
                    sunken++;
                    mlog.addMessage("ship sunken!");
                }
            }
        }
    }

    /**
     * Resets all stats, points, ships, etc, in the board.
     */
    public void reset() {
        // Reset count
        found = 0;
        sunken = 0;

        // Reset active position
        activeX = -1;
        activeY = -1;

        // Reset message log
        mlog.clear();

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
