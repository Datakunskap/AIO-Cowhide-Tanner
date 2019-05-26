package lamerton.troy.tanner.data;

import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;

public class Rings {

    public static boolean hasChargedRingD() {
        if (Inventory.contains(x -> x != null && x.getName().contains("dueling") && x.getName().matches(".*\\d+.*")) ||
                Equipment.contains(x -> x != null && x.getName().contains("dueling") && x.getName().matches(".*\\d+.*"))) {
            return true;
        }
        return false;
    }

    public static boolean hasChargedRingW(){
        if (Inventory.contains(x -> x != null && x.getName().contains("wealth") && x.getName().matches(".*\\d+.*")) ||
                Equipment.contains(x -> x != null && x.getName().contains("wealth") && x.getName().matches(".*\\d+.*"))) {
            return true;
        }
        return false;
    }
}
