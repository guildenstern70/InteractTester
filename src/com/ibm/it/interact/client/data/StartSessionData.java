/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client.data;

import com.unicacorp.interact.api.NameValuePair;
import com.unicacorp.interact.api.NameValuePairImpl;

import java.io.Serializable;
import java.util.Calendar;

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
    private NameValuePair[] parameters;

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

    public void initializeWithDefaults()
    {
        this.relyOnExistingSession = false;
        this.debug = true;
        this.interactiveChannel = "CEP";
        this.audienceLevel = "NDG";

        this.audienceIds = new NameValuePairImpl[]{new NameValuePairImpl()};

        this.audienceIds[0].setName("NDG_KEY");
        this.audienceIds[0].setValueDataType(NameValuePair.DATA_TYPE_NUMERIC);
        this.audienceIds[0].setValueAsNumeric(1932707801.0d);

        this.parameters = new NameValuePairImpl[]{new NameValuePairImpl(),
                new NameValuePairImpl(),
                new NameValuePairImpl()};

        this.parameters[0].setName("UACIExecuteFlowchartByName");
        this.parameters[0].setValueDataType(NameValuePair.DATA_TYPE_STRING);
        this.parameters[0].setValueAsString("CEP_Segmentation_Flowchart");

        this.parameters[1].setName("BASIC_EVENT_TYPE_ID");
        this.parameters[1].setValueDataType(NameValuePair.DATA_TYPE_NUMERIC);
        this.parameters[1].setValueAsNumeric(55.0d);

        Calendar today = Calendar.getInstance();
        this.parameters[2].setName("START_TS");
        this.parameters[2].setValueDataType(NameValuePair.DATA_TYPE_DATETIME);
        this.parameters[2].setValueAsDate(today.getTime());
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
        return parameters;
    }

    public final void setParameters(NameValuePair[] parameters)
    {
        this.parameters = parameters;
    }


}
