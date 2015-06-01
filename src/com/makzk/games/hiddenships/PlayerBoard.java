package com.makzk.games.hiddenships;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * The local player board
 * Created by makzk on 30-05-15.
 */
public class PlayerBoard extends BasicGameState {
    private Board board;
    private boolean[] shipsPlaced = new boolean[Main.shipProps.length];
    private boolean placingDirection = true; // true: horizontal, false: vertical
    private boolean confirmedPositions = false;
    private int activePlacing = 0;

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        board = new Board(Main.boardCols, Main.boardRows, Main.boxSize, 25, 25);
        reset();

        board.mlog.setMaxLog(15);
        board.mlog.addMessage("Please locate your ships by\nleft-clicking with the mouse.");
        board.mlog.addMessage("Change direction pressing\nthe D (key).");
        board.mlog.addMessage("Remove a placed ship by\nright-clicking on it.");
        board.mlog.addMessage("Place the ship [1] size " + Main.shipProps[0]);
        board.setCanCheck(false);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        board.draw(g);

        int x = board.xPos + board.cols * board.blockSize + 20;
        g.drawString("Placing direction:\n" + (placingDirection ? "horizontal" : "vertical"), x, board.yPos);

        if(totalShipsPlayed() < Main.shipProps.length) {
            g.drawString(String.format("Placing ship [%s] size %s", activePlacing+1, Main.shipProps[activePlacing]), x, board.yPos+40);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

    }

    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        board.handleMouseMove(newx, newy);
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        board.handleMouseClick(button, x, y);

        if(button == 0 && totalShipsPlayed() < Main.shipProps.length) {
            Ship ship = Ship.createShip(board.activeX, board.activeY, placingDirection, Main.shipProps[activePlacing]);
            if(board.canBePlaced(ship)) {
                board.place(ship);
                for(ShipPoint pos : ship.getPositions()) {
                    board.setChecked(pos.getX(), pos.getY());
                }

                shipsPlaced[activePlacing] = true;
                updateNextShip();
            }
        }

        // Remove ship on right click
        if(!confirmedPositions && button == 1) {
            if(board.containsShip()) {
                Ship ship = board.getActive();
                board.removeShip(ship);

                // Search for a placed ship with the same size of the removed to check as not placed
                for (int i = 0; i < shipsPlaced.length; i++) {
                    if(shipsPlaced[i] && Main.shipProps[i] == ship.getPositions().length) {
                        shipsPlaced[i] = false;
                        break;
                    }
                }
                updateNextShip();
            }
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        if(c == 'd' && totalShipsPlayed() < Main.shipProps.length) {
            placingDirection = !placingDirection;
        }
    }

    public void reset() {
        board.reset();
        placingDirection = true;
        confirmedPositions = false;

        shipsPlaced = new boolean[Main.shipProps.length];
        for (int i = 0; i < shipsPlaced.length; i++) {
            shipsPlaced[i] = false;
        }
    }

    public int totalShipsPlayed() {
        int count = 0;
        for(boolean placed : shipsPlaced) {
            if(placed) count++;
        }

        return count;
    }

    public int nextShipToPlace() {
        for (int i = 0; i < shipsPlaced.length; i++) {
            if(!shipsPlaced[i]) {
                return i;
            }
        }

        return -1;
    }

    public void updateNextShip() {
        activePlacing = nextShipToPlace();
    }
}
