package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.ExGrandExchange;
import lamerton.troy.tanner.Main;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class SellGE extends Task {

    @Override
    public boolean validate() {
        return Main.restock && !Main.sold && Main.location.getGEArea().contains(Players.getLocal()) && !Main.isMuling;
    }

    @Override
    public int execute() {
        Log.fine("Selling leathers");
        if (!Main.checkedBank) {
            Bank.open(BankLocation.GRAND_EXCHANGE);
            if (Main.location.getGEArea().contains(Players.getLocal()) && Bank.isOpen()) {
                while(Bank.contains(Main.LEATHERS[0])) {
                    Time.sleep(3000);
                    Bank.depositInventory();
                    Time.sleep(3000);
                    Bank.withdrawAll(995);
                    Time.sleep(3000);
                    Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
                    Time.sleep(3000);
                    Bank.withdrawAll(Main.LEATHERS[0]);
                    Time.sleep(3000);
                }
                Bank.close();
                Main.checkedBank = true;
            }
            return 1000;
        }

        if (!ExGrandExchange.sell(Main.LEATHER_NOTE, 0, Main.leatherPrice, false)) {
            Time.sleep(600);
            GrandExchange.collectAll();
            Time.sleep(Random.mid(600, 1000));
            GrandExchange.collectAll();
            Time.sleep(Random.mid(600, 1000));
            GrandExchange.collectAll();
            if (!Inventory.contains(Main.LEATHER_NOTE) && !Inventory.contains(Main.LEATHERS[0])) {
                Main.sold = true;
                Main.checkedBank = false;
                Log.info("Done selling 1");
            }
        } else {
            if (!Inventory.contains(Main.LEATHER_NOTE) && !Inventory.contains(Main.LEATHERS[0])) {
                Main.sold = true;
                Main.checkedBank = false;
                Log.info("Done selling 2");
            }
        }
        return 1000;
    }
}
