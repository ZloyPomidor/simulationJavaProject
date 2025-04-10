package org.example.utils;

import org.example.map.Coordinates;
import org.example.map.WorldMap;
import org.example.utils.asturAlgorithm.AStar;

public class PathFinder {

    public Coordinates findPath(WorldMap worldMap, Coordinates current ){
        AStar searcher = new AStar(worldMap);
        return searcher.search(current);
    }

}
