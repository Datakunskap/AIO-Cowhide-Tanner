package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.ExGrandExchange;
import lamerton.troy.tanner.Main;
import lamerton.troy.tanner.data.Rings;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
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
        if (!Main.checkedBank) {
            Banking.execute();
            Main.checkedBank = true;
        }

        if (Rings.hasChargedRingW()) {
            Main.newRingW = false;
        }
        if (Rings.hasChargedRingD()) {
            Main.newRingD = false;
        }

        if (!GrandExchange.isOpen()) {
            Time.sleepUntil(() -> Npcs.getNearest(x -> x != null && x.getName().contains("Grand Exchange Clerk")).interact("Exchange"), 1000, 10000);
            Time.sleep(700, 1300);
            return 1000;
        }

        if (Main.numStamina > 0 && !hasStaminaPotions() &&
                GrandExchange.getFirst(x -> x != null && x.getItemId() == 12625) == null) {
            Log.fine("Buying Stamina potion(s)");
            buyStamina();
        }
        if (Main.newRingW && GrandExchange.getFirst(x -> x != null && x.getItemId() == 11980) == null) {
            buyRingW();
        }
        if (Main.newRingD && GrandExchange.getFirst(x -> x != null && x.getItemId() == 2552) == null) {
            buyRingD();
        }

        if(Main.gp < Main.cowhidePrice && GrandExchange.getOffers() == null && GrandExchangeSetup.getItem() == null) {
            Log.severe("Not enough moneys");
            Main.sold = false;
            Main.checkedBank = false;
            Main.restock = false;
            closeGE();
        }

        buyQuantity = Main.gp / Main.cowhidePrice;
        if(Main.COWHIDE == 1739) {
            buyQuantity = 2300;
        }

        if (Main.buyPriceChng && (Inventory.contains(Main.COWHIDE) || Inventory.contains(Main.COWHIDE+1)))
            buyQuantity -= (Inventory.getCount(true, x -> x != null && x.getId() == Main.COWHIDE) + Inventory.getCount(true, x -> x != null && x.getId() == Main.COWHIDE+1));
        if (GrandExchange.getFirstActive() == null && ExGrandExchange.buy(Main.COWHIDE, buyQuantity, (Main.cowhidePrice + Main.incBuyPrice), false)) {
            Log.fine("Buying Hides");
        } else {
            Log.info("Waiting to complete  |  Time: " + Main.elapsedSeconds / 60 + "min(s)  |  Price changed " + Main.timesPriceChanged + " time(s)");
            if (!GrandExchange.isOpen()) {
                Npcs.getNearest("Grand Exchange Clerk").interact("Exchange");
                Time.sleep(Main.randInt(700, 1300));
            }
            Time.sleepUntil(() -> GrandExchange.getFirst(x -> x != null).getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED), 2000, 10000);
            GrandExchange.collectAll();
            Time.sleep(1500);
        }

        if (GrandExchange.getFirstActive() == null && !Main.newRingW && !Main.newRingD &&
                (Inventory.contains(Main.COWHIDE) || Inventory.contains(Main.COWHIDE+1))) {
            if (Time.sleepUntil(() -> (Inventory.getCount(true, Main.COWHIDE) +
                    Inventory.getCount(true, Main.COWHIDE+1)) >= buyQuantity, 5000)) {
                Log.fine("Done buying");
                Main.sold = false;
                Main.checkedBank = false;
                Main.restock = false;
                closeGE();
                Main.startTime = System.currentTimeMillis();
                Main.buyPriceChng = false;
                Main.incBuyPrice = 0;
                Main.timesPriceChanged = 0;
            }
        }

        Main.checkTime();
        if(Main.elapsedSeconds > Main.resetGeTime * 60 &&
                GrandExchange.getFirstActive() != null) {
            Log.fine("Increasing hide price by: " + Main.intervalAmnt);
            while(GrandExchange.getFirstActive() != null) {
                Time.sleepUntil(() -> GrandExchange.getFirst(x -> x != null).abort(), 1000, 5000);
                Time.sleep(3000);
                GrandExchange.collectAll();
            }
            Main.incBuyPrice += Main.intervalAmnt;
            Main.setPrices();
            Main.startTime = System.currentTimeMillis();
            Main.buyPriceChng = true;
            Main.timesPriceChanged++;
        }
        GrandExchange.collectAll();
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

    private boolean hasStaminaPotions() {
        if ((Inventory.contains(12625) && Inventory.getCount(true, 12625) >= Main.numStamina) ||
                (Inventory.contains(12625+1) && Inventory.getCount(true, 12625+1) >= Main.numStamina) ||
                (Inventory.contains(12625) && Inventory.contains(12625+1) && (Inventory.getCount(true, 12625) +
                        Inventory.getCount(true, 12625+1) >= Main.numStamina))) {
            return true;
        }
        return false;
    }

    private void buyStamina() {
        if (ExGrandExchange.buy(12625, Main.numStamina, Main.priceStamina+20, false)) {
            Time.sleep(600);
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
        }
    }
}
