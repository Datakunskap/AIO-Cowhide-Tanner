package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class BankLeatherWithdrawCowhide extends Task {

    private Main taskRunner;
    public BankLeatherWithdrawCowhide(Main taskRunner) {
        this.taskRunner = taskRunner;
    }

    @Override
    public boolean validate() {
        return Conditions.atBank() && !Conditions.gotCowhide() || !Conditions.gotEnoughCoins();
    }

    @Override
    public int execute() {
        if (Bank.isOpen()) {
            Time.sleepUntil(() -> Bank.getCount() > 0, 3000);
            // got something other than coins
            if (Conditions.gotJunkOrLeather()) {
                Time.sleep(100, 300);
                if (Bank.depositAllExcept(995, Main.COWHIDE)) {
                    Time.sleepUntil(() -> !Conditions.gotJunkOrLeather(), 2000);
                }
                return Random.nextInt(100, 220);
            }

            // handle coins
            final Item coinsInventory = Inventory.getFirst("Coins");
            final int coinsAmount = coinsInventory != null ? coinsInventory.getStackSize() : 0;

            if (!Conditions.gotEnoughCoins()) {
                Item coinsInBank = Bank.getFirst("Coins");
                if (coinsInBank == null) {
                    // not enough coins to continue
                    Log.info("Out of coins");
                    this.stopScript();
                    return 2000;
                } else {
                    // need more coins
                    Bank.withdrawAll("Coins");
                    Time.sleepUntil(Conditions::gotEnoughCoins, 2000);
                    return Random.nextInt(200, 420);
                }
            }
            Log.info("Coins left", coinsAmount);

            final Item cowhide = Bank.getFirst(Main.COWHIDE);
            final int cowhideBankAmount = cowhide == null ? 0 : cowhide.getStackSize();

            if (cowhideBankAmount >= 1) {
                // bank has more cowhide, withdraw Cowhide
                if (Bank.withdrawAll(Main.COWHIDE)) {
                    Time.sleepUntil(Conditions::gotCowhide, 2000);
                    return Random.nextInt(80, 160);
                } else {
                    Log.severe("Cowhide withdraw failed, retrying...");
                    return 400;
                }
            } else {
                // not enough cowhide to continue
                Log.info(cowhideBankAmount);
                Log.info("Out of cowhide");
                this.stopScript();
                return 2000;
            }
        } else {
            Bank.open();
            return Random.nextInt(400, 600);
        }
    }

    private void stopScript() {
        // not enough cowhides or gp
        this.taskRunner.setStopping(true);
    }
}
