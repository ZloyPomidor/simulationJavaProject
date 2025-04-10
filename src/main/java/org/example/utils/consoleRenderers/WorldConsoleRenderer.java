package org.example.utils.consoleRenderers;

import org.example.entities.Entity;
import org.example.map.Coordinates;
import org.example.map.WorldMap;

public class WorldConsoleRenderer {

    public static final String BACKGROUND = "\uD83D\uDFEB";

    public static final String PREDATOR = "\uD83E\uDD81";

    public static final String GRASS = "\uD83C\uDF3F";

    public static final String TREE = "\uD83C\uDF33";

    public static final String HERBIVORE = "\uD83D\uDC34";

    public static final String ROCK = "⛰";

    public static String getTheSelectedSmiley(Entity obj){
        return switch (obj.getClass().getSimpleName()) {
            case "Grass" -> GRASS;
            case "Herbivore" -> HERBIVORE;
            case "Predator" -> PREDATOR;
            case "Tree" -> TREE;
            case "Rock" -> ROCK;
            default -> "";
        };
    }

    private static String getLineOutPut(Coordinates cord, WorldMap worldMap){
        if (!worldMap.coordinatesIsEmpty(cord)){
            return getTheSelectedSmiley(worldMap.getEntity(cord));
        }else
            return BACKGROUND;
    }

    public static void render(WorldMap worldMap, int stepCounter){
        for (int y = Coordinates.MAX_COORDINATES_OF_COLUMN; y>=Coordinates.MIN_COORDINATES_OF_THE_WORLD; y--){
            StringBuilder line = new StringBuilder();
            for (int x = 0; x<=Coordinates.MAX_COORDINATES_OF_ROW; x++){
                line.append(getLineOutPut(new Coordinates(x, y), worldMap));
            }
            System.out.println(line);
        }
        System.out.println(stepCounter);

    }
}
