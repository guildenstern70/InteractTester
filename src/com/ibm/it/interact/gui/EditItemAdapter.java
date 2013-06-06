/*
 * UNICA INTERACT TESTER
 *   IBM Confidential
 *   OCO Source Materials
 *   (C) IBM Corp. 2013 - All rights reserved.
 *
 *   The source code for this program is not published or otherwise
 *   divested of its trade secrets, irrespective of what has been
 *   deposited with the U.S. Copyright Office.
 *
 *   Author: alessiosaltarin@it.ibm.com
 */

package com.ibm.it.interact.gui;

import com.ibm.it.interact.client.Client;

import javax.swing.JFrame;
import javax.swing.JList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**

 */
public class EditItemAdapter extends MouseAdapter
{
    private JList parametersList;
    private JFrame mainFrame;
    private Client client;

    public EditItemAdapter(JList pList, JFrame pFrame, Client pClient)
    {
        this.parametersList = pList;
        this.mainFrame = pFrame;
        this.client = pClient;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2))
        {
            UIUtils.editNVPItem(parametersList, mainFrame, client);
        }
        super.mouseClicked(e);
    }
}
