package uk.ed.ac.inf;

import com.mapbox.geojson.LineString;

import java.util.ArrayList;
import java.util.List;

public class PathFind {


    public static LineString findPath(Nodes currNode, Nodes target){
        List<Nodes> visited;
        List<Nodes> open;
        LineString x = null;


        for(Nodes e: getNodes())
        //use distanceTo as heuristic

        //if valid calculate its cost and add it to open list and pick lowest cost in opne list as next nodew

        return x;
    }

    public static List<Nodes> getNodes(Nodes currentNode){
        List<Nodes> expanded = new ArrayList<>();
        for(int i =0; i<=350; i+=10){
            Nodes node = new Nodes();
            node.cord = currentNode.cord.nextPosition(i);
            node.angle = i;
            node.parent = currentNode;
            expanded.add(node);
        }
        return  expanded;
    }

    public static Boolean isValid(Nodes a, List<Nodes> visited){
        if(!a.cord.isConfined()){
            return false;
        }
/*        if(!a.cord.isConfined()){ //check if in no fly zone
            return false;
        }*/
        if(visited.contains(a)){
            return false;
        }

        return true;
    }


}
