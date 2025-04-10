package org.example.utils.asturAlgorithm;

import org.example.entities.creatures.Creature;
import org.example.entities.staticEntities.Rock;
import org.example.entities.staticEntities.Tree;
import org.example.map.Coordinates;
import org.example.entities.Entity;
import org.example.map.WorldMap;
import org.example.utils.MoveType;

import java.util.*;
import java.util.stream.Collectors;

public class AStar {

    private final PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(Node::getF));
    private final Set<Coordinates> closedList = new HashSet<>();
    private final List<Coordinates> inaccessibleGoals = new ArrayList<>();
    public final WorldMap worldMap;
    private static final int MOVE_COST = 1;

    public AStar(WorldMap worldMap) {
        this.worldMap = worldMap;
    }

    public Coordinates search(Coordinates start) {
        Creature currentCreature = (Creature) worldMap.getEntity(start);
        Node startNode = new Node(start);
        Coordinates target = getTarget(start, currentCreature.getTarget().getClass());
        Entity entityGoal = worldMap.getEntity(target);
        Node targetNode = new Node(target);
        Node currentNode;
        startNode.setG(0);
        startNode.setH(getHeuristic(startNode, targetNode));
        startNode.setF(startNode.getG() + startNode.getH());
        boolean goalHasBeenNotFunded = false;
        openList.add(startNode);
        if (target.equals(start)){
            goalHasBeenNotFunded = true;
        }
        while (!goalHasBeenNotFunded) {
            while (!openList.isEmpty()) {
                currentNode = openList.poll();
                if (currentNode.equals(targetNode)) {
                    return reconstructPath(currentNode);
                }
                closedList.add(currentNode);
                List<Node> neighbors = getNeighbors(currentNode);
                for (Node neighbor : neighbors) {
                    if (closedList.contains(neighbor) || neighborIsBusy(entityGoal.getClass(), neighbor)) {
                        continue;
                    }
                    int tentativeG = currentNode.getG() + MOVE_COST;
                    neighbor.setG(tentativeG);
                    neighbor.setH(getHeuristic(neighbor, targetNode));
                    neighbor.setF(neighbor.getG() + neighbor.getH());
                    neighbor.setParent(currentNode);
                    Optional<Node> existingNodeOpt = openList.stream().filter(n -> n.equals(neighbor)).findFirst();
                    if (existingNodeOpt.isPresent()) {
                        Node existingNode = existingNodeOpt.get();
                        if (tentativeG < existingNode.getG()) {
                            existingNode.setG(tentativeG);
                            existingNode.setF(existingNode.getG() + existingNode.getH());
                            existingNode.setParent(currentNode);
                        }
                    } else {
                        openList.add(neighbor);
                    }
                }
            }
            inaccessibleGoals.add(target);
            if (!isGoalNotFunded(currentCreature.getTarget())){
                goalHasBeenNotFunded = true;
                continue;
            }
            target = getTarget(start, currentCreature.getTarget().getClass());
            targetNode = new Node(target);
            closedList.clear();
            openList.clear();
        }
        return start;
    }

    private boolean isGoalNotFunded(Entity goal) {
        return inaccessibleGoals.size() < worldMap.getEntitiesOfType(goal.getClass()).size();
    }

    private Coordinates reconstructPath(Node node) {
        Deque<Coordinates> path = new LinkedList<>();
        Coordinates temp;
        while (node!=null){
            temp = new Coordinates(node.row,node.column);
            path.addFirst(temp);
            node = node.getParent();
        }
        path.removeFirst();
        return path.getFirst();
    }

    private Set<Coordinates> getOpenEntityPosition(Coordinates currentCoordinates) {
        Set<Coordinates> result = new HashSet<>();

        for (Coordinates shift : MoveType.steps()) {
            if (currentCoordinates.canShift(shift)) {
                Coordinates newCordCreature = currentCoordinates.shift(shift);

                if (isPathAvailable(newCordCreature)) {
                    result.add(newCordCreature);
                }
            }
        }
        return result;
    }
    private boolean isPathAvailable(Coordinates cord) {
        if (worldMap.coordinatesIsEmpty(cord)) {
            return true;
        }
        Entity entity = worldMap.getEntity(cord);
        return !entity.getClass().equals(Tree.class) &&
                !entity.getClass().equals(Rock.class);
    }

    private static int getHeuristic(Node x, Node neighbors){
        int xDifference = Math.abs(x.row -neighbors.row);
        int yDifference = Math.abs(x.column -neighbors.column);
        return xDifference+yDifference;
    }

    private static Coordinates toCoordinates(Node Node){
        return new Coordinates(Node.row, Node.column);
    }

    private List<Node> getNeighbors(Node current){
        List<Node> result = new ArrayList<>();
        for(Coordinates i: getOpenEntityPosition(current) ){
            result.add(new Node(i));
        }
        return result;
    }

    private Coordinates getMinDifferenceCoordinate(List<Coordinates> differences) {
        List<Integer> resultsOf_X_Y_Sums = differences.stream().map(a -> a.row + a.column).toList();
        int minResultOf_X_Y_Sum = Collections.min(resultsOf_X_Y_Sums);
        int resultDifferencePosition = 0;

        for (int i = 0; i < differences.size(); i++) {
            if (resultsOf_X_Y_Sums.get(i)==minResultOf_X_Y_Sum) {
                resultDifferencePosition = i;
            }
        }
        return differences.get(resultDifferencePosition);
    }

    private boolean neighborIsBusy(Class<? extends Entity> terminateEntity, Node neighborNode) {
        if (neighborNode == null|| worldMap.coordinatesIsEmpty(toCoordinates(neighborNode))){
            return false;
        }
        Coordinates neighbor = toCoordinates(neighborNode);
        Class<? extends Entity> neighborEntity = worldMap.getEntity(neighbor).getClass();
        return !terminateEntity.equals(neighborEntity);
    }

    private Coordinates getTarget(Coordinates thatCoordinate, Class<? extends Entity> type){
        if (targetsAreAvailable(type)){
            List<Coordinates> targets = worldMap.getEntitiesOfType(type).stream().
                filter(e-> !inaccessibleGoals.contains(e)).collect(Collectors.toList());
        return getNearestTarget(thatCoordinate, targets);
        }
        return thatCoordinate;
    }

    private boolean targetsAreAvailable(Class<? extends Entity> type) {
        List<Coordinates> entitiesOfType = worldMap.getEntitiesOfType(type);
        if(!entitiesOfType.isEmpty()){
            if (!inaccessibleGoals.isEmpty()){
                return entitiesOfType.stream().anyMatch(c ->!inaccessibleGoals.contains(c));
            } else
                return true;
        }
        return false;
    }

    private Coordinates getNearestTarget(Coordinates currentCoordinate, List<Coordinates> listOfTargets) {

        List<Coordinates> differences = getDifferences(listOfTargets, currentCoordinate);
        Coordinates minDifferencesCoordinate = getMinDifferenceCoordinate(differences);
        int resultCoordinatesPosition = 0;

        for (int i = 0; i < listOfTargets.size(); i++) {
            if (minDifferencesCoordinate.equals(differences.get(i))) {
                resultCoordinatesPosition = i;
            }
        }
        return listOfTargets.get(resultCoordinatesPosition);
    }

    private List<Coordinates> getDifferences(List<Coordinates> coordinatesForDifference, Coordinates currentCoordinate) {
        List<Coordinates> differences = new ArrayList<>();
        for (Coordinates c : coordinatesForDifference) {
            differences.add(new Coordinates(Math.abs(c.row - currentCoordinate.row),
                    Math.abs(c.column - currentCoordinate.column)));
        }
        return differences;
    }
}
