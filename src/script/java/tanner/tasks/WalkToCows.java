package script.java.tanner.tasks;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.java.tanner.Main;

public class WalkToCows extends Task {

    private Main main;

    public WalkToCows (Main main) {
        this.main = main;
    }

    @Override
    public boolean validate() {
        return !main.COW_LOCATION.containsPlayer() && main.restock &&
                !main.isMuling && (main.killCows || main.lootCows);
    }

    @Override
    public int execute() {
        if (WalkingHelper.shouldEnableRun()) {
            WalkingHelper.enableRun();
        }

        Log.info("Walking to cows");
        if (WalkingHelper.shouldSetDestination()) {
            if (Movement.walkToRandomized(main.COW_LOCATION.getCowArea()[0].getCenter())) {
                Time.sleepUntil(() -> main.COW_LOCATION.containsPlayer(), Random.mid(1800, 2400));
            }
        }
        return 600;
    }
}
