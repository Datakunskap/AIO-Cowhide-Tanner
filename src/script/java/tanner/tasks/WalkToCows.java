package script.java.tanner.tasks;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.java.tanner.Main;

public class WalkToCows extends Task {

    @Override
    public boolean validate() {
        return !Main.COW_LOCATION.containsPlayer() && Main.restock &&
                !Main.isMuling && (Main.killCows || Main.lootCows);
    }

    @Override
    public int execute() {
        if (WalkingHelper.shouldEnableRun()) {
            WalkingHelper.enableRun();
        }

        Log.info("Walking to cows");
        if (WalkingHelper.shouldSetDestination()) {
            if (Movement.walkToRandomized(Main.COW_LOCATION.getCowArea()[0].getCenter())) {
                Time.sleepUntil(() -> Main.COW_LOCATION.containsPlayer(), Random.mid(1800, 2400));
            }
        }
        return 600;
    }
}
