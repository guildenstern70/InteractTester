/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.gui;

import com.ibm.it.interact.client.Client;
import com.ibm.it.interact.client.data.NameValuePairDecor;
import com.ibm.it.interact.client.data.NameValuePairSorter;
import com.ibm.it.interact.gui.panels.LabeledPopupMenu;
import com.ibm.it.interact.gui.panels.ParameterDialog;
import com.unicacorp.interact.api.NameValuePair;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 */
public class UIUtils
{
    public static void clearList(JList list)
    {
        DefaultListModel model = (DefaultListModel) list.getModel();
        model.removeAllElements();
    }

    public static NameValuePair[] getNameValuePairs(JList control)
    {
        DefaultListModel model = (DefaultListModel) control.getModel();
        int nvpCount = model.getSize();
        NameValuePair[] nvps = new NameValuePair[nvpCount];
        for (int j = 0; j < nvpCount; j++)
        {
            NameValuePairDecor nvpd = (NameValuePairDecor) model.get(j);
            nvps[j] = nvpd.getNameValuePair();
        }
        return nvps;
    }

    public static void addParamToList(JList paramControl, NameValuePairDecor nvp)
    {
        UIUtils.addParamToList(paramControl, nvp, true);
    }

    private static void addParamToList(JList paramControl, NameValuePairDecor nvp, boolean sort)
    {
        DefaultListModel<NameValuePairDecor> dlm = (DefaultListModel<NameValuePairDecor>) paramControl.getModel();
        if (nvp != null)
        {
            List<NameValuePairDecor> nvpList = new ArrayList<NameValuePairDecor>();
            for (int j = 0; j < dlm.getSize(); j++)
            {
                nvpList.add((NameValuePairDecor) dlm.get(j));
            }
            nvpList.add(nvp);

            if (sort)
            {
                Collections.sort(nvpList, new NameValuePairSorter());
            }
            dlm.clear();
            for (NameValuePairDecor item : nvpList)
            {
                dlm.addElement(item);
            }
        }
    }

    /**
     * Fill control with list of NameValuePair
     *
     * @param paramControl The control showing the list of NameValuePair
     * @param nvp          The list of NameValuePair
     * @param sort         True, if the control must show the list in order
     * @param exclude      The key to be excluded from the list
     */
    public static void fillParamsList(JList paramControl, NameValuePair[] nvp, boolean sort, String exclude)
    {
        DefaultListModel<NameValuePairDecor> dlm = (DefaultListModel<NameValuePairDecor>) paramControl.getModel();

        if (nvp.length > 0)
        {
            dlm.removeAllElements();

            List<NameValuePairDecor> nvpList = new ArrayList<NameValuePairDecor>(nvp.length);
            for (NameValuePair item : nvp)
            {
                if (exclude != null)
                {
                    if (!item.getName().equals(exclude))
                    {
                        nvpList.add(new NameValuePairDecor(item));
                    }
                }
                else
                {
                    nvpList.add(new NameValuePairDecor(item));
                }
            }

            if (sort)
            {
                Collections.sort(nvpList, new NameValuePairSorter());
            }

            for (NameValuePairDecor item : nvpList)
            {
                dlm.addElement(item);
            }
        }

    }

    public static void fillParamsList(JList paramControl, NameValuePair[] nvp, boolean sort)
    {
        UIUtils.fillParamsList(paramControl, nvp, sort, null);
    }

    public static void openUrl(String url)
    {
        if (!java.awt.Desktop.isDesktopSupported())
        {
            System.err.println("Desktop is not supported (fatal)");
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE))
        {

            System.err.println("Desktop doesn't support the browse action (fatal)");
        }

        try
        {
            java.net.URI uri = new java.net.URI(url);
            desktop.browse(uri);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

    }

    public static final JPopupMenu buildParametersPopupMenu(final JFrame parent, final Client client)
    {
        final JPopupMenu popup;

        // Context menu items
        final JMenuItem menuItemAdd;
        final JMenuItem menuItemClear;
        final JMenuItem menuItemEdit;
        final JMenuItem menuItemDelete;

        menuItemDelete = new JMenuItem("Delete");
        menuItemAdd = new JMenuItem("Add...");
        menuItemEdit = new JMenuItem("Edit...");
        menuItemClear = new JMenuItem("Clear");

        //Create the popup menu.
        popup = new LabeledPopupMenu("Parameters");
        popup.addPopupMenuListener(new PopupMenuListener()
        {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {
                JList invoker = (JList) popup.getInvoker();
                if (invoker.getSelectedIndex() >= 0)
                {
                    menuItemDelete.setEnabled(true);
                    menuItemEdit.setEnabled(true);
                }
                else
                {
                    menuItemDelete.setEnabled(false);
                    menuItemEdit.setEnabled(false);
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
            {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e)
            {
            }

        });

        // Clear
        menuItemClear.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuItemAddActionPerformed(evt);
            }

            private void menuItemAddActionPerformed(ActionEvent evt)
            {
                JList invoker = (JList) popup.getInvoker();
                DefaultListModel model = (DefaultListModel) invoker.getModel();
                model.clear();
            }
        });
        popup.add(menuItemClear);

        // Add
        menuItemAdd.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuItemAddActionPerformed(evt);
            }

            private void menuItemAddActionPerformed(ActionEvent evt)
            {
                ParameterDialog pdial = ParameterDialog.showDialog(parent, client.getLogger());
                NameValuePair nvp = pdial.getNameValuePair();
                if (nvp != null)
                {
                    JList invoker = (JList) popup.getInvoker();
                    UIUtils.addParamToList(invoker, new NameValuePairDecor(nvp), true);
                }
            }
        });
        popup.add(menuItemAdd);

        // Edit
        menuItemEdit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuItemEditActionPerformed(evt);
            }

            private void menuItemEditActionPerformed(ActionEvent evt)
            {
                JList invoker = (JList) popup.getInvoker();
                NameValuePairDecor nvpd = (NameValuePairDecor) invoker.getSelectedValue();

                ParameterDialog pdial = ParameterDialog.showDialog(parent,
                        client.getLogger(),
                        nvpd);
                NameValuePair nvp = pdial.getNameValuePair();
                if (nvp != null)
                {
                    // Remove selected
                    DefaultListModel model = (DefaultListModel) invoker.getModel();
                    int selIndex = invoker.getSelectedIndex();
                    model.remove(selIndex);

                    // Add new
                    UIUtils.addParamToList(invoker, new NameValuePairDecor(nvp), true);
                }
            }
        });
        popup.add(menuItemEdit);

        // Delete
        menuItemDelete.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuItemDeleteActionPerformed(evt);
            }

            private void menuItemDeleteActionPerformed(ActionEvent evt)
            {
                int dialogResult = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to remove item?", "Warning",
                        JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION)
                {
                    JList invoker = (JList) popup.getInvoker();
                    DefaultListModel model = (DefaultListModel) invoker.getModel();
                    int selIndex = invoker.getSelectedIndex();
                    model.remove(selIndex);
                }
            }
        });
        popup.add(menuItemDelete);

        return popup;

    }

}
