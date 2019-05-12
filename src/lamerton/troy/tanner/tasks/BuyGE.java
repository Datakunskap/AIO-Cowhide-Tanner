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
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class BuyGE extends Task {

    private int gp = 0;
    private int buyQuantity;
    private int currAmount;

    @Override
    public boolean validate() {
        return Main.sold && Main.restock && Main.location.getGEArea().contains(Players.getLocal()) && !Main.isMuling;
    }

    @Override
    public int execute() {
        gp = Inventory.getCount(true, 995) - 1000;
        if(gp < Main.cowhidePrice) {
            Log.severe("Not enough moneys");
            Main.sold = false;
            Main.restock = false;
        }

        buyQuantity = gp / (Main.cowhidePrice);

        if (!Main.checkedBank) {
            Bank.open(BankLocation.GRAND_EXCHANGE);
            if(Bank.isOpen()) {
                currAmount = Bank.getCount(Main.COWHIDE);
                Bank.close();
                Main.checkedBank = true;
            }
            return 1000;
        }

        Log.fine("Buying hides");
        if (ExGrandExchange.buy(Main.COWHIDE, buyQuantity, Main.cowhidePrice, false)) {
            Time.sleep(600);
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));
            GrandExchange.collectAll();
            Time.sleep(Random.mid(300, 600));

            if (Inventory.contains(Main.COWHIDE) || Inventory.contains(Main.COWHIDE+1)) {
                Main.sold = false;
                Main.restock = false;
            }
        }
        return 1000;
    }
}
