package script.java.tanner;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Combat;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import script.java.tanner.data.Location;
import script.java.tanner.data.MuleArea;
import script.java.tanner.tasks.*;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.script.ScriptCategory;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.task.TaskScript;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.java.tanner.ui.Gui;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;

@ScriptMeta(name = "AIO Tanner", developer = "DrScatman", desc = "Tans cowhide for $" +
        "F2P money making", category =
        ScriptCategory.MONEY_MAKING, version = 0.01)
public class Main extends TaskScript implements RenderListener {
    ////////////////////////////////////////////////////////////////////////////////////

    // Increase buying GP per hide
    public static int addHidePrice = 5;
    // Decrease selling GP per leather
    public static int subLeatherPrice = 5;
    // Time(min) to increase/decrease price
    public static int resetGeTime = 5;
    // Amount to increase/decrease each interval
    public static int intervalAmnt = 5;
    // Kill cows restock option
    public static boolean killCows = false;
    // Loot hides restock option
    public static boolean lootCows = false;
    // Food choice
    public static String food = "Trout";
    // Food Amount
    public static int foodAmnt = 0;
    // Amount of hide to loot each restock
    public static int lootAmount = 0;
    // Amount to mule at
    public static int muleAmnt = 1000000;
    // Amount to keep from mule
    public static int muleKeep = 500000;
    // Mules in-game name
    public static String muleName = "";
    // GE area to mule
    public static MuleArea muleArea = MuleArea.GE_NE;
    // Mules World
    public static int muleWorld = 301;

    ////////////////////////////////////////////////////////////////////////////////////
/*
    DO NOT CHANGE
*/

    public static final int COWHIDE = 1739;
    public static boolean restock = true;
    public static final Location GE_LOCATION = Location.GE_AREA;
    public static final Location COW_LOCATION = Location.COW_AREA;
    public static boolean sold = false;
    public static boolean checkedBank = false;
    public static boolean isMuling = false;
    public static boolean geSet = false;
    public static int gp = 0;
    public static int amntMuled = 0;
    public static boolean checkRestock = true;
    public static long startTime = 0;
    public static int elapsedSeconds = 0;
    public static boolean buyPriceChng = false;
    public static int decSellPrice = 0;
    public static int incBuyPrice = 0;
    public static int timesPriceChanged = 0;
    public static int cowHideCount = 0;

    public static int[] HIDES = {
            1739, // cowhide
    };

    public static int[] LEATHERS = {
            1741, // leather
    };
    public static int LEATHER_NOTE = LEATHERS[0] + 1;

    private static void setLeather() {
        // Cow
        if (COWHIDE == 1739) {
            LEATHERS[0] = 1741;
        }
    }

    public static final Area TANNER_AREA = Area.rectangular(3271, 3191, 3277, 3193);

    private final Task[] TASKS = {
            new Mule(),
            new CheckRestock(),
            new Eat(),
            new WalkToCows(),
            new LootHide(),
            new AttackCow(),
            new WalkToGE(),
            new SellGE(),
            new BuyGE(),
            new WalkToBank(),
            new BankAK(),
            new WalkToTanner(),
            new TanHide()
    };

    public static int totalTanned = 0;

    public static int leatherPrice;
    public static int cowhidePrice;
    public static int cowhideSellPrice;

    public static void setPrices() {
        {
            try {
                //Log.info("Setting prices");
                leatherPrice = ExPriceChecker.getOSBuddySellPrice(Main.LEATHERS[0]) - subLeatherPrice;
                cowhidePrice = ExPriceChecker.getOSBuddyBuyPrice(Main.COWHIDE) + addHidePrice;
                cowhideSellPrice = ExPriceChecker.getOSBuddySellPrice(Main.COWHIDE);
            } catch (IOException e) {
                Log.severe("Failed getting price");
                e.printStackTrace();
            }
        }
    }

    public static StopWatch timeRan = null; // stopwatch is started by GUI

    @Override
    public void onStart() {
        setLeather();
        setPrices();

        javax.swing.SwingUtilities.invokeLater(() ->
            new Gui(this)
        );

        submit(TASKS);

        Combat.toggleAutoRetaliate(true);
        setPaused(true);
    }

    @Override
    public void onStop() {
        // in case a ban during muling, which has happened once
        if (isMuling) {
            Mule.logoutMule();
        }
        logStats();
    }

    private int getHourlyRate(Duration sw) {
        double hours = sw.getSeconds() / 3600.0;
        double tannedPerHour = totalTanned / hours;
        return (int) tannedPerHour;
    }

    private void logStats() {
        int[] stats = getStats();
        String statsString = "Tanned: "
                + stats[0]
                + "  |  Total profit: " + stats[1]
                + "  |  Hourly profit: " + stats[2];
        Log.info(statsString);
    }

    private static final DecimalFormat formatNumber = new DecimalFormat("#,###");

    @Override
    public void notify(RenderEvent renderEvent) {

        Graphics g = renderEvent.getSource();

        // render time running
        g.setFont(new Font("TimesRoman", Font.BOLD, 20));

        // sets rendered text color
        Color color = Color.WHITE;
        if (COWHIDE == 1739)
            color = Color.YELLOW.darker();

        drawStringWithShadow(
                g,
                timeRan == null ? "00:00:00" : timeRan.toElapsedString(),
                242,
                21,
                color
        );


        // render tanned and profit
        int[] stats = getStats();
        int totalCowhideTanned = stats[0];
        int totalProfit = stats[1];
        int hourlyProfit = stats[2];

        g.setFont(new Font("TimesRoman", Font.BOLD, 15));

        int adjustY = -5;

        drawStringWithShadow(g, "Total Tanned: " + formatNumber.format(totalCowhideTanned), 8, 269 + adjustY, Color.WHITE);
        drawStringWithShadow(g, "Total Profit: " + formatNumber.format(totalProfit), 8, 294 + adjustY, Color.WHITE);
        drawStringWithShadow(g, "Profit/Hr: " + formatNumber.format(hourlyProfit), 8, 319 + adjustY, Color.WHITE);
    }

    private void drawStringWithShadow(Graphics g, String str, int x, int y, Color color) {
        g.setColor(Color.BLACK);
        g.drawString(str, x + 2, y + 2); // draw shadow
        g.setColor(color);
        g.drawString(str, x, y); // draw string
    }

    private int[] getStats() {
        final Duration durationRunning = timeRan == null ? Duration.ofSeconds(0) : timeRan.getElapsed();

        int totalLeatherValue = totalTanned * leatherPrice;
        int totalProfit = (totalLeatherValue - totalTanned * cowhidePrice);
        int hourlyProfit = getHourlyRate(durationRunning) * (leatherPrice - cowhidePrice);
        int[] stats = {
                totalTanned,
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

    public static void printHide() {
        // Cow
        if (Main.COWHIDE == 1739) {
            Log.fine("Cowhide");
        }
    }

    public static void checkTime() {
        long currTime = System.currentTimeMillis();
        elapsedSeconds = (int) ((currTime - startTime) / 1000);
    }

    public static void closeGE() {
        while(GrandExchange.isOpen() || GrandExchangeSetup.isOpen()) {
            InterfaceComponent X = Interfaces.getComponent(465, 2, 11);
            X.interact(ActionOpcodes.INTERFACE_ACTION);
        }
    }
}