/**
 *   UNICA INTERACT TESTER
 *   (C) IBM Corp. 2013-14 - All rights reserved.
 *
 *   Author: alessiosaltarin@it.ibm.com
 */

package com.ibm.it.interact.gui;

import com.ibm.it.interact.client.Client;
import com.ibm.it.interact.client.Settings;
import com.ibm.it.interact.client.Utils;
import com.ibm.it.interact.client.data.InteractConnection;
import com.ibm.it.interact.client.data.RunData;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

class ConnectionManager extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField interactServerURLTextField;
    private JButton testConnectionButton;
    private JList connectionsList;
    private JButton newConnectionButton;
    private JButton copyButton;
    private JButton deleteButton;
    private JTextField connectionNameTextField;
    private JButton editButton;

    private boolean modifiedName;
    private boolean modifiedURL;

    private final Client client;

    /**
     * Connection manager main class
     *
     * @param theClient Handler to Client object
     */
    public ConnectionManager(Client theClient)
    {
        this.client = theClient;

        $$$setupUI$$$();
        this.setContentPane(contentPane);
        this.setModal(true);
        this.setResizable(false);
        this.getRootPane().setDefaultButton(buttonOK);

        this.setTitle("Interact Connection Manager");
        this.initializeServerList();
        this.initializeEventHandlers();

    }

    private void modifyUrl()
    {
        System.out.println("ASKED TO MODIFY URL: Modified name = " + String.valueOf(this.modifiedName));
        if (this.modifiedURL)
        {
            String newUrl = this.interactServerURLTextField.getText();
            try
            {
                @SuppressWarnings("UnusedAssignment") URL tryUrl = new URL(newUrl);
                if (Utils.isNotNullNotEmptyNotWhiteSpace(newUrl))
                {
                    this.modifyConnection();
                }
            }
            catch (MalformedURLException e)
            {
                JOptionPane.showMessageDialog(this,
                        "The URL is not valid.",
                        "Unrecognized URL", JOptionPane.ERROR_MESSAGE);
                this.interactServerURLTextField.setText("");
            }
        }

    }

    private void modifyName()
    {
        System.out.println("ASKED TO MODIFY NAME: Modified name = " + String.valueOf(this.modifiedName));
        if (this.modifiedName)
        {
            String newConnectionName = this.connectionNameTextField.getText();
            if (Utils.isNotNullNotEmptyNotWhiteSpace(newConnectionName))
            {
                if (newConnectionName.contains(" "))
                {
                    JOptionPane.showMessageDialog(this,
                            "Connection name cannot contain spaces.",
                            "Invalid name", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    this.modifyConnection();
                }
            }
        }
    }

    private void modifyConnection()
    {
        System.out.println("ASKED TO MODIFY CONNECTION: Modified name = " + String.valueOf(this.modifiedName));
        System.out.println("ASKED TO MODIFY CONNECTION: Modified URL = " + String.valueOf(this.modifiedURL));
        if (this.modifiedURL || this.modifiedName)
        {
            InteractConnection oldServer = (InteractConnection) this.connectionsList.getSelectedValue();
            if (oldServer != null)
            {
                String connectionName = this.connectionNameTextField.getText();
                String connectionUrl = this.interactServerURLTextField.getText();
                InteractConnection newServer = new InteractConnection(connectionName, connectionUrl);
                this.replaceOrAddServer(oldServer, newServer);
            }
        }

    }

    private void replaceOrAddServer(InteractConnection oldServer, InteractConnection newServer)
    {
        DefaultListModel<InteractConnection> model =
                (DefaultListModel<InteractConnection>) this.connectionsList.getModel();
        int connections = model.getSize();

        boolean replaced = false;

        for (int j = 0; j < model.getSize(); j++)
        {
            InteractConnection tempConn = model.getElementAt(j);
            if (tempConn.equals(oldServer))
            {
                // Replace
                model.set(j, newServer);
                replaced = true;
            }
        }

        if (!replaced)
        {
            model.addElement(newServer);
        }

    }

    private void initializeEventHandlers()
    {

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

        connectionNameTextField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                modifyName();
                super.focusLost(e);
            }
        });
        interactServerURLTextField.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                modifyUrl();
                super.focusLost(e);
            }
        });

        newConnectionButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                createNewConnection();
            }
        });
        copyButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                copyServer();
            }
        });
        deleteButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                deleteServer();
            }
        });
        testConnectionButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                testConnection();
            }
        });
        buttonOK.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        });

        editButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                editServer();
            }
        });

        connectionNameTextField.addKeyListener(new KeyListener()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                if (connectionNameTextField.isEditable())
                {
                    modifiedName = true;
                    System.out.println("Modified name = " + String.valueOf(modifiedName));
                }
            }

            @Override
            public void keyTyped(KeyEvent e)
            {
            }
        });

        interactServerURLTextField.addKeyListener(new KeyListener()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                if (interactServerURLTextField.isEditable())
                {
                    modifiedURL = true;
                    System.out.println("Modified URL = " + modifiedURL);
                }
            }

            @Override
            public void keyTyped(KeyEvent e)
            {
            }
        });

    }

    private void setConnectionTextBoxedEditable(boolean nameEditable, boolean urlEditable)
    {
        connectionNameTextField.setEditable(nameEditable);
        interactServerURLTextField.setEditable(urlEditable);
        this.modifiedName = nameEditable;
        this.modifiedURL = urlEditable;
        System.out.println("Name Editable = " + nameEditable);
        System.out.println("URL Editable = " + urlEditable);
    }

    private void saveConnections()
    {
        DefaultListModel model = (DefaultListModel) this.connectionsList.getModel();
        int connections = model.getSize();

        if (connections > 0)
        {
            Settings settings = Settings.getInstance(this.client.getLogger());

            ArrayList<InteractConnection> connectionArrayList = new ArrayList<>(connections);
            for (int j = 0; j < model.getSize(); j++)
            {
                InteractConnection ic = (InteractConnection) model.getElementAt(j);
                connectionArrayList.add(ic);
            }

            settings.setUnicaServers(connectionArrayList);
            settings.writeProperties();
        }
    }

    private void initializeServerList()
    {
        Settings settings = Settings.getInstance(this.client.getLogger());
        settings.readProperties();
        Properties props = settings.getProperties();
        Set<String> propertyNames = props.stringPropertyNames();

        for (String prop : propertyNames)
        {
            if (prop.startsWith("interact.server."))
            {
                String connName = prop.substring(16);
                String connUrl = props.getProperty(prop);
                InteractConnection ic = new InteractConnection(connName, connUrl);
                DefaultListModel model = (DefaultListModel) this.connectionsList.getModel();
                model.addElement(ic);
            }
        }

    }

    private void onOK()
    {
        this.saveConnections();
        dispose();
    }

    private void onCancel()
    {
        dispose();
    }

    private void createNewConnection()
    {
        this.setConnectionTextBoxedEditable(true, true);
        int connectionsSize = this.connectionsList.getModel().getSize();
        InteractConnection ic = new InteractConnection(connectionsSize + 1, "http://localhost:8080");
        DefaultListModel model = (DefaultListModel) this.connectionsList.getModel();
        model.addElement(ic);
        this.connectionsList.setSelectedIndex(connectionsSize);
        this.connectionNameTextField.requestFocus();
    }

    private void copyServer()
    {
        InteractConnection ic = (InteractConnection) this.connectionsList.getSelectedValue();
        if (ic != null)
        {
            String copyName = "Copy of " + ic.getConnectionName();
            String copyUrl = ic.getConnectionUrl().toString();
            InteractConnection newIc = new InteractConnection(copyName, copyUrl);
            DefaultListModel model = (DefaultListModel) this.connectionsList.getModel();
            model.addElement(newIc);
        }
    }

    private void editServer()
    {
        this.setConnectionTextBoxedEditable(true, true);
        this.connectionNameTextField.requestFocus();
    }

    private void deleteServer()
    {
        if (this.connectionsList.getSelectedIndex() >= 0)
        {
            DefaultListModel model = (DefaultListModel) this.connectionsList.getModel();
            model.remove(this.connectionsList.getSelectedIndex());
            this.connectionNameTextField.setText("");
            this.interactServerURLTextField.setText("");
        }
    }

    private void setSelectedServer(int serverIndex)
    {
        InteractConnection ic = (InteractConnection) this.connectionsList.getSelectedValue();
        if (ic != null)
        {
            this.connectionNameTextField.setText(ic.getConnectionName());
            this.interactServerURLTextField.setText(ic.getConnectionSimplifiedUrl());
        }
        /*
        else
        {
            this.connectionNameTextField.setText("");
            this.interactServerURLTextField.setText("");
        } */
    }

    private void testConnection()
    {
        String connName = this.connectionNameTextField.getText();
        String connUrl = this.interactServerURLTextField.getText();
        if (Utils.isNotNullNotEmptyNotWhiteSpace(connName))
        {
            if (Utils.isNotNullNotEmptyNotWhiteSpace(connUrl))
            {
                InteractConnection ic = new InteractConnection(connName, connUrl);
                RunData rd = new RunData(ic, null);
                if (this.client.testConnection(rd))
                {
                    JOptionPane.showMessageDialog(this,
                            "Connection test: OK",
                            "Test connection", JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    JOptionPane.showMessageDialog(this,
                            "Connection test: KO",
                            "Test connection", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }

    private void createUIComponents()
    {
        this.connectionsList = new JList();
        this.connectionsList.setName("Connections");
        this.connectionsList.setModel(new DefaultListModel());
        ListSelectionModel listSelectionModel = this.connectionsList.getSelectionModel();
        listSelectionModel.addListSelectionListener(new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                int selIndex = e.getFirstIndex();
                setSelectedServer(selIndex);
            }
        });
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
        contentPane.setLayout(new GridLayoutManager(2, 2, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
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
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new FormLayout("fill:d:grow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel3.add(panel4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Connection Name:");
        CellConstraints cc = new CellConstraints();
        panel4.add(label1, cc.xy(1, 1));
        final JLabel label2 = new JLabel();
        label2.setText("Interact Server URL:");
        panel4.add(label2, cc.xy(1, 5));
        interactServerURLTextField = new JTextField();
        interactServerURLTextField.setEditable(false);
        panel4.add(interactServerURLTextField, cc.xy(1, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        testConnectionButton = new JButton();
        testConnectionButton.setText("Test Connection");
        panel4.add(testConnectionButton, cc.xy(1, 9, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        connectionNameTextField = new JTextField();
        connectionNameTextField.setEditable(false);
        panel4.add(connectionNameTextField, cc.xy(1, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new FormLayout("fill:85px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel3.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        newConnectionButton = new JButton();
        newConnectionButton.setText("New");
        panel5.add(newConnectionButton, cc.xy(1, 7));
        final JLabel label3 = new JLabel();
        label3.setText("Available Connections:");
        panel5.add(label3, cc.xyw(1, 1, 3));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel5.add(scrollPane1, cc.xywh(1, 3, 3, 3, CellConstraints.DEFAULT, CellConstraints.FILL));
        connectionsList.setSelectionMode(1);
        scrollPane1.setViewportView(connectionsList);
        copyButton = new JButton();
        copyButton.setText("Copy");
        panel5.add(copyButton, cc.xy(1, 9));
        deleteButton = new JButton();
        deleteButton.setText("Delete");
        panel5.add(deleteButton, cc.xy(3, 9));
        editButton = new JButton();
        editButton.setText("Edit");
        panel5.add(editButton, cc.xy(3, 7));
        label1.setLabelFor(connectionNameTextField);
        label2.setLabelFor(interactServerURLTextField);
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
