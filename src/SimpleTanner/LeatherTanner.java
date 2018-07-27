package SimpleTanner;

import SimpleTanner.Tasks.BankLeatherWithdrawCowhide;
import SimpleTanner.Tasks.TanHide;
import SimpleTanner.Tasks.WalkToBank;
import SimpleTanner.Tasks.WalkToTanner;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.script.ScriptCategory;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.task.TaskScript;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import java.awt.*;

@ScriptMeta(name = "Best Leather Tanner", developer = "codekiwi", desc = "Tans Cowhide into Leather", category = ScriptCategory.MONEY_MAKING, version = 0.25)
public class LeatherTanner extends TaskScript implements RenderListener {
    public static final int COWHIDE = 1739;
    public static final Area TANNER_AREA = Area.rectangular(3271, 3191, 3277, 3193);

    private final Task[] TASKS = {
        new WalkToBank(),
        new BankLeatherWithdrawCowhide(this),
        new WalkToTanner(),
        new TanHide(this)
    };

    public int totalTanned = 0;
    StopWatch timeRan = null; // stopwatch is started by GUI

    @Override
    public void onStart() {
        SimpleTannerGUI gui = new SimpleTannerGUI(this);
        gui.setVisible(true);
    }

    @Override
    public int loop() {
        for (Task task : TASKS) {
            if (task.validate()) {
                return task.execute();
            }
        }
        return 600;
    }

    @Override
    public void onStop() {
        this.logStats();
    }

    private int getHourlyRate(StopWatch sw) {
        double hours = sw.getElapsed().getSeconds() / 3600.0;
        double tannedPerHour = this.totalTanned / hours;
        return (int) tannedPerHour;
    }

    private void logStats() {
        Log.info("Tanned: " + this.totalTanned);
        if (this.timeRan != null) {
            Log.info("Time running: " + this.timeRan.toElapsedString());
            Log.info("Tanned / hr: " + getHourlyRate(this.timeRan));
        }
    }

    // painting
    private static final Font timesNewRoman = new Font("Times new roman", Font.BOLD, 17);
    private static final Color rectangleColor = new Color(214, 203, 80);

    @Override
    public void notify(RenderEvent renderEvent) {
        Graphics g = renderEvent.getSource();
        int bottomCanvas = 338;
        int rectHeight = 60;
        int topOfRect = bottomCanvas - rectHeight;

        g.setColor(rectangleColor);
        g.fillRect(5, topOfRect, 193, rectHeight);

        g.setColor(Color.BLACK);
        if (!g.getFont().getName().equals(timesNewRoman.getName())) {
            g.setFont(timesNewRoman);
        }

        g.drawString("Tanned: " + this.totalTanned,9,topOfRect + 18);

        if (this.timeRan != null) {
            g.drawString("Time running: " + this.timeRan.toElapsedString(), 9, topOfRect + 36);

            int tannedPerHour = getHourlyRate(this.timeRan);
            g.drawString("Tanned / hr: " + tannedPerHour,9,topOfRect + 55);
        }
    }
}