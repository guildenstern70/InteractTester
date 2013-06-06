/**
 *   UNICA INTERACT TESTER
 *
 *   IBM Confidential
 *   (C) IBM Corp. 2013 - All rights reserved.
 *
 *   The source code for this program is not published or otherwise
 *   divested of its trade secrets, irrespective of what has been
 *   deposited with the U.S. Copyright Office.
 *
 *   Author: alessiosaltarin@it.ibm.com
 */

package com.ibm.it.interact.gui.panels;

import com.ibm.it.interact.client.Client;
import com.ibm.it.interact.client.Settings;
import com.ibm.it.interact.client.Utils;
import com.ibm.it.interact.client.data.NameValuePairDecor;
import com.ibm.it.interact.client.data.RunData;
import com.ibm.it.interact.client.data.StartSessionData;
import com.ibm.it.interact.gui.EditItemAdapter;
import com.ibm.it.interact.gui.MainForm;
import com.ibm.it.interact.gui.UIUtils;
import com.unicacorp.interact.api.NameValuePair;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 *
 */
public final class StartSession implements ITabbedPanel
{
    private static final String TITLE = "Start Session";

    // Controls
    private JTextField interactiveChannelText;
    private JTextField audienceLevelText;
    private JCheckBox relyOnExistingSessionCheckBox;
    private JList parametersList;
    private JPanel startSessionPanel;
    private JList audienceIdList;
    private JButton runButton;
    private JTextField flowchartNameTextField;
    final private JFrame mainFrame;

    // Business logic variables
    final private MainForm parent;
    final private Client client;

    public StartSession(MainForm main)
    {
        this.parent = main;
        this.mainFrame = main.getFrame();
        this.client = this.parent.getClient();
        EditItemAdapter mouseAdapter1 = new EditItemAdapter(this.parametersList, this.mainFrame, this.client);
        EditItemAdapter mouseAdapter2 = new EditItemAdapter(this.audienceIdList, this.mainFrame, this.client);

        this.initializePopupParamsMenu();
        startSessionPanel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                parametersList.clearSelection();
                audienceIdList.clearSelection();
            }
        });
        runButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                run();
            }
        });
        parametersList.addMouseListener(mouseAdapter1);
        audienceIdList.addMouseListener(mouseAdapter2);
    }

    @Override
    public String getTitle()
    {
        return TITLE;
    }

    public void clear()
    {
        this.relyOnExistingSessionCheckBox.setSelected(false);
        this.interactiveChannelText.setText("");
        this.audienceLevelText.setText("");
        this.flowchartNameTextField.setText("");
        UIUtils.clearList(this.audienceIdList);
        UIUtils.clearList(this.parametersList);
    }

    @Override
    public JPanel getPanel()
    {
        return this.startSessionPanel;
    }

    @Override
    public String toString()
    {
        return this.getDataFromUI().toString();
    }

    public StartSessionData getDataFromUI()
    {
        StartSessionData ssd = new StartSessionData();
        ssd.setInteractiveChannel(this.interactiveChannelText.getText());
        ssd.setAudienceLevel(this.audienceLevelText.getText());
        ssd.setAudienceIds(UIUtils.getNameValuePairs(this.audienceIdList));
        ssd.setParameters(UIUtils.getNameValuePairs(this.parametersList));
        ssd.setFlowchartName(this.flowchartNameTextField.getText());
        ssd.setRelyOnExistingSession(this.relyOnExistingSessionCheckBox.isSelected());
        return ssd;
    }

    public void updateUIFromData(StartSessionData ssd)
    {
        this.relyOnExistingSessionCheckBox.setSelected(ssd.isRelyOnExistingSession());
        this.interactiveChannelText.setText(ssd.getInteractiveChannel());
        this.audienceLevelText.setText(ssd.getAudienceLevel());
        this.flowchartNameTextField.setText(ssd.getFlowchartName());

        UIUtils.fillParamsList(this.audienceIdList, ssd.getAudienceIds(), true);
        UIUtils.fillParamsList(this.parametersList, ssd.getParameters(), true, "UACIExecuteFlowchartByName");
    }

    private void initializePopupParamsMenu()
    {
        JPopupMenu popupParams = UIUtils.buildParametersPopupMenu(mainFrame, client);
        JPopupMenu audienceParams = UIUtils.buildParametersPopupMenu("Audience IDs", mainFrame, client);

        MouseListener popupListener1 = new PopupListener(popupParams);
        MouseListener popupListener2 = new PopupListener(audienceParams);
        this.audienceIdList.addMouseListener(popupListener2);
        this.parametersList.addMouseListener(popupListener1);

        this.getPanel().updateUI();
        this.getPanel().validate();
    }

    private boolean isReadyToRun()
    {
        boolean readyToRun = true;

        String sessionId = this.parent.getSessionId();
        String interactiveChannel = this.interactiveChannelText.getText();
        String audienceLevel = this.audienceLevelText.getText();
        NameValuePair[] audienceIds = UIUtils.getNameValuePairs(this.audienceIdList);

        if (!Utils.isNotNullNotEmptyNotWhiteSpace(interactiveChannel))
        {
            readyToRun = false;
            JOptionPane.showMessageDialog(this.getPanel(),
                    "Interactive Channel cannot be null",
                    "Invalid Interactive Channel", JOptionPane.WARNING_MESSAGE);
        }
        else if (!Utils.isNotNullNotEmptyNotWhiteSpace(sessionId))
        {
            readyToRun = false;
            JOptionPane.showMessageDialog(this.getPanel(),
                    "SessionID cannot be null",
                    "Invalid SessionID", JOptionPane.WARNING_MESSAGE);
        }
        else if (!Utils.isNotNullNotEmptyNotWhiteSpace(audienceLevel))
        {
            readyToRun = false;
            JOptionPane.showMessageDialog(this.getPanel(),
                    "Audience Level cannot be null",
                    "Invalid Audience Level", JOptionPane.WARNING_MESSAGE);
        }
        else if (audienceIds.length == 0)
        {
            readyToRun = false;
            JOptionPane.showMessageDialog(this.getPanel(),
                    "Audience IDs cannot be null",
                    "Invalid Audience IDs", JOptionPane.WARNING_MESSAGE);
        }

        return readyToRun;
    }

    private void run()
    {
        if (this.client != null)
        {
            String sessionId = this.parent.getSessionId();
            if (!Utils.isNotNullNotEmptyNotWhiteSpace(sessionId))
            {
                Settings settings = this.parent.getSettings();
                if (settings != null)
                {
                    if (settings.isGenerateSessionIdAtStartup())
                    {
                        this.parent.generateRandomSessionId();
                    }
                }
            }

            if (this.isReadyToRun()) // validation
            {
                this.parent.showStatusMessage("Running StartSession...");
                RunData rd = new RunData(this.parent.getInteractServer(), this.parent.getSessionId());
                rd.setStartSessionData(this.getDataFromUI());
                this.client.runStartSession(rd, true);
                this.parent.showStatusMessage("Ready.");
            }
        }
        else
        {
            System.err.println("Critical: Error is NULL.");
        }
    }

    private void createUIComponents()
    {
        this.audienceIdList = new JList();
        this.parametersList = new JList();

        this.audienceIdList.setName("Audience");
        this.parametersList.setName("Parameters");

        this.audienceIdList.setModel(new DefaultListModel<NameValuePairDecor>());
        this.parametersList.setModel(new DefaultListModel<NameValuePairDecor>());
    }
}
