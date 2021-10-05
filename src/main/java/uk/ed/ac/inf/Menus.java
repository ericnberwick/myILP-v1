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
    private static final HttpClient client = HttpClient.newHttpClient();
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
        String urlMenu = String.format("http://%s:%s/menus/menus.json", machine, port);
        HttpResponse<String> response;

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlMenu)).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type listType = new TypeToken<ArrayList<MenuObject>>() {}.getType();
            ArrayList<MenuObject> jsonMenu = new Gson().fromJson(response.body(), listType);
            for(String i: varStrings) {
                boolean broke = false;
                for (MenuObject j : jsonMenu) {
                    for (Item k : j.menu) {
                        if (k.item.equals(i)){
                            totalCost = totalCost + k.getPence();
                            broke = true;
                            break;

                        }
                    }
                    if(broke){ break; }
                }
            }
        } catch (IOException e) {
            System.err.println("IO Exception Error");
        } catch (RuntimeException e) {
            System.err.println("Runtime Exception Error");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            return totalCost + 50;
        }


    }

