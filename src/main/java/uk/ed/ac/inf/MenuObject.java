package uk.ed.ac.inf;
import java.util.List;

public class MenuObject {

    String name;
    String location;
    List<Item> menu;

    /**
     * Getter for menu
     *
     * @return menu
     */
    public List<Item> getMenu(){
        return menu;
    }

}
