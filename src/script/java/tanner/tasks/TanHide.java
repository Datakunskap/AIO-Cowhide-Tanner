package script.java.tanner.tasks;

import script.java.tanner.Main;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.script.task.Task;

public class TanHide extends Task {

    public static int TANNER_ID = 3231;

    @Override
    public boolean validate() {
        return (CommonConditions.nearTanner() && CommonConditions.gotEnoughCoins() && CommonConditions.gotCowhide()) &&
                !Main.restock && !Main.isMuling;
    }

    @Override
    public int execute() {
        if (CommonConditions.tanInterfaceIsOpen()) {
            InterfaceComponent leatherComponent = null;
            // Cow
            if (Main.COWHIDE == 1739) {
                leatherComponent = Interfaces.getComponent(324, 124);
            }
            // Green
            if (Main.COWHIDE == 1753) {
                leatherComponent = Interfaces.getComponent(324, 128);
            }
            // Blue
            if (Main.COWHIDE == 1751) {
                leatherComponent = Interfaces.getComponent(324, 129);
            }
            // Red
            if (Main.COWHIDE == 1749) {
                leatherComponent = Interfaces.getComponent(324, 130);
            }
            // Black
            if (Main.COWHIDE == 1747) {
                leatherComponent = Interfaces.getComponent(324, 131);
            }

            if (leatherComponent != null && leatherComponent.interact(ActionOpcodes.INTERFACE_ACTION)) {
                // wait for all cowhides to turn into leather
                if (Time.sleepUntil(() -> !CommonConditions.gotCowhide(), 3000)) {
                    Main.totalTanned += Inventory.getCount(Main.LEATHERS[0]);
                    return 300;
                }
            }
            return 600;
        } else {
            Npc tanner = Npcs.getNearest(TANNER_ID);
            if (tanner.interact("Trade")) {
                Time.sleepUntil(CommonConditions::tanInterfaceIsOpen, 7000);
                return 400;
            }
            return 600;
        }
    }
}
