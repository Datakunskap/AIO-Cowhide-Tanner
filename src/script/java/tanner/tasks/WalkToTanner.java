package script.java.tanner.tasks;

import org.rspeer.ui.Log;
import script.java.tanner.Main;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.script.task.Task;

public class WalkToTanner extends Task {

    private Main main;
    private CommonConditions cc;

    public WalkToTanner (Main main) {
        this.main = main;
        cc = new CommonConditions(main);
    }

    @Override
    public boolean validate() {
        // True if player is far away from the tanner
        return (!cc.nearTanner() && cc.gotCowhide() && cc.gotEnoughCoins()) &&
                !main.restock && !main.isMuling;
    }

    @Override
    public int execute() {
        Log.info("Walking to tanner");
        if (WalkingHelper.shouldEnableRun()) {
            WalkingHelper.enableRun();
        }
        if (WalkingHelper.shouldSetDestination()) {
            if (Movement.walkToRandomized(main.TANNER_AREA.getCenter())) {
                Time.sleepUntil(cc::nearTanner, Random.mid(1800, 2400));
            }
        }
        return 600;
    }
}
