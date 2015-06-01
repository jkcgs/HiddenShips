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
     * Sets the max amount of messages to be logged and stored.
     * @param maxLog The amount of messages
     */
    public void setMaxLog(int maxLog) {
        this.maxLog = maxLog;
    }

    /**
     * Adds a message to the log. The new message will be split on new line characters, and each line will be
     * counted as different messages, then adding them in reverse order to show them as a multiline message.
     * If the log exceeds its maximum amount of messages, the exceeding amount of
     * message will be removed from the beginning of the messages list.
     * @param msg The message(s) to add.
     */
    public void addMessage(String msg) {
        String msgs[] = msg.split("\n");
        for (String m : msgs) {
            messages.add(m);
        }

        if(messages.size() > maxLog) {
            int diff = messages.size() - maxLog;
            messages.subList(0, diff).clear();
        }
    }

    /**
     * Removes all messages from the log
     */
    public void clear() {
        messages.clear();
    }
}
