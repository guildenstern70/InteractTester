/**
 *   UNICA INTERACT TESTER
 *   (C) IBM Corp. 2013-14 - All rights reserved.
 *
 *   Author: alessiosaltarin@it.ibm.com
 */

package com.ibm.it.interact.client;

import com.ibm.it.interact.client.data.*;
import com.unicacorp.interact.api.*;
import com.unicacorp.interact.api.jsoverhttp.InteractAPI;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;

public final class Client
{
    private final XLog logger;

    public Client(XLog log)
    {
        this.logger = log;
        this.logger.log("Welcome to Unica Interact Tester v." + Settings.VERSION);
    }

    public XLog getLogger()
    {
        return this.logger;
    }

    public BatchResponse runBatch(RunData rd, boolean sendEndSession)
    {
        BatchResponse response = null;
        InteractAPI api = this.initializeAPI(rd);

        BatchExecuteData bed = rd.getBatchExecuteData();

        if (bed.numberOfCommands() > 0)
        {

            this.logger.log("-----------------------");
            this.logger.log("  RUNNING BATCH EXECUTE  ");
            this.logger.log("-----------------------");

            ArrayList<Command> commands = new ArrayList<>();

            if (bed.isExeStartSession())
            {
                this.logger.log("  Adding 'StartSession' to command queue.");
                CommandImpl cmdStartSession = rd.getStartSessionData().getCommand();
                commands.add(cmdStartSession);
            }

            if (bed.isExeGetOffers())
            {
                this.logger.log("  Adding 'GetOffers' to command queue.");
                CommandImpl cmdGetOffers = rd.getGetOffersData().getCommand();
                commands.add(cmdGetOffers);
            }

            if (bed.isExePostEvent())
            {
                this.logger.log("  Adding 'PostEvent' to command queue.");
                CommandImpl cmdPostEvent = rd.getPostEventData().getCommand();
                commands.add(cmdPostEvent);
            }

            if (sendEndSession)
            {
                CommandImpl cmd = new CommandImpl();
                cmd.setMethodIdentifier("endSession");
                commands.add(cmd);
            }

            try
            {
                this.logger.log("  Beginning batch execute...");
                response = api.executeBatch(rd.getSessionId(), commands.toArray(new Command[commands.size()]));
                this.logger.log("  ... done.");
                this.processBatchResponse(response);
            }
            catch (RemoteException rex)
            {
                this.logger.log(Level.SEVERE, "ERROR> Batch execute critical error.");
                this.logger.log(Level.SEVERE, rex.getMessage());
            }
        }
        else
        {
            this.logger.log("No command selected. Abort.");
        }

        return response;

    }

    public Response getProfile(RunData rd)
    {
        Response resp = null;
        String sessionId = rd.getSessionId();
        InteractAPI api = this.initializeAPI(rd);

        this.logger.log("-----------------------");
        this.logger.log("  RUNNING GET PROFILE    ");
        this.logger.log("-----------------------");
        this.logger.log("> Parameters given: ");
        this.logger.log("> SessionID = " + sessionId);

        try
        {
            resp = api.getProfile(sessionId);
            this.processResponse("getProfile", resp);
        }
        catch (RemoteException e)
        {
            this.logger.log(Level.SEVERE, e.getMessage());
        }

        return resp;
    }

    public Response runPostEvent(RunData rd)
    {
        Response resp = null;
        PostEventData ped = rd.getPostEventData();
        InteractAPI api = this.initializeAPI(rd);

        this.logger.log("-----------------------");
        this.logger.log("  RUNNING POST EVENT    ");
        this.logger.log("-----------------------");
        this.logger.log("> Parameters given: ");
        this.logger.log("> Event Name = " + ped.getEventName());
        for (NameValuePair prm : ped.getPostEventParams())
        {
            NameValuePairDecor nvp = new NameValuePairDecor(prm);
            this.logger.log("> Parameter " + nvp.toString());
        }

        try
        {
            resp = api.postEvent(rd.getSessionId(),
                    ped.getEventName(),
                    ped.getPostEventParams());
            this.processResponse("postEvent", resp);
        }
        catch (RemoteException e)
        {
            this.logger.log(Level.SEVERE, e.getMessage());
        }

        return resp;
    }

    public Response endSession(RunData rd)
    {
        Response resp = null;

        try
        {
            InteractAPI api = this.initializeAPI(rd);

            this.logger.log("-----------------------");
            this.logger.log("     END SESSION       ");
            this.logger.log("-----------------------");

            this.logger.log("Ending session " + rd.getSessionId() + "...");
            resp = api.endSession(rd.getSessionId());
            this.logger.log("Session ended.");

            this.processResponse("endSession", resp);
        }
        catch (RemoteException e)
        {
            this.logger.log(Level.SEVERE, e.getMessage());
        }

        return resp;

    }

    public Response runGetOffers(RunData rd)
    {
        Response resp = null;
        try
        {
            InteractAPI api = this.initializeAPI(rd);
            GetOffersData gofd = rd.getGetOffersData();

            this.logger.log("-----------------------");
            this.logger.log("  RUNNING GET_OFFERS    ");
            this.logger.log("-----------------------");

            resp = api.getOffers(rd.getSessionId(),
                    gofd.getInteractionPoint(),
                    gofd.getNumberOfOffers());

            this.processResponse("getOffers", resp);

            for (OfferList of : resp.getAllOfferLists())
            {
                int j = 0;
                Offer[] offers = of.getRecommendedOffers();
                if (offers != null)
                {
                    for (Offer offer : offers)
                    {
                        j++;
                        String offerNum = "OFFER#" + j;
                        this.logger.log("");
                        this.logger.log("*** " + offerNum + ": " + offer.getOfferName());
                        String treatmentCode = offer.getTreatmentCode();
                        String[] offerCodes = offer.getOfferCode();
                        for (String offerCode : offerCodes)
                        {
                            this.logger.log("   OFFER CODE = " + offerCode);
                        }
                        NameValuePair[] offerValues = offer.getAdditionalAttributes();
                        if (treatmentCode != null)
                        {
                            this.logger.log("   TREATMENT CODE " + treatmentCode);
                            if (offerValues != null)
                            {
                                for (NameValuePair nvp : offerValues)
                                {
                                    String nvpKey = nvp.getName();
                                    String nvpValue = nvp.getValueAsString();

                                    this.logger.log("   " + nvpKey + " = " + nvpValue);
                                }
                            }

                        }

                    }
                }
                else
                {
                    this.logger.log("No offers found.");
                }
            }
        }
        catch (RemoteException e)
        {
            this.logger.log(Level.SEVERE, e.getMessage());
        }

        return resp;

    }

    public Response runStartSession(RunData runData, boolean debug)
    {
        StartSessionData ssd = runData.getStartSessionData();

        InteractAPI api = this.initializeAPI(runData);
        Response resp = null;

        this.logger.log(ssd.toString());

        if (api != null)
        {
            try
            {
                this.logger.log("-----------------------");
                this.logger.log("   START SESSION       ");
                this.logger.log("-----------------------");
                resp = api.startSession(runData.getSessionId(),
                        ssd.isRelyOnExistingSession(), debug,
                        ssd.getInteractiveChannel(),
                        ssd.getAudienceIds(),
                        ssd.getAudienceLevel(),
                        ssd.getParameters());

                this.logger.log("Response > Server version " + resp.getApiVersion());
                this.logger.log("Response > Status code = " + resp.getStatusCode());
                this.processResponse("startSession", resp);

            }
            catch (RemoteException e)
            {
                this.logger.log(Level.SEVERE, e.getMessage());
            }

        }

        return resp;
    }

    @SuppressWarnings("boxing")
    private void startSession(InteractAPI api, RunData runData)
    {

        Response resp;
        StartSessionData ssd = runData.getStartSessionData();

        try
        {
            this.logger.log("-----------------------");
            this.logger.log("   STARTING SESSION ");
            this.logger.log("-----------------------");
            resp = api.startSession(runData.getSessionId(), false, true,
                    ssd.getInteractiveChannel(),
                    ssd.getAudienceIds(),
                    ssd.getAudienceLevel(),
                    ssd.getParameters());

            this.logger.log("Response > Server version" + resp.getApiVersion());
            this.logger.log("Response > Status code = " + resp.getStatusCode());
            this.processResponse("startSession", resp);

        }
        catch (RemoteException e)
        {
            this.logger.log(Level.SEVERE, e.getMessage());
        }
    }

    void processResponse(String apiname, Response response)
    {
        // check if response is successful or not
        if (response.getStatusCode() == Response.STATUS_SUCCESS)
        {
            this.logger.log(apiname + " call OK: no warnings or errors");
            this.logger.log(apiname + " SESSION ID = " + response.getSessionID());
        }
        else if (response.getStatusCode() == Response.STATUS_WARNING)
        {
            this.logger.log(Level.WARNING, apiname + " call processed with a warning");
        }
        else
        {
            this.logger.log(Level.SEVERE, apiname + " call processed with an error");
        }
        // For any non-successes, there should be advisory messages explaining why
        if (response.getStatusCode() != Response.STATUS_SUCCESS)
        {
            for (AdvisoryMessage am : response.getAdvisoryMessages())
            {
                this.logger.log(Level.SEVERE, am.getMessage());
                this.logger.log(Level.SEVERE, am.getDetailMessage());
            }
        }
    }

    public boolean testConnection(RunData runData)
    {
        InteractAPI api;
        boolean connectOk = false;
        String url = runData.getInteractURL();

        this.logger.log("Testing connection...");
        this.logger.log("Connecting to " + url);

        try
        {
            api = InteractAPI.getInstance(url);
            Response response;
            response = api.getVersion();
            if (response.getStatusCode() == Response.STATUS_SUCCESS)
            {
                this.logger.log("OK. Connection successful.");
                String apiVersion = response.getApiVersion();
                this.logger.log("API Version " + apiVersion);
                connectOk = true;
            }
            else
            {
                this.processResponse("getVersion", response);
            }
        }
        catch (MalformedURLException e)
        {
            this.logger.log("KO. Can't connect with server: malformed URL.");
            this.logger.log(Level.FINEST, e.getMessage());
        }
        catch (RemoteException e)
        {
            this.logger.log("KO. Can't connect with server: remote exception.");
            this.logger.log(Level.FINEST, e.getMessage());
        }
        catch (Exception e)
        {
            this.logger.log("KO. Can't connect with server.");
            this.logger.log(Level.FINEST, e.getMessage());
        }

        return connectOk;

    }

    private void processBatchResponse(BatchResponse batchResponse)
    {
        /** Process the response appropriately */
        // Top level status code is a short cut to determine if there
        // are any non-successes in the array of Response objects
        if (batchResponse.getBatchStatusCode() == Response.STATUS_SUCCESS)
        {
            this.logger.log(Level.INFO, "ExecuteBatch ran OK with 0 errors and 0 warnings.");
        }
        else if (batchResponse.getBatchStatusCode() == Response.STATUS_WARNING)
        {
            this.logger.log(Level.WARNING, "ExecuteBatch call processed with at least one warning");
        }
        else
        {
            this.logger.log(Level.SEVERE, "ExecuteBatch call processed with at least one error");
        }
        // Iterate through the array, and print out the message for any non-successes
        for (Response response : batchResponse.getResponses())
        {
            if (response.getStatusCode() != Response.STATUS_SUCCESS)
            {
                for (AdvisoryMessage am : response.getAdvisoryMessages())
                {
                    this.logger.log(Level.SEVERE, am.getMessage());
                    this.logger.log(Level.SEVERE, am.getDetailMessage());
                }
            }
        }
    }

    private InteractAPI initializeAPI(RunData runData)
    {
        InteractAPI api = null;
        String url = runData.getInteractURL();

        try
        {
            if (Utils.isNotNullNotEmptyNotWhiteSpace(url))
            {
                api = InteractAPI.getInstance(url);
                Response response;
                response = api.getVersion();

                if (response.getStatusCode() == Response.STATUS_SUCCESS)
                {
                    this.logger.log("API object initialized");
                }
                else
                {
                    this.processResponse("getVersion", response);
                }
            }
            else
            {
                this.logger.log(Level.SEVERE, "URL is null or empty.");
            }

        }
        catch (MalformedURLException | RemoteException e)
        {
            this.logger.log(Level.SEVERE, e.getMessage());
        }

        return api;

    }

}
