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
    private Main main;
    private Banking banking;

    public BuyGE (Main main) {
        this.main = main;
        banking = new Banking(main);
    }

    @Override
    public boolean validate() {
        return main.sold && main.restock && main.GE_LOCATION.containsPlayer() && !main.isMuling;
    }

    @Override
    public int execute() {
        if (!main.checkedBank) {
            banking.execute();
            main.checkedBank = true;
        }

        if (!GrandExchange.isOpen()) {
            Npc n = Npcs.getNearest(x -> x != null && x.getName().contains("Grand Exchange Clerk"));
            if (n != null) {
                Time.sleepUntil(() -> n.interact("Exchange"), 1000, 10000);
                Time.sleep(700, 1300);
            }
            return 1000;
        }

        if(main.gp < main.cowhidePrice && GrandExchange.getOffers() == null && GrandExchangeSetup.getItem() == null) {
            Log.severe("Not enough moneys");
            main.sold = false;
            main.checkedBank = false;
            main.restock = false;
            main.closeGE();
        }

        // sets quantity to buy
        buyQuantity = main.gp / main.cowhidePrice;

        // Checks if done buying
        if (GrandExchange.getFirstActive() == null && (Inventory.contains(main.COWHIDE) || Inventory.contains(main.COWHIDE+1))) {
            if (Time.sleepUntil(() -> (Inventory.getCount(true, main.COWHIDE) +
                    Inventory.getCount(true, main.COWHIDE+1)) >= buyQuantity, 5000)) {
                Log.fine("Done buying");
                main.sold = false;
                main.checkedBank = false;
                main.restock = false;
                main.closeGE();
                main.startTime = System.currentTimeMillis();
                main.buyPriceChng = false;
                main.incBuyPrice = 0;
                main.timesPriceChanged = 0;
                // Handled manually
                banking.openAndDepositAll();
                return 2000;
            }
        }

        // Lowers quantity if some sold before price change
        if (main.buyPriceChng && (Inventory.contains(main.COWHIDE) || Inventory.contains(main.COWHIDE+1)))
            buyQuantity -= (Inventory.getCount(true, x -> x != null && x.getId() == main.COWHIDE) + Inventory.getCount(true, x -> x != null && x.getId() == main.COWHIDE+1));

        // Buys hides -> having issues with Buraks toBank param so handled manually
        if (GrandExchange.getFirstActive() == null && ExGrandExchange.buy(main.COWHIDE, buyQuantity, (main.cowhidePrice + main.incBuyPrice), false)) {
            Log.fine("Buying Hides");
        } else {
            Log.info("Waiting to complete  |  Time: " + main.elapsedSeconds / 60 + "min(s)  |  Price changed " + main.timesPriceChanged + " time(s)");
            if (!GrandExchange.isOpen()) {
                Npcs.getNearest("Grand Exchange Clerk").interact("Exchange");
                Time.sleep(main.randInt(700, 1300));
            }
            Time.sleepUntil(() -> GrandExchange.getFirst(x -> x != null).getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED), 2000, 10000);
            GrandExchange.collectAll();
            Keyboard.pressEnter();
            Time.sleep(1500);
        }

        // Increases buy price if over time
        main.checkTime();
        if(main.elapsedSeconds > main.resetGeTime * 60 && GrandExchange.getFirstActive() != null) {
            Log.fine("Increasing hide price by: " + main.intervalAmnt);
            while(GrandExchange.getFirstActive() != null) {
                Time.sleepUntil(() -> GrandExchange.getFirst(x -> x != null).abort(), 1000, 5000);
                GrandExchange.collectAll();
                Time.sleep(5000);
                GrandExchange.collectAll();
            }
            main.incBuyPrice += main.intervalAmnt;
            main.setPrices();
            main.startTime = System.currentTimeMillis();
            main.buyPriceChng = true;
            main.timesPriceChanged++;
        }
        GrandExchange.collectAll();
        return 1000;
    }
}
