package org.example.actions;

import org.example.entities.Entity;
import org.example.entities.creatures.Creature;
import org.example.map.Coordinates;
import org.example.map.WorldMap;

import java.util.List;

import static org.example.utils.consoleRenderers.WorldConsoleRenderer.*;

public class Actions {

    private final WorldMap worldMap;
    private final EntitiesSpawner spawner;
    public Actions(WorldMap worldMap) {
        this.worldMap = worldMap;
        this.spawner = new EntitiesSpawner(worldMap);
    }
    private int stepCounter = 0;

    public void turnActions() throws InterruptedException {
        stepCounter++;
        List<Coordinates> coordinates = worldMap.getEntitiesOfType(Creature.class);
        for (Coordinates coordinate : coordinates) {
            if (!worldMap.coordinatesIsEmpty(coordinate)){
                Creature creature = (Creature) worldMap.getEntity(coordinate);
                creature.makeMove(this.worldMap,this, coordinate);
                isGoalsNotEnough(creature.getTarget());
            }
        }
        render(worldMap, stepCounter);
    }



    private void isGoalsNotEnough(Entity goal) {
        if(spawner.isEntityAddable(goal)){
            spawner.addEntity(goal);
        }
    }

    public int getStepCounter() {
        return stepCounter;
    }
}

