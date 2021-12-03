package uk.ed.ac.inf;

public class Drone {
    private static final double HOMELONG = -3.186874;                                                                   //Appleton Longitude
    private static final double HOMELAT = 55.944494;                                                                    //Appleton Latitude
    LongLat position;
    LongLat home = new LongLat(HOMELONG, HOMELAT);
    int battery;
}
