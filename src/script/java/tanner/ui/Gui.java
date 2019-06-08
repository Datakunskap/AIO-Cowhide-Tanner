package script.java.tanner.ui;

import net.miginfocom.swing.MigLayout;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.StopWatch;
import script.java.tanner.Main;
import script.java.tanner.data.MuleArea;

import java.awt.*;
import java.awt.event.ItemEvent;
import javax.swing.*;

public class Gui extends JFrame {

    private Main ctx;
    private final String[] RESTOCK_OPTIONS = new String[]{"GE Restock", "Kill Cows + Loot Hides", "Loot Hides"};

    private JFrame frame;
    private JPanel p1;
    private JButton buttonStart;
    private JComboBox restockOption;
    private JLabel addHidePriceLabel;
    private JTextField addHidePrice;
    private JLabel subLeatherPriceLabel;
    private JTextField subLeatherPrice;
    private JLabel resetGeTimeLabel;
    private JTextField resetGeTime;
    private JLabel intervalAmntLabel;
    private JTextField intervalAmnt;
    private JLabel muleWorldLabel;
    private JTextField muleWorld;
    private JLabel muleAmntLabel;
    private JTextField muleAmnt;
    private JLabel muleKeepLabel;
    private JTextField muleKeep;
    private JLabel muleAreaLabel;
    private JLabel muleNameLabel;
    private JTextField muleName;
    private JComboBox muleArea;
    private JLabel restockOptionLabel;
    private JLabel foodLabel;
    private JTextField food;
    private JLabel lootAmountLabel;
    private JTextField lootAmount;

    public Gui(Main ctx) {
        this.ctx = ctx;
        initComponents();
        this.setVisible(false);
    }

    private void buttonStartActionPerformed() {
        Main.timeRan = StopWatch.start();

        // ge restock
        if (restockOption.getSelectedItem().equals(RESTOCK_OPTIONS[0])) {
            Main.killCows = false;
            Main.lootCows = false;
        // kill cows + loot hide
        } else if (restockOption.getSelectedItem().equals(RESTOCK_OPTIONS[1])) {
            Main.killCows = true;
            Main.lootCows = false;

            Main.food = food.getText();
            if(lootAmount != null && !lootAmount.getText().equals(""))
                Main.lootAmount = Integer.parseInt(lootAmount.getText());
        // loot hide
        } else {
            Main.killCows = false;
            Main.lootCows = true;

            Main.food = food.getText();
            if(lootAmount != null && !lootAmount.getText().equals(""))
                Main.lootAmount = Integer.parseInt(lootAmount.getText());
        }

        if (addHidePrice != null && !addHidePrice.getText().equals(""))
            Main.addHidePrice = Integer.parseInt(addHidePrice.getText());
        if (subLeatherPrice != null && !subLeatherPrice.getText().equals(""))
            Main.subLeatherPrice = Integer.parseInt(subLeatherPrice.getText());
        if (resetGeTime != null && !resetGeTime.getText().equals(""))
            Main.resetGeTime = Integer.parseInt(resetGeTime.getText());
        if (intervalAmnt != null && !intervalAmnt.getText().equals(""))
            Main.intervalAmnt = Integer.parseInt(intervalAmnt.getText());

        // handles if blank wont mule
        if (muleAmnt != null && muleKeep != null && muleArea != null && muleName != null && muleWorld != null &&
                !muleAmnt.getText().equals("") && !muleKeep.getText().equals("") && !muleWorld.getText().equals("")) {
            Main.muleAmnt = Integer.parseInt(muleAmnt.getText());
            Main.muleKeep = Integer.parseInt(muleKeep.getText());
            Main.muleWorld = Integer.parseInt(muleWorld.getText());
            Main.muleArea = (MuleArea) muleArea.getSelectedItem();
            Main.muleName = muleName.getText();
            Main.muleWorld = Integer.parseInt(muleWorld.getText());
        } else {
            Main.muleAmnt = Integer.MAX_VALUE;
            Main.muleKeep = Integer.MAX_VALUE;
        }

        frame.setVisible(false);
        ctx.setPaused(false);
    }


    private void initComponents() {
        //======== JFrame/JPanel ========
        frame = new JFrame("AIO Tanner");
        p1 = new JPanel(new MigLayout("filly, wrap 2"));

        //======== Instantiation ========
        buttonStart = new JButton("START");

        restockOptionLabel = new JLabel("Hide Restock Method:");
        restockOption = new JComboBox(RESTOCK_OPTIONS);
        foodLabel = new JLabel("Food To Use:");
        food = new JTextField();
        lootAmountLabel = new JLabel("Number Of Hides To Loot:");
        lootAmount = new JTextField();
        addHidePriceLabel = new JLabel("Increase Set Buying GP Per Hide By:");
        addHidePrice = new JTextField();
        subLeatherPriceLabel = new JLabel("Decrease Set Selling GP Per Leather By:");
        subLeatherPrice = new JTextField();
        resetGeTimeLabel = new JLabel("Time(min) To Increase/Decrease Price:");
        resetGeTime = new JTextField();
        intervalAmntLabel = new JLabel("Amount To Increase/Decrease By Each Interval:");
        intervalAmnt = new JTextField();

        muleNameLabel = new JLabel("Mules In-Game Name:");
        muleName = new JTextField();
        muleWorldLabel = new JLabel("Mules World:");
        muleWorld = new JTextField();
        muleAmntLabel = new JLabel("Amount To Mule At:");
        muleAmnt = new JTextField();
        muleKeepLabel = new JLabel("Amount To Keep From Mule:");
        muleKeep = new JTextField();
        muleAreaLabel = new JLabel("Mule Area:");
        muleArea = new JComboBox(MuleArea.values());

        //---- buttonStart ----
        buttonStart.setFocusCycleRoot(true);
        buttonStart.setFont(new Font(".SF NS Text", Font.PLAIN, 20));
        buttonStart.setBackground(new Color(57, 67, 54));
        buttonStart.addActionListener(e -> buttonStartActionPerformed());

        //======== Add to Panel ========
        addDefaultComponents();

        restockOption.addItemListener(this::restockSelectionHandler);

        setFrameSizeAndLoc();
        // buttonStartActionPerformed();
    }

    private void addDefaultComponents() {
        p1.add(restockOptionLabel, "wrap, growx");
        p1.add(restockOption, "wrap, growx");
        p1.add(addHidePriceLabel, "wrap, growx");
        p1.add(addHidePrice, "wrap, growx");
        p1.add(subLeatherPriceLabel, "wrap, growx");
        p1.add(subLeatherPrice, "wrap, growx");
        p1.add(resetGeTimeLabel, "wrap, growx");
        p1.add(resetGeTime, "wrap, growx");
        p1.add(intervalAmntLabel, "wrap, growx");
        p1.add(intervalAmnt, "wrap, growx");

        addMuleComponents();
        buttonStart.setBackground(Color.CYAN.brighter());
        p1.add(buttonStart, "wrap, growx");
    }

    private void setFrameSizeAndLoc() {
        JPanel contentPane = new JPanel(new MigLayout("filly"));
        contentPane.add(p1, "growy");

        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(Game.getCanvas());
        frame.pack();
        setLocationRelativeTo(getOwner());
        frame.setVisible(true);
    }

    private void addMuleComponents() {
        muleName.setBackground(Color.CYAN.darker());
        muleWorld.setBackground(Color.CYAN.darker());
        muleArea.setBackground(Color.CYAN);
        muleAmnt.setBackground(Color.CYAN.darker());
        muleKeep.setBackground(Color.CYAN.darker());

        p1.add(muleNameLabel, "wrap, growx");
        p1.add(muleName, "wrap, growx");
        p1.add(muleWorldLabel, "wrap, growx");
        p1.add(muleWorld, "wrap, growx");
        p1.add(muleAreaLabel, "wrap, growx");
        p1.add(muleArea, "wrap, growx");
        p1.add(muleAmntLabel, "wrap, growx");
        p1.add(muleAmnt, "wrap, growx");
        p1.add(muleKeepLabel, "wrap, growx");
        p1.add(muleKeep, "wrap, growx");
    }

    private void restockSelectionHandler(ItemEvent e) {
        String selection = (String) e.getItem();
        if (!selection.equals(RESTOCK_OPTIONS[0])) {
            p1.removeAll();

            p1.add(restockOptionLabel, "wrap, growx");
            p1.add(restockOption, "wrap, growx");
            if (selection.equals(RESTOCK_OPTIONS[1])) {
                p1.add(lootAmountLabel, "wrap, growx");
                p1.add(lootAmount, "wrap, growx");
                p1.add(foodLabel, "wrap, growx");
                p1.add(food, "wrap, growx");
            } else {
                p1.add(lootAmountLabel, "wrap, growx");
                p1.add(lootAmount, "wrap, growx");
            }
            addMuleComponents();
            p1.add(buttonStart, "wrap, growx");

            p1.updateUI();
            setFrameSizeAndLoc();
        } else {
            p1.removeAll();
            addDefaultComponents();

            p1.updateUI();
            setFrameSizeAndLoc();
        }
    }
}
