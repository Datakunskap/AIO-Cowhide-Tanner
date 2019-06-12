package script.java.tanner.tasks;

import script.java.tanner.Main;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class WalkToGE extends Task {

    private Main main;
    private CommonConditions cc;

    public WalkToGE (Main main) {
        this.main = main;
        cc = new CommonConditions(main);
    }

    @Override
    public boolean validate() {
        return !main.GE_LOCATION.containsPlayer() && main.restock &&
                !main.isMuling && !main.killCows && !main.lootCows;
    }

    @Override
    public int execute() {
        if (WalkingHelper.shouldEnableRun()) {
            WalkingHelper.enableRun();
        }

        Log.info("Walking to GE");
        if (WalkingHelper.shouldSetDestination()) {
            if (Movement.walkToRandomized(BankLocation.GRAND_EXCHANGE.getPosition())) {
                Time.sleepUntil(cc::atGE, Random.mid(1800, 2400));
            }
        }
        return 600;
    }
}
