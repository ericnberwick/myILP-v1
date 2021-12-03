package uk.ed.ac.inf;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    /**
     *Get all the orders for the given date
     * @param dbString
     * @param date
     * @return List of Orders
     */
    public static List<Orders> getOrder(String dbString, String date){
        List<Orders> ordersForDay = new ArrayList<>();
        try{
            Connection conn = DriverManager.getConnection(dbString);                                                    //Establish connection to database
            final String myQuery = "SELECT * FROM orders WHERE deliveryDate =(?)";                                      //Create query template to be passed to the database
            PreparedStatement psCourseQuery = conn.prepareStatement(myQuery);
            psCourseQuery.setString(1, date);
            ResultSet rs = psCourseQuery.executeQuery();                                                                //Execute query with given paramter of date inputted to the function
            while (rs.next()) {                                                                                         //For each result of database
                Orders aOrder = new Orders();
                aOrder.orderNo = rs.getString("orderNo");
                aOrder.date = rs.getString("deliveryDate");
                aOrder.customer = rs.getString("customer");
                aOrder.deliverTo = rs.getString("deliverTo");
                ordersForDay.add(aOrder);                                                                               //create orders object and add to list to be returned as orders for the day
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordersForDay;
    }

    /**
     *For a given order(identified by its orderNo) return the items the order contains
     * @param orderNo
     * @param dbString
     * @return List of items of an order
     */
    public static List<Item> getOrderDetails(String orderNo, String dbString){
        List<Item> orderItems = new ArrayList<>();
        try{
            Connection conn = DriverManager.getConnection(dbString);                                                    //Establish connection
            final String myQuery = "SELECT * FROM orderDetails WHERE orderDetails.orderNo =(?)";                        //Create query template
            PreparedStatement psCourseQuery = conn.prepareStatement(myQuery);
            psCourseQuery.setString(1, orderNo);
            ResultSet rs = psCourseQuery.executeQuery();                                                                //Execute query on database with orderNo given
            while (rs.next()) {                                                                                         //For each row returned
                Item aItem = new Item();
                aItem.item = rs.getString("item");
                orderItems.add(aItem);                                                                                  //Create item object and to list of items to be returned
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }

    /**
     * Add deliveries table to Database
     * Check if a deliveries table exists if it does drop it so no SQL errors occur
     * @param dbString
     */
    public static void createDeliveries(String dbString){
        try{
            Connection conn = DriverManager.getConnection(dbString);                                                    //establish connection
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "DELIVERIES", null);
            if (resultSet.next()) {                                                                                     //if table exists
                statement.execute("drop table DELIVERIES");                                                         //drop it
            }
            statement.execute(                                                                                          //Create deliveries table in database
                    "create table deliveries(" +
                            "orderNo char(8), " +
                            "deliveredTo varchar(19), " +
                            "costInPence int)");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Create flightpath table in database
     * If a flightpath table already exists drop it to ensure no SQL errors occur
     * @param dbString
     */
    public static void createFlightPath(String dbString){
        try{
            Connection conn = DriverManager.getConnection(dbString);                                                    //Establish conncection
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "FLIGHTPATH", null);
            if (resultSet.next()) {                                                                                     //if table exists
                statement.execute("drop table flightpath");                                                         //drop it
            }
            statement.execute(                                                                                          //Create flightpath table in the database
                    "create table flightpath(" +
                            "orderNo char(8)," +
                            "fromLongitude double, " +
                            "fromLatitude double, " +
                            "angle integer, " +
                            "toLongitude double, " +
                            "toLatitude double)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Write a flightpath given as a list of node to the flightpath table in the database
     * @param flightpath
     * @param orderNo
     * @param db_string
     */
    public static void writeFlightPath(List<Nodes> flightpath, String orderNo, String db_string){
        for(int i=0; i<flightpath.size()-1; i++){                                                                       //Repeat for length of list -1 (As last node will not go to anywhere)
            Double fromLong = flightpath.get(i).cord.longitude;
            Double fromLat = flightpath.get(i).cord.latitude;
            int angle = flightpath.get(i).angle;
            Double toLong = flightpath.get(i+1).cord.longitude;
            Double toLat = flightpath.get(i+1).cord.latitude;
            UpdateFlightpath(orderNo, fromLong, fromLat,angle,toLong,toLat, db_string);                                 //Assign flightpath to the flightpath table in the database
        }
    }
    /**
     * Add order to deliveries table in database
     * @param orderNo
     * @param deliveredTo
     * @param cost
     * @param jdbcString
     */
    public static void updateDeliveries(String orderNo, String deliveredTo, int cost, String jdbcString){
        try{
            Connection conn = DriverManager.getConnection(jdbcString);                                                  //Establish connection with the database
            PreparedStatement psDeliveries = conn.prepareStatement("insert into deliveries values (?, ?, ?)");      //Create SQL statement template
            psDeliveries.setString(1, orderNo);                                                             //Give orderNo to statement
            psDeliveries.setString(2, deliveredTo);                                                         //Give deliverTo to statement
            psDeliveries.setInt(3, cost);                                                                   //Give cost to statement
            psDeliveries.execute();                                                                                     //Execute the statement
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Add a single move to flightpath table in database
     * @param orderNo
     * @param fromLong
     * @param fromLat
     * @param angle
     * @param toLong
     * @param toLat
     * @param jdbcString
     */
    public static void UpdateFlightpath(String orderNo, double fromLong, double fromLat, int angle, Double toLong, Double toLat, String jdbcString){
        try{
            Connection conn = DriverManager.getConnection(jdbcString);                                                  //Establish database connection
            PreparedStatement psFlight = conn.prepareStatement("insert into flightpath values (?, ?, ?, ?, ?, ?)"); //Create SQL statement template to insert data
            psFlight.setString(1, orderNo);                                                                 //Give orderNo to sql statement
            psFlight.setDouble(2, fromLong);                                                                //Give fromLong to sql statement
            psFlight.setDouble(3, fromLat);                                                                 //Give fromLat to sql statement
            psFlight.setInt(4, angle);                                                                      //Give angle of move to sql statement
            psFlight.setDouble(5, toLong);                                                                  //Give toLong to sql statement
            psFlight.setDouble(6, toLat );                                                                  //Give toLat to sql statement
            psFlight.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Add hover flightpath to database
     * use the dummy -999 value to indicate drone is hovering
     * @param orderNo
     * @param p1
     * @param q1
     * @param dbString
     */
    public static void hover(String orderNo, Double p1, Double q1, String dbString){
        Database.UpdateFlightpath(orderNo, p1,q1, -999, p1, q1, dbString);                                        //Write hover move to flightpath table in database
    }

    /**
     * Create geojson file of flightpath for drones path for the day from the flightpath table in the database
     * file will be created in the current directory
     * @param dbString
     * @param date
     */
    public static void createGSON(String dbString, String date){
        List<Point> points = new ArrayList<>();
        try{
            Connection conn = DriverManager.getConnection(dbString);                                                    //establish database connection
            final String myQuery = "SELECT * FROM flightpath";                                                          //String Query to get all data from flightpath table in database
            PreparedStatement psCourseQuery = conn.prepareStatement(myQuery);
            ResultSet rs = psCourseQuery.executeQuery();                                                                //Execute query and save results to rs
            while (rs.next()) {                                                                                         //For each row
                double fromLong = rs.getDouble("fromLongitude");
                double fromLat = rs.getDouble("fromLatitude");
                Point a = Point.fromLngLat(fromLong,fromLat);
                points.add(a);                                                                                          //Add fromPoint
                double toLong = rs.getDouble("toLongitude");
                double toLat = rs.getDouble("toLatitude");
                Point b = Point.fromLngLat(toLong,toLat);
                points.add(b);                                                                                          //Add toPoint
            }
            LineString droneMovements = LineString.fromLngLats(points);                                                 //Create LineString from list of points
            String fileName = String.format("%s.geojson", date);
            try (FileWriter file = new FileWriter(fileName)) {
                file.write(droneMovements.toJson());                                                                    //Write geojson file
                file.flush();
            } catch (IOException o) {
                o.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
