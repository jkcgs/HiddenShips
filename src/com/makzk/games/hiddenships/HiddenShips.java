package com.makzk.games.hiddenships;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

/**
 * Hidden ships (submarines) game simulation
 * Created by makzk on 27-05-15.
 */
public class HiddenShips extends StateBasedGame {
    public static final String version = "v0.1a";

    // Window properties
    public static final String winTitle = "HiddenShips";
    public static final int winWidth = 1050;
    public static final int winHeight = 600;

    // Game definitions
    public static final int boardCols = 15;
    public static final int boardRows = 11;
    public static final int boxSize = 50;
    public static final int[] shipProps = new int[]{2,3,3,4,5};

    public OpponentBoard game;
    public PlayerBoard playerBoard;

    public HiddenShips(String title) {
        super(title);

        game = new OpponentBoard();
        playerBoard = new PlayerBoard();
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        addState(game);
        addState(playerBoard);

        enterState(playerBoard.getID());
    }

    @Override
    public void keyPressed(int key, char c) {
        super.keyPressed(key, c);

        switch (c) {
            case 'q':
                enterState(game.getID()); break;
            case 'e':
                enterState(playerBoard.getID()); break;
            case 'r':
                reset(); break;
        }
    }

    public void reset() {
        game.reset();
        playerBoard.reset();
        enterState(playerBoard.getID());
    }

    public static void main(String[] args) {
        try {
            AppGameContainer appgc;
            appgc = new AppGameContainer(new HiddenShips(winTitle + " " + version));
            appgc.setDisplayMode(winWidth, winHeight, false);
            appgc.setShowFPS(false);
            appgc.setVerbose(false);
            appgc.start();
        } catch (SlickException e) {
            Log.error(null, e);
        }
    }


}
