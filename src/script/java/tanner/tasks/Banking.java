package script.java.tanner.tasks;

import script.java.tanner.Main;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.ui.Log;

public class Banking {

    private Main main;

    Banking(Main main) {
        this.main = main;
    }

    public int execute() {
        Log.info("Banking");
        openAndDepositAll();

        calcSpendAmount();

        // Withdraw GP
        Time.sleep(500, 1500);
        Bank.withdrawAll(995);
        Time.sleepUntil(() -> !Bank.contains(995), 5000);

        // Withdraw leathers to sell
        if (Bank.contains(main.LEATHER)) {
            Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
            Time.sleepUntil(() -> Bank.getWithdrawMode().equals(Bank.WithdrawMode.NOTE), 5000);
            Bank.withdrawAll(main.LEATHER);
            Time.sleepUntil(() -> !Bank.contains(main.LEATHER), 5000);
        }

        // Withdraw leftover hides to sell
        if (Bank.contains(main.COWHIDE)) {
            Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
            Time.sleepUntil(() -> Bank.getWithdrawMode().equals(Bank.WithdrawMode.NOTE), 5000);
            Bank.withdrawAll(main.COWHIDE);
            Time.sleepUntil(() -> !Bank.contains(main.COWHIDE), 5000);
        }

        Bank.close();
        Time.sleepUntil(() -> !Bank.isOpen(), 2000);

        main.startTime = System.currentTimeMillis();
        return 1000;
    }

    private void calcSpendAmount() {
        // Calculate GP to spend
        main.gp = Bank.getCount(995);
        main.setPrices();

        // Keep X gp for tanning
        int tanningGp = main.gp / main.cowhidePrice;
        main.gp -= tanningGp;
    }

    public void openAndDepositAll() {
        while (!Bank.isOpen()) {
            Bank.open();
            Time.sleep(1000);
        }

        Bank.depositInventory();
        Time.sleepUntil(() -> Inventory.isEmpty(), 5000);

        if (main.killCows && main.foodAmnt > 0 &&
                Bank.contains(main.food) && !Inventory.contains(main.food)) {
            Bank.withdraw(main.food, main.foodAmnt);
            Time.sleepUntil(() -> Inventory.contains(main.food), 5000);
        }
        main.cowHideCount = Bank.getCount(main.COWHIDE);
    }
}


