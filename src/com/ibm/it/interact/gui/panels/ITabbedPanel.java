package com.ibm.it.interact.gui.panels;

import javax.swing.JPanel;

/**
 *
 *
 */
public interface ITabbedPanel
{
    String getTitle();

    JPanel getPanel();

    void clear();
}
