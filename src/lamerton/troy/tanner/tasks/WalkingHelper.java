package lamerton.troy.tanner.tasks;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;

class WalkingHelper {
    static boolean shouldSetDestination() {
        // small chance to force new destination in case of the rare problem:
        // having a destination set but player is not moving towards it
        // I don't trust Players.getLocal().isMoving() for this
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
        if (Movement.getDestinationDistance() <= Random.nextInt(2,3)) {
            return true;
        }

        return false;
    }

    static boolean shouldEnableRun() {
        if (Movement.isRunEnabled()) {
            return false;
        }
        if (Random.nextInt(1, 5000) == 1) {
            // sometimes I like to random enable run, so my bot should too
            return true;
        }
        return Movement.getRunEnergy() > Random.nextInt(40, 55);
    }

    static boolean enableRun() {
        Movement.toggleRun(true);
        return Time.sleepUntil(Movement::isRunEnabled, 500);
    }
}
