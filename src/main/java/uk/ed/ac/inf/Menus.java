package uk.ed.ac.inf;
import com.google.gson.Gson;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import com.google.gson.reflect.TypeToken;

public class Menus {
    private static final HttpClient client = HttpClient.newHttpClient();                            //Creat HTTP client must be static
    public String machine;
    public String port;

    /**
     * Constructor for Menu
     *
     * @param nameOfMachine the name of the machine
     * @param  portNo the port
     */
    public Menus(String nameOfMachine, String portNo){
        machine = nameOfMachine;
        port = portNo;

    }
    /**
     * Calculates the cost of given items including the delivery fee
     *
     * @param varStrings List of items
     * @return the total cost of items including delivery cost
     */
    public int getDeliveryCost(String... varStrings){
        int totalCost = 0;
        String urlMenu = String.format("http://%s:%s/menus/menus.json", machine, port);             //format address with given machine and port
        HttpResponse<String> response;

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlMenu)).build();        //Create HTTP request
            response = client.send(request, HttpResponse.BodyHandlers.ofString());                  //Creat a response
            Type listType = new TypeToken<ArrayList<MenuObject>>() {}.getType();                    //Create a tyoe token
            ArrayList<MenuObject> jsonMenu = new Gson().fromJson(response.body(), listType);        //Creat turn string into object
            for(String i: varStrings) {                                                             //For each given item
                boolean broke = false;
                for (MenuObject j : jsonMenu) {                                                     //For each restuarant jsonMenu

                    for (Item k : j.menu) {                                                         //For each ite in restuaurants menu

                        if (k.item.equals(i)){                                                      //If item found
                            totalCost = totalCost + k.getPence();                                   //Add cost to total
                            broke = true;                                                           //break out of the loop to reduce redundant loops
                            break;

                        }
                    }
                    if(broke){ break; }                                                             //break back to move to next item to find
                }
            }
        } catch (IOException e) {
            System.err.println("IO Exception Error");
        } catch (RuntimeException e) {
            System.err.println("Runtime Exception Error");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            return totalCost + 50;                                                                  //Return total cost + delivery charge
        }


    }

