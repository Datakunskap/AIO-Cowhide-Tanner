package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
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

    private Main taskRunner;
    public TanHide(Main taskRunner) {
        this.taskRunner = taskRunner;
    }

    @Override
    public boolean validate() {
        return Conditions.nearTanner() && Conditions.gotEnoughCoins() && Conditions.gotCowhide();
    }

    @Override
    public int execute() {
        if (Conditions.tanInterfaceIsOpen()) {
            InterfaceComponent leatherComponent = Interfaces.getComponent(324, 124);

            if (leatherComponent.interact(ActionOpcodes.INTERFACE_ACTION)) {
                // wait for all cowhides to turn into leather
                if (Time.sleepUntil(() -> !Conditions.gotCowhide(), 3000)) {
                    taskRunner.totalTanned += Inventory.getCount("Leather");
                    return 300;
                }
            }
            return 600;
        } else {
            Npc tanner = Npcs.getNearest(TANNER_ID);
            if (tanner.interact("Trade")) {
                Time.sleepUntil(Conditions::tanInterfaceIsOpen, 7000);
                return 400;
            }
            return 600;
        }
    }
}
