package com.makzk.games.hiddenships;

/**
 * Ship objects basically used to connect positions
 * Created by makzk on 29-05-15.
 */
public class Ship {
    private ShipPoint positions[];
    private boolean isHorizontal;

    /**
     * Checks if the ship collides with another ship
     * @param other The other ship get possible collisions
     * @return A boolean depending if the ship collides with the other ship.
     */
    public boolean collides(Ship other) {
        ShipPoint[] otherPos = other.getPositions();
        for (int i = 0; i < positions.length; i++) {
            if(isIn(otherPos[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the ship contains a point
     * @param point The point to check
     * @return A boolean depending if the ship contains the point.
     */
    public boolean isIn(ShipPoint point) {
        for (int i = 0; i < positions.length; i++) {
            if(positions[i].equals(point)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the ship contains a point
     * @param x The X coordinate to check
     * @param y The Y coordinate to check
     * @return A boolean depending if the ship contains the point.
     */
    public boolean isIn(int x, int y) {
        return isIn(new ShipPoint(x, y));
    }


    public ShipPoint[] getPositions() {
        return positions;
    }

    public void setPositions(ShipPoint[] positions) {
        this.positions = positions;
    }

    public void setHorizontal(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
    }

    /**
     * Returns a ship point by a coordinate.
     * @param x The X position of the point
     * @param y The Y position of the point
     * @return The ShipPoint from the ship. If the point does not belong to the ship, null is returned.
     */
    public ShipPoint getPosition(int x, int y) {
        if(!isIn(x, y)) {
            return null;
        }

        for(ShipPoint pos : positions) {
            if(pos.equals(x, y)) {
                return pos;
            }
        }

        return null;
    }

    /**
     * Check if the ship is totally sunken, by checking all ship points.
     * @return A boolean, depending on the ship status.
     */
    public boolean isTotallySunken() {
        for(ShipPoint pos : positions) {
            if(!pos.isSunken()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates the ship object
     * @param ix The initial point on X axis to begin "building" the ship
     * @param iy The initial point on Y axis to begin "building" the ship
     * @param isHorizontal The ship can have two orientations
     * @param size Amount of parts (positions) of the ship, with size >= 1.
     * @return The generated ship.
     */
    public static Ship createShip(int ix, int iy, boolean isHorizontal, int size) {
        if(size < 1) {
            return null;
        }

        Ship ship = new Ship();
        ShipPoint positions[] = new ShipPoint[size];
        for (int i = 0; i < size; i++) {
            int x = isHorizontal ? ix + i : ix;
            int y = isHorizontal ? iy : iy + i;
            positions[i] = new ShipPoint(x, y);
        }

        ship.setPositions(positions);
        ship.setHorizontal(isHorizontal);
        return ship;
    }
}
