package com.makzk.games.hiddenships;

import org.newdawn.slick.*;

/**
 * Created by makzk on 27-05-15.
 */
public class HiddenShips extends BasicGame {

    /**
     * Create a new basic game
     *
     * @param title The title for the game
     */
    // Position of the first square
    private int initialX = 20;
    private int initialY = 20;

    private int cols = 15;
    private int rows = 11;
    private int size = 50;

    private int activeX = 0;
    private int activeY = 0;

    private int nSelected = 20;
    private int found = 0;
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
            g.drawLine(x, initialX, x, initialY + sqHeight);
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
        g.drawString(String.format("Found: %s/%s", found, nSelected), 10, 50);
    }

    public void mouseMoved(int oldx, int oldy, int mouseX, int mouseY) {
        if(mouseX < initialX || mouseX > initialX + cols*size
                || mouseY < initialY || mouseY > rows*size ) {
            activeX = -1;
            activeY = -1;
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

        checked[activeY][activeX] = true;
        if(data[activeY][activeX]) {
            found++;
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
            int x = (int)(Math.random() * cols);
            int y = (int)(Math.random() * rows);

            if(!data[y][x]) {
                data[y][x] = true;
                c++;
            }
        }
    }

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new HiddenShips());
        app.setDisplayMode(800, 600, false);
        app.start();
    }
}
