package SimpleTanner.Tasks;

import SimpleTanner.LeatherTanner;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.script.task.Task;

public class WalkToTanner extends Task {

    @Override
    public boolean validate() {
        // True if player is far away from the tanner
        return !Conditions.nearTanner() && Conditions.gotCowhide() && Conditions.gotEnoughCoins();
    }

    @Override
    public int execute() {
        if (WalkingHelper.shouldEnableRun()) {
            WalkingHelper.enableRun();
        }
        if (WalkingHelper.shouldSetDestination()) {
            if (Movement.walkToRandomized(LeatherTanner.TANNER_AREA.getCenter())) {
                Time.sleepUntil(Conditions::nearTanner, Random.mid(1800, 2400));
            }
        }
        return 600;
    }
}
