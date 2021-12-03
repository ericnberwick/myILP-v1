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
        Orders.deliverOrders(drone, web_port, dbString, listOfOrders);                                                  //Deliver orders
        Nodes curPos = Nodes.longLatToNode(drone.position);
        Nodes apple = Nodes.longLatToNode(drone.home);
        List<Nodes> home = PathFind.findPath(curPos,apple, web_port);
        drone.position = drone.home;                                                                                    //Once drone can't deliver anymore return to appleton
        Database.writeFlightPath(home, "Appleton ", dbString);                                                  //Write return flightpath to database
        String dronedate = String.format("drone-%s-%s-%s", day,month,year);
        Database.createGSON(dbString, dronedate);                                                                       //Create geojson file of flightpath of given day
    }



}
