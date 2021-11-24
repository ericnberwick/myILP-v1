package uk.ed.ac.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Polygon;
import java.util.List;
import java.util.ArrayList;
public class noflyzone {

    public static List<Polygon> StringToGeoNoFly(String geoStr){
        ArrayList<Polygon> noFlyPolygons = new ArrayList<Polygon>(); // Create an ArrayList object
        FeatureCollection fc = FeatureCollection.fromJson(geoStr);
        for(Feature j : fc.features()){

            noFlyPolygons.add((Polygon)(j.geometry()));
        }

        return noFlyPolygons;
    }
}
