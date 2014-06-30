/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013-14 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client.data;

import com.ibm.it.interact.client.XLog;

import java.io.*;
import java.util.logging.Level;

/**
 *
 *
 */
public class RunDataSerializer
{
    public static void Serialize(RunData rd, String fullPath, XLog log)
    {
        ObjectOutputStream oos;
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
        FileInputStream fis;
        RunData rd = null;

        try
        {
            fis = new FileInputStream(fileFullPath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            rd = (RunData) ois.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            log.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        return rd;

    }
}
