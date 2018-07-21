/*
 * Created by JFormDesigner on Sat Jul 21 14:43:37 NZST 2018
 */

package SimpleTanner;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class SimpleTannerGUI extends JFrame {
    private MainClass ctx;

    SimpleTannerGUI(MainClass main) {
        this.ctx = main;
        initComponents();
    }

    private void buttonStartActionPerformed(ActionEvent e) {
        ctx.setStartScript();
        this.setVisible(false);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Troy Lamerton
        textArea1 = new JTextArea();
        buttonStart = new JButton();

        //======== this ========
        setBackground(new Color(51, 51, 51));
        setTitle("Simple Tanner");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- textArea1 ----
        textArea1.setText("Tans Cowhide into Leather in Al Kharid. Stops when you run out of Cowhide or coins.");
        textArea1.setLineWrap(true);
        textArea1.setWrapStyleWord(true);
        textArea1.setEditable(false);
        textArea1.setFont(new Font(".SF NS Text", Font.PLAIN, 16));
        contentPane.add(textArea1);
        textArea1.setBounds(10, 10, 342, 55);

        //---- buttonStart ----
        buttonStart.setText("START");
        buttonStart.setFocusCycleRoot(true);
        buttonStart.setFont(new Font(".SF NS Text", Font.PLAIN, 20));
        buttonStart.setBackground(new Color(57, 67, 54));
        buttonStart.addActionListener(e -> buttonStartActionPerformed(e));
        contentPane.add(buttonStart);
        buttonStart.setBounds(8, 75, 346, 60);

        contentPane.setPreferredSize(new Dimension(365, 140));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private JButton buttonStart;
    private JTextArea textArea1;

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Troy Lamerton
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
