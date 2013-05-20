package com.ibm.it.interact.gui.panels;

import com.ibm.it.interact.client.Client;
import com.ibm.it.interact.client.data.BatchExecuteData;
import com.ibm.it.interact.client.data.RunData;
import com.ibm.it.interact.gui.MainForm;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
    private JFrame mainFrame;

    // Business logic variables
    private MainForm parent;
    private Client client;

    public BatchExecute(MainForm mainForm)
    {
        this.parent = mainForm;
        this.mainFrame = mainForm.getFrame();
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

    public BatchExecuteData getDataFromUI()
    {
        BatchExecuteData bed = new BatchExecuteData();

        bed.setExeStartSession(this.startSessionCheckBox.isSelected());
        bed.setExeGetOffers(this.getOffersCheckBox.isSelected());
        bed.setExePostEvent(this.postEventCheckBox.isSelected());
        bed.setExeEndSession(this.endSessionCheckBox.isSelected());

        return bed;
    }

    public void run()
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

                rd.setBatchExecuteData(bed);
                this.client.runBatch(rd, this.endSessionCheckBox.isSelected());

            }
        }
        else
        {
            System.err.println("Critical: client is NULL.");
        }

    }
}