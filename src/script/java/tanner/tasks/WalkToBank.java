package script.java.tanner.tasks;

import org.rspeer.ui.Log;
import script.java.tanner.Main;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.script.task.Task;

public class WalkToBank extends Task {
    @Override
    public boolean validate() {
        // True if player is far away from the bank
        return (!CommonConditions.atBank() && (!CommonConditions.gotCowhide() || !CommonConditions.gotEnoughCoins())) &&
                !Main.restock && !Main.isMuling;
    }

    @Override
    public int execute() {
        Log.info("Walking to AK bank");
        if (WalkingHelper.shouldEnableRun()) {
            WalkingHelper.enableRun();
        }
        if (WalkingHelper.shouldSetDestination()) {
            if (Movement.walkToRandomized(BankLocation.AL_KHARID.getPosition())) {
                Time.sleepUntil(CommonConditions::atBank, Random.mid(1800, 2400));
            }
        }
        return 600;
    }
}
