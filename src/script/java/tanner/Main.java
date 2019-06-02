package script.java.tanner;

import script.java.tanner.data.Location;
import script.java.tanner.data.MuleArea;
import script.java.tanner.tasks.*;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.component.*;
import org.rspeer.runetek.api.component.chatter.Chat;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.event.listeners.ChatMessageListener;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.script.ScriptCategory;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.task.TaskScript;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.java.tanner.ui.Gui;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;

@ScriptMeta(name = "Ultimate AIO Tanner", developer = "DrScatman", desc = "Tans hides for $" +
        "F2P money making", category =
        ScriptCategory.MONEY_MAKING, version = 0.01)
public class Main extends TaskScript implements RenderListener, ChatMessageListener {
    ////////////////////////////////////////////////////////////////////////////////////
/*
    fill out values ->
*/
    // 1753 green, 1749 red, 1751 blue, 1747 black, 1739 cow
    public static int COWHIDE = 1753;
    // Switches to max profit hide after selling leathers
    public static boolean restockMaxProfitHide = true;
    // Will calc max profit on restock/start
    public static boolean calcMacProfitOnStart = true;
    // Can tan the same hide twice in a row, Otherwise sets second most profitable
    public static boolean canTanSameHideTwice = false;
    // Increase buying GP per hide
    public static int addHidePrice = 10;
    // Decrease selling GP per leather
    public static int subLeatherPrice = 5;
    // Time(min) to increase/decrease price
    public static int resetGeTime = 5;
    // Amount to increase/decrease each interval
    public static int intervalAmnt = 8;
    // Number of stamina potions to buy each restock
    public static int numStamina = 0;
    // Will increase the number of potions you buy to what you needed last time
    public static boolean smartPotions = false;
    // Amount to mule at
    public static int muleAmnt = 5250000;
    // Amount to keep from mule
    public static int muleKeep = 5000000;
    // GE area to mule
    public static MuleArea muleArea = MuleArea.GE_NE;
    // Buy Ring of wealth
    public static boolean willBuyW = true;
    // Buy Ring of dueling
    public static boolean willBuyD = true;

    ////////////////////////////////////////////////////////////////////////////////////
/*
    DO NOT CHANGE
*/
    public static boolean restock = true;
    public static boolean newRingW = false;
    public static boolean newRingD = false;
    public static Location location;
    public static boolean sold = false;
    public static boolean checkedBank = false;
    public static boolean isMuling = false;
    public static boolean geSet = false;
    public static int gp = 0;
    public static int amntMuled = 0;
    public static boolean checkRestock = true;
    public static long startTime = 0;
    public static int elapsedSeconds = 0;
    public static long staminaPotionIntake = 0;
    public static boolean buyPriceChng = false;
    public static int decSellPrice = 0;
    public static int incBuyPrice = 0;
    public static int timesPriceChanged = 0;

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
    public static int LEATHER_NOTE = LEATHERS[0] + 1;

    public static void setMaxProfitHide() throws IOException {
        int maxH = COWHIDE;
        int prevH = COWHIDE;
        int maxP = leatherPrice - cowhidePrice;

        // Sets highest profit hide
        for (int h : HIDES) {
            COWHIDE = h;
            setLeather();
            setPrices();
            if ((leatherPrice - cowhidePrice) > maxP) {
                maxH = h;
                maxP = leatherPrice - cowhidePrice;
            }
        }

        // Switch to second highest if just tanned
        // Always switches if just tanned black
        if ((maxH == prevH && !canTanSameHideTwice)) {
            Log.info("Same Hide -> Setting Second Highest");
            int[] temp = new int[HIDES.length - 1];
            int x = 0;
            for (int i = 0; i < HIDES.length; i++) {
                if (HIDES[i] != prevH) {
                    temp[x] = HIDES[i];
                    x++;
                }
            }
            maxP = 0;
            for (int h : temp) {
                COWHIDE = h;
                setLeather();
                setPrices();
                if ((leatherPrice - cowhidePrice) > maxP) {
                    maxH = h;
                    maxP = leatherPrice - cowhidePrice;
                }
            }
        }

        COWHIDE = maxH;
        setLeather();
        setPrices();
        printHide();
    }

    public static void setLeather() {
        // Cow
        if (COWHIDE == 1739) {
            LEATHERS[0] = 1741;
        }
        // Green
        if (COWHIDE == 1753) {
            LEATHERS[0] = 1745;
        }
        // Blue
        if (COWHIDE == 1751) {
            LEATHERS[0] = 2505;
        }
        // Red
        if (COWHIDE == 1749) {
            LEATHERS[0] = 2507;
        }
        // Black
        if (COWHIDE == 1747) {
            LEATHERS[0] = 2509;
        }
        LEATHER_NOTE = LEATHERS[0] + 1;
    }

    public static void setHideFromLeather() {
        // Cow
        if (LEATHERS[0] == 1741) {
            COWHIDE = 1739;
        }
        // Green
        if (LEATHERS[0] == 1745) {
            COWHIDE = 1753;
        }
        // Blue
        if (LEATHERS[0] == 2505) {
            COWHIDE = 1751;
        }
        // Red
        if (LEATHERS[0] == 2507) {
            COWHIDE = 1749;
        }
        // Black
        if (LEATHERS[0] == 2509) {
            COWHIDE = 1747;
        }
        LEATHER_NOTE = LEATHERS[0] + 1;
    }

    public static final Area TANNER_AREA = Area.rectangular(3271, 3191, 3277, 3193);

    private final Task[] TASKS = {
            new Mule(),
            new CheckRestock(),
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
    public static int cowhideSellPrice;
    public static int priceRingW;
    public static int priceRingD;
    public static int priceStamina;

    public static void setPrices() {
        {
            // Green or Cow set default
            if (COWHIDE == 1753 || COWHIDE == 1739) {
                try {
                    //Log.info("Setting prices");
                    leatherPrice = ExPriceChecker.getOSBuddySellPrice(Main.LEATHERS[0]);
                    cowhidePrice = ExPriceChecker.getOSBuddyBuyPrice(Main.COWHIDE);
                    cowhideSellPrice = ExPriceChecker.getOSBuddySellPrice(Main.COWHIDE);
                    priceRingW = ExPriceChecker.getOSBuddyBuyPrice(11980) + 500;
                    priceRingD = ExPriceChecker.getOSBuddyBuyPrice(2552) + 500;
                    priceStamina = ExPriceChecker.getOSBuddyBuyPrice(12625) + 50;
                } catch (IOException e) {
                    Log.severe("Failed getting price");
                    e.printStackTrace();
                }
            } else {
                try {
                    //Log.info("Setting prices");
                    leatherPrice = ExPriceChecker.getOSBuddySellPrice(Main.LEATHERS[0]) - subLeatherPrice;
                    cowhidePrice = ExPriceChecker.getOSBuddyBuyPrice(Main.COWHIDE) + addHidePrice;
                    cowhideSellPrice = ExPriceChecker.getOSBuddySellPrice(Main.COWHIDE);
                    priceRingW = ExPriceChecker.getOSBuddyBuyPrice(11980) + 500;
                    priceRingD = ExPriceChecker.getOSBuddyBuyPrice(2552) + 500;
                    priceStamina = ExPriceChecker.getOSBuddyBuyPrice(12625) + 50;
                } catch (IOException e) {
                    Log.severe("Failed getting price");
                    e.printStackTrace();
                }
            }
        }
    }

    public StopWatch timeRan = null; // stopwatch is started by GUI

    @Override
    public void onStart() {
        location = Location.GE_AREA;
        setLeather();
        setPrices();

        javax.swing.SwingUtilities.invokeLater(() -> {
            new Gui(this);
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

    private static final DecimalFormat formatNumber = new DecimalFormat("#,###");

    @Override
    public void notify(RenderEvent renderEvent) {
        Graphics g = renderEvent.getSource();

        // render time running
        g.setFont(new Font("TimesRoman", Font.BOLD, 20));

        Color color = Color.WHITE;
        if (COWHIDE == 1739)
            color = Color.YELLOW.darker();
        if (COWHIDE == 1753)
            color = Color.GREEN.darker();
        if (COWHIDE == 1751)
            color = Color.BLUE.darker();
        if (COWHIDE == 1749)
            color = Color.RED.darker();
        if (COWHIDE == 1747)
            color = Color.BLACK.darker();

        this.drawStringWithShadow(
                g,
                this.timeRan == null ? "00:00:00" : this.timeRan.toElapsedString(),
                242,
                21,
                color
        );


        // render tanned and profit
        int[] stats = this.getStats();
        int totalCowhideTanned = stats[0];
        int totalProfit = stats[1];
        int hourlyProfit = stats[2];

        g.setFont(new Font("TimesRoman", Font.BOLD, 15));

        int adjustY = -5;

        this.drawStringWithShadow(g, "Total Tanned: " + formatNumber.format(totalCowhideTanned), 8, 269 + adjustY, Color.WHITE);
        this.drawStringWithShadow(g, "Total Profit: " + formatNumber.format(totalProfit), 8, 294 + adjustY, Color.WHITE);
        this.drawStringWithShadow(g, "Profit/Hr: " + formatNumber.format(hourlyProfit), 8, 319 + adjustY, Color.WHITE);
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

    public static void printHide() {
        // Cow
        if (Main.COWHIDE == 1739) {
            Log.fine("Cowhide");
        }
        // Green
        if (Main.COWHIDE == 1753) {
            Log.fine("Green Dragonhide | Profit: " + (leatherPrice - cowhidePrice));
        }
        // Blue
        if (Main.COWHIDE == 1751) {
            Log.fine("Blue Dragonhide | Profit: " + (leatherPrice - cowhidePrice));
        }
        // Red
        if (Main.COWHIDE == 1749) {
            Log.fine("Red Dragonhide | Profit: " + (leatherPrice - cowhidePrice));
        }
        // Black
        if (Main.COWHIDE == 1747) {
            Log.fine("Black Dragonhide | Profit: " + (leatherPrice - cowhidePrice));
        }
    }

    public static void checkTime() {
        long currTime = System.currentTimeMillis();
        elapsedSeconds = (int) ((currTime - startTime) / 1000);
    }

    public static final String[] staminaNames = {"Stamina potion(4)", "Stamina potion(3)", "Stamina potion(2)", "Stamina potion(1)"};
    public static long randStamPotionTime = randInt(120000, 360000);

    public static boolean shouldDrinkPotion() {
        long staminaPotionDuration = System.currentTimeMillis() - Main.staminaPotionIntake;
        return !Main.restock && !Main.isMuling && staminaPotionDuration > randStamPotionTime;
    }

    public static void drinkStaminaPotion() {
        if (Inventory.contains(staminaNames) && Inventory.getFirst(staminaNames).interact("Drink")) {
            Log.fine("Drinking Stamina potion");
            Main.staminaPotionIntake = System.currentTimeMillis();
            randStamPotionTime = randInt(120000, 360000);
        }
    }

    @Override
    public void notify(ChatMessageEvent msg) {
        if (msg.getMessage().contains("bot") || msg.getMessage().contains("Bot") && !isMuling && !Bank.isOpen() &&
                !GrandExchange.isOpen() && GrandExchangeSetup.isOpen() && Interfaces.getComponent(324, 124) == null) {
            Chat.send("Of course not");
            Keyboard.pressEnter();
        }
    }
}