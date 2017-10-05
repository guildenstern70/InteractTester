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

import com.ibm.it.interact.client.Client;
import com.ibm.it.interact.client.Settings;
import com.ibm.it.interact.client.Utils;
import com.ibm.it.interact.client.data.NameValuePairDecor;
import com.ibm.it.interact.client.data.RunData;
import com.ibm.it.interact.client.data.StartSessionData;
import com.ibm.it.interact.gui.EditItemAdapter;
import com.ibm.it.interact.gui.MainForm;
import com.ibm.it.interact.gui.UIUtils;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.unicacorp.interact.api.NameValuePair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
    private final JFrame mainFrame;

    // Business logic variables
    private final MainForm parent;
    private final Client client;

    public StartSession(MainForm main)
    {
        this.parent = main;
        $$$setupUI$$$();
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
        startSessionPanel = new JPanel();
        startSessionPanel.setLayout(new FormLayout("fill:117px:noGrow,left:4dlu:noGrow,fill:255px:noGrow,left:4dlu:noGrow,fill:109px:grow,left:4dlu:noGrow,fill:79px:noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:62px:grow,top:5dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:30px:noGrow"));
        startSessionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15), null));
        final JLabel label1 = new JLabel();
        label1.setText("Interactive Channel: ");
        CellConstraints cc = new CellConstraints();
        startSessionPanel.add(label1, cc.xy(1, 3));
        interactiveChannelText = new JTextField();
        startSessionPanel.add(interactiveChannelText, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label2 = new JLabel();
        label2.setText("Audience IDs: ");
        startSessionPanel.add(label2, cc.xy(1, 9, CellConstraints.DEFAULT, CellConstraints.TOP));
        final JScrollPane scrollPane1 = new JScrollPane();
        startSessionPanel.add(scrollPane1, cc.xywh(5, 3, 3, 9, CellConstraints.DEFAULT, CellConstraints.FILL));
        parametersList.setSelectionMode(0);
        scrollPane1.setViewportView(parametersList);
        final JLabel label3 = new JLabel();
        label3.setText("Parameters:");
        startSessionPanel.add(label3, cc.xy(5, 1));
        final JScrollPane scrollPane2 = new JScrollPane();
        startSessionPanel.add(scrollPane2, cc.xywh(3, 9, 1, 3, CellConstraints.DEFAULT, CellConstraints.FILL));
        audienceIdList.setSelectionMode(0);
        scrollPane2.setViewportView(audienceIdList);
        relyOnExistingSessionCheckBox = new JCheckBox();
        relyOnExistingSessionCheckBox.setText("Rely on existing session");
        startSessionPanel.add(relyOnExistingSessionCheckBox, cc.xyw(1, 13, 3, CellConstraints.DEFAULT, CellConstraints.BOTTOM));
        runButton = new JButton();
        runButton.setText("Run");
        runButton.setMnemonic('R');
        runButton.setDisplayedMnemonicIndex(0);
        startSessionPanel.add(runButton, cc.xy(7, 13, CellConstraints.FILL, CellConstraints.BOTTOM));
        flowchartNameTextField = new JTextField();
        startSessionPanel.add(flowchartNameTextField, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label4 = new JLabel();
        label4.setText("Flowchart Name:");
        startSessionPanel.add(label4, cc.xy(1, 5));
        final JLabel label5 = new JLabel();
        label5.setText("Audience Level: ");
        startSessionPanel.add(label5, cc.xy(1, 7));
        audienceLevelText = new JTextField();
        audienceLevelText.setEnabled(true);
        startSessionPanel.add(audienceLevelText, cc.xy(3, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        label1.setLabelFor(interactiveChannelText);
        label4.setLabelFor(flowchartNameTextField);
        label5.setLabelFor(audienceLevelText);
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
        return startSessionPanel;
    }
}
