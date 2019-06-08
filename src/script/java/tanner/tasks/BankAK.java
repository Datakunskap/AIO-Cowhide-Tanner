package script.java.tanner.tasks;

import org.rspeer.runetek.api.component.tab.Inventory;
import script.java.tanner.Main;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class BankAK extends Task {

    @Override
    public boolean validate() {
        return (!Main.restock && !Main.isMuling) && (CommonConditions.atBank() && !CommonConditions.gotCowhide() || !CommonConditions.gotEnoughCoins());
    }

    @Override
    public int execute() {
        if (Bank.isOpen()) {
            Time.sleepUntil(() -> Bank.getCount() > 0, 3000);
            // got something other than coins
            if (CommonConditions.gotJunkOrLeather()) {
                Time.sleep(100, 300);
                if (Bank.depositAllExcept(995, Main.COWHIDE)) {
                    Time.sleepUntil(() -> !CommonConditions.gotJunkOrLeather(), 2000);
                }
                return Random.nextInt(100, 220);
            }

            if (!Inventory.contains(995) && Inventory.isFull()) {
                Bank.deposit(Main.COWHIDE, 1);
                Time.sleepUntil(() -> !Inventory.isFull(), 5000);
            }

            final int hidesAmount = Bank.getCount(Main.COWHIDE);

            if (!CommonConditions.gotEnoughCoins()) {
                Item coinsInBank = Bank.getFirst("Coins");
                if (coinsInBank == null) {
                    // not enough coins to continue
                    Log.info("Out of coins");
                    Banking.openAndDepositAll();
                    Bank.close();

                    Main.checkRestock = true;
                    Main.restock = true;
                    return 2000;
                } else {
                    // need more coins
                    Bank.withdrawAll("Coins");
                    Time.sleepUntil(CommonConditions::gotEnoughCoins, 2000);
                    return Random.nextInt(200, 420);
                }
            }
            // Log.info("Coins left", coinsAmount);
            Log.info("Hides left", hidesAmount);

            final Item cowhide = Bank.getFirst(Main.COWHIDE);
            final int cowhideBankAmount = cowhide == null ? 0 : cowhide.getStackSize();

            if (cowhideBankAmount >= 1) {

                // bank has more cowhide, withdraw Cowhide
                if (Bank.withdrawAll(Main.COWHIDE)) {
                    Time.sleepUntil(CommonConditions::gotCowhide, 2000);
                    return Random.nextInt(80, 160);
                } else {
                    Log.severe("Hide withdraw failed, retrying...");
                    return 400;
                }
            } else {
                // not enough cowhide to continue
                Log.info(cowhideBankAmount);
                Log.info("Out of hide");
                Banking.openAndDepositAll();
                Bank.close();

                Main.restock = true;
                Main.checkRestock = true;
                return 2000;
            }
        } else {
            Bank.open();
            return Random.nextInt(400, 600);
        }
    }
}
