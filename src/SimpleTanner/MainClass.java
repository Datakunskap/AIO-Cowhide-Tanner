package SimpleTanner;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptCategory;
import org.rspeer.script.ScriptMeta;
import org.rspeer.ui.Log;

import java.awt.*;

@ScriptMeta(name = "Best Leather Tanner", developer = "codekiwi", desc = "Tans Cowhide into Leather", category = ScriptCategory.MONEY_MAKING, version = 0.02)
public class MainClass extends Script implements RenderListener {

    private static final Area TANNER_AREA = Area.rectangular(3271, 3191, 3277, 3193);
    private static final int COWHIDE = 1739;

    private int totalTanned = 0;
    StopWatch timeRan = null; // stopwatch is started by GUI

    @Override
    public void onStart() {
        SimpleTannerGUI gui = new SimpleTannerGUI(this);
        gui.setVisible(true);
    }

    @Override
    public int loop() {
        boolean gotCowhide = false;
        boolean gotCoins = false;

        Item[] items = Inventory.getItems();
        for (Item item : items) {
            if (item != null) {
                if (item.getName().equals("Coins")) {
                    if (item.getStackSize() >= 27) {
                        gotCoins = true;
                    }
                } else if (item.getId() == COWHIDE) {
                    gotCowhide = true;
                }
            }
        }

        boolean readyToTan = gotCoins && gotCowhide;

        if (readyToTan) {
            // tanning
            if(TANNER_AREA.contains(Players.getLocal())) {
                this.tanHides();
            } else {
                // walk to tanner

                if(Movement.walkTo(TANNER_AREA.getCenter().randomize(Random.low(2, 3)))) {
                    Time.sleepUntil(() -> TANNER_AREA.contains(Players.getLocal()), Random.mid(2000, 3000));
                }
            }
        } else {
            // banking
            if(BankLocation.AL_KHARID.getPosition().distance(Players.getLocal().getPosition()) <= 3) {
                this.bankForCowhides();
            } else {
                // walk to Al Kharid bank
                if(Movement.walkTo(BankLocation.AL_KHARID.getPosition())) {
                    Time.sleepUntil(() -> BankLocation.AL_KHARID.getPosition().distance(Players.getLocal()) <= 3, Random.mid(1800, 2400));
                }
            }
        }

        return 600;
    }

    private void bankForCowhides() {
        if (Bank.isOpen()) {
            // deposit all except money
            if (Inventory.getCount() == 0) {
                // check that there is enough cowhides and gp
                Item coinsInBank = Bank.getFirst("Coins");
                int coins = 0;
                if (coinsInBank != null) {
                    coins = coinsInBank.getStackSize();
                    Bank.withdrawAll("Coins");
                    Time.sleepUntil(() -> Inventory.contains("Coins"), 2000);
                    Time.sleep(100, 200);
                }

                Item cowhide = Bank.getFirst(COWHIDE);

                int cowhideAmount = cowhide == null ? 0 : cowhide.getStackSize();

                if (cowhideAmount >= 1 && coins >= cowhideAmount) {
                    // withdraw Cowhide
                    if (Bank.withdrawAll(COWHIDE)) {
                        Time.sleepUntil(() -> Inventory.contains(COWHIDE), 8000);
                    }
                } else {
                    // not enough cowhides or gp
                    Log.info("Finished tanning all cowhides!");
                    setStopping(true);
                }
            } else {
                Bank.depositInventory();
                Time.sleepUntil(() -> Inventory.getCount() == 0, 8000);
            }
        } else {
            Bank.open();
        }
    }

    private void tanHides() {
        Npc tanner = Npcs.getNearest(3231);
        if (tanner.interact("Trade")) {
            if (Time.sleepUntil(() -> Interfaces.getComponent(324, 124) != null, 8000)) {
                InterfaceComponent leatherWidget = Interfaces.getComponent(324, 124);

                if (leatherWidget.interact(ActionOpcodes.INTERFACE_ACTION)) {
                    // wait for all cowhides to turn into leather
                    if (Time.sleepUntil(() -> !Inventory.contains(COWHIDE), 8000)) {
                        this.totalTanned += Inventory.getCount("Leather");
                    }
                }
            }
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
}
