package SimpleTanner;

import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.web.node.impl.bank.WebBankArea;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import java.util.List;
import java.awt.*;

@ScriptManifest(name = "Best Leather Tanner", category = Category.MONEYMAKING, author = "codekiwi", version = 0.9)
public class MainClass extends AbstractScript {

    private static Area tannerArea = new Area(3271,3191,3277,3193, 0);

    private boolean scriptStarted = false;
    private int totalTanned = 0;
    private Timer timeRan;

    void setStartScript() {
        this.scriptStarted = true;
        this.timeRan = new Timer();
    }

    @Override
    public void onStart() {
        SimpleTannerGUI gui = new SimpleTannerGUI(this);
        gui.setVisible(true);
    }

    @Override
    public int onLoop() {
        if (!this.scriptStarted) {
            return 500;
        }

        boolean gotCowhide = false;
        boolean gotCoins = false;

        List<Item> items = getInventory().all();
        for (Item item : items) {
            if (item != null) {
                if (item.getName().equals("Coins")) {
                    if (item.getAmount() >= 27) {
                        gotCoins = true;
                    }
                } else if (item.getName().equals("Cowhide")) {
                    gotCowhide = true;
                }
            }
        }

        boolean readyToTan = gotCoins && gotCowhide;

        if (readyToTan) {
            // tanning
            if(tannerArea.contains(getLocalPlayer())) {
                this.tanHides();
            } else {
                // walk to tanner
                if(getWalking().walk(tannerArea.getRandomTile())) {
                    sleepUntil(() -> tannerArea.contains(getLocalPlayer()), 2000);
                }
            }
        } else {
            // banking
            if(WebBankArea.AL_KHARID.getArea().contains(getLocalPlayer())) {
                this.bankForCowhides();
            } else {
                // walk to Al Kharid bank
                if(getWalking().walk(WebBankArea.AL_KHARID.getArea().getRandomTile())) {
                    sleepUntil(() -> WebBankArea.AL_KHARID.getArea().contains(getLocalPlayer()), 2000);
                }
            }
        }

        return 500;
    }

    private void bankForCowhides() {
        if (getBank().isOpen()) {
            // deposit all except money
            if (getInventory().isEmpty()) {
                // check that there is enough cowhides and gp
                Item coinsInBank = getBank().get(995);
                int coins = 0;
                if (coinsInBank != null) {
                    coins = coinsInBank.getAmount();
                    getBank().withdrawAll("Coins");
                }

                Item cowhide = getBank().get("Cowhide");

                int cowhideAmount = cowhide == null ? 0 : cowhide.getAmount();

                if (cowhideAmount >= 1 && coins >= cowhideAmount) {
                    // withdraw Cowhide
                    if (getBank().withdrawAll("Cowhide")) {
                        sleepUntil(() -> getInventory().contains("Cowhide"), 8000);
                    }
                } else {
                    // not enough cowhides or gp
                    log("Finished tanning all cowhides!");
                    this.logStats();
                    stop();
                }
            } else {
                getBank().depositAllItems();
                sleepUntil(getInventory()::isEmpty, 8000);
            }
        } else {
            getBank().open();
        }
    }

    private void tanHides() {
        NPC tanner = getNpcs().closest(3231);
        if (tanner.interact("Trade")) {
            if (sleepUntil(() -> getWidgets().getWidgetChild(324, 148) != null, 8000)) {
                WidgetChild leatherWidget = getWidgets().getWidgetChild(324, 148);

                if (leatherWidget.interact("Tan All")) {
                    // wait for all cowhides to turn into leather
                    if (sleepUntil(() -> !getInventory().contains("Cowhide"), 8000)) {
                        this.totalTanned += 27;
                    }
                }
            }
        }
    }

    private void logStats() {
        log("Tanned: " + this.totalTanned);
        log("Time running: " + this.timeRan.formatTime());
        int tannedPerHour = this.timeRan.getHourlyRate(this.totalTanned);
        log("Tanned / hr: " + tannedPerHour);
    }

    // painting
    private static Font timesNewRoman = new Font("Times new roman", Font.BOLD, 17);
    private static Color rectangleColor = new Color(214, 203, 80);

    @Override
    public void onPaint(Graphics g) {
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

        g.drawString("Time running: " + this.timeRan.formatTime(),9,topOfRect + 36);

        int tannedPerHour = this.timeRan.getHourlyRate(this.totalTanned);
        g.drawString("Tanned / hr: " + tannedPerHour,9,topOfRect + 55);
    }
}
