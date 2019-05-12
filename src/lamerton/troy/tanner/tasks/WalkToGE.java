package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class WalkToGE extends Task {

    @Override
    public boolean validate() {
        return !Main.location.getGEArea().contains(Players.getLocal()) && Main.restock && !Main.isMuling;
    }

    @Override
    public int execute() {
        if (WalkingHelper.shouldEnableRun()) {
            WalkingHelper.enableRun();
        }

        Log.info("Walking to GE");
        Movement.walkToRandomized(BankLocation.GRAND_EXCHANGE.getPosition());
        return Main.randInt(2000, 4000);
    }
}