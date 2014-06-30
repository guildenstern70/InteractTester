/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013-14 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client.data;

import com.unicacorp.interact.api.CommandImpl;

import java.io.Serializable;

/**
 * Data needed to run Get Offers Interact API
 */
public class GetOffersData implements Serializable
{
    private static final long serialVersionUID = 7526472295622776120L;

    private String interactionPoint;
    private int numberOfOffers;

    public GetOffersData()
    {
        this.initialize();
    }

    private void initialize()
    {
        this.interactionPoint = "";
        this.numberOfOffers = 1;
    }

    public String getInteractionPoint()
    {
        return interactionPoint;
    }

    public void setInteractionPoint(String interactionPoint)
    {
        this.interactionPoint = interactionPoint;
    }

    public int getNumberOfOffers()
    {
        return numberOfOffers;
    }

    public void setNumberOfOffers(int numberOfOffers)
    {
        this.numberOfOffers = numberOfOffers;
    }

    public CommandImpl getCommand()
    {
        CommandImpl cmd = new CommandImpl();
        cmd.setMethodIdentifier("getOffers");
        cmd.setInteractionPoint(this.interactionPoint);
        cmd.setNumberRequested(this.numberOfOffers);
        return cmd;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("GetOffers Data:");
        sb.append(System.lineSeparator());
        sb.append(" Interaction Point = ");
        sb.append(this.interactionPoint);
        sb.append(System.lineSeparator());
        sb.append(" Number Of Offers = ");
        sb.append(this.numberOfOffers);
        sb.append(System.lineSeparator());
        return sb.toString();
    }

}
