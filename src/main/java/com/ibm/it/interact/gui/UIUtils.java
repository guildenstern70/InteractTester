/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013-14 - All rights reserved.
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

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User Interface Utilities
 */
public class UIUtils
{

    /**
     * Clear a JList list
     *
     * @param list
     */
    public static void clearList(JList list)
    {
        DefaultListModel model = (DefaultListModel) list.getModel();
        model.removeAllElements();
    }

    /**
     * Extract a list of NameValuePair from a List with NameValuePairs items
     *
     * @param control
     * @return
     */
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

    /**
     * Fill control with list of NameValuePair
     *
     * @param paramControl The control showing the list of NameValuePair
     * @param nvp          The list of NameValuePair
     * @param sort         True, if the control must show the list in order
     */
    public static void fillParamsList(JList paramControl, NameValuePair[] nvp, boolean sort)
    {
        UIUtils.fillParamsList(paramControl, nvp, sort, null);
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

            List<NameValuePairDecor> nvpList = new ArrayList<>(nvp.length);
            for (NameValuePair item : nvp)
            {
                NameValuePairDecor newNVP = new NameValuePairDecor(item);
                if (!newNVP.isNull())
                {
                    if (exclude != null)
                    {
                        if (!item.getName().equals(exclude))
                        {
                            nvpList.add(newNVP);
                        }
                    }
                    else
                    {
                        nvpList.add(newNVP);
                    }
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

    /**
     * Open a web site on desktop
     *
     * @param url
     */
    public static void openUrl(String url)
    {
        if (!java.awt.Desktop.isDesktopSupported())
        {
            System.err.println("Desktop is not supported (fatal)");
            return;
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE))
        {

            System.err.println("Desktop doesn't support the browse action (fatal)");
            return;
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

    /**
     * Build a popup menu on a frame
     *
     * @param parent Frame on which the popup menu is to be displayed
     * @param client Business logic handle
     * @return the popup menu
     */
    public static JPopupMenu buildParametersPopupMenu(final JFrame parent, final Client client)
    {
        return UIUtils.buildParametersPopupMenu("Parameters", parent, client);
    }

    /**
     * Build a popup menu on a frame
     *
     * @param title  popup menu title
     * @param parent Frame on which the popup menu is to be displayed
     * @param client Business logic handle
     * @return the popup menu
     */
    public static JPopupMenu buildParametersPopupMenu(String title, final JFrame parent, final Client client)
    {
        final JPopupMenu popup;

        // Context menu items
        final JMenuItem menuItemAdd = new JMenuItem("Add...");
        final JMenuItem menuItemClear = new JMenuItem("Clear");
        final JMenuItem menuItemEdit = new JMenuItem("Edit...");
        final JMenuItem menuItemDelete = new JMenuItem("Delete");
        final JMenuItem menuItemCut = new JMenuItem("Cut");
        final JMenuItem menuItemCopy = new JMenuItem("Copy");
        final JMenuItem menuItemPaste = new JMenuItem("Paste");

        //Create the popup menu.
        popup = new LabeledPopupMenu(title);
        popup.addPopupMenuListener(new PopupMenuListener()
        {
            private boolean isThereAnyItemToPaste()
            {
                boolean isThereAny = false;

                try
                {
                    Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    Transferable nvpdTrs = clpbrd.getContents(null);
                    if (nvpdTrs != null)
                    {
                        if (nvpdTrs.getTransferData(NameValuePairDecor.clipboardDataFlavor) != null)
                        {
                            isThereAny = true;
                        }
                    }
                }
                catch (Exception exc)
                {
                }

                return isThereAny;
            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {
                JList invoker = (JList) popup.getInvoker();
                boolean isSelected = (invoker.getSelectedIndex() >= 0);
                menuItemDelete.setEnabled(isSelected);
                menuItemEdit.setEnabled(isSelected);
                menuItemCut.setEnabled(isSelected);
                menuItemCopy.setEnabled(isSelected);
                menuItemPaste.setEnabled(this.isThereAnyItemToPaste());
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
        popup.add(new JSeparator());

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
                editNVPItem(invoker, parent, client);
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
        popup.add(new JSeparator());

        // Cut
        menuItemCut.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuItemCutActionPerformed(evt);
            }

            private void menuItemCutActionPerformed(ActionEvent evt)
            {
                JList invoker = (JList) popup.getInvoker();
                NameValuePairDecor nvpd = (NameValuePairDecor) invoker.getSelectedValue();
                NameValuePair nvp = nvpd.getNameValuePair();
                if (nvp != null)
                {
                    // Remove selected
                    DefaultListModel model = (DefaultListModel) invoker.getModel();
                    int selIndex = invoker.getSelectedIndex();
                    model.remove(selIndex);

                    // Copy value to clipboard
                    Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clpbrd.setContents(nvpd, null);
                }
            }
        });
        popup.add(menuItemCut);

        // Copy
        menuItemCopy.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuItemCopyActionPerformed(evt);
            }

            private void menuItemCopyActionPerformed(ActionEvent evt)
            {
                JList invoker = (JList) popup.getInvoker();
                NameValuePairDecor nvpd = (NameValuePairDecor) invoker.getSelectedValue();
                NameValuePair nvp = nvpd.getNameValuePair();
                if (nvp != null)
                {
                    // Copy value to clipboard
                    Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clpbrd.setContents(nvpd, null);
                }
            }
        });
        popup.add(menuItemCopy);

        // Paste
        menuItemPaste.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                menuItemPasteActionPerformed(evt);
            }

            private void menuItemPasteActionPerformed(ActionEvent evt)
            {
                // Copy value to clipboard
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable nvpdTrs = clpbrd.getContents(null);

                try
                {
                    if (nvpdTrs != null)
                    {
                        JList invoker = (JList) popup.getInvoker();
                        NameValuePairDecor nvpd;
                        nvpd = (NameValuePairDecor) nvpdTrs.getTransferData(NameValuePairDecor.clipboardDataFlavor);
                        UIUtils.addParamToList(invoker, nvpd, true);
                    }
                }
                catch (UnsupportedFlavorException | IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        popup.add(menuItemPaste);

        return popup;

    }

    public static void editNVPItem(JList invoker, JFrame parent, Client client)
    {

        NameValuePairDecor nvpd = (NameValuePairDecor) invoker.getSelectedValue();
        if (nvpd != null)
        {
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
    }

    private static void addParamToList(JList paramControl, NameValuePairDecor nvp, boolean sort)
    {
        DefaultListModel<NameValuePairDecor> dlm = (DefaultListModel<NameValuePairDecor>) paramControl.getModel();
        if (nvp != null)
        {
            List<NameValuePairDecor> nvpList = new ArrayList<>();
            for (int j = 0; j < dlm.getSize(); j++)
            {
                nvpList.add(dlm.get(j));
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

}
