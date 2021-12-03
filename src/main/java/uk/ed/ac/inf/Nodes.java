package uk.ed.ac.inf;

public class Nodes {
    public LongLat cord;
    public int angle;
    public Nodes parent;
    public int parentNo;
    public Double g;
    public Double h;

    /**
     * Convert LongLat object to Nodes object
     * @param a givne long loat
     * @return Node Converted LongLat object
     */
    public static Nodes longLatToNode(LongLat a){
        Nodes b = new Nodes();                                                                                          //Create node object
        b.cord = new LongLat(a.longitude, a.latitude);                                                                  //Assign node LongLat field to given LongLat
        return b;                                                                                                       //Return a node
    }
}
