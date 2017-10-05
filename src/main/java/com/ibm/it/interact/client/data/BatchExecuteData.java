/**
 *   UNICA INTERACT TESTER
 *   (C) IBM Corp. 2013-14 - All rights reserved.
 *
 *   Author: alessiosaltarin@it.ibm.com
 */

package com.ibm.it.interact.client.data;

import java.io.Serializable;

/**
 */
public class BatchExecuteData implements Serializable
{
    private static final long serialVersionUID = 7526999995622776122L;

    private boolean exeStartSession;
    private boolean exeGetOffers;
    private boolean exePostEvent;

    public boolean isExeEndSession()
    {
        return exeEndSession;
    }

    public void setExeEndSession(boolean exeEndSession)
    {
        this.exeEndSession = exeEndSession;
    }

    private boolean exeEndSession;

    public boolean isExeStartSession()
    {
        return exeStartSession;
    }

    public void setExeStartSession(boolean exeStartSession)
    {
        this.exeStartSession = exeStartSession;
    }

    public boolean isExeGetOffers()
    {
        return exeGetOffers;
    }

    public void setExeGetOffers(boolean exeGetOffers)
    {
        this.exeGetOffers = exeGetOffers;
    }

    public boolean isExePostEvent()
    {
        return exePostEvent;
    }

    public void setExePostEvent(boolean exePostEvent)
    {
        this.exePostEvent = exePostEvent;
    }

    public int numberOfCommands()
    {
        int commands = 0;

        if (this.exeStartSession)
        {
            commands += 1;
        }

        if (this.exeGetOffers)
        {
            commands += 1;
        }

        if (this.exePostEvent)
        {
            commands += 1;
        }

        return commands;
    }

}
