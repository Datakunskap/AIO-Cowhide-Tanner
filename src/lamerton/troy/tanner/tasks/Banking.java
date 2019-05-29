package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.ui.Log;

public class Banking {//extends Task {

//    @Override
//    public boolean validate() {
//        return Main.restock && !Main.checkedBank && Main.location.getGEArea().contains(Players.getLocal()) && !Main.isMuling;
//    }

    public static int execute() {
        Log.info("Banking");
        while (!Bank.isOpen()) {
            Bank.open(BankLocation.GRAND_EXCHANGE);
            Time.sleep(1000);
        }
        if (Bank.isOpen()) {
            Bank.depositInventory();
            Time.sleepUntil(() -> Inventory.isEmpty(), 5000);

            // Calculate GP to spend
            Main.gp = Bank.getCount(995);
            Main.setPrices();
            // Keep X gp for tanning
            if (Main.COWHIDE != 1739) {
                int tanningGp = (Main.gp / Main.cowhidePrice) * 20;
                Main.gp -= tanningGp;
                Main.gp -= (Main.numStamina * Main.priceStamina);
            }

            Time.sleep(500, 1500);
            Bank.withdrawAll(995);
            Time.sleepUntil(() -> Inventory.contains(995), 5000);
            Bank.withdraw(x -> x != null && x.getName().contains("Ring of dueling") && x.getName().matches(".*\\d+.*"), 1);
            Time.sleepUntil(() -> Inventory.contains(x -> x != null && x.getName().contains("Ring of dueling") && x.getName().matches(".*\\d+.*")), 5000);
            // Withdraw leathers to sell
            if (Bank.contains(Main.LEATHERS[0])) {
                Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
                Time.sleepUntil(() -> Bank.getWithdrawMode().equals(Bank.WithdrawMode.NOTE), 5000);
                Bank.withdrawAll(Main.LEATHERS[0]);
                Time.sleepUntil(() -> Inventory.contains(Main.LEATHERS[0]), 5000);
            }

            // Withdraw leftover hides to sell
            if (Bank.contains(Main.COWHIDE)) {
                Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
                Time.sleepUntil(() -> Bank.getWithdrawMode().equals(Bank.WithdrawMode.NOTE), 5000);
                Bank.withdrawAll(Main.COWHIDE);
                Time.sleepUntil(() -> Inventory.contains(Main.COWHIDE), 5000);
            }
        }
        Bank.close();
        Time.sleepUntil(() -> !Bank.isOpen(), 2000);

        Main.startTime = System.currentTimeMillis();
        return 1000;
    }
}


