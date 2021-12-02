package uk.ed.ac.inf;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Polygon;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
public class noflyzone {

    /**
     * Check if two lines intersect
     * @param p1
     * @param q1
     * @param p2
     * @param q2
     * @return
     */
    public static boolean intersect(LongLat p1, LongLat q1, LongLat p2, LongLat q2){
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);
        if (o1 != o2 && o3 != o4){
            return true;
        }
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;
        return false;
    }

    /**
     * Find orientation of lines
     * @param p
     * @param q
     * @param r
     * @return
     */
    public static int orientation(LongLat p, LongLat q, LongLat r) {
        double val = (q.latitude - p.latitude) * (r.longitude - q.longitude) - (q.longitude - p.longitude) * (r.latitude - q.latitude);
        if (val == 0){  // colinear
            return 0;
        }
        return (val > 0)? 1: 2; // clock or counterclock wise
    }

    /**
     * Find if lines are on Segment/ on each other
     * @param p
     * @param q
     * @param r
     * @return
     */
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
