package org.example.utils;

import org.example.map.Coordinates;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MoveType {
    public static Set<Coordinates> steps() {
        return new HashSet<>(Arrays.asList(

                new Coordinates(1,0),
                new Coordinates(-1,0),

                new Coordinates(1,1),
                new Coordinates(0,1),
                new Coordinates(-1,1),

                new Coordinates(1,-1),
                new Coordinates(0,-1),
                new Coordinates(-1,-1)
        ));
    }

}
