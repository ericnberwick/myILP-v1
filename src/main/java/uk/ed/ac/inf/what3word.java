package uk.ed.ac.inf;
import java.util.List;

public class what3word {
    String country;
    shape square;
    String nearestPlace;
    cords coordinates;
    String words;
    String language;
    String map;
    public static class cords {
        Double lng;
        Double lat;
    }
    public static class shape {
        cords southwest;
        cords northeast;
    }
}
