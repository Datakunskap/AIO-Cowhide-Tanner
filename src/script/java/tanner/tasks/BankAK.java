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

    private Main main;
    private Banking banking;
    private CommonConditions cc;

    public BankAK (Main main) {
        this.main = main;
        banking = new Banking(main);
        cc = new CommonConditions(main);
    }

    @Override
    public boolean validate() {
        return (!main.restock && !main.isMuling) && (cc.atBank() && !cc.gotCowhide() || !cc.gotEnoughCoins());
    }

    @Override
    public int execute() {
        if (Bank.isOpen()) {
            Time.sleepUntil(() -> Bank.getCount() > 0, 3000);
            // got something other than coins
            if (cc.gotJunkOrLeather()) {
                Time.sleep(100, 300);
                if (Bank.depositAllExcept(995, main.COWHIDE)) {
                    Time.sleepUntil(() -> !cc.gotJunkOrLeather(), 2000);
                }
                return Random.nextInt(100, 220);
            }

            if (!Inventory.contains(995) && Inventory.isFull()) {
                Bank.deposit(main.COWHIDE, 1);
                Time.sleepUntil(() -> !Inventory.isFull(), 5000);
            }

            final int hidesAmount = Bank.getCount(main.COWHIDE);

            if (!cc.gotEnoughCoins()) {
                Item coinsInBank = Bank.getFirst("Coins");
                if (coinsInBank == null) {
                    // not enough coins to continue
                    Log.info("Out of coins");
                    banking.openAndDepositAll();
                    Bank.close();

                    main.checkRestock = true;
                    main.restock = true;
                    return 2000;
                } else {
                    // need more coins
                    Bank.withdrawAll("Coins");
                    Time.sleepUntil(cc::gotEnoughCoins, 2000);
                    return Random.nextInt(200, 420);
                }
            }
            // Log.info("Coins left", coinsAmount);
            Log.info("Hides left", hidesAmount);

            final Item cowhide = Bank.getFirst(main.COWHIDE);
            final int cowhideBankAmount = cowhide == null ? 0 : cowhide.getStackSize();

            if (cowhideBankAmount >= 1) {

                // bank has more cowhide, withdraw Cowhide
                if (Bank.withdrawAll(main.COWHIDE)) {
                    Time.sleepUntil(cc::gotCowhide, 2000);
                    return Random.nextInt(80, 160);
                } else {
                    Log.severe("Hide withdraw failed, retrying...");
                    return 400;
                }
            } else {
                // not enough cowhide to continue
                Log.info(cowhideBankAmount);
                Log.info("Out of hide");
                banking.openAndDepositAll();
                Bank.close();

                main.restock = true;
                main.checkRestock = true;
                return 2000;
            }
        } else {
            Bank.open();
            return Random.nextInt(400, 600);
        }
    }
}
