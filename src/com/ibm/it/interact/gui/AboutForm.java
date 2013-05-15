/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.gui;

import com.ibm.it.interact.client.Settings;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class AboutForm extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel pictureLabel;
    private JLabel versionLabel;

    public AboutForm()
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("About");
        buttonOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onOK();
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.versionLabel.setText("Version " + Settings.VERSION + ". Copyright IBM Corp. 2013");
    }

    private void onOK()
    {
        dispose();
    }

    private void onCancel()
    {
        dispose();
    }

    private void createUIComponents()
    {
        try
        {
            this.pictureLabel = new JLabel();
            String imgLocation = "/res/logo.png";
            URL imageURL = getClass().getResource(imgLocation);
            final ImageIcon topbar = new ImageIcon(imageURL);
            this.pictureLabel.setBounds(new Rectangle(50, 20, 230, 200));
            this.pictureLabel.setText("");
            this.pictureLabel.setIcon(topbar);
            this.pictureLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        catch (NullPointerException np)
        {
            System.out.println("Cannot find Unica logo file");
        }
    }
}
