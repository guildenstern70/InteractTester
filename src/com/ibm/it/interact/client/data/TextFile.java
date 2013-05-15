package com.ibm.it.interact.client.data;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**

 */
public class TextFile extends FileFilter
{
    public boolean accept(File f)
    {
        if (f.isDirectory())
        {
            return true;
        }
        return f.getName().endsWith(".txt");
    }

    public String getDescription()
    {
        return "Text files (*.txt)";
    }
}
