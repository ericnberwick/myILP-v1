package uk.ed.ac.inf;

public class LongLat {

    public double longitude; // or should i use float
    public double latitude;

    public LongLat(double givenLongitude, double givenLatitude){
        longitude = givenLongitude;
        latitude = givenLatitude;

    }

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

    public double distanceTo(LongLat a, LongLat b){
        double theDistance = 0;

        theDistance = Math.sqrt(Math.pow((a.longitude - b.longitude),2) + Math.pow((a.latitude  -b.latitude),2));

        return theDistance;
    }


}
