/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client;

import java.text.DecimalFormat;
import java.util.Properties;

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

    /**
     * Read properties file
     *
     * @return Handle to read properties
     */
    public static Properties getClientProperties(XLog logger)
    {
        Settings settings = Settings.getInstance(logger);
        settings.readProperties();
        return settings.getProperties();
    }
}

