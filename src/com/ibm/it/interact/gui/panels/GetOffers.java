/**
 *   UNICA INTERACT TESTER
 *
 *   IBM Confidential
 *   (C) IBM Corp. 2013-14 - All rights reserved.
 *
 *   The source code for this program is not published or otherwise
 *   divested of its trade secrets, irrespective of what has been
 *   deposited with the U.S. Copyright Office.
 *
 *   Author: alessiosaltarin@it.ibm.com
 */

package com.ibm.it.interact.gui.panels;

import com.ibm.it.interact.client.Client;
import com.ibm.it.interact.client.Utils;
import com.ibm.it.interact.client.data.GetOffersData;
import com.ibm.it.interact.client.data.NameValuePairDecor;
import com.ibm.it.interact.client.data.RunData;
import com.ibm.it.interact.gui.MainForm;
import com.ibm.it.interact.gui.UIUtils;
import com.unicacorp.interact.api.Offer;
import com.unicacorp.interact.api.OfferList;
import com.unicacorp.interact.api.Response;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 *
 */
public final class GetOffers implements ITabbedPanel
{
    private static final String TITLE = "Get Offers";

    private JPanel getOffersPanel;
    private JTextField interactionPointTextField;
    private JTextField numberOfOffersTextField;
    private JButton runButton;
    private JComboBox<Integer> selectOfferComboBox;
    private JList offerParametersList;

    // Business logic variables
    private final MainForm parent;
    private final Client client;
    private Map<Integer, OfferParams> offers;

    public GetOffers(MainForm mainForm)
    {
        this.parent = mainForm;
        JFrame mainFrame = mainForm.getFrame();
        this.client = this.parent.getClient();

        runButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                run();
            }
        });
        selectOfferComboBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                onComboChange();
            }
        });
        selectOfferComboBox.setEnabled(false);
    }

    public void onComboChange()
    {
        this.client.getLogger().log(Level.INFO, "Selected 'offer change'...");
        this.client.getLogger().log(Level.INFO, "Offers has now " + String.valueOf(this.offers.size()) + " items.");
        Integer selectedItem = (Integer) this.selectOfferComboBox.getSelectedItem();
        if (selectedItem != null)
        {
            this.client.getLogger().log(Level.INFO, "Trying to get offer #" + String.valueOf(selectedItem));
            OfferParams op = this.offers.get(selectedItem);
            this.client.getLogger().log(Level.INFO, "Offer #" + String.valueOf(selectedItem) + " has " + op.getOfferDetails().length + " items.");
            UIUtils.fillParamsList(this.offerParametersList, op.getOfferDetails(), false);
        }
    }

    public String getInteractionPoint()
    {
        return this.interactionPointTextField.getText();
    }

    public void updateUIFromData(GetOffersData god)
    {
        this.interactionPointTextField.setText(god.getInteractionPoint());
        this.numberOfOffersTextField.setText(String.valueOf(god.getNumberOfOffers()));
    }

    /**
     * Set a new GetOffersData populating its fields (Interaction Point + MAX nr of offers)
     * by the User Interface
     *
     * @return A newly created GetOffersData object
     */
    public GetOffersData getDataFromUI()
    {
        GetOffersData god = new GetOffersData();
        int nroff;

        try
        {
            nroff = Integer.parseInt(this.numberOfOffersTextField.getText());
        }
        catch (NumberFormatException nfe)
        {
            nroff = 100;
        }

        god.setInteractionPoint(this.interactionPointTextField.getText());
        god.setNumberOfOffers(nroff);

        return god;
    }

    @Override
    public JPanel getPanel()
    {
        return this.getOffersPanel;
    }

    @Override
    public void clear()
    {
        this.interactionPointTextField.setText("");
        this.numberOfOffersTextField.setText("");
    }

    @Override
    public String getTitle()
    {
        return TITLE;
    }

    private void fillOffersData(Response resp)
    {
        this.offers = new HashMap<>();
        int offerNum = 0;

        OfferList[] offerLists = resp.getAllOfferLists();
        if (offerLists != null)
        {
            for (OfferList of : offerLists)
            {
                Offer[] offers = of.getRecommendedOffers();
                if (offers != null)
                {
                    for (Offer offer : offers)
                    {
                        OfferParams op = new OfferParams(offer, ++offerNum);
                        this.offers.put(offerNum, op);
                    }
                }
            }
        }

        // Fill offer combobox on this and other panels
        PostEvent pe = this.parent.getPostEventPanel();
        pe.addOffers(offerNum);
        this.selectOfferComboBox.removeAllItems();
        for (int j = 0; j < offerNum; j++)
        {
            Integer offerNumber = new Integer(j + 1);
            this.selectOfferComboBox.addItem(offerNumber);
        }

        // Fill List with first offer
        if (!this.offers.isEmpty())
        {
            OfferParams op = this.offers.get(1);
            UIUtils.fillParamsList(this.offerParametersList, op.getOfferDetails(), false);
            selectOfferComboBox.setEnabled(true);
        }
        else
        {
            selectOfferComboBox.setEnabled(false);
        }

    }

    private void run()
    {
        if (this.client != null)
        {
            if (this.isReadyToRun()) // validation
            {
                this.parent.showStatusMessage("Running GetOffers...");

                RunData rd = new RunData(this.parent.getInteractServer(), this.parent.getSessionId());
                GetOffersData god = this.getDataFromUI();
                if (god != null)
                {
                    rd.setGetOffersData(god);
                    this.client.getLogger().log(Level.INFO, "Running GetOffers...");
                    Response resp = this.client.runGetOffers(rd);
                    this.client.getLogger().log(Level.INFO, "...done");
                    this.fillOffersData(resp);
                }

            }
        }
        else
        {
            System.err.println("Critical: client is NULL.");
        }

        this.parent.showStatusMessage("Ready.");
    }

    private boolean isReadyToRun()
    {
        boolean readyToRun = true;

        String intPoint = this.interactionPointTextField.getText();
        String numOffers = this.numberOfOffersTextField.getText();

        if (!Utils.isNotNullNotEmptyNotWhiteSpace(intPoint))
        {
            readyToRun = false;
            JOptionPane.showMessageDialog(this.getPanel(),
                    "Interaction Point cannot be null",
                    "Invalid InteractionPoint", JOptionPane.WARNING_MESSAGE);
        }
        else if (!Utils.isNotNullNotEmptyNotWhiteSpace(numOffers))
        {
            readyToRun = false;
            JOptionPane.showMessageDialog(this.getPanel(),
                    "Number of Offers cannot be null",
                    "Invalid Nr. Of Offers", JOptionPane.WARNING_MESSAGE);
        }

        try
        {
            @SuppressWarnings("UnusedAssignment") int p = Integer.parseInt(numOffers);
        }
        catch (NumberFormatException nfe)
        {
            readyToRun = false;
            JOptionPane.showMessageDialog(this.getPanel(),
                    "Invalid number of offers",
                    "Invalid Nr. Of Offers", JOptionPane.WARNING_MESSAGE);
        }

        return readyToRun;
    }

    private void createUIComponents()
    {
        this.offerParametersList = new JList();
        this.offerParametersList.setName("Offers");
        this.offerParametersList.setModel(new DefaultListModel<NameValuePairDecor>());
    }
}
