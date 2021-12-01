package uk.ed.ac.inf;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) {
        Drone drone = new Drone();
        drone.position = drone.home;                                                                                    //Drone starts from appleton
        drone.battery = 1500;                                                                                           //Battery initially 1500
        String day = args[0];
        String month = args[1];
        String year = args[2];
        String web_port = args[3];
        String db_port = args[4];
        String dbString = String.format("jdbc:derby://localhost:%s/derbyDB", db_port);
        String dbDate= String.format("%s-%s-%s", year,month,day);
        Database.createDeliveries(dbString);                                                                            //Create deliveries table in Database
        Database.createFlightPath(dbString);                                                                            //Create flighpath table in Database
        List<Orders> listOfOrders = Database.getOrder(dbString, dbDate);                                                //We have list of orders of given day

        for(Orders ord: listOfOrders) {                                                                                 //for each item
            List<Item> orderItems= Database.getOrderDetails(ord.orderNo, dbString);                                     //Get the items in the order

            List<LongLat> shops = new ArrayList<>();
            LongLat deliverTo = webConnection.threeWordsToLongLat(ord.deliverTo, "localhost", web_port);        //Get shop location as a long and lat

            for(Item x: orderItems){                                                                                    //for each item in the order
                String threewords = (webConnection.getShopLocation(x.item, "localhost", web_port));
                LongLat itemShop = webConnection.threeWordsToLongLat(threewords, "localhost", web_port);
                if(!shops.contains(itemShop)){
                    shops.add(itemShop);                                                                                //get long lat of the shop
                }
            }
            if(drone.battery >= PathFind.orderMoveCost(drone, deliverTo, shops) ){                                      //if the order is feasible(within battery capacity)
                int batteryUsed = 0;
                Nodes initPosition = Nodes.longLatToNode(drone.position);
                Nodes shop1 = Nodes.longLatToNode(shops.get(0));
                List<Nodes> path1 = PathFind.findPath(initPosition,shop1);                                              //Go to first shop
                Database.writeFlightPath(path1, ord.orderNo, dbString);                                                 //Once move is made write the flightpath to the DB
                batteryUsed = batteryUsed + (path1.size()-1);
                drone.position = shops.get(0);                                                                          //Change drone position to the shop
                //HOVER
                Database.hover(ord.orderNo, drone.position.longitude,drone.position.latitude, dbString);                //HOVER for 1 move once at the location
                batteryUsed = batteryUsed + 1;
                if(shops.size() > 1){                                                                                   //if more than one shop to go to
                    Nodes shop2 = Nodes.longLatToNode(shops.get(1));
                    List<Nodes> path2 = PathFind.findPath(shop1, shop2);
                    Database.writeFlightPath(path2, ord.orderNo, dbString);
                    batteryUsed = batteryUsed + (path2.size()-1);
                    drone.position = shops.get(1);
                    //HOVER
                    Database.hover(ord.orderNo, drone.position.longitude,drone.position.latitude, dbString);
                    batteryUsed = batteryUsed + 1;
                    Nodes deliverToNode = Nodes.longLatToNode(deliverTo);
                    List<Nodes> path3 = PathFind.findPath(shop2, deliverToNode);
                    Database.writeFlightPath(path3, ord.orderNo, dbString);
                    batteryUsed = batteryUsed + (path3.size()-1);
                    drone.position = deliverTo;
                    //HOVER
                    Database.hover(ord.orderNo, drone.position.longitude,drone.position.latitude, dbString);
                    batteryUsed = batteryUsed + 1;
                    drone.battery = drone.battery - batteryUsed;

                } else{                                                                                                 //if just one shop
                    Nodes deliverToNode = Nodes.longLatToNode(deliverTo);
                    List<Nodes> path2 = PathFind.findPath(shop1,deliverToNode);
                    Database.writeFlightPath(path2, ord.orderNo, dbString);
                    batteryUsed = batteryUsed + (path2.size()-1);
                    drone.position = deliverTo;
                    //HOVER
                    Database.hover(ord.orderNo, drone.position.longitude,drone.position.latitude, dbString);
                    batteryUsed = batteryUsed + 1;
                    drone.battery = drone.battery - batteryUsed;
                }
                String[] itemStrings = new String[orderItems.size()];
                for(int i =0; i< orderItems.size(); i++){
                    itemStrings[i] = orderItems.get(i).item ;
                }
                Menus r = new Menus("localhost", web_port);
                int totalOrderCost = r.getDeliveryCost(itemStrings);                                                    //Get cost of order
                Database.updateDeliveries(ord.orderNo, ord.deliverTo, totalOrderCost, dbString);                        //Once delivered add order to deliveries table in database

            }                                                                                                           //if order feasible

        }                                                                                                               //for each order
        Nodes curPos = Nodes.longLatToNode(drone.position);
        Nodes apple = Nodes.longLatToNode(drone.home);
        List<Nodes> home = PathFind.findPath(curPos,apple);
        drone.position = drone.home;                                                                                    //Once it can't deliver anymore return to appleton
        Database.writeFlightPath(home, "appleton ", dbString);                                                  //Write return flightpath to database
        String dronedate = String.format("drone-%s-%s-%s", day,month,year);
        Database.createGSON(dbString, dronedate);                                                                       //Create flightpath of days delivery
    }
}
