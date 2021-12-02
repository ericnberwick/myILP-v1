package uk.ed.ac.inf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Orders {
    public String orderNo;
    public String date;
    public String customer;
    public String deliverTo;
    public String item;
    public Double value;

    /**
     * Sort orders in order to maximise profit
     * @param listOfOrders
     * @param dbString
     * @param machine
     * @param web_port
     * @return
     */
    public static List<Orders> sortBest(List<Orders> listOfOrders, String dbString, String machine, String web_port){
        Double path1 = 0.00;
        Double path2 = 0.00;
        List<Orders> bestOrder = new ArrayList<>();
        List<LongLat> shops = new ArrayList<>();
        for(Orders ord: listOfOrders){
            LongLat deliverTo = webConnection.threeWordsToLongLat(ord.deliverTo, "localhost", web_port);
            int orderCost = 0;
            List<Item> orderItems= Database.getOrderDetails(ord.orderNo, dbString);
            String[] itemStrings = new String[orderItems.size()];
            for(int i =0; i< orderItems.size(); i++){
                itemStrings[i] = orderItems.get(i).item ;
                String threewords = (webConnection.getShopLocation(orderItems.get(i).item, "localhost", web_port));
                LongLat itemShop = webConnection.threeWordsToLongLat(threewords, "localhost", web_port);
                if(!shops.contains(itemShop)){
                    shops.add(itemShop);                                                                                //get long lat of the shop
                }
            }
            if(shops. size() > 1){
                path1 = shops.get(0).distanceTo(shops.get(1));
                path2 = shops.get(1).distanceTo(deliverTo);

            } else {
                path1 = shops.get(0).distanceTo(deliverTo);
            }

            Menus r = new Menus(machine, web_port);
            ord.value =  r.getDeliveryCost(itemStrings)/ (path1 + path2);
            bestOrder.add(ord);
        }
        bestOrder.sort(Comparator.comparing(a -> a.value));
        return bestOrder;
    }
}
