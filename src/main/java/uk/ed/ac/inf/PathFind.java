package uk.ed.ac.inf;

import com.mapbox.geojson.*;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PathFind {
    private final static double MOVEDISTANCE = 0.00015;

    /**
     * Find a flightpath from two locations while complying to flight constraints
     * @param startNode
     * @param target
     * @return
     */
    public static List<Nodes> findPath(Nodes startNode, Nodes target){
        List<Nodes> path = new ArrayList<>();
        List<Nodes> visited = new ArrayList<>();
        List<Nodes> open= new ArrayList<>();
        Nodes currNode = startNode;
        currNode.parent = null;
        currNode.parentNo = 0;
        visited.add(currNode);
        while(!(currNode.cord.closeTo(target.cord))){               //while we are not at the target
            List<Nodes> newNodes = new ArrayList<>();               //each iteration create new nodes
            List<Nodes> possibleNodes = getNodes(currNode);         //get possible nodes
            for(Nodes e: possibleNodes){                            //for each possible node
                if(isValid(e, visited, currNode)){                            //Check if it's a valid move: 1) not been visited 2) In confinement 3)Does not cross no-fly-zone
                    e.parent = currNode;                            //if it is valid set the parent to the current
                    newNodes.add(e);                                //add it to new nodes list
                }
            }
            open = getOpen(newNodes, target, open);                 //Assign. each new node a 'G' and 'H' value and add to open list
            currNode = nextNode(open);                              //set the current node to the best node in open
            open.remove(currNode);                                  //remove that node from open list
            visited.addAll(newNodes);                               //add all nodes explored to visited
        }                                                           //Repeat until we are closeTo target node
        while(currNode.parent != null) {                            //now the current node is the target
            path.add(currNode);                                     //add current node to the path
            currNode = currNode.parent;                             //set current node to the previous/parent node
        }                                                           //repeat until at first node i.e which has no parent
        Collections.reverse(path);                                  //reverse the list
        return path;
    }

    /**
     * Assign each node a 'G' and 'H' value and add to open list
     * @param newNode
     * @param target
     * @param open
     * @return
     */
    public static List<Nodes> getOpen(List<Nodes> newNode, Nodes target, List<Nodes> open){
        for(Nodes e: newNode){
            e.h = e.cord.distanceTo(target.cord);
            e.parentNo = e.parent.parentNo + 1;
            e.g = MOVEDISTANCE * e.parentNo;
            open.add(e);
        }
        return open;
    }

    /**
     * Find the next node with lowest F(g+h) value
     * @param open
     * @return
     */
    public static Nodes nextNode(List<Nodes> open){
        Nodes bestF = open.get(0);
        for(Nodes n: open){
            if((n.g + n.h)<(bestF.g + bestF.h)){
                bestF = n;
            }
        }
        return bestF;
    }

    /**
     * Find all possible nodes around a node
     * @param currentNode
     * @return
     */
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

    /**
     * Find out if a move is valid:
     *  1) Not already been visited
     *  2) Within confinement zone
     *  3) Doesn't cross no fly zone
     * @param a
     * @param visited
     * @param currNode
     * @return
     */
    public static Boolean isValid(Nodes a, List<Nodes> visited, Nodes currNode){
        if(!a.cord.isConfined()){
            return false;
        }
        for(Nodes x: visited){
            if(x.cord.closeTo(a.cord)){
                return false;
            }
        }
        if(!doesntGoInNoFly(currNode, a)){
            return false;
        }
        return true;
    }

    public static boolean doesntGoInNoFly(Nodes a, Nodes b){
        LongLat r = new LongLat(a.cord.longitude, a.cord.latitude);
        LongLat s = new LongLat(b.cord.longitude, b.cord.latitude);
        String source = webConnection.getNoFlyString();
        FeatureCollection fc = FeatureCollection.fromJson(source);
        List<Feature> f = fc.features();
        for(Feature ftr: f){
            Geometry g = ftr.geometry();
            Polygon p = ((Polygon)g);
            List<Point> lst = (p.coordinates().get(0));
            for (int i = 0; i < lst.size() - 1; i++) {
                LongLat c = new LongLat(lst.get(i).coordinates().get(0), lst.get(i).coordinates().get(1));
                LongLat d = new LongLat(lst.get(i+1).coordinates().get(0), lst.get(i+1).coordinates().get(1));
                if(noflyzone.intersect(r,s,c,d)){
                    return false;
                }
            }
        }
       return true;

    }

    /**
     * Convert list of nodes ot LineString
     * @param x
     * @return
     */
    public static LineString getLineStr(List<Nodes> x){                                                                 //remove before submission
        List<Point> points = new ArrayList<>();
        for(Nodes i: x){
            Point a =Point.fromLngLat(i.cord.longitude,i.cord.latitude);
            points.add(a);
        }
        return LineString.fromLngLats(points);
    }

    /**
     * Find cost of order/number of moves of an order
     * @param drone
     * @param deliverTo
     * @param shopLocs
     * @return
     */
    public static int orderMoveCost(Drone drone, LongLat deliverTo, List<LongLat> shopLocs){
        Nodes deliverToNode = Nodes.longLatToNode(deliverTo);
        Nodes appleNode = Nodes.longLatToNode(drone.home);
        Nodes shop1 = Nodes.longLatToNode(shopLocs.get(0));
        Nodes initPos = Nodes.longLatToNode(drone.position);
        int shop1ToShop2 = 0;
        int shop2toDeliverTo = 0;
        int shop1ToDeliverTo = 0;
        int deliverLocToApple = (PathFind.findPath(deliverToNode,appleNode)).size() - 1 ;
        int droneToShop1 = findPath(initPos, shop1).size() -1;
        if(shopLocs.size() > 1){
            Nodes shop2 = Nodes.longLatToNode(shopLocs.get(1));
            shop1ToShop2 = shop1ToShop2 + (findPath(shop1, shop2).size() -1);
            shop2toDeliverTo = shop2toDeliverTo + (findPath(shop2, deliverToNode).size() -1);
        }
        shop1ToDeliverTo = shop1ToDeliverTo + (findPath(shop1,deliverToNode).size() -1);
        return droneToShop1 + shop1ToShop2 + shop2toDeliverTo + shop1ToDeliverTo + deliverLocToApple;
    }

}
