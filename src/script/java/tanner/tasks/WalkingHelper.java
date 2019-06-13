package script.java.tanner.tasks;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;

class WalkingHelper {
    static boolean shouldSetDestination() {
        // small chance to force new destination in case of the rare problem:
        // having a destination set but player is not moving towards it
        if (Random.nextInt(1, 200) == 1) {
            return true;
        }

        if (!Players.getLocal().isMoving()) {
            return true;
        }

        if (!Movement.isDestinationSet()) {
            return true;
        }

        // almost at destination
        return Movement.getDestinationDistance() <= Random.nextInt(2,3);
    }

    static boolean shouldEnableRun() {
        if (Movement.isRunEnabled()) {
            return false;
        }
        if (Random.nextInt(1, 1000) == 1) {
            // sometimes I like to random enable run
            return true;
        }
        return Movement.getRunEnergy() > Random.nextInt(4, 25);
    }

    static void enableRun() {
        Movement.toggleRun(true);
        Time.sleepUntil(Movement::isRunEnabled, 500);
    }
}
