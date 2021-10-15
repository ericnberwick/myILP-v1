package uk.ed.ac.inf;

public class LongLat {

    public final double longitude;
    public final double latitude;
    private final static double MINLONG = -3.192473;
    private final static double MAXLONG = -3.184319;
    private final static double MINLAT = 55.942617;
    private final static double MAXLAT = 55.946233;
    private final static double ISCLOSEDIST = 0.00015;
    private final static int HOVERING = -999;

    /**
     * Constructor for LongLat
     *
     * @param givenLongitude The longitude given
     * @param givenLatitude  The latitude given
     */
    public LongLat(double givenLongitude, double givenLatitude){
        longitude = givenLongitude;
        latitude = givenLatitude;

    }
    /**
     * Checks if the drone is within the confinement area
     *
     * @return true if drone is in the confinement area
     */
    public boolean isConfined(){
        boolean inConfineArea = false;
        boolean inLong = false;
        boolean inLat = false;

        if((longitude > MINLONG) && (longitude < MAXLONG)){                                              //Is drone in given longitude of the confinement area
            inLong = true;
        }
        if((latitude > MINLAT) && (latitude < MAXLAT)){                                                //Is drone in given latitude of the confinement area
            inLat = true;
        }
        if(inLong & inLat){                                                                                  //Is drone in the confinement area
            inConfineArea = true;
        }
        return  inConfineArea;


    }
    /**
     * Calculates the distance from drone to a given point(LongLat object)
     *
     * @param a Takes in a LongLat object as a parameter
     * @return theDistance a (double) pythagorean distance between the drones position to LongLat object a
     */
    public double distanceTo(LongLat a){
        return Math.sqrt(Math.pow((a.longitude - longitude),2) + Math.pow((a.latitude  - latitude),2));     //pythagorean distance formula
    }

    /**
     * Finds out if drone is near a given point
     *
     * @param b Takes in a LongLat object as a parameter
     * @return true if the drone is close to LongLat object b
     */
    public boolean closeTo(LongLat b){
        return distanceTo(b) < ISCLOSEDIST;                                                                    //0.00015 is the tolerance specified to be close to a location
    }

    /**
     * Returns the new postion of drone after going at angle x a distance of y
     *
     * @param givenAngle Takes in an integer representing an angle
     * @return the drones new position after going the move distance at the given angle
     */
    public LongLat nextPosition(int givenAngle){
        if (givenAngle == HOVERING){                                                                            //Dummy value to show the drone is hovering and keep longitude and latitude the same
            LongLat newPosition = new LongLat(longitude, latitude);
            return  newPosition;
        }

        double angleToRadians = Math.toRadians(givenAngle);                                                //Convert given angle to radians
        double newLong = longitude + ISCLOSEDIST * Math.cos(angleToRadians);                               //Calculate new longitude
        double newLat = latitude + ISCLOSEDIST * Math.sin(angleToRadians);                                 //Calculate new latitude
        LongLat newPosition = new LongLat(newLong, newLat);
        return  newPosition;


    }


}
