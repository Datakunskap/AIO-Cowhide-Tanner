package script.java.tanner.tasks;

import script.java.tanner.Main;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.ui.Log;

public class Banking {

    public static int execute() {
        Log.info("Banking");
        openAndDepositAll();

        calcSpendAmount();

        // Withdraw GP
        Time.sleep(500, 1500);
        Bank.withdrawAll(995);
        Time.sleepUntil(() -> !Bank.contains(995), 5000);

        // Withdraw leathers to sell
        if (Bank.contains(Main.LEATHERS[0])) {
            Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
            Time.sleepUntil(() -> Bank.getWithdrawMode().equals(Bank.WithdrawMode.NOTE), 5000);
            Bank.withdrawAll(Main.LEATHERS[0]);
            Time.sleepUntil(() -> !Bank.contains(Main.LEATHERS[0]), 5000);
        }

        // Withdraw leftover hides to sell
        if (Bank.contains(Main.COWHIDE)) {
            Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
            Time.sleepUntil(() -> Bank.getWithdrawMode().equals(Bank.WithdrawMode.NOTE), 5000);
            Bank.withdrawAll(Main.COWHIDE);
            Time.sleepUntil(() -> !Bank.contains(Main.COWHIDE), 5000);
        }

        Bank.close();
        Time.sleepUntil(() -> !Bank.isOpen(), 2000);

        Main.startTime = System.currentTimeMillis();
        return 1000;
    }

    private static void calcSpendAmount() {
        // Calculate GP to spend
        Main.gp = Bank.getCount(995);
        Main.setPrices();

        // Keep X gp for tanning
        int tanningGp = Main.gp / Main.cowhidePrice;
        Main.gp -= tanningGp;
    }

    public static void openAndDepositAll() {
        while (!Bank.isOpen()) {
            Bank.open();
            Time.sleep(1000);
        }

        Bank.depositInventory();
        Time.sleepUntil(() -> Inventory.isEmpty(), 5000);

        if (Main.killCows && Main.foodAmnt > 0 &&
                Bank.contains(Main.food) && !Inventory.contains(Main.food)) {
            Bank.withdraw(Main.food, Main.foodAmnt);
            Time.sleepUntil(() -> Inventory.contains(Main.food), 5000);
        }
        Main.cowHideCount = Bank.getCount(Main.COWHIDE);
    }
}


