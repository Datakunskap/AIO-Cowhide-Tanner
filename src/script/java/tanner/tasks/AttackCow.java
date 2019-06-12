package script.java.tanner.tasks;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.java.tanner.Main;

public class AttackCow extends Task {

    private Main main;

    public AttackCow(Main main) {
        this.main = main;
    }

    @Override
    public boolean validate() {
        return main.restock && main.killCows && main.COW_LOCATION.containsPlayer() &&
                !Players.getLocal().isAnimating() && Players.getLocal().getHealthPercent() > 0;
    }

    @Override
    public int execute() {
        Log.info("Attacking Cow");
        Npc cow = Npcs.getNearest("Cow");
        if(cow != null && cow.getHealthPercent() > 0 && cow.isPositionInteractable())
            cow.interact("Attack");
        return 2000;
    }
}
