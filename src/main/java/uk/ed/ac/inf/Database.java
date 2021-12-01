package uk.ed.ac.inf;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    public static List<Orders> getOrder(String dbString, String date){
        List<Orders> ordersForDay = new ArrayList<>();
        try{
            Connection conn = DriverManager.getConnection(dbString);
            //Statement statement = conn.createStatement();
            final String myQuery = "SELECT * FROM orders WHERE deliveryDate =(?)";
            PreparedStatement psCourseQuery = conn.prepareStatement(myQuery);
            psCourseQuery.setString(1, date);
            ResultSet rs = psCourseQuery.executeQuery();
            while (rs.next()) {
                Orders aOrder = new Orders();
                aOrder.orderNo = rs.getString("orderNo");
                aOrder.date = rs.getString("deliveryDate");
                aOrder.customer = rs.getString("customer");
                aOrder.deliverTo = rs.getString("deliverTo");
                ordersForDay.add(aOrder);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordersForDay;
    }
    public static List<Item> getOrderDetails(String orderNo, String dbString){
        List<Item> orderItems = new ArrayList<>();
        try{
            Connection conn = DriverManager.getConnection(dbString);
            //Statement statement = conn.createStatement();
            final String myQuery = "SELECT * FROM orderDetails WHERE orderDetails.orderNo =(?)";
            PreparedStatement psCourseQuery = conn.prepareStatement(myQuery);
            psCourseQuery.setString(1, orderNo);
            ResultSet rs = psCourseQuery.executeQuery();
            while (rs.next()) {
                Item aItem = new Item();
                aItem.item = rs.getString("item");
                orderItems.add(aItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }
    public static void createDeliveries(String dbString){

        try{
            Connection conn = DriverManager.getConnection(dbString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            // Note: must capitalise STUDENTS in the call to getTables
            ResultSet resultSet = databaseMetadata.getTables(null, null, "DELIVERIES", null);
                                    // If the resultSet is not empty then the table exists, so we can drop it
            if (resultSet.next()) {
                statement.execute("drop table DELIVERIES");
            }

            statement.execute(
                    "create table deliveries(" +
                            "orderNo char(8), " +
                            "deliveredTo varchar(19), " +
                            "costInPence int)");

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    public static void writeFlightPath(List<Nodes> flightpath, String orderNo, String db_string){

        for(int i=0; i<flightpath.size()-1; i++){

            Double fromLong = flightpath.get(i).cord.longitude;
            Double fromLat = flightpath.get(i).cord.latitude;
            int angle = flightpath.get(i).angle;
            Double toLong = flightpath.get(i+1).cord.longitude;
            Double toLat = flightpath.get(i+1).cord.latitude;

            UpdateFlightpath(orderNo, fromLong, fromLat,angle,toLong,toLat, db_string);

        }
    }


    public static void createFlightPath(String dbString){

        try{
            Connection conn = DriverManager.getConnection(dbString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            // Note: must capitalise STUDENTS in the call to getTables
            ResultSet resultSet = databaseMetadata.getTables(null, null, "FLIGHTPATH", null);
            // If the resultSet is not empty then the table exists, so we can drop it
            if (resultSet.next()) {
                statement.execute("drop table flightpath");
            }

            statement.execute(
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
    public static void updateDeliveries(String orderNo, String deliveredTo, int cost, String jdbcString){
        try{
            Connection conn = DriverManager.getConnection(jdbcString);

            PreparedStatement psDeliveries = conn.prepareStatement("insert into deliveries values (?, ?, ?)");
            psDeliveries.setString(1, orderNo);
            psDeliveries.setString(2, deliveredTo);
            psDeliveries.setInt(3, cost);

            psDeliveries.execute();

        } catch (SQLException e){
            e.printStackTrace();
        }

    }
    public static void UpdateFlightpath(String orderNo, double fromLong, double fromLat, int angle, Double toLong, Double toLat, String jdbcString){

        try{

            Connection conn = DriverManager.getConnection(jdbcString);
            PreparedStatement psDeliveries = conn.prepareStatement("insert into flightpath values (?, ?, ?, ?, ?, ?)");
            psDeliveries.setString(1, orderNo);
            psDeliveries.setDouble(2, fromLong);
            psDeliveries.setDouble(3, fromLat);
            psDeliveries.setInt(4, angle);
            psDeliveries.setDouble(5, toLong);
            psDeliveries.setDouble(6, toLat );

            psDeliveries.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }

    }
    public static void hover(String orderNo, Double p1, Double q1, String dbString){
        Database.UpdateFlightpath(orderNo, p1,q1, -999, p1, q1, dbString);
    }
    public static void createGSON(String dbString, String date){
        List<Point> points = new ArrayList<>();
        try{
            Connection conn = DriverManager.getConnection(dbString);
            //Statement statement = conn.createStatement();
            final String myQuery = "SELECT * FROM flightpath";
            PreparedStatement psCourseQuery = conn.prepareStatement(myQuery);
            ResultSet rs = psCourseQuery.executeQuery();
            while (rs.next()) {
                double fromLong = rs.getDouble("fromLongitude");
                double fromLat = rs.getDouble("fromLatitude");
                Point a = Point.fromLngLat(fromLong,fromLat);
                points.add(a);
                double toLong = rs.getDouble("toLongitude");
                double toLat = rs.getDouble("toLatitude");
                Point b = Point.fromLngLat(toLong,toLat);
                points.add(b);
            }
            LineString droneMovements = LineString.fromLngLats(points);
            String fileName = String.format("%s.geojson", date);
            try (FileWriter file = new FileWriter(fileName)) {
                //We can write any JSONArray or JSONObject instance to the file
                file.write(droneMovements.toJson());
                file.flush();

            } catch (IOException o) {
                o.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }





    }


}
