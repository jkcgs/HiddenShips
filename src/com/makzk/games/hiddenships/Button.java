package com.makzk.games.hiddenships;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

enum ButtonState {
    BUTTON_STATE_ACTIVE,
    BUTTON_STATE_HOVER,
    BUTTON_STATE_NONE
}

/**
 * UI Button element
 * Created by makzk on 01-06-2015.
 */
public class Button {
    private int x = 0;
    private int y = 0;
    private boolean posCentered = false;
    private boolean mustRunAction = false;
    private int verticalPadding = 5;
    private int horizontalPadding = 10;
    private int minWidth = 0;

    private Font font = null;
    private Color bgColor = Color.darkGray;
    private Color fgColor = Color.white;
    private Color hoverBgColor = Color.gray;
    private Color hoverFgColor = Color.white;
    private Color activeBgColor = new Color(30, 30, 30);
    private Color activeFgColor = Color.white;

    private String text;

    private Runnable action = null;
    private ButtonState state = ButtonState.BUTTON_STATE_NONE;

    private int finalWidth = 0;
    private int finalHeight = 0;

    public Button(int x, int y, String text, Runnable action) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.action = action;
    }

    public Button(int x, int y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public void draw(Graphics g) {
        if(font == null) {
            font = g.getFont();
        }

        finalHeight = font.getHeight(text) + verticalPadding*2;
        finalWidth = Math.max(font.getWidth(text) + horizontalPadding*2, minWidth);

        // If button positions is relative to its center, then its axis is moved
        x = posCentered ? x - finalWidth/2 : x;
        y = posCentered ? y - finalHeight/2 : y;

        switch (state) {
            case BUTTON_STATE_ACTIVE: g.setColor(activeBgColor); break;
            case BUTTON_STATE_HOVER: g.setColor(hoverBgColor); break;
            case BUTTON_STATE_NONE: g.setColor(bgColor); break;
        }
        g.fillRect(x, y, finalWidth, finalHeight);

        switch (state) {
            case BUTTON_STATE_ACTIVE: g.setColor(activeFgColor); break;
            case BUTTON_STATE_HOVER: g.setColor(hoverFgColor); break;
            case BUTTON_STATE_NONE: g.setColor(fgColor); break;
        }
        int fPadding = (finalWidth - font.getWidth(text)) / 2;
        Font prevFont = g.getFont();
        g.setFont(font);
        g.drawString(text, x + fPadding, y + verticalPadding);
        g.setFont(prevFont);
    }

    public void handleUpdate(GameContainer gc) {
        int my = gc.getHeight() - Mouse.getY();
        int mx = Mouse.getX();

        if(mx >= x && mx < x + finalWidth && my > y && my < y + finalHeight) {
            if(Mouse.isButtonDown(0)) {
                mustRunAction = true;
                state = ButtonState.BUTTON_STATE_ACTIVE;
            } else {
                state = ButtonState.BUTTON_STATE_HOVER;
                if(mustRunAction && action != null) {
                    action.run();
                    mustRunAction = false;
                }
            }
        } else if(!Mouse.isButtonDown(0)) {
            state = ButtonState.BUTTON_STATE_NONE;
            mustRunAction = false;
        }
    }
}
