package org.example.entities.creatures;

import org.example.actions.Actions;
import org.example.map.Coordinates;
import org.example.entities.Entity;
import org.example.map.WorldMap;
import org.example.utils.PathFinder;


public abstract class Creature extends Entity {

    private int hp;
    private final int MAX_HP_VALUE;
    private final Entity target;
    private final int speed;

    public Creature(int hp, int MAX_HP_VALUE, Entity target, int speed) {
        this.hp = hp;
        this.MAX_HP_VALUE = MAX_HP_VALUE;
        this.target = target;
        this.speed = speed;
    }

    public abstract void attack(WorldMap worldMap, Coordinates to);


    public void makeMove(WorldMap worldMap, Actions actions, Coordinates currentCoordinate){
        if (isTimeToWalk(actions)){
            PathFinder pathFinder = new PathFinder();
            Coordinates nextCoordinate = pathFinder.findPath(worldMap, currentCoordinate);
            worldMap.moveEntity(currentCoordinate,nextCoordinate);
        }
    }

    public void eit() {
        if(getHp()< MAX_HP_VALUE){
            hpApp();
        }
    }

    public boolean isTarget(Entity entityTo) {
        return target.getClass().equals(entityTo.getClass());
    }

    private boolean isTimeToWalk(Actions actions){
        return actions.getStepCounter()% speed ==0;
    }

    public void hpApp(){
        this.hp++;
    }

    public void hpDown(int attackPower){
        this.hp = hp - attackPower;
    }

    public int getHp() {
        return hp;
    }

    public Entity getTarget() {return target;}
}