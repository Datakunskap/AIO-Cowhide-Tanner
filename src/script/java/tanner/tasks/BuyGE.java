package script.java.tanner.tasks;

import script.java.tanner.ExGrandExchange;
import script.java.tanner.Main;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class BuyGE extends Task {

    private int buyQuantity;

    @Override
    public boolean validate() {
        return Main.sold && Main.restock && Main.GE_LOCATION.containsPlayer() && !Main.isMuling;
    }

    @Override
    public int execute() {
        if (!Main.checkedBank) {
            Banking.execute();
            Main.checkedBank = true;
        }

        if (!GrandExchange.isOpen()) {
            Npc n = Npcs.getNearest(x -> x != null && x.getName().contains("Grand Exchange Clerk"));
            if (n != null) {
                Time.sleepUntil(() -> n.interact("Exchange"), 1000, 10000);
                Time.sleep(700, 1300);
            }
            return 1000;
        }

        if(Main.gp < Main.cowhidePrice && GrandExchange.getOffers() == null && GrandExchangeSetup.getItem() == null) {
            Log.severe("Not enough moneys");
            Main.sold = false;
            Main.checkedBank = false;
            Main.restock = false;
            Main.closeGE();
        }

        // sets quantity to buy
        buyQuantity = Main.gp / Main.cowhidePrice;

        // Checks if done buying
        if (GrandExchange.getFirstActive() == null && (Inventory.contains(Main.COWHIDE) || Inventory.contains(Main.COWHIDE+1))) {
            if (Time.sleepUntil(() -> (Inventory.getCount(true, Main.COWHIDE) +
                    Inventory.getCount(true, Main.COWHIDE+1)) >= buyQuantity, 5000)) {
                Log.fine("Done buying");
                Main.sold = false;
                Main.checkedBank = false;
                Main.restock = false;
                Main.closeGE();
                Main.startTime = System.currentTimeMillis();
                Main.buyPriceChng = false;
                Main.incBuyPrice = 0;
                Main.timesPriceChanged = 0;
                // Handled manually
                Banking.openAndDepositAll();
                return 2000;
            }
        }

        // Lowers quantity if some sold before price change
        if (Main.buyPriceChng && (Inventory.contains(Main.COWHIDE) || Inventory.contains(Main.COWHIDE+1)))
            buyQuantity -= (Inventory.getCount(true, x -> x != null && x.getId() == Main.COWHIDE) + Inventory.getCount(true, x -> x != null && x.getId() == Main.COWHIDE+1));

        // Buys hides -> having issues with Buraks toBank param so handled manually
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
            Keyboard.pressEnter();
            Time.sleep(1500);
        }

        // Increases buy price if over time
        Main.checkTime();
        if(Main.elapsedSeconds > Main.resetGeTime * 60 && GrandExchange.getFirstActive() != null) {
            Log.fine("Increasing hide price by: " + Main.intervalAmnt);
            while(GrandExchange.getFirstActive() != null) {
                Time.sleepUntil(() -> GrandExchange.getFirst(x -> x != null).abort(), 1000, 5000);
                GrandExchange.collectAll();
                Time.sleep(5000);
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
}
