package uk.ed.ac.inf;

public class Nodes {

    public LongLat cord;
    public int angle;
    public Nodes parent;
    public int parentNo;
    public Double g;
    public Double h;

    public static Nodes longLatToNode(LongLat a){
        Nodes b = new Nodes();
        b.cord = new LongLat(a.longitude, a.latitude);
        return b;
    }

}
