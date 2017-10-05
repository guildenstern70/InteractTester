package com.ibm.it.interact.gui.panels;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class PopupListener extends MouseAdapter
{
    private final JPopupMenu popup;

    public PopupListener(JPopupMenu menu)
    {
        this.popup = menu;
    }

    public void mousePressed(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e)
    {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e)
    {
        if (e.isPopupTrigger())
        {
            this.popup.show(e.getComponent(),
                    e.getX(), e.getY());
        }
    }
}
