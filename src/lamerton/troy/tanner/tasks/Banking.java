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
            Time.sleep( 1000);
        }
        if (Bank.isOpen()) {
            Bank.depositInventory();
            Time.sleep(2500);

            // Keep X gold for tanning
            Main.gp = Bank.getCount(995);
            Main.gp -= Main.gp / 80;

            Time.sleep(2500);
            Bank.withdrawAll(995);
            Time.sleep(5000);
            if(Bank.contains(2552) || Bank.contains(Main.LEATHERS[0])) {
                Bank.withdrawAll(2552);
                Time.sleep(5000);
                Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
                Time.sleep(5000);
                Bank.withdrawAll(Main.LEATHERS[0]);
                Time.sleep(2000);
            }
        }
        Bank.close();
        Time.sleepUntil(() -> !Bank.isOpen(), 2000);
        return 1000;
    }
}


