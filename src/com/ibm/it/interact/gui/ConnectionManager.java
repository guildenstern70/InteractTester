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

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

public class ConnectionManager extends JDialog
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

    public ConnectionManager(Client theClient)
    {
        this.client = theClient;

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
                        "Unrecognized URL format.", JOptionPane.ERROR_MESSAGE);
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
                this.resetTextBoxes();
            }
        }

    }

    private void resetTextBoxes()
    {
        this.connectionNameTextField.setEditable(false);
        this.interactServerURLTextField.setEditable(false);
        this.modifiedName = false;
        this.modifiedURL = false;
        System.out.println("== RESET RESET RESET ==");
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

    private void setConnectionTextBoxedEditable(boolean editable)
    {
        connectionNameTextField.setEditable(editable);
        interactServerURLTextField.setEditable(editable);
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
        this.setConnectionTextBoxedEditable(true);
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
        this.setConnectionTextBoxedEditable(true);
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
}
