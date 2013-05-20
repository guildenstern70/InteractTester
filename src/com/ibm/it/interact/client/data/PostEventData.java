/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client.data;

import com.ibm.it.interact.client.Utils;
import com.unicacorp.interact.api.CommandImpl;
import com.unicacorp.interact.api.NameValuePair;

import java.io.Serializable;

/**
 * Data needed to run Post Event Interact API
 */
public class PostEventData implements Serializable
{
    private static final long serialVersionUID = 7526472295622776133L;

    private String eventName;
    private NameValuePair[] postEventParams;

    public String getEventName()
    {
        return eventName;
    }

    public void setEventName(String eventName)
    {
        this.eventName = eventName;
    }

    public NameValuePair[] getPostEventParams()
    {
        return postEventParams;
    }

    public void setPostEventParams(NameValuePair[] postEventParams)
    {
        this.postEventParams = postEventParams;
    }

    public CommandImpl getCommand()
    {
        CommandImpl cmd = new CommandImpl();
        cmd.setMethodIdentifier("postEvent");
        cmd.setEvent(this.eventName);
        cmd.setEventParameters(Utils.toNVPImpl(this.postEventParams));
        return cmd;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("PostEvent Data:");
        sb.append(System.lineSeparator());
        sb.append(" Event Name = ");
        sb.append(this.eventName);
        sb.append(System.lineSeparator());
        sb.append(" Parameters = ");
        for (NameValuePair s : this.postEventParams)
        {
            sb.append(System.lineSeparator());
            NameValuePairDecor nvd = new NameValuePairDecor(s);
            sb.append("  > " + nvd.toExtendedString());
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }
}
