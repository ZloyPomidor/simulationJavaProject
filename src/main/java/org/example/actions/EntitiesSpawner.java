package org.example.actions;

import org.example.entities.Entity;
import org.example.entities.creatures.Herbivore;
import org.example.entities.creatures.Predator;
import org.example.entities.staticEntities.Grass;
import org.example.entities.staticEntities.Rock;
import org.example.entities.staticEntities.Tree;
import org.example.map.Coordinates;
import org.example.map.WorldMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EntitiesSpawner {
    public static final double DEFULT_PREDATOR_QUANTITY_SPAWN = 0.02;
    public static final double DEFULT_HERBIVORE_QUANTITY_SPAWN = 0.09;
    public static final double DEFULT_GRASS_QUANTITY_SPAWN = 0.10;
    public static final double DEFULT_TREE_QUANTITY_SPAWN = 0.20;
    public static final double DEFULT_ROCK_QUANTITY_SPAWN = 0.20;
    public static final double MIN_QUANTITY_HERBIVORE_FOR_ADD = 0.09;
    public static final double MIN_QUANTITY_GRASS_FOR_ADD = 0.10;
    private final List<Entity> entities = List.of(new Herbivore(), new Predator(), new Grass(), new Rock(), new Tree());
    private final Pattern PATTERN_FOR_SPLIT = Pattern.compile("_");
    public static final String EXCEPTION_MESSAGE ="Please check the compliance of the entity spawn" +
            " parameters with the provided specification!";
    private final Map<Entity, Integer> spawnData = new HashMap<>();
    protected final List<String> constantsNames = getConstants();
    private final String FOR_ADD_CHECK = "ADD";
    private final String FOR_SPAWN_CHECK = "SPAWN";
    private final Map<Entity, Integer> addData = new HashMap<>();
    private final WorldMap worldMap;
    private Coordinates randomCoordinates;
    private String[] constantNameParts;
    private int quantityEntitiesOnMap;
    private int targetConstant;
    private double spawnCounter;

    public EntitiesSpawner(WorldMap worldMap) {
        this.worldMap = worldMap;
    }

    public void spawnEntities() {
        setSpawnAndAddData();
        try {
            if(isSpawnDataIsValid()) {
                for (java.util.Map.Entry<Entity, Integer> e : spawnData.entrySet()) {
                    if (e.getValue()==0){
                        continue;
                    }
                    for (int i = 0; i < e.getValue()+1; i++) {
                        randomCoordinates = Coordinates.getRandomAvailableCoordinate(this.worldMap);
                        worldMap.setEntityOnMap(randomCoordinates, e.getKey());
                    }
                }
            }else
                throw new IllegalConstantArgumentException();
        }catch (IllegalConstantArgumentException e){
            System.out.println(EXCEPTION_MESSAGE);
        }

    }


    private boolean isSpawnDataIsValid(){
        if(!spawnData.isEmpty()){
            int sumOfConstants = spawnData.values().stream().mapToInt(Integer::intValue).sum();
            return sumOfConstants <= WorldMap.MAP_SIZE && addDataIsValid();
        }
        return false;
    }

    private boolean addDataIsValid() {
        for(Entity e:spawnData.keySet()){
            if(spawnData.get(e)<addData.get(e)){
               return false;
            }
        }
        return true;
    }

    private int getTargetConstant(Entity entity, String constantAssigning){
        try {
            spawnCounter = 0;
            for (String constantName:constantsNames){
                constantNameParts = PATTERN_FOR_SPLIT.split(constantName);
                String entitySimpleName = entity.getClass().getSimpleName();

                if(isTargetConstant(entitySimpleName,constantAssigning)){
                    setSpawnCounter(constantName);
                }
            }
            return (int) spawnCounter;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void setSpawnAndAddData() {
        spawnData.clear();
        for (Entity entity: entities) {
            targetConstant = getTargetConstant(entity, FOR_SPAWN_CHECK);
            spawnData.put(entity,targetConstant);

            targetConstant = getTargetConstant(entity,FOR_ADD_CHECK);
            addData.put(entity,targetConstant);
        }
    }

    private void setSpawnCounter(String constantName) throws NoSuchFieldException, IllegalAccessException {
        Field field = EntitiesSpawner.class.getField(constantName);
        spawnCounter = (double) field.get(null);
        spawnCounter = spawnCounter*WorldMap.MAP_SIZE;
    }

    private boolean isTargetConstant(String entityClassSimpleName, String constantForSpawnOrAdd){
        return Arrays.stream(constantNameParts).anyMatch(d ->d.equalsIgnoreCase(constantForSpawnOrAdd)&&
                Arrays.stream(constantNameParts).anyMatch(s ->s.equalsIgnoreCase(entityClassSimpleName)));
    }

    private List<String> getConstants(){
        return Arrays.stream(EntitiesSpawner.class.getDeclaredFields()).
                filter(field->Modifier.isFinal(field.getModifiers())&&Modifier.isStatic(field.getModifiers())).
                map(Field::getName).collect(Collectors.toList());
    }

    public void addEntity(Entity entity) {
        targetConstant = Math.abs(targetConstant-quantityEntitiesOnMap);
        for (int i = 0; i<=targetConstant; i++){
            randomCoordinates = Coordinates.getRandomAvailableCoordinate(worldMap);
            worldMap.setEntityOnMap(randomCoordinates, entity);
        }
    }
    protected boolean isEntityAddable(Entity entity){
        if (isEntityAvailableForAdd(entity)){
           quantityEntitiesOnMap = worldMap.getEntitiesOfType(entity.getClass()).size();
           targetConstant = getTargetConstant(entity,FOR_ADD_CHECK);
           return quantityEntitiesOnMap < targetConstant;
        }
        return false;
    }

    private boolean isEntityAvailableForAdd(Entity entityForAdd){
        return entityForAdd instanceof Herbivore || entityForAdd instanceof Grass;
    }

}
