package lamerton.troy.tanner;

import lamerton.troy.tanner.data.Location;
import lamerton.troy.tanner.data.MuleArea;
import lamerton.troy.tanner.tasks.*;
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
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;

@ScriptMeta(name = "Ultimate Tanner", developer = "DrScatman", desc = "Tans hides " +
        "F2P money making", category =
        ScriptCategory.MONEY_MAKING, version = 0.01)
public class Main extends TaskScript implements RenderListener, ImageObserver {
    public static final int COWHIDE = 1739;
    public static final int LEATHER_NOTE = 1742;
    public static Location location;
    public static boolean restock = true;
    public static boolean sold = false;
    public static boolean checkedBank = false;
    public static MuleArea muleArea = MuleArea.GE_NW;
    public static boolean isMuling = false;
    public static boolean newRingW = false;

    // TODO: dragon hide IDS add here ->

    public static final int[] HIDES = {
            1739, // cowhide
            0, // green dhide
            0, // red dhide
            0, // blue dhide
            0, // black dhide
    };
    public static final int[] LEATHERS = {
            1741, // leather
            1, // green dhide leather
            1, // red dhide leather
            1, // blue dhide leather
            1, // black dhide leather
    };

    public static final Area TANNER_AREA = Area.rectangular(3271, 3191, 3277, 3193);

    private final Task[] TASKS = {
            new Mule(),
            new WalkToGE(),
            new SellGE(),
            new BuyGE(),
            new WalkToBank(),
            new BankLeatherWithdrawCowhide(this),
            new WalkToTanner(),
            new TanHide(this)
    };

    public int totalTanned = 0;

    public static int leatherPrice;
    public static int cowhidePrice;
    public static int priceRingW;

    {
        try {
            leatherPrice = ExPriceChecker.getRSPrice(1741);
            cowhidePrice = ExPriceChecker.getRSPrice(Main.COWHIDE);
            priceRingW = ExPriceChecker.getRSPrice(11980);
        } catch (IOException e) {
            Log.severe("Failed getting price");
            e.printStackTrace();
        }
    }

    StopWatch timeRan = null; // stopwatch is started by GUI

    @Override
    public void onStart() {
        location = Location.GE_AREA;
        javax.swing.SwingUtilities.invokeLater(() -> {
            // TODO: remove gui -> auto detect hides in inventory/bank
            new SimpleTannerGUI(this);
        });
        submit(TASKS);
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
        int[] stats = this.getStats();
        String statsString = "Tanned: "
                + stats[0]
                + "  |  Total profit: " + stats[1]
                + "  |  Hourly profit: " + stats[2];
        Log.info(statsString);
    }

    // painting
    private static final Font runescapeFont = FetchHelper.getRunescapeFont();
    private static final Font runescapeFontSmall = runescapeFont.deriveFont(runescapeFont.getSize2D() + 2f);
    private static final Font runescapeFontBigger = runescapeFont.deriveFont(runescapeFont.getSize2D() + 5f);
    private static final DecimalFormat formatNumber = new DecimalFormat("#,###");
    private static final String imageUrl = "https://i.imgur.com/1qEl73a.png";
    private static final Image image1 = FetchHelper.getImage(imageUrl);

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        return false;
    }

    @Override
    public void notify(RenderEvent renderEvent) {
        Graphics g = renderEvent.getSource();

        // render the paint layout
        g.drawImage(Main.image1, 0, 0, this);

        // render time running
        g.setFont(runescapeFontSmall);
        this.drawStringWithShadow(
                g,
                this.timeRan == null ? "00:00:00" : this.timeRan.toElapsedString(),
                242,
                21,
                Color.YELLOW.darker()
        );


        // render tanned and profit
        int[] stats = this.getStats();
        int totalCowhideTanned = stats[0];
        int totalProfit = stats[1];
        int hourlyProfit = stats[2];

        g.setFont(runescapeFontBigger);

        int adjustY = -5;

        this.drawStringWithShadow(g, formatNumber.format(totalCowhideTanned), 68, 229 + adjustY, Color.YELLOW);
        this.drawStringWithShadow(g, formatNumber.format(totalProfit), 68, 274 + adjustY, Color.WHITE);
        this.drawStringWithShadow(g, formatNumber.format(hourlyProfit), 68, 319 + adjustY, Color.WHITE);
    }

    private void drawStringWithShadow(Graphics g, String str, int x, int y, Color color) {
        g.setColor(Color.BLACK);
        g.drawString(str, x + 2, y + 2); // draw shadow
        g.setColor(color);
        g.drawString(str, x, y); // draw string
    }

    private int[] getStats() {
        final Duration durationRunning = this.timeRan == null ? Duration.ofSeconds(0) : this.timeRan.getElapsed();

        int totalLeatherValue = this.totalTanned * this.leatherPrice;
        int totalProfit = totalLeatherValue - this.totalTanned * this.cowhidePrice;
        int hourlyProfit = this.getHourlyRate(durationRunning) * (this.leatherPrice - this.cowhidePrice);
        int[] stats = {
                this.totalTanned,
                totalProfit,
                hourlyProfit
        };
        return stats;
    }

    public static int randInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        java.util.Random rand = new java.util.Random();
        int randomNum = rand.nextInt(max - min + 1) + min;
        return randomNum;
    }
}