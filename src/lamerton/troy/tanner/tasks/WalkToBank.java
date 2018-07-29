package lamerton.troy.tanner.tasks;

import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.script.task.Task;

public class WalkToBank extends Task {
    @Override
    public boolean validate() {
        // True if player is far away from the bank
        return !Conditions.atBank() && (!Conditions.gotCowhide() || !Conditions.gotEnoughCoins());
    }

    @Override
    public int execute() {
        // walk to Al Kharid bank
        if (WalkingHelper.shouldEnableRun()) {
            WalkingHelper.enableRun();
        }
        if (WalkingHelper.shouldSetDestination()) {
            if (Movement.walkToRandomized(BankLocation.AL_KHARID.getPosition())) {
                Time.sleepUntil(Conditions::atBank, Random.mid(1800, 2400));
            }
        }
        return 600;
    }
}
