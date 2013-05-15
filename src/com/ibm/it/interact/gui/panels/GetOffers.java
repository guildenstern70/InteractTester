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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

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
    private JCheckBox sendParametersFromStartCheckBox;
    private JButton runButton;
    private JComboBox selectOfferComboBox;
    private JList offerParametersList;
    private JFrame mainFrame;

    // Business logic variables
    private MainForm parent;
    private Client client;
    private Map<Integer, OfferParams> offers;

    public GetOffers(MainForm mainForm)
    {
        this.parent = mainForm;
        this.mainFrame = mainForm.getFrame();
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
        int selectedItem = this.selectOfferComboBox.getSelectedIndex();
        OfferParams op = this.offers.get(selectedItem + 1);
        UIUtils.fillParamsList(this.offerParametersList, op.getOfferDetails(), false);
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

    public GetOffersData getDataFromUI()
    {
        GetOffersData god = null;
        int nroff;

        god = new GetOffersData();

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
        this.offers = new HashMap<Integer, OfferParams>();
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
                        this.offers.put(new Integer(offerNum), op);
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
            this.selectOfferComboBox.addItem(new Integer(j + 1));
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
                RunData rd = new RunData(this.parent.getInteractServer(),
                        this.parent.getSessionId());
                GetOffersData god = this.getDataFromUI();
                if (god != null)
                {
                    rd.setGetOffersData(god);
                    Response resp = this.client.runGetOffers(rd);
                    this.fillOffersData(resp);
                }
            }
        }
        else
        {
            System.err.println("Critical: client is NULL.");
        }
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
                    "Invalid InteractionPoint", JOptionPane.OK_OPTION);
        }
        else if (!Utils.isNotNullNotEmptyNotWhiteSpace(numOffers))
        {
            readyToRun = false;
            JOptionPane.showMessageDialog(this.getPanel(),
                    "Number of Offers cannot be null",
                    "Invalid Nr. Of Offers", JOptionPane.OK_OPTION);
        }

        try
        {
            int p = Integer.parseInt(numOffers);
        }
        catch (NumberFormatException nfe)
        {
            readyToRun = false;
            JOptionPane.showMessageDialog(this.getPanel(),
                    "Invalid number of offers",
                    "Invalid Nr. Of Offers", JOptionPane.OK_OPTION);
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
