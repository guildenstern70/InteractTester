/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013-14 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client.data;

import com.ibm.it.interact.client.Utils;
import com.unicacorp.interact.api.NameValuePair;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility wrapper class for
 * com.unicacorp.interact.api.NameValuePair.
 * Adds toString() and String representation for value.
 */
public final class NameValuePairDecor implements Serializable, Transferable, ClipboardOwner
{
    private static final long serialVersionUID = 7526472295622776121L;
    public static final DataFlavor clipboardDataFlavor =
            new DataFlavor(NameValuePairDecor.class, "NameValuePairDecor Object");

    private final NameValuePair nvp;
    private String kind;

    public NameValuePairDecor(NameValuePair xNvp)
    {
        this.nvp = xNvp;
        this.kind = this.nvp.getValueDataType();
        if (this.kind == null)
        {
            this.kind = "string";
        }
    }

    public final NameValuePair getNameValuePair()
    {
        return this.nvp;
    }

    public final boolean isNumeric()
    {
        return this.kind.equals("numeric");
    }

    public final boolean isString()
    {
        return this.kind.equals("string");
    }

    public final boolean isDate()
    {
        return this.kind.equals("datetime");
    }

    public final String getKey()
    {
        return this.getNameValuePair().getName();
    }

    public final boolean isNull()
    {
        // A parameter is null when it's value is null
        boolean isNull = false;

        switch (this.kind)
        {
            case "numeric":
                Double valDbl = this.nvp.getValueAsNumeric();
                if (valDbl == null)
                    isNull = true;
                break;
            case "string":
                String strVal = this.nvp.getValueAsString();
                if (strVal == null || strVal.toLowerCase().equals("null"))
                    isNull = true;
                break;
            default:
                Date dt = this.nvp.getValueAsDate();
                if (dt == null)
                    isNull = true;
                break;
        }

        return isNull;
    }

    public final String getValue()
    {
        String val;

        switch (this.kind)
        {
            case "numeric":
                Double valDbl = this.nvp.getValueAsNumeric();
                val = Utils.formatFromDouble(valDbl);
                break;
            case "string":
                val = this.nvp.getValueAsString();
                break;
            default:
                // Target format: 2014-06-12 10:45:54.552
                SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
                val = format.format(this.nvp.getValueAsDate());
                break;
        }
        return val;
    }

    public final String toExtendedString()
    {
        StringBuilder sb = new StringBuilder(this.toString());
        sb.append(" (");
        sb.append(this.nvp.getValueDataType());
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.nvp.getName());
        sb.append(" = ");
        sb.append(this.getValue());
        return sb.toString();
    }

    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
        DataFlavor[] ret = { NameValuePairDecor.clipboardDataFlavor };
        return ret;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        return NameValuePairDecor.clipboardDataFlavor.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {

        if (this.isDataFlavorSupported(flavor))
        {
            return this;
        }

        return null;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents)
    {
        System.out.println("MyObjectSelection: Lost ownership");
    }
}
