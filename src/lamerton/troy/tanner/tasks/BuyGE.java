package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.ExGrandExchange;
import lamerton.troy.tanner.Main;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class BuyGE extends Task {

    private int buyQuantity;

    @Override
    public boolean validate() {
        return Main.sold && Main.restock && Main.location.getGEArea().contains(Players.getLocal()) && !Main.isMuling;
    }

    @Override
    public int execute() {
        if (Inventory.contains(11980)) {
            Main.newRingW = false;
        }
        if (Equipment.contains(2552)) {
            Main.newRingD = false;
        }
        if (Main.newRingW) {
            buyRingW();
        }
        if (Main.newRingD) {
            buyRingD();
        }
        if (Inventory.contains(11980)) {
            Main.newRingW = false;
        }
        if (Equipment.contains(2552)) {
            Main.newRingD = false;
        }

        if (!Main.checkedBank) {
            Banking.execute();
            Main.checkedBank = true;
        }

        if(Main.gp < Main.cowhidePrice && GrandExchange.getOffers() == null && GrandExchangeSetup.getItem() == null) {
            Log.severe("Not enough moneys");
            Main.sold = false;
            Main.checkedBank = false;
            Main.restock = false;
            closeGE();
        }

        buyQuantity = Main.gp / Main.cowhidePrice;

        if (GrandExchange.getFirstActive() == null && ExGrandExchange.buy(Main.COWHIDE, buyQuantity, Main.cowhidePrice, false)) {
            Log.fine("Buying hides");
        } else {
            Log.info("Waiting to complete");
            Time.sleepUntil(() -> GrandExchange.getFirst(x -> x != null).getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED), 2000, 10000);
            GrandExchange.collectAll();
            Keyboard.pressEnter();
        }
        if (Inventory.contains(Main.COWHIDE) || Inventory.contains(Main.COWHIDE+1)) {
            if (Time.sleepUntil(() -> (Inventory.getCount(true, Main.COWHIDE) +
                    Inventory.getCount(true, Main.COWHIDE+1)) >= buyQuantity, 5000)) {
                Log.fine("Done buying");
                Main.sold = false;
                Main.checkedBank = false;
                Main.restock = false;
                closeGE();
            }
        }
        return 1000;
    }

    private void closeGE() {
        while(GrandExchange.isOpen() || GrandExchangeSetup.isOpen()) {
            InterfaceComponent X = Interfaces.getComponent(465, 2, 11);
            X.interact(ActionOpcodes.INTERFACE_ACTION);
        }

    }

    private void buyRingW(){
        Log.fine("Buying Ring Of Wealth");
        if (ExGrandExchange.buy(11980, 1, Main.priceRingW+500, false)) {
            Time.sleep(600);
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
        }
    }

    private boolean equipD(){
        Log.info("Equipping ring");
        if(GrandExchange.isOpen() || GrandExchangeSetup.isOpen()) {
            Npcs.getNearest("Banker").interact("Bank");
            Time.sleep(1000);
            Bank.close();
        }
        if(Inventory.getFirst(x -> x != null && x.getName().contains("Ring of dueling") && x.getName().matches(".*\\d+.*")).interact("Wear")) {
            if (Time.sleepUntil(() -> Equipment.contains(i -> i != null && i.getName().contains("Ring of dueling") && i.getName().matches(".*\\d+.*")), Random.mid(2300, 2850))) {
                return true;
            }
        }
        return false;
    }

    private void buyRingD(){
        Log.fine("Buying Ring Of Dueling");
        if (ExGrandExchange.buy(2552, 1, Main.priceRingD+500, false)) {
            Time.sleep(600);
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));

            if (Inventory.contains(i -> i != null && i.getName().contains("Ring of dueling") && i.getName().matches(".*\\d+.*"))) {
                while (!equipD()) {
                    Time.sleep(1000);
                }
            }
        }
    }
}
