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
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.unicacorp.interact.api.Offer;
import com.unicacorp.interact.api.OfferList;
import com.unicacorp.interact.api.Response;

import javax.swing.*;
import java.awt.*;
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
        $$$setupUI$$$();
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

    void onComboChange()
    {
        this.client.getLogger().log(Level.INFO, "Selected 'offer change'...");
        this.client.getLogger().log(Level.INFO, "Offers has now " + String.valueOf(this.offers.size()) + " items.");
        int selectedIndex = this.selectOfferComboBox.getSelectedIndex();

        this.client.getLogger().log(Level.INFO, "Trying to get offer #" + String.valueOf(selectedIndex));
        OfferParams op = this.offers.get(selectedIndex + 1);
        this.client.getLogger().log(Level.INFO, "Offer #" + String.valueOf(selectedIndex) + " has " + op.getOfferDetails().length + " items.");
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
            this.selectOfferComboBox.addItem(j + 1);
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
                    "Invalid Offers", JOptionPane.WARNING_MESSAGE);
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
                    "Invalid Offers", JOptionPane.WARNING_MESSAGE);
        }

        return readyToRun;
    }

    private void createUIComponents()
    {
        this.offerParametersList = new JList();
        this.offerParametersList.setName("Offers");
        this.offerParametersList.setModel(new DefaultListModel<NameValuePairDecor>());
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
        getOffersPanel = new JPanel();
        getOffersPanel.setLayout(new FormLayout("fill:101px:noGrow,left:4dlu:noGrow,fill:131px:grow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:d:grow,left:5dlu:noGrow,fill:96px:noGrow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,top:p:noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:d:noGrow"));
        getOffersPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15), null));
        final JLabel label1 = new JLabel();
        label1.setText("Interaction Point:");
        CellConstraints cc = new CellConstraints();
        getOffersPanel.add(label1, cc.xy(1, 1));
        interactionPointTextField = new JTextField();
        getOffersPanel.add(interactionPointTextField, cc.xyw(3, 1, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        numberOfOffersTextField = new JTextField();
        getOffersPanel.add(numberOfOffersTextField, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label2 = new JLabel();
        label2.setText("Number Of Offers:");
        getOffersPanel.add(label2, cc.xy(1, 3));
        runButton = new JButton();
        runButton.setText("Run");
        getOffersPanel.add(runButton, cc.xywh(9, 5, 1, 5, CellConstraints.FILL, CellConstraints.BOTTOM));
        selectOfferComboBox = new JComboBox();
        getOffersPanel.add(selectOfferComboBox, cc.xy(7, 5, CellConstraints.DEFAULT, CellConstraints.TOP));
        final Spacer spacer1 = new Spacer();
        getOffersPanel.add(spacer1, cc.xy(5, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label3 = new JLabel();
        label3.setText("Select Offer:");
        getOffersPanel.add(label3, cc.xy(5, 5, CellConstraints.RIGHT, CellConstraints.BOTTOM));
        final JScrollPane scrollPane1 = new JScrollPane();
        getOffersPanel.add(scrollPane1, cc.xywh(1, 7, 7, 2, CellConstraints.DEFAULT, CellConstraints.FILL));
        offerParametersList.setBackground(new Color(-4600075));
        Font offerParametersListFont = this.$$$getFont$$$(null, -1, 10, offerParametersList.getFont());
        if (offerParametersListFont != null) offerParametersList.setFont(offerParametersListFont);
        scrollPane1.setViewportView(offerParametersList);
        final JLabel label4 = new JLabel();
        label4.setText("Offer Attributes:");
        label4.setVerticalAlignment(0);
        label4.setVerticalTextPosition(0);
        getOffersPanel.add(label4, cc.xy(1, 5, CellConstraints.DEFAULT, CellConstraints.BOTTOM));
        label1.setLabelFor(interactionPointTextField);
        label2.setLabelFor(numberOfOffersTextField);
        label3.setLabelFor(selectOfferComboBox);
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
        return getOffersPanel;
    }
}
