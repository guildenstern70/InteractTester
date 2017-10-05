/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013-14 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client;

import com.unicacorp.interact.api.NameValuePair;
import com.unicacorp.interact.api.NameValuePairImpl;

import java.text.DecimalFormat;

public class Utils
{
    /**
     * If the string is null or empty (or whitespace)
     *
     * @param string
     * @return
     */
    public static boolean isNotNullNotEmptyNotWhiteSpace(final String string)
    {
        return string != null && !string.isEmpty() && !string.trim().isEmpty();
    }

    /**
     * Transform NameValuePair array into NameValuePairImpl array
     *
     * @param nvps
     * @return
     */
    public static NameValuePairImpl[] toNVPImpl(NameValuePair[] nvps)
    {
        NameValuePairImpl[] nvpis = new NameValuePairImpl[nvps.length];

        for (int j = 0; j < nvps.length; j++)
        {
            nvpis[j] = (NameValuePairImpl) nvps[j];
        }

        return nvpis;
    }

    public static String formatFromDouble(Double arg)
    {
        String retStr = null;

        if (arg != null)
        {
            try
            {
                retStr = new DecimalFormat("#").format(arg);
            }
            catch (ClassCastException exc)
            {
                retStr = arg.toString();
            }
        }

        return retStr;
    }

}

