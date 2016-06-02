package nl.siegmann.epublib.viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * First stab at an about dialog.
 * @author paul.siegmann
 */
public class AboutDialog extends JDialog {

    private static final long serialVersionUID = -1766802200843275782L;

    public AboutDialog(JFrame parent) {
        super(parent, true);

        super.setResizable(false);
        super.getContentPane().setLayout(new GridLayout(3, 1));
        super.setSize(400, 150);
        super.setTitle("About epublib");
        super.setLocationRelativeTo(parent);

        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AboutDialog.this.dispose();
            }
        });
        super.getRootPane().setDefaultButton(close);
        add(new JLabel("epublib viewer"));
        add(new JLabel("http://www.siegmann.nl/epublib"));
        add(close);
        super.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                AboutDialog.this.dispose();
            }
        });
        pack();
        setVisible(true);

    }
}