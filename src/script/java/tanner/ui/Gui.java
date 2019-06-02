package script.java.tanner.ui;

import net.miginfocom.swing.MigLayout;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.StopWatch;
import script.java.tanner.Main;
import script.java.tanner.data.Hides;
import script.java.tanner.data.MuleArea;

import java.awt.*;
import javax.swing.*;

public class Gui extends JFrame {
    private Main ctx;

    public Gui(Main main) {
        this.ctx = main;
        initComponents();
        this.setVisible(false);
    }

    private void buttonStartActionPerformed() {
        ctx.timeRan = StopWatch.start();
        Hides sel = (Hides) hide.getSelectedItem();
        ctx.COWHIDE = sel.getHideId();
        ctx.restockMaxProfitHide = restockMaxProfitHide.isSelected();
        ctx.calcMacProfitOnStart = calcMacProfitOnStart.isSelected();
        ctx.canTanSameHideTwice = canTanSameHideTwice.isSelected();
        ctx.addHidePrice = Integer.parseInt(addHidePrice.getText());
        ctx.subLeatherPrice = Integer.parseInt(subLeatherPrice.getText());
        ctx.resetGeTime = Integer.parseInt(resetGeTime.getText());
        ctx.intervalAmnt = Integer.parseInt(intervalAmnt.getText());
        ctx.numStamina = Integer.parseInt(numStamina.getText());
        ctx.smartPotions = smartPotions.isSelected();
        ctx.willBuyW = willBuyW.isSelected();
        ctx.willBuyD = willBuyD.isSelected();
        ctx.muleAmnt = Integer.parseInt(muleAmnt.getText());
        ctx.muleKeep = Integer.parseInt(muleKeep.getText());
        ctx.muleArea = (MuleArea) muleArea.getSelectedItem();

        frame.setVisible(false);
        ctx.setPaused(false);
    }


    private void initComponents() {
        //======== JFrame/JPanel ========
        frame = new JFrame("Ultimate AIO Tanner");
        JPanel p1 = new JPanel(new MigLayout("filly, wrap 2"));

        //======== Instantiation ========
        buttonStart = new JButton("START");
        hideLabel = new JLabel("Select Starting Hide");
        hide = new JComboBox(Hides.values());
        restockMaxProfitHide = new JCheckBox("Calculate & Switch To Max Profit Hide After Selling Leathers?");
        calcMacProfitOnStart = new JCheckBox("Switch to Max Profit On Restock/Start?");
        canTanSameHideTwice = new JCheckBox("Can Tan The Same Hide Twice In A Row? Otherwise, Sets Second Most Profitable");
        addHidePriceLabel = new JLabel("Increase Set Buying GP Per Hide By:");
        addHidePrice = new JTextField();
        subLeatherPriceLabel = new JLabel("Decrease Set Selling GP Per Leather By:");
        subLeatherPrice = new JTextField();
        resetGeTimeLabel = new JLabel("Time(min) To Increase/Decrease Price:");
        resetGeTime = new JTextField();
        intervalAmntLabel = new JLabel("Amount To Increase/Decrease By Each Interval:");
        intervalAmnt = new JTextField();
        numStaminaLabel = new JLabel("Number Of Stamina Potions To Buy Each Restock:");
        numStamina = new JTextField();
        smartPotions = new JCheckBox("Smart Potions: Increase The Number Of Potions To What You Needed Last Time");
        willBuyW = new JCheckBox("Buy Ring Of Wealth?");
        willBuyD = new JCheckBox("Buy Ring Of Dueling?");
        muleAmntLabel = new JLabel("Amount To Mule At:");
        muleAmnt = new JTextField();
        muleKeepLabel = new JLabel("Amount To Keep From Mule:");
        muleKeep = new JTextField();
        muleAreaLabel = new JLabel("GE Area To Mule:");
        muleArea = new JComboBox(MuleArea.values());

        //---- buttonStart ----
        buttonStart.setFocusCycleRoot(true);
        buttonStart.setFont(new Font(".SF NS Text", Font.PLAIN, 20));
        buttonStart.setBackground(new Color(57, 67, 54));
        buttonStart.addActionListener(e -> buttonStartActionPerformed());

        //======== Add to Panel ========
        p1.add(hideLabel, "wrap, growx");
        p1.add(hide, "wrap, growx");
        p1.add(restockMaxProfitHide, "wrap, growx");
        p1.add(calcMacProfitOnStart, "wrap, growx");
        p1.add(canTanSameHideTwice, "wrap, growx");
        p1.add(addHidePriceLabel, "wrap, growx");
        p1.add(addHidePrice, "wrap, growx");
        p1.add(subLeatherPriceLabel, "wrap, growx");
        p1.add(subLeatherPrice, "wrap, growx");
        p1.add(resetGeTimeLabel, "wrap, growx");
        p1.add(resetGeTime, "wrap, growx");
        p1.add(intervalAmntLabel, "wrap, growx");
        p1.add(intervalAmnt, "wrap, growx");
        p1.add(numStaminaLabel, "wrap, growx");
        p1.add(numStamina, "wrap, growx");
        p1.add(smartPotions, "wrap, growx");
        p1.add(willBuyW, "wrap, growx");
        p1.add(willBuyD, "wrap, growx");
        p1.add(muleAreaLabel, "wrap, growx");
        p1.add(muleArea, "wrap, growx");
        p1.add(muleAmntLabel, "wrap, growx");
        p1.add(muleAmnt, "wrap, growx");
        p1.add(muleKeepLabel, "wrap, growx");
        p1.add(muleKeep, "wrap, growx");

        p1.add(buttonStart, "wrap, growx");

        JPanel contentPane = new JPanel(new MigLayout("filly"));
        contentPane.add(p1, "growy");

        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(Game.getCanvas());
        frame.pack();
        setLocationRelativeTo(getOwner());
        frame.setVisible(true);
        // buttonStartActionPerformed();
    }

    private JFrame frame;
    private JButton buttonStart;
    private JComboBox hide;
    private JLabel hideLabel;
    private JCheckBox restockMaxProfitHide;
    private JCheckBox calcMacProfitOnStart;
    private JCheckBox canTanSameHideTwice;
    private JLabel addHidePriceLabel;
    private JTextField addHidePrice;
    private JLabel subLeatherPriceLabel;
    private JTextField subLeatherPrice;
    private JLabel resetGeTimeLabel;
    private JTextField resetGeTime;
    private JLabel intervalAmntLabel;
    private JTextField intervalAmnt;
    private JLabel numStaminaLabel;
    private JTextField numStamina;
    private JCheckBox smartPotions;
    private JCheckBox willBuyW;
    private JCheckBox willBuyD;
    private JLabel muleAmntLabel;
    private JTextField muleAmnt;
    private JLabel muleKeepLabel;
    private JTextField muleKeep;
    private JLabel muleAreaLabel;
    private JComboBox muleArea;
}
