package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import java.io.IOException;

public class BankLeatherWithdrawCowhide extends Task {

    private Main taskRunner;

    public BankLeatherWithdrawCowhide(Main taskRunner) {
        this.taskRunner = taskRunner;
    }

    @Override
    public boolean validate() {
        return (!Main.restock && !Main.isMuling) && (Conditions.atBank() && !Conditions.gotCowhide() || !Conditions.gotEnoughCoins());
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

            // Coins and Hides amount
            // final int coinsAmount = Inventory.getCount(true, 996);
            final int hidesAmount = Bank.getCount(Main.COWHIDE);

            if (!Conditions.gotEnoughCoins()) {
                Item coinsInBank = Bank.getFirst("Coins");
                if (coinsInBank == null) {
                    // not enough coins to continue
                    Log.info("Out of coins");
                    if (!Bank.contains(x -> x != null && x.getName().contains("wealth") && x.getName().matches(".*\\d+.*")) &&
                            !Inventory.contains(x -> x != null && x.getName().contains("wealth") && x.getName().matches(".*\\d+.*"))) {
                        Main.newRingW = true;
                    } else {
                        Log.info("Getting Ring of wealth");
                        Bank.withdraw(x -> x != null && x.getName().contains("wealth") && x.getName().matches(".*\\d+.*"), 1);
                        Time.sleep(3000);
                    }
                    // Log.fine("Restocking");
                    Bank.close();

                    Main.checkRestock = true;
                    Main.restock = true;
                    return 2000;
                } else {
                    // need more coins
                    Bank.withdrawAll("Coins");
                    Time.sleepUntil(Conditions::gotEnoughCoins, 2000);
                    return Random.nextInt(200, 420);
                }
            }
            // Log.info("Coins left", coinsAmount);
            Log.info("Hides left", hidesAmount);

            final Item cowhide = Bank.getFirst(Main.COWHIDE);
            final int cowhideBankAmount = cowhide == null ? 0 : cowhide.getStackSize();

            if (cowhideBankAmount >= 1) {
                // Drink Stamina potion
                if(Bank.contains(Main.staminaNames) && Main.shouldDrinkPotion()){
                    Bank.withdraw(x -> x != null && x.getName().contains("Stamina") && x.getName().matches(".*\\d+.*"), 1);
                    Time.sleepUntil(() -> Inventory.contains(Main.staminaNames), 5000);
                    Bank.close();
                    Time.sleepUntil(() -> Bank.isClosed(), 5000);
                    Main.drinkStaminaPotion();
                    Bank.open();
                    Time.sleepUntil(() -> Bank.isOpen(), 5000);
                    Bank.depositAll(Main.staminaNames);
                    Time.sleepUntil(() -> !Inventory.contains(Main.staminaNames), 5000);
                    if (!Bank.contains(Main.staminaNames) && Main.numStamina > 0 && Main.smartPotions)
                        Main.numStamina++;
                }

                // bank has more cowhide, withdraw Cowhide
                if (Bank.withdrawAll(Main.COWHIDE)) {
                    Time.sleepUntil(Conditions::gotCowhide, 2000);
                    return Random.nextInt(80, 160);
                } else {
                    Log.severe("Dragonhide withdraw failed, retrying...");
                    return 400;
                }
            } else {
                // not enough cowhide to continue
                Log.info(cowhideBankAmount);
                Log.info("Out of dragonhide");
                if (!Bank.contains(x -> x != null && x.getName().contains("wealth") && x.getName().matches(".*\\d+.*")) &&
                        !Inventory.contains(x -> x != null && x.getName().contains("wealth") && x.getName().matches(".*\\d+.*"))) {
                    Main.newRingW = true;
                } else {
                    Log.info("Getting Ring of wealth");
                    Bank.withdraw(x -> x != null && x.getName().contains("wealth") && x.getName().matches(".*\\d+.*"), 1);
                    Time.sleep(3000);
                }
               // Log.fine("Restocking");
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

    private void stopScript() {
        // not enough cowhides or gp
        this.taskRunner.setStopping(true);
    }
}
