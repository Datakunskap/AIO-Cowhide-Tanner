package script.java.tanner.tasks;

import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.java.tanner.Main;

public class LootHide extends Task {

    private Pickable item;
    private String itemToLoot = "Cowhide";

    @Override
    public boolean validate() {
        if (Main.restock && Inventory.isFull()) {
            Banking.openAndDepositAll();
            return false;
        }

        item = Pickables.getNearest(itemToLoot);
        return Main.restock && Main.lootCows && item != null && item.isPositionInteractable() &&
                !Inventory.isFull() && Main.COW_LOCATION.containsPlayer();
    }

    @Override
    public int execute() {
        Log.info("Picking up Cowhide");
        item.interact("Take");

        if (WalkingHelper.shouldEnableRun()) {
            WalkingHelper.enableRun();
        }

        checkHideAmount();
        return 1000;
    }

    private void checkHideAmount() {
        if (Main.cowHideCount >= Main.lootAmount)
            Main.restock = false;

        if (Inventory.contains(Main.COWHIDE) && ((Inventory.getCount(false, Main.COWHIDE) + Main.cowHideCount) >= Main.lootAmount))
           Main.restock = false;

        Log.info("Total Hides: " + (Inventory.getCount(false, Main.COWHIDE) + Main.cowHideCount));
    }
}
