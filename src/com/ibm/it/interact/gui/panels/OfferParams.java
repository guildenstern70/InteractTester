package com.ibm.it.interact.gui.panels;

import com.ibm.it.interact.client.data.NameValuePairDecor;
import com.ibm.it.interact.client.data.NameValuePairSorter;
import com.unicacorp.interact.api.NameValuePair;
import com.unicacorp.interact.api.NameValuePairImpl;
import com.unicacorp.interact.api.Offer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 */
public class OfferParams
{
    private int offerId;
    private Offer unicaOffer;
    private String treatmentCode;
    private String name;
    private String[] offerCodes;
    private List<NameValuePairDecor> params;

    public OfferParams(Offer offer, int id)
    {
        this.unicaOffer = offer;
        this.offerId = id;
        this.params = new ArrayList<NameValuePairDecor>();
        this.initialize();
    }

    public String getName()
    {
        return this.name;
    }

    public NameValuePair[] getOfferDetails()
    {
        final int size = this.params.size() + this.offerCodes.length + 2;
        NameValuePair nvps[] = new NameValuePair[size];

        int j = 0;

        nvps[j] = new NameValuePairImpl();
        nvps[j].setName("Offer Name");
        nvps[j].setValueAsString(this.name);
        nvps[j].setValueDataType("string");

        j++;

        nvps[j] = new NameValuePairImpl();
        nvps[j].setName("Treatment Code");
        nvps[j].setValueAsString(this.treatmentCode);
        nvps[j].setValueDataType("string");

        for (String offerCode : this.offerCodes)
        {
            j++;
            nvps[j] = new NameValuePairImpl();
            nvps[j].setName("Offer Code");
            nvps[j].setValueAsString(offerCode);
            nvps[j].setValueDataType("string");
        }

        Collections.sort(this.params, new NameValuePairSorter());

        for (NameValuePairDecor nvpd : this.params)
        {
            j++;
            nvps[j] = nvpd.getNameValuePair();
        }

        return nvps;
    }

    public int getOfferId()
    {
        return offerId;
    }

    public Offer getUnicaOffer()
    {
        return unicaOffer;
    }

    public String getTreatmentCode()
    {
        return treatmentCode;
    }

    public String[] getOfferCodes()
    {
        return offerCodes;
    }

    public List<NameValuePairDecor> getParams()
    {
        return params;
    }

    private void initialize()
    {
        this.treatmentCode = this.unicaOffer.getTreatmentCode();
        this.offerCodes = this.unicaOffer.getOfferCode();
        this.name = this.unicaOffer.getOfferName();
        NameValuePair[] offerValues = this.unicaOffer.getAdditionalAttributes();
        if (treatmentCode != null)
        {
            if (offerValues != null)
            {
                for (NameValuePair nvp : offerValues)
                {
                    this.params.add(new NameValuePairDecor(nvp));
                }
            }
        }

    }
}
