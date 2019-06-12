package script.java.tanner.tasks;

import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.java.tanner.Main;

public class LootHide extends Task {

    private final String LOOT_ITEM = "Cowhide";
    private Pickable item;

    private Main main;
    private Banking banking;

    public LootHide (Main main) {
        this.main = main;
        banking = new Banking(main);
    }

    @Override
    public boolean validate() {
        if (main.restock && Inventory.isFull()) {
            banking.openAndDepositAll();
            return false;
        }

        item = Pickables.getNearest(LOOT_ITEM);
        return main.restock && main.lootCows && item != null && item.isPositionInteractable() &&
                !Inventory.isFull() && main.COW_LOCATION.containsPlayer();
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
        if (main.cowHideCount >= main.lootAmount)
            main.restock = false;

        if (Inventory.contains(main.COWHIDE) && ((Inventory.getCount(false, main.COWHIDE) + main.cowHideCount) >= main.lootAmount))
           main.restock = false;

        Log.info("Total Hides: " + (Inventory.getCount(false, main.COWHIDE) + main.cowHideCount));
    }
}
