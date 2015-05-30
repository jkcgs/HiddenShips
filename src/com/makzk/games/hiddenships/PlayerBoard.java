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
    private int shipsPlaced = 0;
    private boolean placingDirection = true; // true: horizontal, false: vertical

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        board = new Board(Main.boardCols, Main.boardRows, Main.boxSize, 25, 25);

        board.mlog.setMaxLog(15);
        board.mlog.addMessage("Please locate your ships by\nleft-clicking with the mouse.");
        board.mlog.addMessage("Change direction with\nright click.");
        board.mlog.addMessage("Place the ship [1] size " + Main.shipProps[0]);
        board.setCanCheck(false);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        board.draw(g);

        if(shipsPlaced < Main.shipProps.length) {
            int x = board.xPos + board.cols * board.blockSize + 20;
            g.drawString("Placing direction:\n" + (placingDirection ? "horizontal" : "vertical"), x, board.yPos);
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
        if(button == 1 && shipsPlaced < Main.shipProps.length) {
            placingDirection = !placingDirection;
        }

        board.handleMouseClick(button, x, y);

        if(button == 0 && shipsPlaced < Main.shipProps.length) {
            Ship ship = Ship.createShip(board.activeX, board.activeY, placingDirection, Main.shipProps[shipsPlaced]);
            if(board.canBePlaced(ship)) {
                board.place(ship);
                for(ShipPoint pos : ship.getPositions()) {
                    board.setChecked(pos.getX(), pos.getY());
                }

                shipsPlaced++;
                board.mlog.addMessage("Ship [" + shipsPlaced + "] placed");
                if(shipsPlaced < Main.shipProps.length) {
                    board.mlog.addMessage(
                            String.format("Place ship [%s] size %s", shipsPlaced+1, Main.shipProps[shipsPlaced])
                    );
                }
            }
        }
    }

    public void reset() {
        board.reset();
        shipsPlaced = 0;
        placingDirection = true;
    }
}
