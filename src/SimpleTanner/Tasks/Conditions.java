package SimpleTanner.Tasks;

import SimpleTanner.LeatherTanner;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Players;

class Conditions {
    static boolean gotCowhide() {
        return Inventory.contains(LeatherTanner.COWHIDE);
    }

    static boolean gotEnoughCoins() {
        Item coins = Inventory.getFirst("Coins");
        return coins != null && coins.getStackSize() >= 27;
    }

    static boolean atTanner() {
        return LeatherTanner.TANNER_AREA.contains(Players.getLocal());
    }

    static boolean atBank() {
        return BankLocation.AL_KHARID.getPosition().distance(Players.getLocal().getPosition()) < 3;
    }
}