package uk.ed.ac.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Polygon;

import java.util.Collections;
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
    public static boolean intersect(LongLat p1, LongLat q1, LongLat p2, LongLat q2){
        // Find the four orientations needed for general and
        // special cases
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);
        // General case
        if (o1 != o2 && o3 != o4){
            return true;
        }

        // Special Cases
        // p1, q1 and p2 are colinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;
        // p1, q1 and q2 are colinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;
        // p2, q2 and p1 are colinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;
        // p2, q2 and q1 are colinear and q1 lies on segment p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;

        return false; // Doesn't fall in any of the above cases


    }
    public static int orientation(LongLat p, LongLat q, LongLat r) {

        double val = (q.latitude - p.latitude) * (r.longitude - q.longitude) - (q.longitude - p.longitude) * (r.latitude - q.latitude);
        if (val == 0){  // colinear
            return 0;
        }
        return (val > 0)? 1: 2; // clock or counterclock wise
    }
    public static boolean onSegment(LongLat p, LongLat q, LongLat r) {
        List<Double> one = new ArrayList<>();
        one.add(p.longitude);
        one.add(r.longitude);
        List<Double> two = new ArrayList<>();
        two.add(p.longitude);
        two.add(r.longitude);
        List<Double> three = new ArrayList<>();
        three.add(p.latitude);
        three.add(r.latitude);
        List<Double> four = new ArrayList<>();
        four.add(p.latitude);
        four.add(r.latitude);
        if (q.longitude <= Collections.max(one) && q.longitude >= Collections.max(two) &&
                q.latitude <= Collections.max(three) && q.latitude >= Collections.max(four)){
            return true;
        }

        return false;
    }

}
