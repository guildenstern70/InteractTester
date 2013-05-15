/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client.data;

import com.ibm.it.interact.client.XLog;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;

/**
 *
 *
 */
public class RunDataSerializer
{
    public static void Serialize(RunData rd, String fullPath, XLog log)
    {
        ObjectOutputStream oos = null;
        try
        {
            FileOutputStream fos = new FileOutputStream(fullPath);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(rd);
        }
        catch (IOException e)
        {
            log.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public static RunData Deserialize(String fileFullPath, XLog log)
    {
        FileInputStream fis = null;
        RunData rd = null;

        try
        {
            fis = new FileInputStream(fileFullPath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            rd = (RunData) ois.readObject();
        }
        catch (IOException e)
        {
            log.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            log.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return rd;

    }
}
