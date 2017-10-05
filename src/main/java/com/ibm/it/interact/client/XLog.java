/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013-14 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client;

import com.ibm.it.interact.gui.MainForm;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Logger utility class
 */
public class XLog
{
    private static final String LOG_ID = "com.ibm.it.interact";
    private static final Logger LOGGER = Logger.getLogger(LOG_ID);

    private final MainForm guiForm;

    /**
     * Constructor
     *
     * @param form Parent caller form, indicates
     *             what form must be called to display log
     */
    public XLog(MainForm form)
    {
        this.guiForm = form;
    }

    /**
     * Initializes logger by reading the 'application.properties' file.
     */
    public void initialize()
    {
        Properties prop = System.getProperties();
        prop.setProperty(
                "java.util.logging.config.file",
                "application.properties");

        try
        {
            LogManager.getLogManager().readConfiguration();
        }
        catch (SecurityException | IOException e)
        {
            LOGGER.severe(e.getMessage());
        }
    }

    /**
     * Log a message
     *
     * @param message Message to log
     */
    public void log(final String message)
    {
        this.log(Level.INFO, message);
    }

    /**
     * Log a message with a given level
     *
     * @param level   Level of log
     * @param message Message to log
     */
    public void log(Level level, final String message)
    {
        guiForm.updateConsole(XLog.formatMessageWithDateTime(message));
        LOGGER.log(level, message);
    }

    private static String formatMessageWithDateTime(String message)
    {
        StringBuilder sb = new StringBuilder();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        sb.append(dateFormat.format(date));
        sb.append(" > ");
        sb.append(message);
        return sb.toString();
    }

}
