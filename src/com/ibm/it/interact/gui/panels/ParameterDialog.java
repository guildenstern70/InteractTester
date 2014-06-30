/**
 *   UNICA INTERACT TESTER
 *
 *   IBM Confidential
 *   (C) IBM Corp. 2013-14 - All rights reserved.
 *
 *   The source code for this program is not published or otherwise
 *   divested of its trade secrets, irrespective of what has been
 *   deposited with the U.S. Copyright Office.
 *
 *   Author: alessiosaltarin@it.ibm.com
 */

package com.ibm.it.interact.gui.panels;

import com.ibm.it.interact.client.Utils;
import com.ibm.it.interact.client.XLog;
import com.ibm.it.interact.client.data.NameValuePairDecor;
import com.unicacorp.interact.api.NameValuePair;
import com.unicacorp.interact.api.NameValuePairImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class ParameterDialog extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField keyTextField;
    private JTextField valueTextField;
    private JComboBox typeComboBox;
    private JLabel labelLabel;
    private JLabel labelDate;
    private JSpinner spinner1;
    private NameValuePair nvp;
    private JFrame parent;
    final private XLog log;

    private ParameterDialog(JFrame parent, XLog logger)
    {
        this.log = logger;
        this.nvp = new NameValuePairImpl();
        this.setTitle("Adding Name-Value Parameter...");
        this.init();
        typeComboBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                onChangeType();
            }
        });
    }

    private void onChangeType()
    {
        int comboIndex = this.typeComboBox.getSelectedIndex();
        if (comboIndex == 2)
        {
            this.valueTextField.setVisible(false);
            this.labelLabel.setVisible(false);
            this.spinner1.setVisible(true);
            this.labelDate.setVisible(true);
        }
        else
        {
            this.spinner1.setVisible(false);
            this.labelDate.setVisible(false);
            this.labelLabel.setVisible(true);
            this.valueTextField.setVisible(true);
        }

    }

    private ParameterDialog(JFrame parent, XLog logger, NameValuePairDecor nvpd)
    {
        this.log = logger;
        this.nvp = nvpd.getNameValuePair();
        this.setTitle("Editing Parameter " + nvpd.getNameValuePair().getName());
        this.init();
        this.fillFields(nvpd);
    }

    public NameValuePair getNameValuePair()
    {
        return this.nvp;
    }

    private void fillFields(NameValuePairDecor nvp)
    {
        NameValuePair nvpX = nvp.getNameValuePair();
        this.valueTextField.setText(nvp.getValue());
        this.keyTextField.setText(nvpX.getName());
        if (nvp.isNumeric())
        {
            this.typeComboBox.setSelectedIndex(1);
        }
        else if (nvp.isString())
        {
            this.typeComboBox.setSelectedIndex(0);
        }
        else
        {
            this.typeComboBox.setSelectedIndex(2);
        }
    }

    private void init()
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(this.parent);

        this.spinner1.setVisible(false);
        this.labelDate.setVisible(false);

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
                                           }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK()
    {
        if (this.updateNvp())
        {
            boolean okToGo = true;
            NameValuePairDecor nvpd = new NameValuePairDecor(this.nvp);

            if (!Utils.isNotNullNotEmptyNotWhiteSpace(nvpd.getKey()))
            {
                okToGo = false;
                JOptionPane.showMessageDialog(this,
                        "Invalid Key",
                        "Key not recognized", JOptionPane.ERROR_MESSAGE);
            }
            else if (!Utils.isNotNullNotEmptyNotWhiteSpace(nvpd.getValue()))
            {
                okToGo = false;
                JOptionPane.showMessageDialog(this,
                        "Invalid Value",
                        "Value not recognized", JOptionPane.ERROR_MESSAGE);
            }

            if (okToGo)
            {
                dispose();
            }
        }
    }

    private void onCancel()
    {
        this.nvp = null;
        dispose();
    }

    private boolean updateNvp()
    {
        boolean updateWasCorrect = true;

        this.nvp.setName(this.keyTextField.getText());
        int index = this.typeComboBox.getSelectedIndex();
        switch (index)
        {
            case 0: // string
                this.nvp.setValueDataType("string");
                this.nvp.setValueAsString(this.valueTextField.getText());
                break;

            case 1: // numeric
                this.nvp.setValueDataType("numeric");
                this.nvp.setValueAsNumeric(Double.parseDouble(this.valueTextField.getText()));
                break;

            case 2: // date
                this.nvp.setValueDataType("datetime");
                Date d1;
                SpinnerDateModel model = (SpinnerDateModel) this.spinner1.getModel();
                d1 = model.getDate();
                this.nvp.setValueAsDate(d1);
                break;
        }

        return updateWasCorrect;
    }

    public static ParameterDialog showDialog(JFrame parent, XLog log)
    {
        ParameterDialog dialog = new ParameterDialog(parent, log);
        dialog.pack();
        dialog.setSize(new Dimension(450, 220));
        dialog.setVisible(true);
        return dialog;
    }

    public static ParameterDialog showDialog(JFrame parent, XLog log, NameValuePairDecor nvp)
    {
        ParameterDialog dialog = new ParameterDialog(parent, log, nvp);
        dialog.pack();
        dialog.setSize(new Dimension(450, 220));
        dialog.setVisible(true);
        return dialog;
    }

    private void createUIComponents()
    {
        this.spinner1 = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(this.spinner1, "dd-MMM-yyyy HH:mm:ss");
        this.spinner1.setEditor(timeEditor);
        this.spinner1.setValue(new Date()); // will only show the current time
    }
}
