package com.makzk.games.hiddenships;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * The local player board
 * Created by makzk on 30-05-15.
 */
public class PlayerBoard extends BasicGameState {
    private Board board;
    private boolean[] shipsPlaced = new boolean[HiddenShips.shipProps.length];
    private boolean placingDirection = true; // true: horizontal, false: vertical
    private boolean confirmedPositions = false;
    private int activePlacing = 0;
    private boolean opponentTurn = false;
    private boolean opponentTurnProcessed = false;
    private final int turnPostDelay = 3000;
    private int delayAccum = 0;

    private Button btnConfirm;
    private Button btnDirection;

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer container, final StateBasedGame game) throws SlickException {
        board = new Board(HiddenShips.boardCols, HiddenShips.boardRows, HiddenShips.boxSize, 25, 25);

        btnDirection = new Button(800, 100, "Direction: horizontal", new Runnable() {
            @Override
            public void run() {
                placingDirection = !placingDirection;
                btnDirection.setText("Direction: " + (placingDirection ? "horizontal" : "vertical"));
            }
        });
        btnConfirm = new Button(800, 140, "Confirm positions", new Runnable() {
            @Override
            public void run() {
                if(!confirmedPositions
                        && totalShipsPlayed() == HiddenShips.shipProps.length) {
                    confirmedPositions = true;

                    // clear board
                    board.mlog.clear();
                    board.uncheckAll();

                    // hide buttons
                    btnConfirm.setShown(false);
                    btnDirection.setShown(false);

                    // go back!
                    game.enterState(0);
                }
            }
        });

        reset();

        board.mlog.setMaxLog(15);
        board.mlog.addMessage("Please locate your ships by\nclicking with the mouse.");
        board.mlog.addMessage("Remove a placed ship by\nclicking on it.");
        board.mlog.addMessage("Place the ship [1] size " + HiddenShips.shipProps[0]);
        board.setCanCheck(false);
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        board.draw(g);

        if(!confirmedPositions) {
            int x = board.xPos + board.cols * board.blockSize + 20;
            if (totalShipsPlayed() < HiddenShips.shipProps.length) {
                g.drawString(String.format("Placing ship [%s] size %s",
                                activePlacing + 1, HiddenShips.shipProps[activePlacing]), x, board.yPos + 40
                );
            }
        }

        btnConfirm.draw(g);
        btnDirection.draw(g);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        // Do opponent turn
        if(opponentTurn) {
            if(!opponentTurnProcessed) {
                doOpponentTurn();
                opponentTurnProcessed = true;
            }

            delayAccum += delta;
            if(delayAccum >= turnPostDelay) {
                delayAccum = 0;

                // Reset opp turn processed
                opponentTurn = false;
                opponentTurnProcessed = false;

                // go back!
                game.enterState(0);
            }
        }

        btnConfirm.handleUpdate(container);
        btnDirection.handleUpdate(container);
    }

    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        board.handleMouseMove(newx, newy);
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        board.handleMouseClick(button, x, y);

        if(board.activeY >= 0 && board.activeY >= 0) {
            if (button == 0 && !confirmedPositions) {
                // Removes actual ship
                if (board.containsShip()) {
                    Ship ship = board.getActive();
                    board.removeShip(ship);

                    // Search for a placed ship with the same size of the removed to check as not placed
                    for (int i = 0; i < shipsPlaced.length; i++) {
                        if (shipsPlaced[i] && HiddenShips.shipProps[i] == ship.getPositions().length) {
                            shipsPlaced[i] = false;
                            break;
                        }
                    }
                    updateNextShip();
                // Places a ship
                } else if(totalShipsPlayed() < HiddenShips.shipProps.length) {
                    Ship ship = Ship.createShip(board.activeX, board.activeY, placingDirection, HiddenShips.shipProps[activePlacing]);
                    if (board.canBePlaced(ship)) {
                        board.place(ship);
                        for (ShipPoint pos : ship.getPositions()) {
                            board.setChecked(pos.getX(), pos.getY());
                        }

                        shipsPlaced[activePlacing] = true;
                        updateNextShip();
                    }
                }
            }
        }
    }

    public void reset() {
        board.reset();
        placingDirection = true;
        confirmedPositions = false;
        activePlacing = 0;
        btnConfirm.setShown(true);
        btnConfirm.setDisabled(true);
        btnDirection.setShown(true);

        shipsPlaced = new boolean[HiddenShips.shipProps.length];
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
        btnConfirm.setDisabled(activePlacing != -1);
    }

    public void setOpponentTurn(boolean opponentTurn) {
        this.opponentTurn = opponentTurn;
    }

    public void doOpponentTurn() {
        // Can't play if it's ready!
        if(board.found == board.totalParts()) {
            board.mlog.addMessage("[Opp] I'm ready, not playing!");
            return;
        }

        // Scary!
        while(true) {
            int x = (int)(Math.random() * board.cols);
            int y = (int)(Math.random() * board.rows);

            if(!board.isChecked(x, y)) {
                board.setChecked(x, y);
                board.mlog.addMessage(String.format("Playing (%s, %s)", x, y));
                board.processCheck(x, y);

                break; // please reach here
            }
        }
    }
}
