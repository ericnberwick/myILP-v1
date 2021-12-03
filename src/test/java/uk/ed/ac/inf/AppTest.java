package uk.ac.ed.inf;

import com.mapbox.geojson.LineString;
import org.junit.Test;
import uk.ed.ac.inf.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AppTest {

    private static final String VERSION = "1.0.5";
    private static final String RELEASE_DATE = "September 28, 2021";

    private final LongLat appletonTower = new LongLat(-3.186874, 55.944494);
    private final LongLat businessSchool = new LongLat(-3.1873,55.9430);
    private final LongLat greyfriarsKirkyard = new LongLat(-3.1928,55.9469);

    @Test
    public void myTest1(){
        String dbString = "jdbc:derby://localhost:9876/derbyDB";
        String date = "2022-02-02";
        List<Orders> ords;
        ords = Database.getOrder(dbString, date);
        for(Orders x: ords){
            System.out.println(x.orderNo);
        }
        Database.createDeliveries(dbString);
        Database.createFlightPath(dbString);
    }

    public void myTestNoFly(){
        LongLat a = new LongLat(-3.186874,55.944494);
        LongLat b = new LongLat(-3.1911,55.9456);
        LongLat c = new LongLat(-3.1861, 55.9447);
        LongLat d = new LongLat(-3.1862, 55.9457);
        Boolean res = noflyzone.intersect(a,b,c,d);
        System.out.println("They lines intersect and my function says this is" + res);
        //System.out.println(PathFind.getPoly());
        //System.out.println("\n");
        //System.out.println(PathFind.getPoly().coordinates().get(0).get(0).coordinates());
    }

    @Test
    public void testPath(){
        String dbString = "jdbc:derby://localhost:9876/derbyDB";
        String date = "2022-02-02";
        List<Nodes> q = new ArrayList<>();
        Nodes a = new Nodes();
        a.cord = new LongLat(-3.1913,55.9456);
        Nodes b = new Nodes();
        b.cord = new LongLat(-3.1885,55.9440);
        //System.out.println(nxt.h + nxt.g);
        String web_port = "9898";
        q = PathFind.findPath(b,a, web_port);
        LineString lnStr = PathFind.getLineStr(q);
        System.out.println(lnStr.toJson());
    }


    /*
    public void testThreewords(){
        String dbString = "jdbc:derby://localhost:9876/derbyDB";
        String date = "2022-02-02";
        List<Orders> ords = Database.getOrder(dbString, date);
        String machine = "localhost";
        String port = "9898";
        String three = webConnection.getShopLocation(ords.get(0), machine, port);
        System.out.println("tt");
        System.out.println(three);
        //LongLat loc = webConnection.threeWordsToLongLat(three, machine, port);
        //System.out.println(loc);
        //System.out.println("The longitude is "+ loc.longitude + " the latutude is " + loc.latitude);

    }
    */





}