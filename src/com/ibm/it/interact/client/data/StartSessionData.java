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
 * Data needed to run Start Session Interact API
 */
public class StartSessionData implements Serializable
{
    private static final long serialVersionUID = 7526472295622776123L;

    private String sessionId;
    private boolean relyOnExistingSession;
    private boolean debug;
    private String interactiveChannel; // aka Interactive Channel
    private NameValuePair[] audienceIds;
    private String audienceLevel;
    private List<NameValuePair> parameters;

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("Start Session Data:");
        sb.append(System.lineSeparator());
        sb.append(" Interactive Channel = ");
        sb.append(this.interactiveChannel);
        sb.append(System.lineSeparator());
        sb.append(" Audience Level = ");
        sb.append(this.audienceLevel);
        sb.append(System.lineSeparator());
        sb.append(" Audience Ids = ");
        for (NameValuePair s : this.audienceIds)
        {
            sb.append(System.lineSeparator());
            NameValuePairDecor nvd = new NameValuePairDecor(s);
            sb.append("  > " + nvd.toExtendedString());

        }
        sb.append(System.lineSeparator());
        sb.append(" Parameters = ");
        for (NameValuePair s : this.getParameters())
        {
            sb.append(System.lineSeparator());
            NameValuePairDecor nvd = new NameValuePairDecor(s);
            sb.append("  > " + nvd.toExtendedString());
        }
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    public CommandImpl getCommand()
    {
        CommandImpl cmd = new CommandImpl();
        cmd.setMethodIdentifier("startSession");
        cmd.setRelyOnExistingSession(this.relyOnExistingSession);
        cmd.setDebug(true);
        cmd.setInteractiveChannel(this.interactiveChannel);
        cmd.setAudienceID(Utils.toNVPImpl(this.audienceIds));
        cmd.setAudienceLevel(this.audienceLevel);
        cmd.setEventParameters(Utils.toNVPImpl(this.getParameters()));
        return cmd;
    }

    public String getFlowchartName()
    {
        String flowchartName = "";

        for (NameValuePair nvp : this.parameters)
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

        if (this.parameters != null)
        {

            for (NameValuePair nvp : this.parameters)
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
                this.parameters.add(nvpFlowchart);
            }

        }
        else
        {
            System.err.println("Parameters null?!");
        }
    }

    public final String getSessionId()
    {
        return sessionId;
    }

    public final void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public final boolean isRelyOnExistingSession()
    {
        return relyOnExistingSession;
    }

    public final void setRelyOnExistingSession(boolean relyOnExistingSession)
    {
        this.relyOnExistingSession = relyOnExistingSession;
    }

    public final boolean isDebug()
    {
        return debug;
    }

    public final void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public final String getInteractiveChannel()
    {
        return interactiveChannel;
    }

    public final void setInteractiveChannel(String interactiveChannel)
    {
        this.interactiveChannel = interactiveChannel;
    }

    public final NameValuePair[] getAudienceIds()
    {
        return audienceIds;
    }

    public final void setAudienceIds(NameValuePair[] audienceIds)
    {
        this.audienceIds = audienceIds;
    }

    public final String getAudienceLevel()
    {
        return audienceLevel;
    }

    public final void setAudienceLevel(String audienceLevel)
    {
        this.audienceLevel = audienceLevel;
    }

    public final NameValuePair[] getParameters()
    {
        return this.parameters.toArray(new NameValuePair[this.parameters.size()]);
    }

    public final void setParameters(NameValuePair[] parameters)
    {
        this.parameters = new ArrayList<NameValuePair>(parameters.length);
        this.parameters.addAll(Arrays.asList(parameters));
    }


}
