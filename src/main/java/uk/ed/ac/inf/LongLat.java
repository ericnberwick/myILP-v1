package uk.ed.ac.inf;

public class LongLat {

    public double longitude;
    public double latitude;

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
     * Checks the drone postition is within confinement area
     *
     * @return true if drone is in the confinement area
     */
    public boolean isConfined(){
        boolean inConfineArea = false;
        boolean inLong = false;
        boolean inLat = false;

        if((longitude >= -3.192473) && (longitude <= -3.184319)){
            inLong = true;
        }
        if((latitude >= 55.942617) && (latitude <= 55.946233)){
            inLat = true;
        }
        if(inLong & inLat){
            inConfineArea = true;
        }
        return  inConfineArea;
    }
    /**
     * Calculates the distance from drone to a given point(LongLat object)
     *
     * @param a Takes in a LongLat object as a parameter
     * @return theDistance a pythagorean distance between the drones position to LongLat object a
     */
    public double distanceTo(LongLat a){
        double theDistance = 0;

        theDistance = Math.sqrt(Math.pow((a.longitude - longitude),2) + Math.pow((a.latitude  - latitude),2)); //distance formula

        return theDistance;
    }

    /**
     * Finds out if drone is near a given point
     *
     * @param b Takes in a LongLat object as a parameter
     * @return true if the drone is close to LongLat object b
     */
    public boolean closeTo(LongLat b){
        boolean isClose = false;
        if (distanceTo(b) <= 0.00015){
            isClose = true;
        }
        return  isClose;
    }

    /**
     * Returns the new postion of drone after going at angle x a distance of y
     *
     * @param givenAngle Takes in an integer representing an angle
     * @return the drones new position after going the move distance at the given angle
     */
    public LongLat nextPosition(int givenAngle){
        if (givenAngle == -999 || givenAngle == 999){
            //lat and long stay the same as drone is hovering
            LongLat newPosition = new LongLat(longitude, latitude);
            return  newPosition;
        }

        double moveDistance = 0.00015;
        double angleToRadians = Math.toRadians(givenAngle);
        double newLong = longitude + moveDistance * Math.cos(angleToRadians);
        double newLat = latitude + moveDistance * Math.sin(angleToRadians);
        LongLat newPosition = new LongLat(newLong, newLat);
        return  newPosition;


    }


}
