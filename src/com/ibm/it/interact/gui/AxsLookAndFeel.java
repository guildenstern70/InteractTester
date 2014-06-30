/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013-14 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.gui;

/**
 * Look and feel enumeration.
 * To use with JGoodies Looks
 */
public enum AxsLookAndFeel
{
    LOOKS(0), METAL(1), WINDOWS(2), SYSTEM(3), PLASTIC(4),
    PLASTICXP(5), XPLATFORM(6), OCEAN(7), NIMBUS(8);

    public int value()
    {
        return laf;
    }

    private AxsLookAndFeel(int value)
    {
        this.laf = value;
    }

    private final int laf;

}
