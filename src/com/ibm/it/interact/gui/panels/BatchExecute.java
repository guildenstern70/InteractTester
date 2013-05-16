package com.ibm.it.interact.gui.panels;

import com.ibm.it.interact.client.Client;
import com.ibm.it.interact.gui.MainForm;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
    private JFrame mainFrame;

    // Business logic variables
    private MainForm parent;
    private Client client;

    public BatchExecute(MainForm mainForm)
    {
        this.parent = mainForm;
        this.mainFrame = mainForm.getFrame();
        this.client = this.parent.getClient();
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
    }

    @Override
    public String getTitle()
    {
        return TITLE;
    }
}
