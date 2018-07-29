package SimpleTanner.Tasks;

import SimpleTanner.LeatherTanner;
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
            Time.sleep(200, 400);
            // deposit all except coins
            Bank.depositAllExcept(995);
            Time.sleepUntil(() -> Inventory.getCount() <= 1, 2000);

            // check that there is enough cowhides and gp
            Item coinsInBank = Bank.getFirst("Coins");
            Item coinsInInventory = Inventory.getFirst("Coins");

            int coinsAmount = coinsInInventory != null ? coinsInInventory.getStackSize() : 0;

            if (coinsInBank != null) {
                coinsAmount += coinsInBank.getStackSize();
                Bank.withdrawAll("Coins");
                Time.sleepUntil(() -> !Bank.contains("Coins"), 2000);
                Time.sleep(100, 200);
            }

            Item cowhide = Bank.getFirst(LeatherTanner.COWHIDE);

            int cowhideAmount = cowhide == null ? 0 : cowhide.getStackSize();

            if (cowhideAmount >= 1 && coinsAmount >= cowhideAmount) {
                // withdraw Cowhide
                if (Bank.withdrawAll(LeatherTanner.COWHIDE)) {
                    Time.sleepUntil(Conditions::gotCowhide, 8000);
                }
            } else {
                // not enough cowhides or gp
                Log.info("Finished tanning all cowhides!");
                int[] debugStats = {coinsAmount, cowhideAmount};
                Log.info(debugStats);
                this.taskRunner.setStopping(true);
            }
        } else {
            Bank.open();
        }

        return 600;
    }
}
