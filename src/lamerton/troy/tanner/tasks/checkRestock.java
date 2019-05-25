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
        Log.info("Checking if need to restock");
        if (Inventory.contains(Main.COWHIDE) || Inventory.contains(Main.COWHIDE+1)){
            Main.restock = false;
            Main.checkRestock = false;
            taskRunner.remove(this);
        } else {
            while (!Bank.isOpen()) {
                Bank.open();
                Time.sleep(1000);
            }
            if (Bank.contains(Main.COWHIDE) || Bank.contains(Main.COWHIDE + 1)) {
                Main.restock = false;
                Main.checkRestock = false;
                taskRunner.remove(this);
            } else {
                Log.fine("Restocking");
                Main.restock = true;
                Main.checkRestock = false;
                taskRunner.remove(this);
            }
        }
        return 1000;
    }
}
