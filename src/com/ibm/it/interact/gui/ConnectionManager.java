package com.ibm.it.interact.gui;

import com.ibm.it.interact.client.Client;
import com.ibm.it.interact.client.Settings;
import com.ibm.it.interact.client.Utils;
import com.ibm.it.interact.client.data.InteractConnection;
import com.ibm.it.interact.client.data.RunData;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

    private Client client;

    public ConnectionManager(Client theClient)
    {
        this.client = theClient;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.setTitle("Interact Connection Manager");
        this.initializeServerList();
        this.initializeEventHandlers();
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
    }

    private void modifyUrl()
    {
        String newUrl = this.interactServerURLTextField.getText();

        try
        {
            URL tryUrl = new URL(newUrl);
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

    private void modifyName()
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
                DefaultListModel model = (DefaultListModel) this.connectionsList.getModel();
                this.connectionsList.setSelectedIndex(model.getSize() - 1);
            }
        }
    }

    private void modifyConnection()
    {
        InteractConnection oldServer = (InteractConnection) this.connectionsList.getSelectedValue();

        if (oldServer != null)
        {
            String connectionName = this.connectionNameTextField.getText();
            String connectionUrl = this.interactServerURLTextField.getText();
            InteractConnection newServer = new InteractConnection(connectionName, connectionUrl);
            this.replaceOrAddServer(oldServer, newServer);
        }
        else
        {
            System.out.println("A server is not selected from the list.");
        }

    }

    private void replaceOrAddServer(InteractConnection oldServer, InteractConnection newServer)
    {
        DefaultListModel model = (DefaultListModel) this.connectionsList.getModel();
        int connections = model.getSize();

        // Build a list of servers. If the oldServer is found, it is not added
        // to the list: it will be added the new one. If it is not found, then
        // the new one will be added.
        ArrayList<InteractConnection> connectionArrayList = new ArrayList<InteractConnection>(connections);
        for (int j = 0; j < model.getSize(); j++)
        {
            InteractConnection tempConn = (InteractConnection) model.getElementAt(j);
            if (!tempConn.equals(oldServer))
            {
                System.out.println("Adding " + tempConn.toString());
                connectionArrayList.add(tempConn);
            }
        }
        System.out.println("Adding NEW SERVER " + newServer.toString());
        connectionArrayList.add(newServer);

        // Clear the list and replace with the new one
        model.clear();
        for (InteractConnection conn : connectionArrayList)
        {
            model.addElement(conn);
        }

    }

    private void modifyConnection(String newConnectionName, InteractConnection oldConn)
    {
        InteractConnection newConn = new InteractConnection(newConnectionName,
                this.interactServerURLTextField.getText());
        DefaultListModel model = (DefaultListModel) this.connectionsList.getModel();
        int connections = model.getSize();
        ArrayList<InteractConnection> connectionArrayList = new ArrayList<InteractConnection>(connections);
        for (int j = 0; j < model.getSize(); j++)
        {
            InteractConnection tempConn = (InteractConnection) model.getElementAt(j);
            if (!tempConn.equals(oldConn))
            {
                System.out.println("Adding " + tempConn.toString());
                connectionArrayList.add(tempConn);
            }
        }
        model.clear();
        for (InteractConnection conn : connectionArrayList)
        {
            model.addElement(conn);
        }
        model.addElement(newConn);
    }

    private void initializeEventHandlers()
    {
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

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
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

    }

    private void saveConnections()
    {
        DefaultListModel model = (DefaultListModel) this.connectionsList.getModel();
        int connections = model.getSize();

        if (connections > 0)
        {
            Settings settings = Settings.getInstance(this.client.getLogger());

            ArrayList<InteractConnection> connectionArrayList = new ArrayList<InteractConnection>(connections);
            for (int j = 0; j < model.getSize(); j++)
            {
                InteractConnection ic = (InteractConnection) model.getElementAt(j);
                System.out.println("Adding " + ic.toString());
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
        int connectionsSize = this.connectionsList.getModel().getSize();
        InteractConnection ic = new InteractConnection(connectionsSize + 1, "http://localhost:8080");
        DefaultListModel model = (DefaultListModel) this.connectionsList.getModel();
        model.addElement(ic);
        this.connectionsList.setSelectedIndex(connectionsSize);
    }

    private void copyServer()
    {
        InteractConnection ic = (InteractConnection) this.connectionsList.getSelectedValue();
        if (ic != null)
        {
            int connectionsSize = this.connectionsList.getModel().getSize();
            String copyName = "Copy of " + ic.getConnectionName();
            String copyUrl = ic.getConnectionUrl().toString();
            InteractConnection newIc = new InteractConnection(copyName, copyUrl);
            DefaultListModel model = (DefaultListModel) this.connectionsList.getModel();
            model.addElement(newIc);
        }
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
