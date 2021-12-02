package uk.ed.ac.inf;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) {
        Drone drone = new Drone();
        drone.position = drone.home;                                                                                    //Drone starts from appleton
        drone.battery = 1500;                                                                                           //Battery is initially full(1500)
        String day = args[0];
        String month = args[1];
        String year = args[2];
        String web_port = args[3];
        String db_port = args[4];
        String dbString = String.format("jdbc:derby://localhost:%s/derbyDB", db_port);
        String dbDate= String.format("%s-%s-%s", year,month,day);
        Database.createDeliveries(dbString);                                                                            //Create deliveries table in the Database
        Database.createFlightPath(dbString);                                                                            //Create flightpath table in the Database
        List<Orders> listOfOrders = Database.getOrder(dbString, dbDate);                                                //Get orders of given day
        listOfOrders = Orders.sortBest(listOfOrders, dbString, "localhost", web_port);                          //Order list to maximise profit/monetary value
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
            if(drone.battery >= PathFind.orderMoveCost(drone, deliverTo, shops) ){                                      //If the order is feasible (within battery capacity)
                int batteryUsed = 0;                                                                                    //Initialise variable to track battery usage of order
                Nodes initPosition = Nodes.longLatToNode(drone.position);
                Nodes shop1 = Nodes.longLatToNode(shops.get(0));
                List<Nodes> path1 = PathFind.findPath(initPosition,shop1);                                              //Find flightpath to shop 1
                Database.writeFlightPath(path1, ord.orderNo, dbString);                                                 //Write the flightpath to the DB
                batteryUsed = batteryUsed + (path1.size()-1);                                                           //Update battery usage
                drone.position = shops.get(0);                                                                          //Move drone position to the shop
                Database.hover(ord.orderNo, drone.position.longitude,drone.position.latitude, dbString);                //HOVER for 1 move once at the location
                batteryUsed = batteryUsed + 1;
                if(shops.size() > 1){                                                                                   //if more than one shop to go to
                    Nodes shop2 = Nodes.longLatToNode(shops.get(1));
                    List<Nodes> path2 = PathFind.findPath(shop1, shop2);
                    Database.writeFlightPath(path2, ord.orderNo, dbString);
                    batteryUsed = batteryUsed + (path2.size()-1);
                    drone.position = shops.get(1);
                    Database.hover(ord.orderNo, drone.position.longitude,drone.position.latitude, dbString);            //HOVER
                    batteryUsed = batteryUsed + 1;
                    Nodes deliverToNode = Nodes.longLatToNode(deliverTo);
                    List<Nodes> path3 = PathFind.findPath(shop2, deliverToNode);
                    Database.writeFlightPath(path3, ord.orderNo, dbString);
                    batteryUsed = batteryUsed + (path3.size()-1);
                    drone.position = deliverTo;
                    Database.hover(ord.orderNo, drone.position.longitude,drone.position.latitude, dbString);            //HOVER
                    batteryUsed = batteryUsed + 1;
                    drone.battery = drone.battery - batteryUsed;                                                        //Update drones battery from battery used in move

                } else{                                                                                                 //if just one shop
                    Nodes deliverToNode = Nodes.longLatToNode(deliverTo);
                    List<Nodes> path2 = PathFind.findPath(shop1,deliverToNode);
                    Database.writeFlightPath(path2, ord.orderNo, dbString);
                    batteryUsed = batteryUsed + (path2.size()-1);
                    drone.position = deliverTo;
                    Database.hover(ord.orderNo, drone.position.longitude,drone.position.latitude, dbString);            //HOVER
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
        Nodes curPos = Nodes.longLatToNode(drone.position);
        Nodes apple = Nodes.longLatToNode(drone.home);
        List<Nodes> home = PathFind.findPath(curPos,apple);
        drone.position = drone.home;                                                                                    //Once drone can't deliver anymore return to appleton
        Database.writeFlightPath(home, "appleton ", dbString);                                                  //Write return flightpath to database
        String dronedate = String.format("drone-%s-%s-%s", day,month,year);
        Database.createGSON(dbString, dronedate);                                                                       //Create flightpath of days delivery
    }
}
