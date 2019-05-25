package lamerton.troy.tanner.tasks;

import lamerton.troy.tanner.Main;
import lamerton.troy.tanner.data.MuleArea;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Login;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.EnterInput;
import org.rspeer.runetek.api.component.Trade;
import org.rspeer.runetek.api.component.WorldHopper;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import java.io.*;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Mule extends Task {

    public int Gold;
    public int Gold2;
    public int gold3;
    public String status1;
    private final String user = "milleja1";
    private final int muleAmnt = Main.muleAmnt;
    private final int muleWorld = 301;
    private final int muleKeep = Main.muleKeep;
    public String status = "needgold";
    public static String Username;
    public static String Password;
    private boolean muleing = false;
    private int begWorld = -1;

    private void loginMule() {
        try {
            File file = new File("C:" + File.separator + "Mule"+ File.separator + "mule.txt");

            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter pw = new PrintWriter(file);
            pw.println("mule");
            pw.close();

            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            while (((status1 = br.readLine())) != null) {
                Log.info(status1);
            }

            br.close();
        } catch (IOException e) {
            Log.info("File not found");
        }

    }

    public void logoutMule() {
        try {
            File file = new File("C:" + File.separator + "Mule"+ File.separator + "mule.txt");

            if (!file.exists()) {
                Log.info("Logout file not found");
            }
            PrintWriter pw = new PrintWriter(file);
            pw.println("done");
            pw.close();

            Log.info("done");

        } catch (IOException e) {
            Log.info("File not found");
        }
    }

    @Override
    public boolean validate() { return Inventory.getCount(true, 995) >= muleAmnt || muleing; }

    @Override
    public int execute() {
        Main.isMuling = true;
        loginMule();

        if(Worlds.getCurrent() != muleWorld){
            begWorld = Worlds.getCurrent();
            WorldHopper.hopTo(muleWorld);
            Time.sleepUntil(() -> Worlds.getCurrent() == muleWorld, 10000);
        }

        if (status != null) {
            status = status.trim();
        }
        if (org.rspeer.runetek.api.component.Dialog.canContinue()) {
            Dialog.processContinue();
            Time.sleep(1000);
        }
        if (!Main.muleArea.getMuleArea().contains(Players.getLocal())) {
            if (WalkingHelper.shouldEnableRun()) {
                WalkingHelper.enableRun();
            }
            Movement.setWalkFlag(Main.muleArea.getMuleArea().getTiles().get(Main.randInt(0, Main.muleArea.getMuleArea().getTiles().size()-1)));
        }

        if (Inventory.getFirst(995) != null) {
            Gold = Inventory.getFirst(995).getStackSize();
        }

        gold3 = Gold2 - Gold;

        if (status.contains("needgold")) {
            if (!Game.isLoggedIn() && Username != null && Password != null) {
                Login.enterCredentials(Username, Password);
                Keyboard.pressEnter();
                Time.sleep(200);
                Keyboard.pressEnter();
                Time.sleep(200);
                Keyboard.pressEnter();
                Time.sleep(200);
                Keyboard.pressEnter();
            }
            if (Players.getNearest(user) != null && !Trade.isOpen()) {
                Players.getNearest(user).interact("Trade with");
                Time.sleep(3000);
            }
            if (Inventory.getFirst(995) != null) {
                if (!Trade.contains(true, 995)) {
                    int Coins = Inventory.getFirst(995).getStackSize();
                    if (Trade.isOpen(false)) {
                        muleing = true;
                        // handle first trade window...
                        int attempts = 0;
                        while (true) {
                            attempts++;
                            Log.info("Entering trade offer");
                            Trade.offer("Coins", x -> x.contains("X"));
                            Time.sleep(1000);
                            if (EnterInput.isOpen()) {
                                EnterInput.initiate(Coins - muleKeep);
                                Time.sleep(1000);
                            }
                            if (Time.sleepUntil(() -> Trade.contains(true, 995), 500, 3500)) {
                                Log.info("Trade entered & accepted");
                                Trade.accept();
                                Time.sleepUntil(() -> Trade.isOpen(true), 5000);
                                break;
                            }
                            if (attempts > 6) {
                                break;
                            }
                        }
                    }
                    if (Trade.isOpen(true)) {
                        // handle second trade window...
                        Time.sleep(500, 1500);
                        if (Trade.accept()) {
                            Time.sleep(3000);
                            Log.fine("Trade completed shutting down mule");
                            logoutMule();
                            muleing = false;
                            Main.amntMuled += (Coins - Main.muleKeep);
                            if(begWorld != -1) {
                                WorldHopper.hopTo(begWorld);
                                Time.sleepUntil(() -> Worlds.getCurrent() == begWorld, 10000);
                            }
                            Time.sleep(8000, 10000);
                            Main.isMuling = false;
                        }
                        Time.sleep(700);
                    }

                }
            }
        }
        return 500;
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }
}

