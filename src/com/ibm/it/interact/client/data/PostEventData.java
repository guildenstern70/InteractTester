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
import com.unicacorp.interact.api.NameValuePairImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data needed to run Post Event Interact API
 */
public class PostEventData implements Serializable
{
    private static final long serialVersionUID = 7526472295622776133L;

    private String eventName;
    private List<NameValuePair> postEventParams;

    public PostEventData()
    {
        this.eventName = "";
    }

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
        return this.postEventParams.toArray(new NameValuePair[this.postEventParams.size()]);
    }

    public void setPostEventParams(NameValuePair[] parameters)
    {
        this.postEventParams = new ArrayList<>(parameters.length);
        this.postEventParams.addAll(Arrays.asList(parameters));
    }

    public String getFlowchartName()
    {
        String flowchartName = "";

        for (NameValuePair nvp : this.postEventParams)
        {
            if (nvp.getName().equals("UACIExecuteFlowchartByName"))
            {
                flowchartName = nvp.getValueAsString();
                break;
            }
        }

        return flowchartName;
    }

    public void setFlowchartName(String flowchart)
    {
        boolean found = false;

        if (this.postEventParams != null)
        {

            for (NameValuePair nvp : this.postEventParams)
            {
                if (nvp.getName().equals("UACIExecuteFlowchartByName"))
                {
                    nvp.setValueAsString(flowchart);
                    found = true;
                    break;
                }
            }

            if (!found)
            {
                NameValuePairImpl nvpFlowchart = new NameValuePairImpl();
                nvpFlowchart.setName("UACIExecuteFlowchartByName");
                nvpFlowchart.setValueDataType(NameValuePair.DATA_TYPE_STRING);
                nvpFlowchart.setValueAsString(flowchart);
                this.postEventParams.add(nvpFlowchart);
            }

        }
        else
        {
            System.err.println("Parameters null? Always call setPostEventParameters() before setFlowchartName.");
        }
    }

    public CommandImpl getCommand()
    {
        CommandImpl cmd = new CommandImpl();
        cmd.setMethodIdentifier("postEvent");
        cmd.setEvent(this.eventName);
        cmd.setEventParameters(Utils.toNVPImpl(this.getPostEventParams()));
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
            sb.append("  > ");
            sb.append(nvd.toExtendedString());
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }
}
