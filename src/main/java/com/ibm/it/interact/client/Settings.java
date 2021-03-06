/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013-14 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client;

import com.ibm.it.interact.ContentUtils;
import com.ibm.it.interact.client.data.InteractConnection;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

/**
 * Settings persistence helper class.
 */
public final class Settings
{
    private static final String PROPERTIES_FILE_PATH = "application.properties";
    private static final String INTERACT_SERVER = "interact.server.";
    private static final String AUTOGENERATE_ID = "interact.tester.AutoGenerateId";
    private static final String LAST_USED_URL = "interact.tester.LastUrl";
    private static final String CLIENT_WIDTH = "interact.tester.Width";
    private static final String CLIENT_HEIGHT = "interact.tester.Height";

    public static final String VERSION = "0.7.1";

    private static Settings settings;
    private Properties props;

    private final XLog log;

    // Properties
    private boolean generateSessionIdAtStartup;
    private List<InteractConnection> unicaServers;
    private int lastUserServer;
    private Dimension clientSize;

    /**
     * Settings Factory Method
     *
     * @param logger
     * @return
     */
    public static Settings getInstance(XLog logger)
    {
        if (settings == null)
        {
            settings = new Settings(logger);
        }
        return settings;
    }

    /**
     * Read properties
     */
    public void readProperties()
    {
        this.log.log("Reading properties file...");
        this.props = ContentUtils.getProperties(PROPERTIES_FILE_PATH);
        this.log.log("... done");
        this.syncWithProperties();
    }

    /**
     * Write properties
     */
    public void writeProperties()
    {
        try
        {
            this.buildProperties();
            this.log.log("Writing properties file...");
            this.props.store(new FileOutputStream(PROPERTIES_FILE_PATH), null);
            this.log.log("... done.");
        }
        catch (IOException ex)
        {
            this.log.log(Level.SEVERE, ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Get properties
     *
     * @return
     */
    public Properties getProperties()
    {
        return props;
    }

    /**
     * Get Generate Session ID at startup property
     *
     * @return
     */
    public boolean isGenerateSessionIdAtStartup()
    {
        return generateSessionIdAtStartup;
    }

    /**
     * Set Generate Session ID at startup property
     *
     * @param generateSessionIdAtStartup
     */
    public void setGenerateSessionIdAtStartup(boolean generateSessionIdAtStartup)
    {
        this.generateSessionIdAtStartup = generateSessionIdAtStartup;
    }

    /**
     * Set client dimensions
     *
     * @param d
     */
    public void setClientSize(Dimension d)
    {
        this.clientSize = d;
    }

    /**
     * Get list of Unica Servers
     *
     * @return
     */
    public List<InteractConnection> getUnicaServers()
    {
        return unicaServers;
    }

    /**
     * Set list of Unica Servers
     *
     * @param unicaServers
     */
    public void setUnicaServers(List<InteractConnection> unicaServers)
    {
        this.unicaServers = unicaServers;
    }

    /**
     * Get last used server (as combo box index)
     *
     * @return
     */
    public int getLastUserServer()
    {
        return lastUserServer;
    }

    /**
     * Get client window size
     *
     * @return
     */
    public Dimension clientDimensions()
    {
        return this.clientSize;
    }

    /**
     * Set last used server (as combo box index)
     *
     * @param lastUserServer
     */
    public void setLastUserServer(int lastUserServer)
    {
        this.lastUserServer = lastUserServer;
    }

    private Settings(XLog logger)
    {
        this.props = new Properties();
        this.log = logger;
        this.lastUserServer = 0;
        this.clientSize = null;
    }

    private void buildProperties()
    {
        this.props.clear();

        // Logger: do not change
        this.props.setProperty("handlers", "java.util.logging.ConsoleHandler");
        this.props.setProperty(".level", "ALL");
        this.props.setProperty("java.util.logging.ConsoleHandler.formatter", "com.ibm.it.logger.LogFormatter");

        // Unica Servers
        for (InteractConnection ic : this.unicaServers)
        {
            this.props.setProperty(INTERACT_SERVER + ic.getConnectionName(), ic.getConnectionUrl().toString());
        }

        // Generate random session id
        if (this.generateSessionIdAtStartup)
        {
            this.props.setProperty(AUTOGENERATE_ID, "true");
        }
        else
        {
            this.props.setProperty(AUTOGENERATE_ID, "false");
        }

        // Last used Unica Server
        this.props.setProperty(LAST_USED_URL, String.valueOf(this.lastUserServer));

        // Client size
        this.props.setProperty(CLIENT_WIDTH, String.valueOf(this.clientSize.getWidth()));
        this.props.setProperty(CLIENT_HEIGHT, String.valueOf(this.clientSize.getHeight()));

    }

    private void syncWithProperties()
    {
        // Unica Servers
        Set<String> propertyNames = this.props.stringPropertyNames();
        this.unicaServers = new ArrayList<>();
        for (String prop : propertyNames)
        {
            if (prop.startsWith(INTERACT_SERVER))
            {
                String connName = prop.substring(16);
                String connUrl = this.props.getProperty(prop);
                InteractConnection ic = new InteractConnection(connName, connUrl);
                this.unicaServers.add(ic);
            }
        }

        // Generate random session id
        String autoId = this.props.getProperty(AUTOGENERATE_ID);
        this.generateSessionIdAtStartup = autoId.equals("true");

        // Last used Unica Server
        String lastUsed = this.props.getProperty(LAST_USED_URL);
        try
        {
            this.lastUserServer = Integer.parseInt(lastUsed);
        }
        catch (NumberFormatException nex)
        {
            this.lastUserServer = 0;
        }

        // Client size
        try
        {
            double clientWidth = Double.parseDouble(this.props.getProperty(CLIENT_WIDTH));
            double clientHeight = Double.parseDouble(this.props.getProperty(CLIENT_HEIGHT));
            this.clientSize = new Dimension((int) clientWidth, (int) clientHeight);
        }
        catch (NumberFormatException | NullPointerException nex)
        {
            this.clientSize = new Dimension(770, 660);
        }

    }
}
