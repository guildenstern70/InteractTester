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
import com.ibm.it.interact.client.data.BatchExecuteData;
import com.ibm.it.interact.client.data.RunData;
import com.ibm.it.interact.gui.MainForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 */
public class BatchExecute implements ITabbedPanel
{
    private static final String TITLE = "Batch Execute";

    private JPanel batchExecutePanel;
    private JCheckBox startSessionCheckBox;
    private JCheckBox getOffersCheckBox;
    private JCheckBox postEventCheckBox;
    private JButton runButton;
    private JPanel apiCheckPanel;
    private JCheckBox endSessionCheckBox;

    // Business logic variables
    private final MainForm parent;
    private final Client client;

    public BatchExecute(MainForm mainForm)
    {
        this.parent = mainForm;
        JFrame mainFrame = mainForm.getFrame();
        this.client = this.parent.getClient();
        runButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                run();
            }
        });
    }

    @Override
    public JPanel getPanel()
    {
        return this.batchExecutePanel;
    }

    @Override
    public void clear()
    {
        this.startSessionCheckBox.setSelected(false);
        this.getOffersCheckBox.setSelected(false);
        this.postEventCheckBox.setSelected(false);
        this.endSessionCheckBox.setSelected(false);
    }

    @Override
    public String getTitle()
    {
        return TITLE;
    }

    public void updateUIFromData(BatchExecuteData bed)
    {
        this.startSessionCheckBox.setSelected(bed.isExeStartSession());
        this.getOffersCheckBox.setSelected(bed.isExeGetOffers());
        this.postEventCheckBox.setSelected(bed.isExePostEvent());
        this.endSessionCheckBox.setSelected(bed.isExeEndSession());
    }

    BatchExecuteData getDataFromUI()
    {
        BatchExecuteData bed = new BatchExecuteData();

        bed.setExeStartSession(this.startSessionCheckBox.isSelected());
        bed.setExeGetOffers(this.getOffersCheckBox.isSelected());
        bed.setExePostEvent(this.postEventCheckBox.isSelected());
        bed.setExeEndSession(this.endSessionCheckBox.isSelected());

        return bed;
    }

    void run()
    {
        if (this.client != null)
        {
            BatchExecuteData bed = this.getDataFromUI();
            if (bed.numberOfCommands() > 0)
            {
                RunData rd = new RunData(this.parent.getInteractServer(), this.parent.getSessionId());

                if (bed.isExeStartSession())
                {
                    rd.setStartSessionData(parent.getStartSessionPanel().getDataFromUI());
                }

                if (bed.isExeGetOffers())
                {
                    rd.setGetOffersData(parent.getGetOffersPanel().getDataFromUI());
                }

                if (bed.isExePostEvent())
                {
                    rd.setPostEventData(parent.getPostEventPanel().getDataFromUI());
                }

                this.parent.showStatusMessage("Running BatchExecute...");
                rd.setBatchExecuteData(bed);
                this.client.runBatch(rd, this.endSessionCheckBox.isSelected());
                this.parent.showStatusMessage("Ready.");

            }
        }
        else
        {
            System.err.println("Critical: client is NULL.");
        }

    }
}
