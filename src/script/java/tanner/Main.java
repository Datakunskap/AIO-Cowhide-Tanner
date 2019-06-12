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
    public int addHidePrice = 5;
    // Decrease selling GP per leather
    public int subLeatherPrice = 5;
    // Time(min) to increase/decrease price
    public int resetGeTime = 5;
    // Amount to increase/decrease each interval
    public int intervalAmnt = 5;
    // Kill cows restock option
    public boolean killCows = false;
    // Loot hides restock option
    public boolean lootCows = false;
    // Food choice
    public String food = "Trout";
    // Food Amount
    public int foodAmnt = 0;
    // Amount of hide to loot each restock
    public int lootAmount = 0;
    // Amount to mule at
    public int muleAmnt = 1000000;
    // Amount to keep from mule
    public int muleKeep = 500000;
    // Mules in-game name
    public String muleName = "";
    // GE area to mule
    public MuleArea muleArea = MuleArea.GE_NE;
    // Mules World
    public int muleWorld = 301;

    ////////////////////////////////////////////////////////////////////////////////////
/*
    DO NOT CHANGE
*/
    public final int COWHIDE = 1739;
    public final int LEATHER = 1741;
    public final int LEATHER_NOTE = LEATHER + 1;
    public boolean restock = true;
    public final Location GE_LOCATION = Location.GE_AREA;
    public final Location COW_LOCATION = Location.COW_AREA;
    public boolean sold = false;
    public boolean checkedBank = false;
    public boolean isMuling = false;
    public boolean geSet = false;
    public int gp = 0;
    public int amntMuled = 0;
    public boolean checkRestock = true;
    public long startTime = 0;
    public int elapsedSeconds = 0;
    public boolean buyPriceChng = false;
    public int decSellPrice = 0;
    public int incBuyPrice = 0;
    public int timesPriceChanged = 0;
    public int cowHideCount = 0;
    public int totalTanned = 0;
    public final Area TANNER_AREA = Area.rectangular(3271, 3191, 3277, 3193);
    public final int TANNER_ID = 3231;
    public int leatherPrice = 0;
    public int cowhidePrice = 0;

    public void setPrices() {
        {
            try {
                Log.info("Setting prices");
                leatherPrice = ExPriceChecker.getOSBuddySellPrice(LEATHER) - subLeatherPrice;
                cowhidePrice = ExPriceChecker.getOSBuddyBuyPrice(COWHIDE) + addHidePrice;
            } catch (IOException e) {
                Log.severe("Failed getting price");
                e.printStackTrace();
            } finally {
                //Fall-back prices
                if (leatherPrice < 70) {
                    Log.info("Using fall-back leather price");
                    leatherPrice = 70;
                }
                if (cowhidePrice < 50) {
                    Log.info("Using fall-back cowhide price");
                    cowhidePrice = 50;
                }
            }
        }
    }

    public StopWatch timeRan = null; // stopwatch is started by GUI

    @Override
    public void onStart() {
        setPrices();

        javax.swing.SwingUtilities.invokeLater(() ->
            new Gui(this)
        );

        submit( new Mule(this),
                new CheckRestock(this),
                new Eat(this),
                new WalkToCows(this),
                new LootHide(this),
                new AttackCow(this),
                new WalkToGE(this),
                new SellGE(this),
                new BuyGE(this),
                new WalkToBank(this),
                new BankAK(this),
                new WalkToTanner(this),
                new TanHide(this));

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

    private final DecimalFormat formatNumber = new DecimalFormat("#,###");

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

    public int randInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        java.util.Random rand = new java.util.Random();
        int randomNum = rand.nextInt(max - min + 1) + min;
        return randomNum;
    }

    public void printHide() {
        // Cow
        if (COWHIDE == 1739) {
            Log.fine("Cowhide");
        }
    }

    public void checkTime() {
        long currTime = System.currentTimeMillis();
        elapsedSeconds = (int) ((currTime - startTime) / 1000);
    }

    public void closeGE() {
        while(GrandExchange.isOpen() || GrandExchangeSetup.isOpen()) {
            InterfaceComponent X = Interfaces.getComponent(465, 2, 11);
            X.interact(ActionOpcodes.INTERFACE_ACTION);
        }
    }
}