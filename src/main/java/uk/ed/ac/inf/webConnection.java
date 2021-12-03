package uk.ed.ac.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class webConnection {
    private static final HttpClient client = HttpClient.newHttpClient();

    /**
     * Get the no-fly-zone as a string
     * establish connection to the webserver and get each of the buildings no fly zone
     * @return String of no fly zone
     */
    public static String getNoFlyString(String web_port){
        String machine = "localhost";
        String urlMenu = String.format("http://%s:%s/buildings/no-fly-zones.geojson", machine, web_port);               //format address with given machine and port
        HttpResponse<String> response = null;
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlMenu)).build();                            //Create HTTP request
            response = client.send(request, HttpResponse.BodyHandlers.ofString());                                      //Creat a response
            if(response.statusCode() != 200){
                System.err.println("Error response failed");
            }
        } catch (IOException e) {                                                                                       //Error handling check
            System.err.println("IO Exception Error");                                                                   //response.statusCode() if 200 then good otherwise
        } catch (RuntimeException e) {
            System.err.println("Runtime Exception Error");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response.body();                                                                                         //Return total cost + delivery charge
    }

    /**
     * Convert a what3words location to LongLat object
     * Use the webserver to find the coordinates of a what3words address
     * @param threewords String
     * @param machine String localhost
     * @param port String webport
     * @return LongLat object of the corresponding what3words address
     */
    public static LongLat threeWordsToLongLat(String threewords, String machine, String port){
        String[] parts = threewords.split("\\.");                                                                 //Split what3words address and get each word
        String part1 = parts[0];
        String part2 = parts[1];
        String part3 = parts[2];
        String urlMenu = String.format("http://%s:%s/words/%s/%s/%s/details.json", machine, port, part1, part2, part3); //Construct URL address from given what3words
        HttpResponse<String> response;
        LongLat a = null;
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlMenu)).build();                            //Make request to webserver and address constructed previously
            response = client.send(request, HttpResponse.BodyHandlers.ofString());                                      //Get the response of the request
            if(response.statusCode() != 200){                                                                           //If it doesn't get correct results
                System.err.println("Error response failed");
            }
            what3word details = new Gson().fromJson(response.body(), what3word.class);                                  //Concert what3words String to what3words object
            a = new LongLat(details.coordinates.lng, details.coordinates.lat);                                          //Get coordinates of what3words address
        } catch (IOException e) {                                                                                       //Error handling
            System.err.println("IO Exception Error");
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     * Get shop location of item
     * find the item in webserver of a in menu then find location of that item
     * @param order string order
     * @param machine local host
     * @param port webport
     * @return what3words String of shop location
     */
    public static String getShopLocation(String order, String machine, String port){
        String urlMenu = String.format("http://%s:%s/menus/menus.json", machine, port);                                 //format address with given machine and port
        HttpResponse<String> response;
        String threewords = null;
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlMenu)).build();                            //Create HTTP request
            response = client.send(request, HttpResponse.BodyHandlers.ofString());                                      //Creat a response
            if(response.statusCode() != 200){
                System.err.println("Error response failed");
            }
            Type listType = new TypeToken<ArrayList<MenuObject>>() {}.getType();                                        //Create a type token if(response.statusCode()){}
            ArrayList<MenuObject> jsonMenu = new Gson().fromJson(response.body(), listType);                            //Creat turn string into object
            boolean broke = false;
            for (MenuObject j : jsonMenu) {                                                                             //For each restaurant jsonMenu
                for (Item k : j.menu) {                                                                                 //For each ite in restaurants menu
                    if (k.item.equals(order)){                                                                          //If item found
                        threewords = j.location;                                                                        //Add cost to total
                        broke = true;                                                                                   //break out of the loop to eliminate redundant loops
                        break;
                    }
                }
                if(broke){break;}                                                                                       //break back to first loop and move on to find the next item
            }
        } catch (IOException e) {                                                                                       //Error handling check
            System.err.println("IO Exception Error");                                                                   //response.statusCode() if 200 then good otherwise
        } catch (RuntimeException e) {
            System.err.println("Runtime Exception Error");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return threewords;                                                                                              //Return total cost + delivery charge
    }
}
