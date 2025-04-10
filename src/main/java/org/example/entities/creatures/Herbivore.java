package org.example.entities.creatures;

import org.example.map.Coordinates;
import org.example.entities.Entity;
import org.example.entities.staticEntities.Grass;
import org.example.map.WorldMap;

public class Herbivore extends Creature{
    private static final int DEFAULT_HP_VALUE = 4;
    private static final int HP_MAX_VALUE = 4;
    private static final int DEFAULT_SPEED = 1;
    private static final Entity DEFAULT_TARGET = new Grass();

    public Herbivore() {
        super(DEFAULT_HP_VALUE,
                HP_MAX_VALUE,
                DEFAULT_TARGET,
                DEFAULT_SPEED);
    }

    @Override
    public void attack(WorldMap worldMap, Coordinates to) {
      worldMap.removeEntity(to);
      eit();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
