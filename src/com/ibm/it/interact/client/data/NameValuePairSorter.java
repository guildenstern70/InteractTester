/************************************************
 * UNICA INTERACT TESTER
 * (C) IBM Corp. 2013-14 - All rights reserved.
 *
 * Author: alessiosaltarin@it.ibm.com
 *
 ***********************************************/

package com.ibm.it.interact.client.data;

import java.util.Comparator;

/**
 *
 *
 */
public class NameValuePairSorter implements Comparator<NameValuePairDecor>
{
    @Override
    public int compare(NameValuePairDecor o1, NameValuePairDecor o2)
    {
        return o1.getKey().compareTo(o2.getKey());
    }
}

