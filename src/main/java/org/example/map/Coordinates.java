package org.example.map;

import org.example.entities.Entity;

import java.util.*;

public class Coordinates {

    public static final int MAX_COORDINATES_OF_COLUMN = 20;
    public static final int MAX_COORDINATES_OF_ROW = 50;
    public static final int MIN_COORDINATES_OF_THE_WORLD = 0;
    public final int row;
    public final int column;

    public static Coordinates getRandomCoordinates(){
        int row = MIN_COORDINATES_OF_THE_WORLD + (int) (Math.random() *
                (MAX_COORDINATES_OF_ROW - MIN_COORDINATES_OF_THE_WORLD + 1));

        int column = MIN_COORDINATES_OF_THE_WORLD + (int) (Math.random() *
                (MAX_COORDINATES_OF_COLUMN - MIN_COORDINATES_OF_THE_WORLD + 1));
        return new Coordinates(row,column);
    }



    public Coordinates(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Coordinates shift(Coordinates shift) {
        return new Coordinates(this.row + shift.row, this.column + shift.column);
    }

    public boolean canShift(Coordinates shift) {
        int row = this.row + shift.row;
        int column = this.column + shift.column;
        Coordinates shiftCoordinates = new Coordinates(row,column);
        return shiftCoordinates.isCoordinatesValid();
    }

    public boolean isCoordinatesValid() {
        return row <= MAX_COORDINATES_OF_ROW && row >= MIN_COORDINATES_OF_THE_WORLD &&
                column <= MAX_COORDINATES_OF_COLUMN && column >= MIN_COORDINATES_OF_THE_WORLD;
    }


    public static Coordinates getRandomAvailableCoordinate(WorldMap worldMap) {
        Coordinates randomCoordinate = getRandomCoordinates();
        List<Coordinates> mapCoordinates = worldMap.getEntitiesOfType(Entity.class);
        while (mapCoordinates.contains(randomCoordinate)) {
            randomCoordinate = getRandomCoordinates();
        }
        return randomCoordinate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates that)) return false;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
