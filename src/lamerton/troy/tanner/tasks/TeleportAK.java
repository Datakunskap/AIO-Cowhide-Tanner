package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
import lamerton.troy.tanner.data.Rings;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.EquipmentSlot;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class TeleportAK extends Task {

    @Override
    public boolean validate() {
        return !Main.restock && (Main.location.getGEArea().contains(Players.getLocal()) || Main.muleArea.getMuleArea().contains(Players.getLocal())) &&
                !Main.isMuling && hasRing() && !Main.newRingD;
    }

    @Override
    public int execute() {
        Log.fine("Teleporting to AK");
        dueling();
        if (Rings.hasChargedRingD()) {
            Log.info("Ring has charge left");
            Main.newRingD = false;
        } else {
            Log.severe("Ring has no charge");
            Main.newRingD = true;
        }
        return 1000;
    }

    private void dueling() {
        if (!EquipmentSlot.RING.getItem().getName().equals("Ring of dueling") && EquipmentSlot.RING.interact("Duel Arena")) {
            Position current = Players.getLocal().getPosition();
            Time.sleepUntil(() -> !Players.getLocal().getPosition().equals(current), 2000);
            if (Rings.hasChargedRingD()) {
                Log.info("Ring has charge left");
                Main.newRingD = false;
            } else {
                Log.severe("Ring has no charge");
                Main.newRingD = true;
            }
        } else {
            for (Item item : Inventory.getItems()) {
                if (item != null && item.getName().contains("dueling") && item.getName().matches(".*\\d+.*")) {
                    item.interact("Wear");
                    if (Time.sleepUntil(() -> EquipmentSlot.RING.getItem().getId() == item.getId(), 2000)) {
                        break;
                    }
                }
            }
        }
    }

    private boolean hasRing() {
        if (Equipment.contains(i -> i != null && i.getName().contains("Ring of dueling")) || Inventory.contains(i -> i != null && i.getName().contains("Ring of dueling"))) {
            return true;
        }
        return false;
    }
}
