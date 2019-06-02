package script.java.tanner.tasks;

import script.java.tanner.Main;
import script.java.tanner.data.Rings;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.BankLocation;
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
        if (detectPrevHide() && !hasH) {
            hasH = true;
        }
        if (!Main.willBuyD){
            hasD = true;
        }
        if (!Main.willBuyW) {
            hasW = true;
        }

        if (!hasH || !hasD || !hasW || !Conditions.gotEnoughCoins()) {
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
            if (!Conditions.gotEnoughCoins())
                Log.fine("Out of money -> Selling leathers");

            if (Bank.isOpen() && BankLocation.getNearest() != null && BankLocation.getNearest().equals(BankLocation.AL_KHARID)) {
                Log.info("Getting Ring of wealth");
                Bank.withdraw(x -> x != null && x.getName().contains("wealth") && x.getName().matches(".*\\d+.*"), 1);
                Time.sleep(5000);
            }

            Main.restock = true;
        } else {
            Log.fine("Restock not necessary");

            Bank.depositAllExcept(995, 11980, 11982, 11982, 11986, 11988);
            Time.sleep(5000);
            /*if (Inventory.contains(Main.COWHIDE+1)) {
                Bank.depositAll(Main.COWHIDE+1);
                Time.sleep(5000);
            }*/
            if (Bank.isOpen() && BankLocation.getNearest() != null && BankLocation.getNearest().equals(BankLocation.GRAND_EXCHANGE)) {
                Log.info("Getting Ring of dueling");
                Bank.withdraw(x -> x != null && x.getName().contains("dueling") && x.getName().matches(".*\\d+.*"), 1);
                Time.sleep(5000);
            }
            Main.restock = false;
        }
        Main.checkRestock = false;
        return 1000;
    }

    private boolean detectPrevHide() {
        Log.info("Detecting hide");

        // Auto-detect previous hide
        boolean detectedH = false;

        // Detect leather
        if (Bank.contains(2507) || Bank.contains(1745) || Bank.contains(1741) || Bank.contains(2505) || Bank.contains(2509)) {
            Main.LEATHERS[0] = Bank.getFirst(x -> x != null && (x.getName().contains("leather")) ||
                    x.getName().contains("Leather")).getId();
            Main.setHideFromLeather();
            detectedH = false;
        }
        if (Inventory.contains(2507) || Inventory.contains(1745) || Inventory.contains(1741) || Inventory.contains(2505) || Inventory.contains(2509)) {
            Main.LEATHERS[0] = Inventory.getFirst(x -> x != null && x.getName().contains("leather") ||
                    x.getName().contains("Leather")).getId();
            Main.setHideFromLeather();
            detectedH = false;
        }
        for(Item i : Bank.getInventory(x -> x != null && x.getName().contains("leather"))){
            if (i != null) {
                Main.LEATHERS[0] = i.getId();
                Main.setHideFromLeather();
                detectedH = false;
            }
        }

        // Detect hide
        if (Bank.contains(1753) || Bank.contains(1749) || Bank.contains(1751) || Bank.contains(1747) || Bank.contains(1739)) {
            Main.COWHIDE = Bank.getFirst(x -> x != null && x.getName().contains("hide")).getId();
            detectedH = true;
        }
        if (Inventory.contains(1753) || Inventory.contains(1749) || Inventory.contains(1751) || Inventory.contains(1747) || Inventory.contains(1739)) {
            Main.COWHIDE = Inventory.getFirst(x -> x != null && x.getName().contains("hide")).getId();
            detectedH = true;
        }
        for(Item i : Bank.getInventory(x -> x != null && x.getName().contains("hide"))){
            if (i != null) {
                Main.COWHIDE = i.getId();
                detectedH = true;
            }
        }

        Main.setLeather();
        Main.setPrices();
        return detectedH;
    }

    private void closeGE() {
        while(GrandExchange.isOpen() || GrandExchangeSetup.isOpen()) {
            InterfaceComponent X = Interfaces.getComponent(465, 2, 11);
            X.interact(ActionOpcodes.INTERFACE_ACTION);
        }

    }
}
