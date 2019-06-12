package script.java.tanner.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import script.java.tanner.Main;

public class Eat extends Task {

    private Main main;

    public Eat (Main main) {
        this.main = main;
    }

    @Override
    public boolean validate() {
        return (Players.getLocal().getHealthPercent() <= 50 && Inventory.contains(main.food));
    }

    @Override
    public int execute() {
        Item i = Inventory.getFirst(main.food);
        if (i != null)
            i.interact("Eat");
        return 500;
    }
}
