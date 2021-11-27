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

public class webConnection {
    private static final HttpClient client = HttpClient.newHttpClient();



    public static String getNoFlyString(){
        String machine = "localhost";
        String port = "9898";
        String urlMenu = String.format("http://%s:%s/buildings/no-fly-zones.geojson", machine, port);             //format address with given machine and port
        HttpResponse<String> response = null;

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlMenu)).build();        //Create HTTP request
            response = client.send(request, HttpResponse.BodyHandlers.ofString());                  //Creat a response
            if(response.statusCode() != 200){
                System.err.println("Error response failed");
            }
        } catch (IOException e) {                                                                   //Error handling check
            System.err.println("IO Exception Error");                                               //response.statusCode() if 200 then good otherwise
        } catch (RuntimeException e) {
            System.err.println("Runtime Exception Error");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response.body();                                                                  //Return total cost + delivery charge
    }
}
