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
    public int value;


    public static List<Orders> sortBest(List<Orders> listOfOrders, String dbString, String machine, String web_port){
        List<Orders> bestOrder = new ArrayList<>();
        for(Orders ord: listOfOrders){
            int orderCost = 0;
            List<Item> orderItems= Database.getOrderDetails(ord.orderNo, dbString);
            String[] itemStrings = new String[orderItems.size()];
            for(int i =0; i< orderItems.size(); i++){
                itemStrings[i] = orderItems.get(i).item ;
            }
            Menus r = new Menus(machine, web_port);
            ord.value = r.getDeliveryCost(itemStrings);
            bestOrder.add(ord);


        }
        bestOrder.sort(Comparator.comparing(a -> a.value));
        return bestOrder;
    }
}
