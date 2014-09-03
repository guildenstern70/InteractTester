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
import com.ibm.it.interact.client.XLog;
import com.ibm.it.interact.client.data.GetOffersData;
import com.ibm.it.interact.client.data.PostEventData;
import com.ibm.it.interact.client.data.RunData;
import com.ibm.it.interact.gui.EditItemAdapter;
import com.ibm.it.interact.gui.MainForm;
import com.ibm.it.interact.gui.UIUtils;
import com.unicacorp.interact.api.Offer;
import com.unicacorp.interact.api.OfferList;
import com.unicacorp.interact.api.Response;

import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Post Event panel
 */
public final class PostEvent implements ITabbedPanel
{
    private static final String TITLE = "Post Event";
    private static final int MAX_NUMBER_OF_OFFERS = 50;

    private JPanel postEventPanel;
    private JTextField eventNameTextField;
    private JList parametersList;
    private JButton getFromGetOffersButton;
    private JButton runButton;
    private JComboBox getFromOfferComboBox;
    private JTextField flowchartTextField;
    private final JFrame mainFrame;
    private final XLog logger;

    // Business logic variables
    private final MainForm parent;
    private final Client client;

    public PostEvent(MainForm mainForm)
    {
        this.parent = mainForm;
        this.mainFrame = mainForm.getFrame();
        this.client = this.parent.getClient();
        this.logger = this.client.getLogger();

        this.initializePopupParamsMenu();

        runButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                run();
            }
        });
        getFromGetOffersButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int selectedOffer = getFromOfferComboBox.getSelectedIndex() + 1;
                if (selectedOffer > 0)
                {
                    getParametersFromOffer(selectedOffer);
                }
            }
        });
        postEventPanel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                parametersList.clearSelection();
            }
        });
        EditItemAdapter mouseAdapter1 = new EditItemAdapter(this.parametersList, this.mainFrame, this.client);
        this.parametersList.addMouseListener(mouseAdapter1);

        this.getFromOfferComboBox.setEnabled(false);
        this.getFromGetOffersButton.setEnabled(false);
    }

    public void addOffers(int howManyOffers)
    {
        if (howManyOffers > 0)
        {
            this.getFromOfferComboBox.removeAllItems();

            for (int j = 0; j < howManyOffers; j++)
            {
                this.getFromOfferComboBox.addItem(j + 1);
            }

            this.getFromGetOffersButton.setEnabled(true);
            this.getFromOfferComboBox.setEnabled(true);
        }
    }

    @Override
    public String getTitle()
    {
        return TITLE;
    }

    @Override
    public JPanel getPanel()
    {
        return this.postEventPanel;
    }

    @Override
    public void clear()
    {
        this.eventNameTextField.setText("");
        UIUtils.clearList(parametersList);
    }

    public void updateUIFromData(PostEventData ped)
    {
        String eventName = ped.getEventName();
        String flowChart = ped.getFlowchartName();

        this.eventNameTextField.setText(eventName);
        this.flowchartTextField.setText(flowChart);

        UIUtils.fillParamsList(this.parametersList, ped.getPostEventParams(), true, "UACIExecuteFlowchartByName");
    }

    public PostEventData getDataFromUI()
    {
        PostEventData ped = new PostEventData();
        String eventName = this.eventNameTextField.getText();
        String flowChart = this.flowchartTextField.getText();

        if (Utils.isNotNullNotEmptyNotWhiteSpace(eventName))
        {
            ped.setEventName(eventName);
        }

        ped.setPostEventParams(UIUtils.getNameValuePairs(this.parametersList));

        if (Utils.isNotNullNotEmptyNotWhiteSpace(flowChart))
        {
            ped.setFlowchartName(flowChart);
        }

        return ped;
    }

    private void initializePopupParamsMenu()
    {
        MouseListener popupListener = new PopupListener(UIUtils.buildParametersPopupMenu(mainFrame, client));
        this.parametersList.addMouseListener(popupListener);

        this.getPanel().updateUI();
        this.getPanel().validate();
    }

    private boolean isReadyToRun()
    {
        boolean readyToRun = true;

        String eventName = this.eventNameTextField.getText();

        if (!Utils.isNotNullNotEmptyNotWhiteSpace(eventName))
        {
            readyToRun = false;
            JOptionPane.showMessageDialog(this.getPanel(),
                    "Event Name cannot be null",
                    "Invalid Event Name", JOptionPane.ERROR_MESSAGE);
        }

        return readyToRun;
    }

    private void run()
    {
        if (this.client != null)
        {
            if (this.isReadyToRun()) // validation
            {
                RunData rd = new RunData(this.parent.getInteractServer(), this.parent.getSessionId());
                PostEventData pod = this.getDataFromUI();
                if (pod != null)
                {
                    this.logger.log("Running PostEvent");
                    this.parent.showStatusMessage("Running PostEvent...");
                    rd.setPostEventData(pod);
                    this.client.runPostEvent(rd);
                    this.parent.showStatusMessage("Ready.");
                }
            }
        }
        else
        {
            System.err.println("Critical: client is NULL.");
        }

    }

    private void getParametersFromOffer(int offerNumber)
    {
        this.logger.log("Getting parameters from offer " + String.valueOf(offerNumber));
        String sessionId = this.parent.getSessionId();
        String interactionPoint = this.parent.getInteractionPoint();
        Response resp;

        if (!Utils.isNotNullNotEmptyNotWhiteSpace(sessionId))
        {
            JOptionPane.showMessageDialog(this.getPanel(),
                    "Error",
                    "Invalid Session ID", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (this.client != null)
        {
            if (Utils.isNotNullNotEmptyNotWhiteSpace(interactionPoint))
            {
                RunData rd = new RunData(this.parent.getInteractServer(), this.parent.getSessionId());
                GetOffersData god = new GetOffersData();
                god.setInteractionPoint(interactionPoint);
                god.setNumberOfOffers(MAX_NUMBER_OF_OFFERS);  // We try and get all offers here
                rd.setGetOffersData(god);
                this.logger.log("Running get offers...");
                resp = this.client.runGetOffers(rd);

                OfferList[] offerLists = resp.getAllOfferLists();

                if ((offerLists == null) || (offerLists.length == 0))
                {
                    this.logger.log(Level.WARNING, "This campaign has no offers.");
                    JOptionPane.showMessageDialog(this.getPanel(),
                            "No offer found",
                            "Interact did not provide any offer data with 'Get Offers'",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Map<Integer, OfferParams> offers = new HashMap<>();
                int offerNum = 0;

                for (OfferList of : offerLists)
                {
                    for (Offer offer : of.getRecommendedOffers())
                    {
                        offerNum += 1;
                        this.logger.log("Adding offer #" + offerNum);
                        offers.put(offerNum, new OfferParams(offer, offerNum));
                    }
                }

                this.logger.log("Selecting offer #" + offerNumber);
                OfferParams op = offers.get(offerNumber);
                if (op != null)
                {
                    UIUtils.fillParamsList(this.parametersList, op.getOfferDetails(), false);
                }
                else
                {
                    this.logger.log(Level.WARNING, "No offer is found with index=" + String.valueOf(offerNumber));
                }

            }
            else
            {
                JOptionPane.showMessageDialog(this.getPanel(),
                        "Invalid Interaction Point",
                        "Please set 'Interaction Point' in Get Offers panel",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    private void createUIComponents()
    {
        this.parametersList = new JList();
        this.parametersList.setName("Parameters");
        this.parametersList.setModel(new DefaultListModel());
    }
}
