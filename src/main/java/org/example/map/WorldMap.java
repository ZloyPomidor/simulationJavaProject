package org.example.map;

import org.example.entities.Entity;
import org.example.entities.creatures.Creature;
import org.example.utils.consoleRenderers.ConsoleMessagesRenderer;

import java.util.*;

public class WorldMap {

    private final java.util.Map<Coordinates, Entity> worldWap;
    public static final int MAP_SIZE = Coordinates.MAX_COORDINATES_OF_ROW * Coordinates.MAX_COORDINATES_OF_COLUMN;


    public WorldMap() {
        this.worldWap = new HashMap<>();
    }

    public  List<Coordinates> getEntitiesOfType(Class<? extends Entity> searchingType) {
        List<Coordinates> resultsEntities = new ArrayList<>();
        for (java.util.Map.Entry<Coordinates, Entity> e : worldWap.entrySet()) {
            if (searchingType.isInstance(e.getValue()) ) {
                resultsEntities.add(e.getKey());
            }
        }
        return resultsEntities;
    }

    public void moveEntity(Coordinates from, Coordinates to) {
        if (!coordinatesIsEmpty(to)) {
            interactionOnEntity(from, to);
        } else {
            Creature creatureFrom = (Creature) getEntity(from);
            removeEntity(from);
            setEntityOnMap(to, creatureFrom);
        }
    }

    private void interactionOnEntity(Coordinates from, Coordinates to) {
        Entity entityTo = getEntity(to);
        Creature creatureFrom = (Creature) getEntity(from);

        if (creatureFrom.isTarget(entityTo)) {
            creatureFrom.attack(this, to);
        }
    }

    public void setEntityOnMap(Coordinates coordinates, Entity entity) {
        isCoordinatesValid(coordinates);
        if (entity==null){
            throw new NullPointerException(ConsoleMessagesRenderer.ENTITY_IS_NULL_MESSAGE);
        }
        worldWap.put(coordinates, entity);
    }

    public void removeEntity(Coordinates coordinates) {
        isCoordinatesValid(coordinates);
        worldWap.remove(coordinates);
    }

    public boolean coordinatesIsEmpty(Coordinates key) {
       isCoordinatesValid(key);
        return !worldWap.containsKey(key);
    }

    public Entity getEntity(Coordinates coordinates) {
        isCoordinatesValid(coordinates);
        Entity entity = worldWap.get(coordinates);
        if (entity==null){
            throw new NullPointerException(ConsoleMessagesRenderer.ENTITY_IS_NULL_MESSAGE);
        }
        return worldWap.get(coordinates);
    }

    private void isCoordinatesValid(Coordinates coordinates){
       if(!coordinates.isCoordinatesValid()){
            throw new IllegalArgumentException(ConsoleMessagesRenderer.COORDINATES_IS_NOT_VALID_MESSAGE);
       }
    }


}
