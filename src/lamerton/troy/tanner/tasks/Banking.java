package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
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
            Time.sleep(4000);

            Main.gp = Bank.getCount(995);
            // Keep X gp for tanning
            if (Main.COWHIDE != 1739) {
                int tanningGp = (Main.gp / Main.cowhidePrice) * 20;
                if (tanningGp > 90000) {
                    tanningGp -= 25000;
                }
                Main.gp -= tanningGp;
            }

            Time.sleep(1000);
            Bank.withdrawAll(995);
            Time.sleep(5000);
            Bank.withdraw(x -> x != null && x.getName().contains("Ring of dueling") && x.getName().matches(".*\\d+.*"), 1);
            Time.sleep(5000);
            if (Bank.contains(Main.LEATHERS[0])) {
                Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
                Time.sleep(5000);
                Bank.withdrawAll(Main.LEATHERS[0]);
                Time.sleep(2000);
            }
        }
        Bank.close();
        Time.sleepUntil(() -> !Bank.isOpen(), 2000);
        Main.setPrices();
        Main.startTime = System.currentTimeMillis();
        return 1000;
    }
}


