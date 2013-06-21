/**
 *   UNICA INTERACT TESTER
 *   (C) IBM Corp. 2013 - All rights reserved.
 *
 *   Author: alessiosaltarin@it.ibm.com
 */


package com.ibm.it.interact.gui;

import com.ibm.it.interact.client.Settings;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SettingsForm extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox generateRandomSessionIDCheckBox;
    private JCheckBox experimentalFlagCheckBox;
    final private Settings settings;
    private boolean returnOk;

    public SettingsForm(Settings mainSettings)
    {
        this.returnOk = false;
        this.settings = mainSettings;
        this.loadProperties();

        setContentPane(contentPane);
        setModal(true);
        setTitle("Interact Tester Settings");
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public boolean showDialog(JFrame frame)
    {
        this.pack();
        this.setSize(new Dimension(350, 200));
        this.setResizable(false);
        this.setLocationRelativeTo(frame);
        this.setVisible(true);
        return this.returnOk;
    }

    private void onOK()
    {
        this.settings.setGenerateSessionIdAtStartup(this.generateRandomSessionIDCheckBox.isSelected());
        this.settings.writeProperties();
        this.returnOk = true;
        this.setVisible(false);
        dispose();
    }

    private void onCancel()
    {
        this.setVisible(false);
        dispose();
    }

    private void loadProperties()
    {
        this.settings.readProperties();
        this.generateRandomSessionIDCheckBox.setSelected(this.settings.isGenerateSessionIdAtStartup());
    }

}
