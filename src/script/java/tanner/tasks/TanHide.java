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

    private Main main;
    private CommonConditions cc;

    public TanHide (Main main) {
        this.main = main;
        cc = new CommonConditions(main);
    }

    @Override
    public boolean validate() {
        return (cc.nearTanner() && cc.gotEnoughCoins() && cc.gotCowhide()) &&
                !main.restock && !main.isMuling;
    }

    @Override
    public int execute() {
        if (cc.tanInterfaceIsOpen()) {
            InterfaceComponent leatherComponent = Interfaces.getComponent(324, 124);;
            if (leatherComponent != null && leatherComponent.interact(ActionOpcodes.INTERFACE_ACTION)) {
                // wait for all cowhides to turn into leather
                if (Time.sleepUntil(() -> !cc.gotCowhide(), 3000)) {
                    main.totalTanned += Inventory.getCount(main.LEATHER);
                    return 300;
                }
            }
            return 600;
        } else {
            Npc tanner = Npcs.getNearest(main.TANNER_ID);
            if (tanner.interact("Trade")) {
                Time.sleepUntil(cc::tanInterfaceIsOpen, 7000);
                return 400;
            }
            return 600;
        }
    }
}
