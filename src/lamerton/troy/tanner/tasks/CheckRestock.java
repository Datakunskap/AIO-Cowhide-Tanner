package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
import lamerton.troy.tanner.data.Rings;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class CheckRestock extends Task {

    private Main taskRunner;

    @Override
    public boolean validate() {
        return Main.restock && Main.checkRestock;
    }

    @Override
    public int execute() {
        Log.info("Restock check");
        if(Bank.isOpen()){
            Bank.close();
        }
        if(GrandExchange.isOpen() || GrandExchangeSetup.isOpen()){
            closeGE();
        }

        boolean hasW = false;
        boolean hasD = false;
        boolean hasH = false;

        if (Rings.hasChargedRingW()) {
            hasW = true;
        }
        if (Rings.hasChargedRingD()) {
            hasD = true;
        }
        if (Inventory.contains(Main.COWHIDE) || Inventory.contains(Main.COWHIDE + 1)) {
            hasH = true;
        }

        while (!Bank.isOpen()) {
            Bank.open();
            Time.sleep(1000);
        }
        if (!hasW && Bank.contains(x -> x != null && x.getName().contains("wealth") && x.getName().matches(".*\\d+.*"))) {
            Main.newRingW = false;
            hasW = true;
        }
        if (!hasD && Bank.contains(x -> x != null && x.getName().contains("dueling") && x.getName().matches(".*\\d+.*"))) {
            Main.newRingD = false;
            hasD = true;
        }
        if (!hasH && Bank.contains(Main.COWHIDE) || Bank.contains(Main.COWHIDE + 1)) {
            hasH = true;
        }
        if (!hasH || !hasD || !hasW) {
            Log.fine("Restocking: ->");
            Time.sleep(1000);
            if (!hasH)
                Main.printHide();
            if (!hasW) {
                Log.fine("Ring of wealth");
                Main.newRingW = true;
            }
            if (!hasD) {
                Log.fine("Ring of dueling");
                Main.newRingD = true;
            }
            Main.restock = true;
        } else {
            Main.restock = false;
        }
        Main.checkRestock = false;
        return 1000;
    }

    private void closeGE() {
        while(GrandExchange.isOpen() || GrandExchangeSetup.isOpen()) {
            InterfaceComponent X = Interfaces.getComponent(465, 2, 11);
            X.interact(ActionOpcodes.INTERFACE_ACTION);
        }

    }
}
