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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.Duration;

@ScriptMeta(name = "Best Leather Tanner", developer = "codekiwi", desc = "Tans Cowhide into Leather", category = ScriptCategory.MONEY_MAKING, version = 0.32)
public class LeatherTanner extends TaskScript implements RenderListener, ImageObserver {
    public static final int COWHIDE = 1739;
    public static final Area TANNER_AREA = Area.rectangular(3271, 3191, 3277, 3193);

    private final Task[] TASKS = {
        new WalkToBank(),
        new BankLeatherWithdrawCowhide(this),
        new WalkToTanner(),
        new TanHide(this)
    };

    public int totalTanned = 0;
    private final int leatherPrice = (133 + 166) / 2; // TODO: get from a GE API
    private final int cowhidePrice = 72; // TODO: get from a GE API

    StopWatch timeRan = null; // stopwatch is started by GUI
    private Duration durationRunning = Duration.ofSeconds(0);

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
    public void pauseScript() {
        this.durationRunning = timeRan.getElapsed();
        this.setPaused(true);
    }

    @Override
    public void onStop() {
        this.logStats();
    }

    private int getHourlyRate(Duration sw) {
        double hours = sw.getSeconds() / 3600.0;
        double tannedPerHour = this.totalTanned / hours;
        return (int) tannedPerHour;
    }

    private void logStats() {
        Log.info("Tanned: " + this.totalTanned);
        if (this.timeRan != null) {
            Log.info("Time running: " + this.timeRan.toElapsedString());
            Log.info("Tanned / hr: " + getHourlyRate(this.timeRan.getElapsed()));
        }
    }

    // painting
    private static final Font runescapeFont = new Font("RuneScape Small", Font.PLAIN, 24).deriveFont(32f);
    private static final Font runescapeFontSmaller = new Font("RuneScape Small", Font.PLAIN, 24);
    private static final DecimalFormat formatNumber = new DecimalFormat("#,###");
    private static final String imageUrl = "https://i.imgur.com/55MEmwU.png";
    private static final Image image1 = LeatherTanner.getImage(imageUrl);

    private static Image getImage(String url){
        try {
            return ImageIO.read(new URL(url));
        }catch (IOException e){
            return null;
        }
    }

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        return false;
    }

    @Override
    public void notify(RenderEvent renderEvent) {
        Graphics g = renderEvent.getSource();

        // render the paint layout
        g.drawImage(LeatherTanner.image1, 0, 0, this);

        // render time running
        g.setFont(runescapeFontSmaller);
        this.drawStringWithShadow(
                g,
                this.timeRan == null ? "00:00:00" : this.timeRan.toElapsedString(),
                238,
                24,
                Color.YELLOW.darker()
        );

        // render tanned and profit
        Duration validDurationRunning = (this.isPaused() || this.timeRan == null) ? this.durationRunning : this.timeRan.getElapsed();

        int totalLeatherValue = this.totalTanned * this.leatherPrice;
        double hourlyProfit = this.getHourlyRate(validDurationRunning) * (this.leatherPrice - this.cowhidePrice);

        g.setFont(runescapeFont);

        this.drawStringWithShadow(g, formatNumber.format(this.totalTanned), 68 ,229, Color.YELLOW);
        this.drawStringWithShadow(g, formatNumber.format(totalLeatherValue), 68 ,274, Color.WHITE);
        this.drawStringWithShadow(g, formatNumber.format(hourlyProfit), 68 ,319, Color.WHITE);
    }

    private void drawStringWithShadow(Graphics g, String str, int x, int y, Color color) {
        g.setColor(Color.BLACK);
        g.drawString(str, x + 2, y + 2); // draw shadow
        g.setColor(color);
        g.drawString(str, x, y); // draw string
    }
}