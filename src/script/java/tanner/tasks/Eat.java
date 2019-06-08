package script.java.tanner.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import script.java.tanner.Main;

public class Eat extends Task {

    @Override
    public boolean validate() {
        return (Players.getLocal().getHealthPercent() <= 50 && Inventory.contains(Main.food));
    }

    @Override
    public int execute() {
        Item i = Inventory.getFirst(Main.food);
        if (i != null)
            i.interact("Eat");
        return 500;
    }
}
