/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013-14 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client.data;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 *
 *
 */
public class LogDataFile extends FileFilter
{
    private final String[] okFileExtensions =
            new String[] { "log", "txt" };

    @Override
    public boolean accept(File file)
    {

        for (String extension : okFileExtensions)
        {
            if (file.getName().toLowerCase().endsWith(extension))
            {
                return true;
            }
        }
        return false;

    }

    @Override
    public String getDescription()
    {
        return "Log file";
    }
}
