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
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
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
    private final XLog log;

    private ParameterDialog(JFrame parent, XLog logger)
    {
        this.log = logger;
        this.nvp = new NameValuePairImpl();
        $$$setupUI$$$();
        this.setTitle("Add Name-Value Parameter");
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
        $$$setupUI$$$();
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
        this.updateNvp();

        boolean okToGo = true;
        NameValuePairDecor nvpd = new NameValuePairDecor(this.nvp);

        if (!Utils.isNotNullNotEmptyNotWhiteSpace(nvpd.getKey()))
        {
            okToGo = false;
            JOptionPane.showMessageDialog(this,
                    "Key not recognized",
                    "Invalid Key", JOptionPane.ERROR_MESSAGE);
        }

        String nvpdVal = nvpd.getValue();
        if (nvpdVal == null)
        {
            okToGo = false;
            JOptionPane.showMessageDialog(this,
                    "Value not recognized",
                    "Invalid Value", JOptionPane.ERROR_MESSAGE);
        }

        if (okToGo)
        {
            dispose();
        }

    }

    private void onCancel()
    {
        this.nvp = null;
        dispose();
    }

    private void updateNvp()
    {

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
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(this.spinner1, "yyyy-MM-dd HH:mm:ss.SSS");
        this.spinner1.setEditor(timeEditor);
        this.spinner1.setValue(new Date()); // will only show the current time
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$()
    {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(3, 3, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Key:");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        keyTextField = new JTextField();
        panel3.add(keyTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        labelLabel = new JLabel();
        labelLabel.setText("Value: ");
        panel3.add(labelLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, 1, null, null, null, 0, false));
        valueTextField = new JTextField();
        panel3.add(valueTextField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Type: ");
        panel3.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        typeComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("String");
        defaultComboBoxModel1.addElement("Numeric");
        defaultComboBoxModel1.addElement("Date");
        typeComboBox.setModel(defaultComboBoxModel1);
        panel3.add(typeComboBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelDate = new JLabel();
        labelDate.setText("Date:");
        panel3.add(labelDate, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel3.add(spinner1, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("");
        contentPane.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        contentPane.add(spacer2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        label1.setLabelFor(keyTextField);
        labelLabel.setLabelFor(valueTextField);
        label2.setLabelFor(typeComboBox);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont)
    {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null)
        {
            resultName = currentFont.getName();
        }
        else
        {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1'))
            {
                resultName = fontName;
            }
            else
            {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    {
        return contentPane;
    }
}
