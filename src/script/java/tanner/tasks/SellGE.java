package script.java.tanner.tasks;

import script.java.tanner.Main;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import java.util.Objects;

public class SellGE extends Task {

    private Main main;
    private Banking banking;

    public SellGE (Main main) {
        this.main = main;
        banking = new Banking(main);
    }

    @Override
    public boolean validate() {
        return main.restock && !main.sold && main.GE_LOCATION.containsPlayer() && !main.isMuling;
    }

    @Override
    public int execute() {
        if (!main.checkedBank) {
            banking.execute();
            main.checkedBank = true;
        }

        if (!GrandExchange.isOpen()) {
            Log.fine("Selling");
            Npc n = Npcs.getNearest(x -> x != null && x.getName().contains("Grand Exchange Clerk"));
            if (n != null) {
                Time.sleepUntil(() -> n.interact("Exchange"), 1000, 10000);
                Time.sleep(700, 1300);
            }
            return 1000;
        }

        if (GrandExchangeSetup.getItem() == null) {
            main.geSet = false;
        }

        // needs older account
        /*if (!sellRemainingHides()) {
            GrandExchange.collectAll();
            return 1000;
        }*/

        // bc issues with Buraks ExGrandExchange when selling
        if (Inventory.contains(main.LEATHER_NOTE)) {
            Log.fine("Selling Leathers");
            GrandExchange.createOffer(RSGrandExchangeOffer.Type.SELL);
            Time.sleep(800);
            GrandExchangeSetup.setItem(main.LEATHER_NOTE);
            Time.sleep(600);
            GrandExchangeSetup.setPrice(main.leatherPrice - main.decSellPrice);
            Time.sleep(600);
            GrandExchangeSetup.setQuantity(9999999);
            Time.sleep(600);
            GrandExchangeSetup.confirm();
            Time.sleep(600);
            if(GrandExchangeSetup.getItem() != null) {
                main.geSet = true;
            }

            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            Keyboard.pressEnter();
            main.startTime = System.currentTimeMillis();
        }

        if (GrandExchange.getFirst(Objects::nonNull).getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED) &&
                !Inventory.contains(main.LEATHER_NOTE) && !Inventory.contains(main.LEATHER)) {
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));

            Log.info("Done selling");
            main.sold = true;
            main.checkedBank = false;
            main.geSet = false;
            main.decSellPrice = 0;
            main.timesPriceChanged = 0;
            main.setHighestProfitLeather();
        }

        if (GrandExchange.getFirstActive() == null && !GrandExchange.getFirst(Objects::nonNull).getProgress().equals(RSGrandExchangeOffer.Progress.FINISHED) &&
                !Inventory.contains(main.LEATHER_NOTE) && !Inventory.contains(main.LEATHER)){
            Log.info("Done selling 2");
            main.sold = true;
            main.checkedBank = false;
            main.geSet = false;
            main.decSellPrice = 0;
            main.timesPriceChanged = 0;
            main.setHighestProfitLeather();
        }

        main.checkTime();
        Log.info( "Waiting to complete  |  Time: " + main.elapsedSeconds / 60 + "min(s)  |  Price changed " + main.timesPriceChanged + " time(s)");
        if(main.elapsedSeconds > main.resetGeTime * 60 &&
                GrandExchange.getFirstActive() != null) {
            Log.fine("Decreasing leather price by: " + main.intervalAmnt);
            while(!Inventory.contains(main.LEATHER) && GrandExchange.getFirstActive() != null) {
                Time.sleepUntil(() -> GrandExchange.getFirst(Objects::nonNull).abort(), 1000, 5000);
                GrandExchange.collectAll();
                Time.sleep(5000);
                GrandExchange.collectAll();
            }
            main.decSellPrice += main.intervalAmnt;
            main.setPrices();
            main.startTime = System.currentTimeMillis();
            main.timesPriceChanged++;
        }

        // Checks and handles stuck in setup
        if (main.elapsedSeconds > (main.resetGeTime + 1) * 60 && GrandExchange.getFirstActive() == null && GrandExchangeSetup.isOpen()) {
            GrandExchange.open(GrandExchange.View.OVERVIEW);
            if (!Time.sleepUntil(() -> !GrandExchangeSetup.isOpen(), 5000)) {
                main.closeGE();
            }
        }

        GrandExchange.collectAll();
        Keyboard.pressEnter();
        return 1000;
    }
}
