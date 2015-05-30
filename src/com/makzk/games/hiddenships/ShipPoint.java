package com.makzk.games.hiddenships;

/**
 * Parts of a ship
 * Created by makzk on 29-05-15.
 */
public class ShipPoint {
    private int x;
    private int y;

    /**
     * Sets if this ship part has been sunken
     */
    private boolean sunken;

    public ShipPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setSunken(boolean sunken) {
        this.sunken = sunken;
    }

    public void setSunken() {
        this.sunken = true;
    }

    public boolean isSunken() {
        return sunken;
    }

    public boolean equals(ShipPoint other) {
        return other.getX() == x && other.getY() == y;
    }

    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }
}
