package uk.ed.ac.inf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Orders {
    public String orderNo;
    public String date;
    public String customer;
    public String deliverTo;
    public int value;

    /**
     * Sort orders in order to maximise profit
     * Each orders value is determined by the total cost of the order
     * @param listOfOrders List of Orders
     * @param dbString URL of database as string
     * @param machine localhost as String
     * @param web_port string port of webserver
     * @return List of orders in the optimal order
     */
    public static List<Orders> sortBest(List<Orders> listOfOrders, String dbString, String machine, String web_port){
        List<Orders> bestOrder = new ArrayList<>();
        List<LongLat> shops = new ArrayList<>();
        for(Orders ord: listOfOrders){                                                                                  //For every order
            List<Item> orderItems= Database.getOrderDetails(ord.orderNo, dbString);
            String[] itemStrings = new String[orderItems.size()];
            for(int i =0; i< orderItems.size(); i++){                                                                   //For every item
                itemStrings[i] = orderItems.get(i).item ;
                String threewords = (webConnection.getShopLocation(orderItems.get(i).item, "localhost", web_port));
                LongLat itemShop = webConnection.threeWordsToLongLat(threewords, "localhost", web_port);
                if(!shops.contains(itemShop)){
                    shops.add(itemShop);                                                                                //get long lat of the shop
                }
            }
            Menus r = new Menus(machine, web_port);
            ord.value =  r.getDeliveryCost(itemStrings);                                                                //assign each order a value
            bestOrder.add(ord);
        }
        bestOrder.sort(Comparator.comparing(a -> a.value));                                                             //Order list of orders in descending order by value of the order
        return bestOrder;
    }

    /**
     * Deliver the given orders for the day
     * @param drone Drone object
     * @param web_port web port given
     * @param dbString URL of database
     * @param listOfOrders List of orders to be delivered
     */
    public static void deliverOrders(Drone drone, String web_port, String dbString, List<Orders> listOfOrders) {
        for(Orders ord: listOfOrders) {                                                                                 //For each order
            List<Item> orderItems= Database.getOrderDetails(ord.orderNo, dbString);                                     //Get the items in the order
            List<LongLat> shops = new ArrayList<>();
            LongLat deliverTo = webConnection.threeWordsToLongLat(ord.deliverTo, "localhost", web_port);        //Get Location of delivery location
            for(Item x: orderItems){                                                                                    //For each item in the order
                String threewords = (webConnection.getShopLocation(x.item, "localhost", web_port));
                LongLat itemShop = webConnection.threeWordsToLongLat(threewords, "localhost", web_port);        //Get location of each item
                if(!shops.contains(itemShop)){
                    shops.add(itemShop);                                                                                //Add location of shops to list of shops
                }
            }
            if(drone.battery >= PathFind.orderMoveCost(drone, deliverTo, shops, web_port) ){                            //If the order is feasible (within battery capacity)
                int batteryUsed = 0;                                                                                    //Initialise variable to track battery usage of order
                Nodes initPosition = Nodes.longLatToNode(drone.position);
                Nodes shop1 = Nodes.longLatToNode(shops.get(0));
                List<Nodes> path1 = PathFind.findPath(initPosition,shop1, web_port);                                    //Find flightpath to shop 1
                Database.writeFlightPath(path1, ord.orderNo, dbString);                                                 //Write the flightpath to the DB
                batteryUsed = batteryUsed + (path1.size()-1);                                                           //Update battery usage
                drone.position = shops.get(0);                                                                          //Move drone position to the shop
                Database.hover(ord.orderNo, drone.position.longitude, drone.position.latitude, dbString);               //HOVER for 1 move once at the location
                batteryUsed = batteryUsed + 1;
                if(shops.size() > 1){                                                                                   //if more than one shop to go to
                    Nodes shop2 = Nodes.longLatToNode(shops.get(1));
                    List<Nodes> path2 = PathFind.findPath(shop1, shop2, web_port);
                    Database.writeFlightPath(path2, ord.orderNo, dbString);
                    batteryUsed = batteryUsed + (path2.size()-1);
                    drone.position = shops.get(1);
                    Database.hover(ord.orderNo, drone.position.longitude, drone.position.latitude, dbString);           //HOVER
                    batteryUsed = batteryUsed + 1;
                    Nodes deliverToNode = Nodes.longLatToNode(deliverTo);
                    List<Nodes> path3 = PathFind.findPath(shop2, deliverToNode, web_port);
                    Database.writeFlightPath(path3, ord.orderNo, dbString);
                    batteryUsed = batteryUsed + (path3.size()-1);
                    drone.position = deliverTo;
                    Database.hover(ord.orderNo, drone.position.longitude, drone.position.latitude, dbString);           //HOVER
                    batteryUsed = batteryUsed + 1;
                    drone.battery = drone.battery - batteryUsed;                                                        //Update drones battery from battery used in move

                } else{                                                                                                 //if just one shop
                    Nodes deliverToNode = Nodes.longLatToNode(deliverTo);
                    List<Nodes> path2 = PathFind.findPath(shop1,deliverToNode, web_port);
                    Database.writeFlightPath(path2, ord.orderNo, dbString);
                    batteryUsed = batteryUsed + (path2.size()-1);
                    drone.position = deliverTo;
                    Database.hover(ord.orderNo, drone.position.longitude, drone.position.latitude, dbString);           //HOVER
                    batteryUsed = batteryUsed + 1;
                    drone.battery = drone.battery - batteryUsed;                                                        //Update drones battery from battery used in move
                }
                String[] itemStrings = new String[orderItems.size()];
                for(int i =0; i< orderItems.size(); i++){
                    itemStrings[i] = orderItems.get(i).item ;
                }
                Menus r = new Menus("localhost", web_port);
                int totalOrderCost = r.getDeliveryCost(itemStrings);                                                    //Find total cost of order
                Database.updateDeliveries(ord.orderNo, ord.deliverTo, totalOrderCost, dbString);                        //Add delivery to deliveries Database
            }
        }
    }
}
