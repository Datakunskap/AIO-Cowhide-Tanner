package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import java.io.IOException;

public class SellGE extends Task {

    @Override
    public boolean validate() {
        return Main.restock && !Main.sold && Main.location.getGEArea().contains(Players.getLocal()) && !Main.isMuling;
    }

    @Override
    public int execute() {
        if (!Main.checkedBank) {
            Banking.execute();
            Main.checkedBank = true;
        }

        if (!GrandExchange.isOpen()) {
            Log.fine("Selling");
            Npc n = Npcs.getNearest(x -> x != null && x.getName().contains("Grand Exchange Clerk"));
            if (n != null) {
                Time.sleepUntil(() -> n == null || n.interact("Exchange"), 1000, 10000);
                Time.sleep(700, 1300);
            }
            return 1000;
        }

        if (GrandExchangeSetup.getItem() == null) {
            Main.geSet = false;
        }

        if (!sellRemainingHides()) {
            GrandExchange.collectAll();
            return 1000;
        }

        if (Inventory.contains(Main.LEATHER_NOTE)) {
            Log.fine("Selling Leathers");
            GrandExchange.createOffer(RSGrandExchangeOffer.Type.SELL);
            Time.sleep(800);
            GrandExchangeSetup.setItem(Main.LEATHER_NOTE);
            Time.sleep(600);
            GrandExchangeSetup.setPrice(Main.leatherPrice - Main.decSellPrice);
            Time.sleep(600);
            GrandExchangeSetup.setQuantity(9999999);
            Time.sleep(600);
            GrandExchangeSetup.confirm();
            Time.sleep(600);
            if(GrandExchangeSetup.getItem() != null) {
                Main.geSet = true;
            }

            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            Keyboard.pressEnter();
        }

        boolean maxHideSet = false;
        if (GrandExchange.getFirst(x -> x != null).getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED) &&
                !Inventory.contains(Main.LEATHER_NOTE) && !Inventory.contains(Main.LEATHERS[0])) {
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));

            Log.info("Done selling");
            Main.sold = true;
            Main.checkedBank = false;
            Main.geSet = false;
            Main.decSellPrice = 0;
            Main.timesPriceChanged = 0;

            if (Main.restockMaxProfitHide) {
                Log.fine("Calculating most profitable hide...");
                try {
                    Main.setMaxProfitHide();
                    maxHideSet = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (GrandExchange.getFirstActive() == null && !GrandExchange.getFirst(x -> x != null).getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED) &&
                !Inventory.contains(Main.LEATHER_NOTE) && !Inventory.contains(Main.LEATHERS[0])){
            Log.info("Done selling 2");
            Main.sold = true;
            Main.checkedBank = false;
            Main.geSet = false;
            Main.decSellPrice = 0;
            Main.timesPriceChanged = 0;

            if (!maxHideSet && Main.calcMacProfitOnStart && Main.restockMaxProfitHide) {
                Log.fine("Calculating most profitable hide...");
                try {
                    Main.setMaxProfitHide();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Main.calcMacProfitOnStart = false;
            }
        }

        Main.checkTime();
        Log.info( "Waiting to complete  |  Time: " + Main.elapsedSeconds / 60 + "min(s)  |  Price changed " + Main.timesPriceChanged + " time(s)");
        if(Main.elapsedSeconds > Main.resetGeTime * 60 &&
                GrandExchange.getFirstActive() != null) {
            Log.fine("Decreasing leather price by: " + Main.intervalAmnt);
            while(!Inventory.contains(Main.LEATHERS[0]) && GrandExchange.getFirstActive() != null) {
                Time.sleepUntil(() -> GrandExchange.getFirst(x -> x != null).abort(), 1000, 5000);
                GrandExchange.collectAll();
                Time.sleep(5000);
                GrandExchange.collectAll();
            }
            Main.decSellPrice += Main.intervalAmnt;
            Main.setPrices();
            Main.startTime = System.currentTimeMillis();
            Main.timesPriceChanged++;
        }

        GrandExchange.collectAll();
        Keyboard.pressEnter();
        return 1000;
    }

    private boolean sellRemainingHides() {
        if (Inventory.contains(Main.COWHIDE+1)) {
            Log.fine("Selling Remaining Hides");
            GrandExchange.createOffer(RSGrandExchangeOffer.Type.SELL);
            Time.sleep(800);
            GrandExchangeSetup.setItem(Main.COWHIDE + 1);
            Time.sleep(600);
            GrandExchangeSetup.setPrice(Main.cowhideSellPrice - 25);
            Time.sleep(600);
            GrandExchangeSetup.setQuantity(9999999);
            Time.sleep(600);
            GrandExchangeSetup.confirm();
            Time.sleep(600);

            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            Keyboard.pressEnter();

            if (GrandExchange.getFirst(x -> x != null).getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED) && !Inventory.contains(Main.COWHIDE + 1)) {
                GrandExchange.collectAll();
                Time.sleep(Random.mid(300, 600));
                GrandExchange.collectAll();
                Time.sleep(Random.mid(300, 600));
                return true;
            } else {
                return false;
            }
        }

        if (GrandExchange.getFirst(x -> x != null && x.getItemId() == Main.COWHIDE) != null) {
            return false;
        } else {
            GrandExchange.collectAll();
            return true;
        }
    }
}
