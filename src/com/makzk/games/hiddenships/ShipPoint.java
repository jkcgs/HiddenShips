package com.makzk.games.hiddenships;

/**
 * Parts of a ship (ship locations)
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

    /**
     * Checks if point coordinates are equal to another point
     * @param other The other ShipPoint to verify
     * @return A boolean value depending on points equality
     */
    public boolean equals(ShipPoint other) {
        return other.getX() == x && other.getY() == y;
    }

    /**
     * Checks if the point has the given coordinates
     * @param x The X coordinate to check
     * @param y The Y coordinate to check
     * @return A boolean value depending on if point corresponds to given values
     */
    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }
}
