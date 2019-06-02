package script.java.tanner.tasks;

import script.java.tanner.Main;
import script.java.tanner.data.Rings;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.EquipmentSlot;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class TeleportGE extends Task {

    @Override
    public boolean validate() {
        return Main.willBuyW && Main.restock && !Main.location.getGEArea().contains(Players.getLocal()) &&
                !Main.isMuling && hasRing() && !Main.newRingW && Inventory.getCount(true, 995) != Main.muleKeep;
    }

    @Override
    public int execute() {
        Log.fine("Teleporting to GE");
        wealth();
        if (Rings.hasChargedRingW()) {
            Log.info("Ring has charge left");
            Main.newRingW = false;
        } else {
            Log.severe("Ring has no charge");
            Main.newRingW = true;
        }
        return 1000;
    }

    private void wealth() {
        if (Equipment.contains(x -> x != null && x.getName().contains("wealth")) && !EquipmentSlot.RING.getItem().getName().equals("Ring of wealth") &&
                EquipmentSlot.RING.interact("Grand exchange")) {
            Position current = Players.getLocal().getPosition();
            Time.sleepUntil(() -> !Players.getLocal().getPosition().equals(current), 2000);
            Time.sleep(2000);
            if (Rings.hasChargedRingW()) {
                Log.info("Ring has charge left");
                Main.newRingW = false;
            } else {
                Log.severe("Ring has no charge");
                Main.newRingW = true;
            }
        } else {
            for (Item item : Inventory.getItems()) {
                if (item != null && item.getName().contains("wealth") && item.getName().matches(".*\\d+.*")) {
                    item.interact("Wear");
                    if (Time.sleepUntil(() -> EquipmentSlot.RING.getItem() != null &&
                            EquipmentSlot.RING.getItem().getId() == item.getId(), 2000)) {
                        break;
                    }
                }
            }
        }
    }

    private boolean hasRing(){
        if (Equipment.contains(i -> i != null && i.getName().contains("Ring of wealth")) || Inventory.contains(i -> i != null && i.getName().contains("Ring of wealth"))){
            return true;
        }
        return false;
    }
}
