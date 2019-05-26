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
////////////////////////////////////////////////////////////////////////////////////
/*
    fill out values ->
*/
    // 1753 green, 1749 red, 1751 blue, 1747 black, 1739 cow
    public static int COWHIDE = 1751;
    public static boolean restockMaxProfitHide = true;
    public static final int muleAmnt = 5100000;
    public static final int muleKeep = 5000000;
////////////////////////////////////////////////////////////////////////////////////
/*
    DO NOT CHANGE
*/
    public static boolean restock = false;
    public static boolean newRingW = false;
    public static boolean newRingD = false;
    public static Location location;
    public static boolean sold = false;
    public static boolean checkedBank = false;
    public static MuleArea muleArea = MuleArea.GE_NW;
    public static boolean isMuling = false;
    public static boolean geSet = false;
    public static int gp = 0;
    public static int amntMuled = 0;
    public static boolean checkRestock = true;

    public static int[] HIDES = {
            1753, // green dhide
            1749, // red dhide
            1751, // blue dhide
            1747, // black dhide
            1739, // cowhide
    };

    public static int[] LEATHERS = {
            -1, // placeholder
            2507, // red dhide leather
            1745, // green dhide leather
            1741, // leather
            2505, // blue dhide leather
            2509, // black dhide leather
    };
    public static int LEATHER_NOTE = LEATHERS[0]+1;

    public static void setMaxProfitHide() throws IOException {
        int maxH = COWHIDE;
        int maxP = leatherPrice - cowhidePrice;
        for(int h : HIDES) {
            COWHIDE = h;
            setLeather();
            setPrices();
            if ((leatherPrice - cowhidePrice) > maxP) {
                maxH = h;
            }
        }
        COWHIDE = maxH;
        setLeather();
        setPrices();
    }

    public static void setLeather() {
        // Cow
        if (Main.COWHIDE == 1739) {
            LEATHERS[0] = 1741;
        }
        // Green
        if (Main.COWHIDE == 1753) {
            LEATHERS[0] = 1745;
        }
        // Blue
        if (Main.COWHIDE == 1751) {
            LEATHERS[0] = 2505;
        }
        // Red
        if (Main.COWHIDE == 1749) {
            LEATHERS[0] = 2507;
        }
        // Black
        if (Main.COWHIDE == 1747) {
            LEATHERS[0] = 2509;
        }
        LEATHER_NOTE = LEATHERS[0]+1;
    }

    public static final Area TANNER_AREA = Area.rectangular(3271, 3191, 3277, 3193);

    private final Task[] TASKS = {
            new Mule(),
            new checkRestock(this),
            new TeleportGE(),
            new TeleportAK(),
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
    public static int priceRingD;

    public static void setPrices() {
        {
            try {
                Log.info("Getting prices");
                leatherPrice = ExPriceChecker.getOSBuddyPrice(Main.LEATHERS[0]) - 10;
                cowhidePrice = ExPriceChecker.getOSBuddyPrice(Main.COWHIDE) + 20;
                priceRingW = ExPriceChecker.getOSBuddyPrice(11980);
                priceRingD = ExPriceChecker.getOSBuddyPrice(2552);
            } catch (IOException e) {
                Log.severe("Failed getting price");
                e.printStackTrace();
            }
        }
    }

    StopWatch timeRan = null; // stopwatch is started by GUI

    @Override
    public void onStart() {
        location = Location.GE_AREA;
        setLeather();
        setPrices();

        javax.swing.SwingUtilities.invokeLater(() -> {
            new SimpleTannerGUI(this);
        });

        submit(TASKS);
        //this.setPaused(true);
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
        int totalProfit = (totalLeatherValue - this.totalTanned * this.cowhidePrice);
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