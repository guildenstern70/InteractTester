/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client.data;

import com.ibm.it.interact.client.Utils;
import com.unicacorp.interact.api.NameValuePair;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Utility wrapper class for
 * com.unicacorp.interact.api.NameValuePair.
 * Adds toString() and String representation for value.
 */
public final class NameValuePairDecor implements Serializable
{
    private static final long serialVersionUID = 7526472295622776121L;

    private NameValuePair nvp;
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

    public final String getValue()
    {
        String val = "Unknown";

        if (this.kind.equals("numeric"))
        {
            Double valDbl = this.nvp.getValueAsNumeric();
            val = Utils.formatFromDouble(valDbl);
        } else if (this.kind.equals("string"))
        {
            val = this.nvp.getValueAsString();
        } else
        {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
            val = format.format(this.nvp.getValueAsDate());
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
}
