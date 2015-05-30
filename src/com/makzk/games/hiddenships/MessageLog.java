package com.makzk.games.hiddenships;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows a printable message log
 * Created by makzk on 30-05-15.
 */
public class MessageLog {
    private int x;
    private int y;
    private int maxLog = 5;
    private List<String> messages;

    /**
     * Creates the message log object. The position taken is from the bottom left position to show the log
     * @param x The X position where the log is drawn
     * @param y The lower Y position where the log is drawn
     */
    public MessageLog(int x, int y, int maxLog) {
        this.x = x;
        this.y = y;
        this.maxLog = maxLog;
        messages = new ArrayList<String>();
    }

    /**
     * Draws the message log from last to first. Also draws it from down to up. Lower position is
     * defined by X and Y positions from the object.
     * @param g The Graphics object where to draw the log.
     */
    public void draw(Graphics g) {
        g.setColor(Color.white);
        int c = 0;
        for (int i = messages.size() - 1; i >= 0; i--) {
            String msg = messages.get(i);
            g.drawString(msg, x, y - (20 * c));
            c++;
        }
    }

    /**
     * Adds a message to the log. If the log is full, the first message is deleted.
     * @param msg The message to add.
     */
    public void addMessage(String msg) {
        messages.add(msg);
        if(messages.size() > maxLog) {
            messages.remove(0);
        }
    }
}
