package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
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
        return Main.restock && !Main.location.getGEArea().contains(Players.getLocal()) && !Main.isMuling && hasRing();
    }

    @Override
    public int execute() {
        if (hasCharge()) {
            Log.fine("Teleporting to GE");
            wealth();
        } else {
            Log.severe("Ring has no charge");
            Main.newRingW = true;
        }
        return 1000;
    }

    private void wealth() {
        if (!EquipmentSlot.RING.getItem().getName().equals("Ring of wealth") && EquipmentSlot.RING.interact("Grand exchange")) {
            Position current = Players.getLocal().getPosition();
            Time.sleepUntil(() -> !Players.getLocal().getPosition().equals(current), 2000);

        } else {
            for (Item item : Inventory.getItems()) {
                if (item != null && item.getName().contains("wealth") && item.getName().matches(".*\\d+.*")) {
                    item.interact("wear");
                    if (Time.sleepUntil(() -> EquipmentSlot.RING.getItem().getId() == item.getId(), 2000)) {
                        break;
                    }
                }
            }
        }
    }

    private boolean hasRing(){
        if (Equipment.contains(i -> i != null && i.getName().contains("Ring of wealth"))){
            return true;
        }
        return false;
    }

    private boolean hasCharge(){
        // if (Equipment.contains(i -> i != null && i.getName().contains("Ring of wealth")) && !Equipment.contains("Ring of wealth")) {
        if (Equipment.contains(i -> i != null && i.getName().contains("Ring of wealth"))){
            String[] actions = EquipmentSlot.RING.getActions();
            for (String a : actions) {
                if (a.toLowerCase().contains("exchange")) {
                    return true;
                }
            }
        }
        return false;
    }
}
