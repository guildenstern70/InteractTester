/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013-14 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client.data;

import com.ibm.it.interact.client.EqualsUtils;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public class InteractConnection implements Serializable
{
    private static final long serialVersionUID = 7526472345622776121L;
    private static final String interactServicePath = "/interact/servlet/InteractJSService";

    private URL connectionUrl;
    private String connectionName;

    public InteractConnection(int serverNumber, String url)
    {
        StringBuilder sb = new StringBuilder("Server#");
        sb.append(serverNumber);
        this.connectionName = sb.toString();
        this.setConnectionUrl(url);
    }

    public InteractConnection(String name, String url)
    {
        this.connectionName = name;
        this.setConnectionUrl(url);
    }

    public String getConnectionSimplifiedUrl()
    {
        String connectionURL = this.connectionUrl.getProtocol() + "://" + this.connectionUrl.getHost();
        int port = this.connectionUrl.getPort();
        if (port > 0 && port != 80)
        {
            connectionURL += ":" + String.valueOf(port);
        }
        return connectionURL;
    }

    public URL getConnectionUrl()
    {
        return connectionUrl;
    }

    public void setConnectionUrl(URL connectionUrl)
    {
        this.connectionUrl = connectionUrl;
    }

    void setConnectionUrl(String connectionUrl1)
    {
        if (!connectionUrl1.endsWith(InteractConnection.interactServicePath))
        {
            connectionUrl1 += InteractConnection.interactServicePath;
        }

        try
        {
            this.connectionUrl = new URL(connectionUrl1);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    public String getConnectionName()
    {
        return connectionName;
    }

    public void setConnectionName(String connectionName)
    {
        this.connectionName = connectionName;
    }

    @Override
    public String toString()
    {
        return this.connectionName; //+ " (" + this.connectionUrl.toString() + ")";
    }

    @Override
    public boolean equals(Object aThat)
    {
        if (this == aThat)
        {
            return true;
        }

        if (!(aThat instanceof InteractConnection))
        {
            return false;
        }

        InteractConnection that = (InteractConnection) aThat;

        //now a proper field-by-field evaluation can be made
        return
                (EqualsUtils.areEqual(this.connectionName, that.connectionName) &&
                        EqualsUtils.areEqual(this.connectionUrl, that.connectionUrl));

    }
}
