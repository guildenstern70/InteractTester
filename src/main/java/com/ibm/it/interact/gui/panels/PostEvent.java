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
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.unicacorp.interact.api.Offer;
import com.unicacorp.interact.api.OfferList;
import com.unicacorp.interact.api.Response;

import javax.swing.*;
import java.awt.*;
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
    private Map<Integer, OfferParams> offers;

    public PostEvent(MainForm mainForm)
    {
        this.offers = null;
        this.parent = mainForm;
        $$$setupUI$$$();
        this.mainFrame = mainForm.getFrame();
        this.client = this.parent.getClient();
        this.logger = this.client.getLogger();

        this.initializePopupParamsMenu();
        this.initializeEventsAndUI();
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

    /**
     * Update UI from data.
     * It is called from MainForm.java
     *
     * @param ped
     */
    public void updateUIFromData(PostEventData ped)
    {
        String eventName = ped.getEventName();
        String flowChart = ped.getFlowchartName();

        this.eventNameTextField.setText(eventName);
        this.flowchartTextField.setText(flowChart);

        UIUtils.fillParamsList(this.parametersList, ped.getPostEventParams(), true, "UACIExecuteFlowchartByName");
    }

    /**
     * Build a PostEventData object reading data from UI
     *
     * @return
     */
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

    private void getParametersFromOffer(int offerNumber)
    {
        this.logger.log("Getting parameters from offer " + String.valueOf(offerNumber));

        if (this.offers == null || this.offers.size() == 0)
        {
            this.logger.log("Trying to get offers from server...");
            this.initializeOffersFromServer();
            if (this.offers == null || this.offers.size() == 0)
            {
                this.logger.log("Cannot initialize offers from server.");
            }
            else
            {
                this.logger.log("Offers initialized.");
                this.selectOfferAndUpdateUI(offerNumber);
            }
        }
        else
        {
            this.logger.log("Reading Offers from memory...");
            this.selectOfferAndUpdateUI(offerNumber);
        }

    }

    private void initializeOffersFromServer()
    {
        String sessionId = this.parent.getSessionId();
        String interactionPoint = this.parent.getInteractionPoint();
        Response resp;

        if (!Utils.isNotNullNotEmptyNotWhiteSpace(sessionId))
        {
            JOptionPane.showMessageDialog(this.getPanel(),
                    "Invalid Session ID",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (Utils.isNotNullNotEmptyNotWhiteSpace(interactionPoint))
        {
            RunData rd = new RunData(this.parent.getInteractServer(), this.parent.getSessionId());
            GetOffersData god = new GetOffersData();
            god.setInteractionPoint(interactionPoint);
            god.setNumberOfOffers(MAX_NUMBER_OF_OFFERS);  // We try and get all offers here
            rd.setGetOffersData(god);
            this.logger.log("Running get offers...");
            resp = this.client.runGetOffers(rd);
            this.offers = this.buildOffersFromResponse(resp);
        }
        else
        {
            JOptionPane.showMessageDialog(this.getPanel(),
                    "Please set 'Interaction Point' in Get Offers panel",
                    "Invalid Interaction Point",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectOfferAndUpdateUI(int offerNumber)
    {
        this.logger.log("Selecting offer #" + offerNumber);
        OfferParams op = this.offers.get(offerNumber);
        if (op != null)
        {
            UIUtils.fillParamsList(this.parametersList, op.getOfferDetails(), false);
        }
        else
        {
            String msg = "No offer found with index = " + String.valueOf(offerNumber);
            this.logger.log(Level.WARNING, msg);
            JOptionPane.showMessageDialog(this.getPanel(),
                    msg,
                    "Offer Not Found",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Map<Integer, OfferParams> buildOffersFromResponse(Response resp)
    {
        Map<Integer, OfferParams> offers = new HashMap<>();
        OfferList[] offerLists = resp.getAllOfferLists();

        if ((offerLists == null) || (offerLists.length == 0))
        {
            this.logger.log("This campaign has no offers.");
            JOptionPane.showMessageDialog(this.getPanel(),
                    "Interact did not provide any offer data with 'Get Offers'",
                    "No offer found",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

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
        return offers;
    }

    private void createUIComponents()
    {
        this.parametersList = new JList();
        this.parametersList.setName("Parameters");
        this.parametersList.setModel(new DefaultListModel());
    }

    private void initializeEventsAndUI()
    {
        // Run Event
        runButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                run();
            }
        });

        // Get Offer Button Event
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

        // Clear UI Event
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

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$()
    {
        createUIComponents();
        postEventPanel = new JPanel();
        postEventPanel.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:190px:noGrow,left:4dlu:noGrow,fill:50px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:93px:noGrow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,top:28dlu:noGrow"));
        postEventPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15), null));
        final JLabel label1 = new JLabel();
        label1.setText("Event Name: ");
        CellConstraints cc = new CellConstraints();
        postEventPanel.add(label1, cc.xy(1, 1));
        eventNameTextField = new JTextField();
        postEventPanel.add(eventNameTextField, cc.xyw(3, 1, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label2 = new JLabel();
        label2.setText("Parameters:");
        postEventPanel.add(label2, cc.xy(1, 5, CellConstraints.DEFAULT, CellConstraints.TOP));
        runButton = new JButton();
        runButton.setText("Run");
        postEventPanel.add(runButton, cc.xy(11, 7, CellConstraints.DEFAULT, CellConstraints.BOTTOM));
        final JScrollPane scrollPane1 = new JScrollPane();
        postEventPanel.add(scrollPane1, cc.xywh(3, 5, 7, 2, CellConstraints.DEFAULT, CellConstraints.FILL));
        scrollPane1.setViewportView(parametersList);
        getFromOfferComboBox = new JComboBox();
        postEventPanel.add(getFromOfferComboBox, new CellConstraints(5, 7, 1, 1, CellConstraints.DEFAULT, CellConstraints.TOP, new Insets(4, 0, 0, 0)));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FormLayout("fill:d:grow", "center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        postEventPanel.add(panel1, cc.xy(9, 7));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
        panel1.add(panel2, cc.xy(1, 3));
        flowchartTextField = new JTextField();
        postEventPanel.add(flowchartTextField, cc.xyw(3, 3, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label3 = new JLabel();
        label3.setText("Flowchart:");
        postEventPanel.add(label3, cc.xy(1, 3));
        getFromGetOffersButton = new JButton();
        getFromGetOffersButton.setText("Get parameters from offer #");
        postEventPanel.add(getFromGetOffersButton, cc.xy(3, 7, CellConstraints.DEFAULT, CellConstraints.TOP));
        label1.setLabelFor(eventNameTextField);
        label3.setLabelFor(flowchartTextField);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont)
    {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null)
        {
            resultName = currentFont.getName();
        }
        else
        {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1'))
            {
                resultName = fontName;
            }
            else
            {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    {
        return postEventPanel;
    }
}
