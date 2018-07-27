package SimpleTanner.Tasks;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;

class WalkingHelper {
    static boolean shouldSetDestination() {
        if (!Movement.isDestinationSet()) {
            return true;
        }

        // almost at destination
        if (Movement.getDestinationDistance() <= Random.mid(2,3)) {
            return true;
        }

        // don't NEED to set dest. - but sometimes we will for that sweet anti-pattern
        return  Random.high(1, 10) <= 3;
    }

    static boolean shouldEnableRun() {
        return !Movement.isRunEnabled() && Movement.getRunEnergy() > Random.nextInt(40, 55);
    }

    static boolean enableRun() {
        Movement.toggleRun(true);
        return Time.sleepUntil(Movement::isRunEnabled, 500);
    }
}
