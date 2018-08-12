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
            Time.sleep(100, 300);
            InterfaceComponent leatherComponent = Interfaces.getComponent(324, 124);

            if (leatherComponent.interact(ActionOpcodes.INTERFACE_ACTION)) {
                // wait for all cowhides to turn into leather
                if (Time.sleepUntil(() -> !Conditions.gotCowhide(), 3000)) {
                    taskRunner.totalTanned += Inventory.getCount("Leather");
                }
            }
        } else {
            Npc tanner = Npcs.getNearest(3231);
            tanner.interact("Trade");
            Time.sleepUntil(Conditions::tanInterfaceIsOpen, 4000);
        }
        return 600;
    }
}
