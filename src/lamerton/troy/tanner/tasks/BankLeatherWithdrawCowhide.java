package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.LeatherTanner;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class BankLeatherWithdrawCowhide extends Task {

    private LeatherTanner taskRunner;
    public BankLeatherWithdrawCowhide(LeatherTanner taskRunner) {
        this.taskRunner = taskRunner;
    }

    @Override
    public boolean validate() {
        return !Conditions.gotCowhide() || !Conditions.gotEnoughCoins();
    }

    @Override
    public int execute() {
        if (Bank.isOpen()) {
            // got something other than coins
            if (Conditions.gotJunkOrLeather()) {
                Time.sleep(200, 400);
                Bank.depositAllExcept(995, LeatherTanner.COWHIDE);
                Time.sleepUntil(() -> !Conditions.gotJunkOrLeather(), 2000);
            }

            // handle coins
            final Item coinsInventory = Inventory.getFirst("Coins");
            final int coinsAmount = coinsInventory != null ? coinsInventory.getStackSize() : 0;

            if (!Conditions.gotEnoughCoins()) {
                Item coinsInBank = Bank.getFirst("Coins");
                if (coinsInBank == null) {
                    // not enough coins to continue
                    this.stopScript();
                    return 2000;
                } else {
                    // need more coins
                    Bank.withdrawAll("Coins");
                    Time.sleepUntil(Conditions::gotEnoughCoins, 2000);
                }
            }

            final Item cowhide = Bank.getFirst(LeatherTanner.COWHIDE);
            final int cowhideBankAmount = cowhide == null ? 0 : cowhide.getStackSize();

            if (cowhideBankAmount >= 1) {
                // bank has more cowhide, withdraw Cowhide
                if (Bank.withdrawAll(LeatherTanner.COWHIDE)) {
                    Time.sleepUntil(Conditions::gotCowhide, 2000);
                }
            } else {
                // not enough cowhide to continue
                Log.info(coinsAmount);
                Log.info(cowhideBankAmount);
                this.stopScript();
                return 2000;
            }
        } else {
            Bank.open();
        }

        return 600;
    }

    private void stopScript() {
        // not enough cowhides or gp
        Log.info("Finished tanning all cowhides!");
        this.taskRunner.setStopping(true);
    }
}
