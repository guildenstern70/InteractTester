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
 * Defines the file definition used by Open/Save File Dialogs
 */
public class RunDataFile extends FileFilter
{
    @Override
    public boolean accept(File f)
    {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".itf");
    }

    @Override
    public String getDescription()
    {
        return ".itf Interact Tester file";
    }
}
