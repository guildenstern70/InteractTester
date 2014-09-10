package com.ibm.it.interact.gui.panels;

import javax.swing.*;

/**
 *
 *
 */
interface ITabbedPanel
{
    String getTitle();

    JPanel getPanel();

    void clear();
}
