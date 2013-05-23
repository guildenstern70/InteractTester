/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.gui;

import com.ibm.it.interact.client.Client;
import com.ibm.it.interact.client.Settings;
import com.ibm.it.interact.client.Utils;
import com.ibm.it.interact.client.XLog;
import com.ibm.it.interact.client.data.InteractConnection;
import com.ibm.it.interact.client.data.LogDataFile;
import com.ibm.it.interact.client.data.RunData;
import com.ibm.it.interact.client.data.RunDataFile;
import com.ibm.it.interact.client.data.RunDataSerializer;
import com.ibm.it.interact.client.data.TextFile;
import com.ibm.it.interact.gui.panels.BatchExecute;
import com.ibm.it.interact.gui.panels.GetOffers;
import com.ibm.it.interact.gui.panels.PostEvent;
import com.ibm.it.interact.gui.panels.StartSession;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Main Interact Test form.
 * Designed with IntelliJ Idea Form Designer
 */
public final class MainForm
{
    private final static Charset ENCODING = StandardCharsets.UTF_8;
    private static final String TITLE = "IBM Campaign Interact Tester";
    private static final Dimension WINDOW_SIZE = new Dimension(770, 660);

    // Business logic
    private Client client;
    private Settings settings;
    private RunData currentRunData;

    // Panels
    private StartSession startSessionPanel;
    private GetOffers getOffersPanel;
    private PostEvent postEventPanel;
    private BatchExecute batchExecutePanel;

    // Controls
    final private JFrame frame;
    private JFileChooser fileChooser;
    private JPanel pnlMain;
    private JLabel lblStatusBar;
    private JTabbedPane tabbedPane;
    private JTextArea txtConsole;
    private JComboBox interactURLComboBox;
    private JTextField sessionTextField;
    private JButton generateIdButton;
    private JButton connManagerButton;

    // Menu
    private JMenuBar jMenuBar;
    private JMenu jMenuFile;
    private JMenuItem jMenuFileNew;
    private JMenuItem jMenuFileOpen;

    // Private methods
    private JMenuItem jMenuFileSave;
    private JMenuItem jMenuFileSaveAs;
    private JMenuItem jMenuFileExportData;
    private JSeparator jSeparator1;
    private JSeparator jSeparator2;
    private JSeparator jSeparator3;
    private JSeparator jSeparator4;
    private JSeparator jSeparator5;
    private JSeparator jSeparator6;
    private JMenuItem jMenuFileExit;
    private JMenuItem jMenuSettings;
    private JMenu jMenuTools;
    private JMenuItem jMenuToolsClearLog;
    private JMenuItem jMenuToolsSaveLog;
    private JMenuItem jMenuToolsTestConnection;
    private JMenuItem jMenuToolsConnManager;
    private JMenu jMenuHelp;
    private JMenuItem jMenuHelpIBMHome;
    private JMenuItem jMenuHelpUnicaHome;
    private JMenuItem jMenuHelpUnicaManual;
    private JMenuItem jMenuHelpUnicaInteractHome;
    private JMenuItem jMenuHelpAbout;
    private JMenuItem jMenuToolsEndSession;

    private MainForm(JFrame frm)
    {
        System.out.println("Welcome to Interact Tester v." + Settings.VERSION);
        this.frame = frm;
        connManagerButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                showConnectionManager();
            }
        });
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                shutdown();
            }
        });
    }

    /**
     * Main Form display
     *
     * @return The form displayed
     */
    public static MainForm show()
    {
        final JFrame frame = new JFrame("MainForm");
        final MainForm itf = new MainForm(frame);

        // Initialize business logic
        XLog xlog = new XLog(itf);
        xlog.initialize();
        itf.initializeLogicAndReadSettings(xlog);

        // Initialize UI
        itf.initializeTabs();
        itf.initializeMenus();
        itf.initializeControls();

        frame.setContentPane(itf.pnlMain);
        frame.setJMenuBar(itf.jMenuBar);

        Image icon = itf.getWindowIcon();
        if (icon != null)
        {
            frame.setIconImage(itf.getWindowIcon());
        }

        frame.pack();
        itf.showInterface(frame);
        return itf;
    }

    private Image getWindowIcon()
    {
        Image icon = null;

        String imgLocation = "/res/UnicaIcon.gif";
        URL imageURL = getClass().getResource(imgLocation);
        if (imageURL != null)
        {
            icon = Toolkit.getDefaultToolkit().getImage(imageURL);
        }

        return icon;
    }

    private void showInterface(JFrame frame)
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = WINDOW_SIZE;

        if (this.settings != null)
        {
            Dimension savedSize = this.settings.clientDimensions();
            if (savedSize != null)
            {
                size = savedSize;
            }
        }

        frame.setMinimumSize(size);
        frame.setTitle(TITLE);
        frame.setLocation((screenSize.width - size.width) / 2,
                (screenSize.height - size.height) / 2);
        frame.setSize(size);
        frame.setResizable(true);
        frame.setMinimumSize(WINDOW_SIZE);
        frame.setVisible(true);
    }

    public JPanel getMainPanel()
    {
        return this.pnlMain;
    }

    public PostEvent getPostEventPanel()
    {
        return this.postEventPanel;
    }

    public StartSession getStartSessionPanel()
    {
        return this.startSessionPanel;
    }

    public Settings getSettings()
    {
        return this.settings;
    }

    public GetOffers getGetOffersPanel()
    {
        return this.getOffersPanel;
    }

    public JFrame getFrame()
    {
        return this.frame;
    }

    public Client getClient()
    {
        return this.client;
    }

    public String getSessionId()
    {
        return this.sessionTextField.getText();
    }

    public InteractConnection getInteractServer()
    {
        return (InteractConnection) this.interactURLComboBox.getSelectedItem();
    }

    public String getInteractionPoint()
    {
        return this.getOffersPanel.getInteractionPoint();
    }

    public synchronized void updateConsole(final String message)
    {
        final JTextArea console = this.txtConsole;
        console.append(message);
        console.append("\n");
        console.setCaretPosition(console.getText().length() - 1);
    }

    private void initializeTabs()
    {
        this.startSessionPanel = new StartSession(this);
        this.getOffersPanel = new GetOffers(this);
        this.postEventPanel = new PostEvent(this);
        this.batchExecutePanel = new BatchExecute(this);


        this.tabbedPane.addTab(this.startSessionPanel.getTitle(), this.startSessionPanel.getPanel());
        this.tabbedPane.addTab(this.getOffersPanel.getTitle(), this.getOffersPanel.getPanel());
        this.tabbedPane.addTab(this.postEventPanel.getTitle(), this.postEventPanel.getPanel());
        this.tabbedPane.addTab(this.batchExecutePanel.getTitle(), this.batchExecutePanel.getPanel());
    }

    private void initializeLogicAndReadSettings(XLog xlog)
    {
        this.client = new Client(xlog);
        this.settings = Settings.getInstance(this.client.getLogger());
        this.settings.readProperties();
    }

    private void initializeMenus()
    {
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuFileNew = new javax.swing.JMenuItem();
        jMenuFileOpen = new javax.swing.JMenuItem();
        jMenuFileSave = new javax.swing.JMenuItem();
        jMenuFileSaveAs = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuFileExportData = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        jMenuSettings = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuFileExit = new javax.swing.JMenuItem();
        jMenuTools = new javax.swing.JMenu();
        jMenuToolsClearLog = new javax.swing.JMenuItem();
        jMenuToolsSaveLog = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jMenuToolsTestConnection = new javax.swing.JMenuItem();
        jMenuToolsConnManager = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jMenuToolsEndSession = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuHelpIBMHome = new javax.swing.JMenuItem();
        jMenuHelpUnicaHome = new javax.swing.JMenuItem();
        jMenuHelpUnicaManual = new javax.swing.JMenuItem();
        jMenuHelpUnicaInteractHome = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuHelpAbout = new javax.swing.JMenuItem();

        jMenuFile.setText("File");
        jMenuFile.addMenuListener(new MenuListener()
        {

            public void menuSelected(MenuEvent e)
            {
                updateMenu();
            }

            public void menuDeselected(MenuEvent e)
            {
            }

            public void menuCanceled(MenuEvent e)
            {
            }
        });

        jMenuFileNew.setText("New");
        jMenuFileNew.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuFileNewActionPerformed(evt);
            }

            private void jMenuFileNewActionPerformed(ActionEvent evt)
            {
                newAndReset();
            }
        });
        jMenuFile.add(jMenuFileNew);

        jMenuFileOpen.setText("Open...");
        jMenuFileOpen.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuFileOpenActionPerformed(evt);
            }

            private void jMenuFileOpenActionPerformed(ActionEvent evt)
            {
                load();
            }
        });
        jMenuFile.add(jMenuFileOpen);

        jMenuFileSave.setText("Save");
        jMenuFileSave.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuFileSaveActionPerformed(evt);
            }

            private void jMenuFileSaveActionPerformed(ActionEvent evt)
            {
                save();
            }
        });
        jMenuFile.add(jMenuFileSave);

        jMenuFileSaveAs.setText("Save As...");
        jMenuFileSaveAs.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuFileSaveAsActionPerformed(evt);
            }

            private void jMenuFileSaveAsActionPerformed(ActionEvent evt)
            {
                saveAs();
            }
        });
        jMenuFile.add(jMenuFileSaveAs);
        jMenuFile.add(jSeparator1);

        jMenuFileExportData.setText("Export test data...");
        jMenuFileExportData.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuFileExportDataActionPerformed(evt);
            }

            private void jMenuFileExportDataActionPerformed(ActionEvent evt)
            {
                exportTestData();
            }
        });
        jMenuFile.add(jMenuFileExportData);
        jMenuFile.add(jSeparator6);

        jMenuSettings.setText("Settings...");
        jMenuSettings.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuFileSettingsActionPerformed(evt);
            }

            private void jMenuFileSettingsActionPerformed(ActionEvent evt)
            {
                showSettingsForm();
            }
        });
        jMenuFile.add(jMenuSettings);
        jMenuFile.add(jSeparator2);

        jMenuFileExit.setText("Exit");
        jMenuFileExit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuFileExitActionPerformed(evt);
            }

            private void jMenuFileExitActionPerformed(ActionEvent evt)
            {
                shutdown();
            }
        });
        jMenuFile.add(jMenuFileExit);

        jMenuBar.add(jMenuFile);

        jMenuTools.setText("Tools");

        jMenuToolsClearLog.setText("Clear log");
        jMenuToolsClearLog.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuToolsClearLogActionPerformed(evt);
            }

            private void jMenuToolsClearLogActionPerformed(ActionEvent evt)
            {
                clearLog();
            }
        });
        jMenuTools.add(jMenuToolsClearLog);

        jMenuToolsSaveLog.setText("Save log...");
        jMenuToolsSaveLog.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuToolsViewLogActionPerformed(evt);
            }

            private void jMenuToolsViewLogActionPerformed(ActionEvent evt)
            {
                saveLog();
            }
        });
        jMenuTools.add(jMenuToolsSaveLog);
        jMenuTools.add(jSeparator4);

        jMenuToolsConnManager.setText("Connection Manager");
        jMenuToolsConnManager.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuToolsConnManagerActionPerformed(evt);
            }

            private void jMenuToolsConnManagerActionPerformed(ActionEvent evt)
            {
                showConnectionManager();
            }
        });
        jMenuTools.add(jMenuToolsConnManager);

        jMenuToolsTestConnection.setText("Test connection");
        jMenuToolsTestConnection.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuToolsTestConnectionActionPerformed(evt);
            }

            private void jMenuToolsTestConnectionActionPerformed(ActionEvent evt)
            {
                testConnection();
            }
        });
        jMenuTools.add(jMenuToolsTestConnection);
        jMenuTools.add(jSeparator5);

        jMenuToolsEndSession.setText("End Session");
        jMenuToolsEndSession.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuToolsEndSessionActionPerformed(evt);
            }

            private void jMenuToolsEndSessionActionPerformed(ActionEvent evt)
            {
                endSession();
            }
        });
        jMenuTools.add(jMenuToolsEndSession);

        jMenuBar.add(jMenuTools);

        jMenuHelp.setText("Help");

        jMenuHelpIBMHome.setText("IBM Home");
        jMenuHelpIBMHome.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuHelpIBMHomeActionPerformed(evt);
            }

            private void jMenuHelpIBMHomeActionPerformed(ActionEvent evt)
            {
                UIUtils.openUrl("http://www.ibm.com");
            }
        });
        jMenuHelp.add(jMenuHelpIBMHome);

        jMenuHelpUnicaHome.setText("Unica Home");
        jMenuHelpUnicaHome.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuHelpIBMHomeActionPerformed(evt);
            }

            private void jMenuHelpIBMHomeActionPerformed(ActionEvent evt)
            {
                UIUtils.openUrl("http://www-142.ibm.com/software/products/us/en/campaign-management/");
            }
        });
        jMenuHelp.add(jMenuHelpUnicaHome);

        jMenuHelpUnicaInteractHome.setText("Unica Interact Home");
        jMenuHelpUnicaInteractHome.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuHelpIBMHomeActionPerformed(evt);
            }

            private void jMenuHelpIBMHomeActionPerformed(ActionEvent evt)
            {
                UIUtils.openUrl("http://www-142.ibm.com/software/products/us/en/real-time-inbound-marketing/");
            }
        });
        jMenuHelp.add(jMenuHelpUnicaInteractHome);

        //http://www-01.ibm.com/support/docview.wss?crawler=1&uid=swg27027228
        jMenuHelpUnicaManual.setText("Unica Interact Documentation");
        jMenuHelpUnicaManual.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuHelpUnicaManualActionPerformed(evt);
            }

            private void jMenuHelpUnicaManualActionPerformed(ActionEvent evt)
            {
                UIUtils.openUrl("http://www-01.ibm.com/support/docview.wss?crawler=1&uid=swg27027228");
            }
        });
        jMenuHelp.add(jMenuHelpUnicaManual);


        jMenuHelp.add(jSeparator3);

        jMenuHelpAbout.setText("About");
        jMenuHelpAbout.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuHelpAboutActionPerformed(evt);
            }

            private void jMenuHelpAboutActionPerformed(ActionEvent evt)
            {
                showAboutForm();
            }
        });
        jMenuHelp.add(jMenuHelpAbout);

        jMenuBar.add(jMenuHelp);

        // Disable some items by default
        this.jMenuFileSave.setEnabled(false);

    }

    private void exportTestData()
    {
        XLog logger = this.client.getLogger();
        RunData testData = this.getCurrentRunData();

        if (testData.isValid())
        {
            FileSystemView fsv = FileSystemView.getFileSystemView();
            final JFileChooser fc = new JFileChooser(fsv.getRoots()[0]);
            fc.setDialogTitle("Export test data");
            fc.setFileFilter(new TextFile());
            fc.setAcceptAllFileFilterUsed(true);

            int returnVal = fc.showSaveDialog(this.getFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = fc.getSelectedFile();
                String filePath = file.getAbsolutePath();

                if (file.exists())
                {
                    int reply = JOptionPane.showConfirmDialog
                            (this.getFrame(),
                                    "Overwrite existing file?", "File exists",
                                    JOptionPane.YES_NO_OPTION);
                    if (reply != JOptionPane.YES_OPTION)
                    {
                        return;
                    }
                }

                logger.log("Saving data to " + filePath + "...");
                try
                {
                    Path path = Paths.get(filePath);
                    String[] lines = testData.toString().split(System.lineSeparator());
                    Files.write(path, Arrays.asList(lines), ENCODING);
                    logger.log("Saved. ");
                }
                catch (IOException ioe)
                {
                    logger.log("ERROR Saving data to " + filePath + "...");
                    logger.log(ioe.getMessage());
                }
            }
            else
            {
                logger.log("Save command cancelled by user.");
            }
        }
        else
        {
            logger.log("Nothing to save.");
        }
    }

    private void testConnection()
    {
        RunData rd = this.getCurrentRunData();
        client.testConnection(rd);
    }

    public void generateRandomSessionId()
    {
        Random rnd = new Random();
        long rndNum = Math.abs(rnd.nextLong());
        sessionTextField.setText(String.valueOf(rndNum));
    }

    private void initializeControls()
    {
        // Interact URLS
        this.interactURLComboBox.removeAllItems();
        List<InteractConnection> servers = this.settings.getUnicaServers();
        for (InteractConnection url : servers)
        {
            this.interactURLComboBox.addItem(url);
        }

        try
        {
            this.interactURLComboBox.setSelectedIndex(this.settings.getLastUserServer());
        }
        catch (IllegalArgumentException iae)
        {
            this.interactURLComboBox.setSelectedIndex(0);
        }

        this.interactURLComboBox.setFocusable(false);

        // Generate Session ID
        this.generateIdButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                generateRandomSessionId();
            }
        });

        // FileChooser
        FileSystemView fsv = FileSystemView.getFileSystemView();
        this.fileChooser = new JFileChooser(fsv.getRoots()[0]);

    }

    private void newAndReset()
    {
        // Console
        this.client.getLogger().log("New test. Zeroing out parameters.");

        // Business Logic
        this.currentRunData = new RunData(this.getInteractServer(), null);

        // Session ID
        this.sessionTextField.setText("");

        // Title
        this.frame.setTitle(TITLE);

        // Panels
        this.startSessionPanel.clear();
        this.getOffersPanel.clear();
        this.postEventPanel.clear();
        this.batchExecutePanel.clear();
    }

    private void shutdown()
    {
        // Save options
        this.client.getLogger().log("Saving settings.");
        this.settings.setLastUserServer(this.interactURLComboBox.getSelectedIndex());
        this.settings.setClientSize(this.getFrame().getSize());
        this.settings.writeProperties();
        this.client.getLogger().log("Bye.");

        // Exit
        System.exit(0);
    }

    private void saveLog(String filePath, XLog logger)
    {
        BufferedWriter outFile = null;
        try
        {
            outFile = new BufferedWriter(new FileWriter(filePath));
            this.txtConsole.write(outFile);   // *** here: ***
        }
        catch (IOException ex)
        {
            logger.log("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
        finally
        {
            if (outFile != null)
            {
                try
                {
                    outFile.close();
                }
                catch (IOException e)
                {
                }
            }
        }

    }

    private void saveRunData(String filePath)
    {
        RunData runData = this.getCurrentRunData();
        runData.setRunDataFilePath(filePath);
        RunDataSerializer.Serialize(runData, filePath, this.client.getLogger());
        this.currentRunData = runData;
        this.updateTitle(runData);
    }

    private void loadRunData(String filePath)
    {
        RunData runData = RunDataSerializer.Deserialize(filePath, this.client.getLogger());
        this.updateUI(runData);
        this.currentRunData = runData;
    }

    private void endSession()
    {
        String sessionId = this.sessionTextField.getText();
        if (Utils.isNotNullNotEmptyNotWhiteSpace(sessionId))
        {
            RunData runData = this.getCurrentRunData();
            this.client.endSession(runData);
        }
    }

    private void showConnectionManager()
    {
        ConnectionManager connectionManager = new ConnectionManager(this.client);
        connectionManager.pack();
        connectionManager.setSize(new Dimension(690, 350));
        connectionManager.setLocationRelativeTo(this.getFrame());
        connectionManager.setVisible(true);

        this.initializeControls();
    }

    private void updateTitle(RunData runData)
    {
        String fileName = runData.getRunDataName();
        if (Utils.isNotNullNotEmptyNotWhiteSpace(fileName))
        {
            this.frame.setTitle(TITLE + " - [" + fileName + "]");
        }
    }

    private void updateUI(RunData runData)
    {
        // Title
        this.updateTitle(runData);

        // Session Data
        this.startSessionPanel.updateUIFromData(runData.getStartSessionData());

        // GetOffers Data
        this.getOffersPanel.updateUIFromData(runData.getGetOffersData());

        // PostEvent Data
        this.postEventPanel.updateUIFromData(runData.getPostEventData());
    }

    private void setComboURL(InteractConnection url)
    {
        boolean found = false;
        for (int k = 0; k < this.interactURLComboBox.getItemCount(); k++)
        {
            InteractConnection tempUrl = (InteractConnection) this.interactURLComboBox.getItemAt(k);
            if (url.equals(tempUrl))
            {
                this.interactURLComboBox.setSelectedIndex(k);
                found = true;
                break;
            }
        }
        if (!found)
        {
            this.interactURLComboBox.addItem(url);
        }
    }

    private RunData getCurrentRunData()
    {
        InteractConnection url = (InteractConnection) this.interactURLComboBox.getSelectedItem();
        String sessionId = this.sessionTextField.getText();

        String runDataName;
        RunData runData = new RunData(url, sessionId);

        if (this.currentRunData != null)
        {
            runDataName = this.currentRunData.getRunDataFilePath();
            runData.setRunDataFilePath(runDataName);
        }

        // Start Session Data
        runData.setStartSessionData(this.startSessionPanel.getDataFromUI());

        // Get Offers Data
        runData.setGetOffersData(this.getOffersPanel.getDataFromUI());

        // Post Session Data
        runData.setPostEventData(this.postEventPanel.getDataFromUI());

        return runData;
    }

    private void load()
    {
        XLog logger = this.client.getLogger();
        this.fileChooser.setDialogTitle("Load session data");
        this.fileChooser.setFileFilter(new RunDataFile());
        int returnVal = this.fileChooser.showOpenDialog(this.getFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = this.fileChooser.getSelectedFile();
            logger.log("Loading from " + file.getAbsolutePath() + "...");
            this.loadRunData(file.getAbsolutePath());
            logger.log("Loaded. ");
        }
        else
        {
            logger.log("Load command cancelled by user.");
        }
    }

    private void clearLog()
    {
        this.txtConsole.setText("");
    }

    private void saveLog()
    {
        XLog logger = this.client.getLogger();
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save log data");
        fc.setSelectedFile(new File("log.txt"));
        fc.setFileFilter(new LogDataFile());
        int returnVal = fc.showSaveDialog(this.getFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();
            String filePath = file.getAbsolutePath();
            if ((!filePath.endsWith("txt") && (!filePath.endsWith("log"))))
            {
                filePath = filePath + ".txt";
                file = new File(filePath);
            }

            if (file.exists())
            {
                int reply = JOptionPane.showConfirmDialog
                        (this.getFrame(),
                                "Overwrite existing file?", "File exists",
                                JOptionPane.YES_NO_OPTION);
                if (reply != JOptionPane.YES_OPTION)
                {
                    return;
                }
            }

            logger.log("Saving log to " + filePath + "...");
            this.saveLog(filePath, logger);
            logger.log("Saved. ");
        }
        else
        {
            logger.log("Save command cancelled by user.");
        }

    }

    private void updateMenu()
    {
        boolean enableSave = false;

        if (this.currentRunData != null)
        {
            String fileName = this.currentRunData.getRunDataName();
            if (Utils.isNotNullNotEmptyNotWhiteSpace(fileName))
            {
                enableSave = true;
            }
        }
        this.jMenuFileSave.setEnabled(enableSave);
    }

    private void save()
    {
        XLog logger = this.client.getLogger();
        String filePath = this.currentRunData.getRunDataFilePath();
        this.save(logger, filePath);
    }

    private void save(XLog logger, String filePath)
    {
        logger.log("Saving data to " + filePath + "...");
        this.saveRunData(filePath);
        logger.log("Saved. ");
    }

    private void saveAs()
    {
        XLog logger = this.client.getLogger();
        this.fileChooser.setDialogTitle("Save session data");
        this.fileChooser.setFileFilter(new RunDataFile());
        int returnVal = this.fileChooser.showSaveDialog(this.getFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = this.fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();
            if (!filePath.endsWith("itf"))
            {
                filePath = filePath + ".itf";
                file = new File(filePath);
            }

            if (file.exists())
            {
                int reply = JOptionPane.showConfirmDialog
                        (this.getFrame(),
                                "Overwrite existing file?", "File exists",
                                JOptionPane.YES_NO_OPTION);
                if (reply != JOptionPane.YES_OPTION)
                {
                    return;
                }
            }

            this.save(logger, filePath);
        }
        else
        {
            logger.log("Save command cancelled by user.");
        }
    }

    private void showAboutForm()
    {
        AboutForm aboutForm = new AboutForm();
        aboutForm.pack();
        aboutForm.setSize(new Dimension(280, 350));
        aboutForm.setResizable(false);
        aboutForm.setLocationRelativeTo(this.getFrame());
        aboutForm.setVisible(true);
    }

    private void showSettingsForm()
    {
        SettingsForm settingsForm = new SettingsForm(this.settings);
        if (settingsForm.showDialog(this.getFrame()))
        {
            this.settings.readProperties();
        }
    }


}
