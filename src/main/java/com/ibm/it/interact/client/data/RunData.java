/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013-14 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client.data;

import com.ibm.it.interact.client.Utils;

import java.io.File;
import java.io.Serializable;

/**
 * Interact Run Data
 */
public class RunData implements Serializable
{
    private static final long serialVersionUID = 7526472295622776122L;

    // Transient data
    private transient String sessionId;
    private transient InteractConnection interactURL;

    // Session data
    private String fullFilePath;
    private boolean hasBeenModified;

    // Panels data
    private StartSessionData startSessionData;
    private GetOffersData getOffersData;
    private PostEventData postEventData;
    private BatchExecuteData batchExecuteData;

    /**
     * Constructor
     *
     * @param iUrl
     * @param session
     */
    public RunData(InteractConnection iUrl, String session)
    {
        this.sessionId = session;
        this.interactURL = iUrl;
        this.startSessionData = new StartSessionData();
        this.getOffersData = new GetOffersData();
        this.postEventData = new PostEventData();
        this.batchExecuteData = new BatchExecuteData();
    }

    /**
     * True if the data is valid to make minimal API calls
     *
     * @return
     */
    public boolean isValid()
    {
        boolean isValid = true;

        if (this.interactURL == null)
        {
            isValid = false;
        }
        else if (!Utils.isNotNullNotEmptyNotWhiteSpace(this.startSessionData.getInteractiveChannel()))
        {
            isValid = false;
        }

        return isValid;
    }

    public StartSessionData getStartSessionData()
    {
        return this.startSessionData;
    }

    public BatchExecuteData getBatchExecuteData()
    {
        return this.batchExecuteData;
    }

    public void setStartSessionData(StartSessionData startSessionData)
    {
        this.startSessionData = startSessionData;
    }

    public GetOffersData getGetOffersData()
    {
        return this.getOffersData;
    }

    public void setGetOffersData(GetOffersData getOffersData)
    {
        this.getOffersData = getOffersData;
    }

    public PostEventData getPostEventData()
    {
        return postEventData;
    }

    public void setPostEventData(PostEventData postEventData)
    {
        this.postEventData = postEventData;
    }

    public void setBatchExecuteData(BatchExecuteData bed)
    {
        this.batchExecuteData = bed;
    }

    public String getInteractURL()
    {
        return this.interactURL.getConnectionUrl().toString();
    }

    public InteractConnection getInteractServer()
    {
        return this.interactURL;
    }

    public void setInteractServer(InteractConnection iURL)
    {
        this.interactURL = iURL;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getRunDataFilePath()
    {
        return this.fullFilePath;
    }

    public String getRunDataName()
    {
        String fileName = "";

        if (this.fullFilePath != null)
        {
            fileName = new File(this.fullFilePath).getName();
            if (fileName.endsWith(".itf"))
            {
                fileName = fileName.substring(0, fileName.length() - 4);
            }
        }

        return fileName;
    }

    public void setRunDataFilePath(String runDataFullPath)
    {
        this.fullFilePath = runDataFullPath;
    }

    public boolean isHasBeenModified()
    {
        return hasBeenModified;
    }

    public void setHasBeenModified(boolean hasBeenModified)
    {
        this.hasBeenModified = hasBeenModified;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("INTERACT TEST DATA");
        String dataName = this.getRunDataName();
        if (Utils.isNotNullNotEmptyNotWhiteSpace(dataName))
        {
            sb.append(": ");
            sb.append(dataName);
        }
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(this.startSessionData.toString());
        sb.append(System.lineSeparator());
        sb.append(this.getOffersData.toString());
        sb.append(System.lineSeparator());
        sb.append(this.postEventData.toString());
        sb.append(System.lineSeparator());
        return sb.toString();
    }

}
