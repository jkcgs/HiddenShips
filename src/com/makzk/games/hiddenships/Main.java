package com.makzk.games.hiddenships;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

public class Main extends StateBasedGame {
    // Window properties
    public static final String winTitle = "HiddenShips";
    public static final int winWidth = 1050;
    public static final int winHeight = 600;

    public Main(String title) {
        super(title);
        addState(new HiddenShips());
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        enterState(0);
    }

    public static void main(String[] args) {
        try {
            AppGameContainer appgc;
            appgc = new AppGameContainer(new Main(winTitle));
            appgc.setDisplayMode(winWidth, winHeight, false);
            appgc.setShowFPS(false);
            appgc.start();
        } catch (SlickException e) {
            Log.error(null, e);
        }
    }


}
