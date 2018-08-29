package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;

class Conditions {
    // True if got anything other than coins or cowhide
    static boolean gotJunkOrLeather() {
        return Inventory.contains(
            item -> item != null
                && !item.getName().equals("Coins")
                && item.getId() != Main.COWHIDE
        );
    }

    static boolean gotCowhide() {
        return Inventory.contains(Main.COWHIDE);
    }
    static boolean gotHide() {
        return Inventory.contains(Main.HIDES);
    }

    static boolean gotEnoughCoins() {
        Item coins = Inventory.getFirst("Coins");
        return coins != null && coins.getStackSize() >= 27;
    }

    static boolean nearTanner() {
        Npc tanner = Npcs.getNearest(TanHide.TANNER_ID);
        return Main.TANNER_AREA.getCenter().distance(Players.getLocal()) < 10 && tanner != null && tanner.isPositionInteractable();
    }

    static boolean atBank() {
        return BankLocation.AL_KHARID.getPosition().distance(Players.getLocal().getPosition()) <= 7;
    }

    static boolean tanInterfaceIsOpen() {
        return Interfaces.getComponent(324, 124) != null;
    }
}
