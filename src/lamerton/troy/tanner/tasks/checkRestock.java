package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class checkRestock extends Task {

    private Main taskRunner;

    public checkRestock(Main taskRunner) {
        this.taskRunner = taskRunner;
    }

    @Override
    public boolean validate() {
        return Main.checkRestock;
    }

    @Override
    public int execute() {
        Log.info("Restock check");
        boolean checkW = false;
        boolean checkD = false;
        boolean checkH = false;

        if (Inventory.contains(11980)) {
            checkW = true;
        }
        if (Inventory.contains(2552)) {
            checkD = true;
        }
        if (Inventory.contains(Main.COWHIDE) || Inventory.contains(Main.COWHIDE + 1)) {
            checkH = true;
        }

        while (!Bank.isOpen()) {
            Bank.open();
            Time.sleep(1000);
        }
        if (!checkW && !Bank.contains(11980)) {
            Main.restock = true;
            Main.newRingW = true;
            checkW = true;
        }
        if (!checkD && !Bank.contains(2552)) {
            Main.restock = true;
            Main.newRingD = true;
            checkD = true;
        }
        if (!checkH && Bank.contains(Main.COWHIDE) || Bank.contains(Main.COWHIDE + 1)) {
            Main.restock = false;
            Main.checkRestock = false;
            checkH = true;
            taskRunner.remove(this);
        }
        if (!checkH || !checkD || !checkW) {
            Log.fine("Restocking");
            Main.restock = true;
            Main.checkRestock = false;
            taskRunner.remove(this);
        }

        return 1000;
    }
}
