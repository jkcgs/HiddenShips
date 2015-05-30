package com.makzk.games.hiddenships;

import org.newdawn.slick.*;

/**
 * Hidden ships (submarines) game simulation
 * Created by makzk on 27-05-15.
 */
public class HiddenShips extends BasicGame {
    // Position where the squares begin
    private int initialX = 20;
    private int initialY = 20;

    // Number of cols and rows, and square size
    private int cols = 15;
    private int rows = 11;
    private int size = 50;

    // Active square coordinates, the square that has the focus
    private int activeX = 0;
    private int activeY = 0;

    // Minimum size of each ship
    private int minLength = 3;
    private int maxLength = 5;

    // Number of ships to place
    private int nSelected = 5;

    // Total amount of ship parts placed
    private int totalSelected = 0;

    // Ship parts found
    private int found = 0;

    // check
    private boolean[][] checked;
    private boolean[][] data;

    public HiddenShips() {
        super("Hola");
    }

    @Override
    public void init(GameContainer container) throws SlickException {
        checked = new boolean[rows][cols];
        data = new boolean[rows][cols];
        reset();
    }

    @Override
    public void update(GameContainer c, int delta) throws SlickException {
        // :D
    }

    @Override
    public void render(GameContainer c, Graphics g) throws SlickException {
        // Grid drawing!
        g.setColor(Color.white);

        // Draw main rectangle, based on margin
        int sqWidth = size * cols;
        int sqHeight = size * rows;
        g.drawRect(initialX, initialY, sqWidth, sqHeight);

        // Draw vertical lines
        float colWidth = sqWidth / cols;
        for(int i = 1; i < cols; i++) {
            float x = initialX + colWidth*i;
            g.drawLine(x, initialY, x, initialY + sqHeight);
        }

        // Draw horizontal lines
        float rowHeight = sqHeight / rows;
        for(int i = 1; i < rows; i++) {
            float y = initialY + rowHeight*i;
            g.drawLine(initialX, y, initialY + sqWidth, y);
        }

        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = initialX + size * j + 1;
                int y = initialY + size * i + 1;

                if(checked[i][j] && data[i][j]) {
                    g.setColor(Color.red);
                } else {
                    g.setColor(checked[i][j] ? Color.lightGray : Color.darkGray);
                }
                g.fillRect(x, y, size-1, size-1);
            }
        }

        // Draw selected square
        g.setColor(new Color(200, 200, 200, 100));
        if(activeX >= 0 && activeY >= 0) {
            float x = activeX * colWidth + initialX + 1;
            float y = activeY * rowHeight + initialY + 1;
            g.fillRect(x, y, colWidth - 1, rowHeight - 1);
        }

        g.setColor(Color.white);
        g.drawString(String.format("Active: (%s, %s)", activeX, activeY), 10, 30);
        g.drawString(String.format("Found: %s/%s", found, totalSelected), 10, 50);
    }

    public void mouseMoved(int oldx, int oldy, int mouseX, int mouseY) {
        // De-set active coordinate if mouse is out of the blocks area
        if(mouseX < initialX || mouseX > initialX + cols*size
                || mouseY < initialY || mouseY > initialY + rows*size ) {
            activeX = -1;
            activeY = -1;
            return;
        }

        int mbX = (mouseX - initialX) / size;
        int mbY = (mouseY - initialY) / size;

        if(mbX < cols) activeX = mbX;
        if(mbY < rows) activeY = mbY;
    }

    public void mousePressed(int button, int x, int y) {
        // Wrong button
        if(button != 0) {
            return;
        }

        // Wrong active coordinate
        if(activeX < 0 || activeY < 0) {
            return;
        }


        if(!checked[activeY][activeX]) {
            checked[activeY][activeX] = true;
            if(data[activeY][activeX]) {
                found++;
            }
        }
    }

    public void keyPressed(int key, char c) {
        if(c == 'r') {
            reset();
        }
    }

    public void reset() {
        // Set a 'null' active block
        activeX = -1;
        activeY = -1;

        // Reset count
        found = 0;
        totalSelected = 0;

        // Reset arrays
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                checked[i][j] = false;
                data[i][j] = false;
            }
        }

        // Set special blocks
        int c = 0;
        while(c < nSelected) {
            int size = minLength + (int)(Math.random() * (maxLength - minLength));
            boolean direction = Math.random() >= .5;

            // true: horizontal, false: vertical
            if(direction) { // horizontal
                // Initial positions for ship
                int ix = (int)(Math.random() * (cols - size)); // initial x
                int iy = (int)(Math.random() * rows); // initial y

                // Check for collisions
                for (int i = ix; i < ix + size; i++) {
                    if(data[iy][i]) {
                        break;
                    }

                    // No collisions
                    if(i == (ix + size - 1)) {
                        // Place ship
                        for (int j = ix; j < ix + size; j++) {
                            data[iy][j] = true;
                            totalSelected++;
                        }

                        // Placed ships counter
                        c++;
                    }
                }
            } else { // Vertical
                // Initial positions for ship
                int ix = (int)(Math.random() * cols); // initial x
                int iy = (int)(Math.random() * (rows - size)); // initial y

                // Check for collisions
                for (int i = iy; i < iy + size; i++) {
                    if(data[i][ix]) {
                        break;
                    }

                    // No collisions
                    if(i == (iy + size - 1)) {
                        // Place ship
                        for (int j = iy; j < iy + size; j++) {
                            data[j][ix] = true;
                            totalSelected++;
                        }

                        // Placed ships counter
                        c++;
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new HiddenShips());
        app.setDisplayMode(800, 600, false);
        app.start();
    }
}
