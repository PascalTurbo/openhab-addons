package org.openhab.binding.ochsnerweb2com.internal;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.measure.Unit;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.DigestAuthentication;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.openhab.binding.ochsnerweb2com.internal.model.Body;
import org.openhab.binding.ochsnerweb2com.internal.model.DataPointConfiguration;
import org.openhab.binding.ochsnerweb2com.internal.model.DataPointRequest;
import org.openhab.binding.ochsnerweb2com.internal.model.DataPointResponse;
import org.openhab.binding.ochsnerweb2com.internal.model.Envelope;
import org.openhab.binding.ochsnerweb2com.internal.model.Reference;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OchsnerWeb2ComConnection {

    private final static Logger logger = LoggerFactory.getLogger(OchsnerWeb2ComBridgeHandler.class);

    private final OchsnerWeb2ComBridgeHandler bridgeHandler;

    private final HttpClient httpClient;

    private final OchsnerWeb2ComConfiguration configuration;

    private AuthenticationStore authenticationStore;

    public OchsnerWeb2ComConnection(OchsnerWeb2ComBridgeHandler bridgeHandler, HttpClient httpClient) {
        this.configuration = bridgeHandler.getConfiguration();
        this.bridgeHandler = bridgeHandler;
        this.httpClient = httpClient;

        // TODO Remove - only for debugging
        this.authenticationStore = httpClient.getAuthenticationStore();
    }

    private static Envelope buildRequestEnvelope(String oid, Integer startIndex, Integer count) {
        Reference reference = new Reference(oid, null);

        DataPointRequest dataPointRequest = new DataPointRequest(reference, startIndex, count);

        Body requestBody = new Body(dataPointRequest);

        return (new Envelope(requestBody));
    }

    private static String buildRequestBody(String oid, Integer startIndex, Integer count) {
        Envelope envelope = buildRequestEnvelope(oid, startIndex, count);

        String request = "";

        try {
            JAXBContext requestContext = JAXBContext.newInstance(Envelope.class);

            Marshaller requestMarshaller = requestContext.createMarshaller();
            requestMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter stringWriter = new StringWriter();
            requestMarshaller.marshal(envelope, stringWriter);

            request = stringWriter.toString();

            // TODO: Ugly hack! replace ns3 should not be the solution
            request = request.replace("ns3:", "");

            logger.debug("Request XML: {request}");

        } catch (JAXBException e) {
            logger.warn("Error while creating request body xml: {}", e.getMessage());
            logger.debug("Creating request body xml failed with JAXBException", e);
        }

        return request;
    }

    // TODO RE-Authenticate of configuration changes
    private void setAuthentication(URI uri) {
        String realm = "RC7000";

        // Authentication authentication = authenticationStore.findAuthentication("Digest", uri, realm);

        // while (authentication != null) {
        // authenticationStore.removeAuthentication(authentication);
        // logger.info("Authentication removed.");

        // authentication = authenticationStore.findAuthentication("Digest", uri, realm);
        // }

        // AuthenticationStore authenticationStore = httpClient.getAuthenticationStore();
        authenticationStore.addAuthentication(
                new DigestAuthentication(uri, realm, configuration.username, configuration.password));
    }

    // Test the connection with the configured connection parameters
    // Returns true, if the connection is successful,
    // else returns false
    public void testBridgeConnection(String oid) {

        // There is no known local tls encrypted api endpoint
        // ToDo: Support configuration of port and protocol as username.ochsner-web.com:444 is also a valid endpoint
        String url = "http://" + configuration.hostname + "/ws";

        String request = buildRequestBody(oid, 0, -1);

        try {
            setAuthentication(new URI(url));
        } catch (URISyntaxException e) {
            logger.error("Error while creating URI: {}", e.getMessage());
        }

        try {
            ContentResponse response = httpClient.POST(url).header(HttpHeader.CONTENT_TYPE, "text/xml; charset=utf-8")
                    .header("SOAPAction", "http://ws01.lom.ch/soap/getDP").content(new StringContentProvider(request))
                    .send();

            if (response.getStatus() == 401) {
                logger.warn("Error while testing connection: Authentication Error.");
                bridgeHandler.setStatusInfo(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Authentication failed. Check username and password.");
            }

            if (response.getStatus() == 200) {
                logger.debug("Successfully established connection.");
                bridgeHandler.setStatusInfo(ThingStatus.ONLINE, ThingStatusDetail.NONE,
                        "Successfully established connection.");
            }

        } catch (InterruptedException | ExecutionException | TimeoutException e) {

            logger.debug("Exception while connecting to endpoint '" + url + "'", e);
            logger.error(e.getMessage());

        }
    }

    // TODO Both methods are nearely equal. Define Interface iHandler which defines setStatusInfo() and use one method
    // for both
    public void testConnection(String oid, OchsnerWeb2ComThingHandler thingHandler) {

        // There is no known local tls encrypted api endpoint
        // ToDo: Support configuration of port and protocol as username.ochsner-web.com:444 is also a valid endpoint
        String url = "http://" + configuration.hostname + "/ws";

        String request = buildRequestBody(oid, 0, -1);

        try {
            setAuthentication(new URI(url));
        } catch (URISyntaxException e) {
            logger.error("Error while creating URI: {}", e.getMessage());
        }

        try {
            ContentResponse response = httpClient.POST(url).header(HttpHeader.CONTENT_TYPE, "text/xml; charset=utf-8")
                    .header("SOAPAction", "http://ws01.lom.ch/soap/getDP").content(new StringContentProvider(request))
                    .send();

            if (response.getStatus() == 401) {
                logger.warn("Error while testing connection: Authentication Error.");
                thingHandler.setStatusInfo(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Authentication failed. Check username and password.");
            }

            if (response.getStatus() == 200) {
                logger.debug("Successfully established connection.");
                thingHandler.setStatusInfo(ThingStatus.ONLINE, ThingStatusDetail.NONE,
                        "Successfully established connection.");
            }

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error(e.getMessage());
            logger.debug("Exception while connecting to endpoint '" + url + "'", e);
        }
    }

    public DataPointResponse getDataPointResponse(String oid) {
        String url = "http://" + configuration.hostname + "/ws";

        String request = buildRequestBody(oid, 0, -1);

        try {
            setAuthentication(new URI(url));
        } catch (URISyntaxException e) {
            logger.error("Error while creating URI: {}", e.getMessage());
        }

        try {
            ContentResponse response = httpClient.POST(url).header(HttpHeader.CONTENT_TYPE, "text/xml; charset=utf-8")
                    .header("SOAPAction", "http://ws01.lom.ch/soap/getDP").content(new StringContentProvider(request))
                    .send();

            if (response.getStatus() == 200) {
                logger.debug("Successfully established connection.");

                JAXBContext jaxbContext;
                try {
                    jaxbContext = JAXBContext.newInstance(Envelope.class);
                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                    Envelope envelope = (Envelope) jaxbUnmarshaller
                            .unmarshal(new StringReader(response.getContentAsString()));

                    System.out.println(envelope);

                    return envelope.getBody().getDataPointResponse();

                } catch (Exception e) {
                    System.out.println("Exception while parsing xml: " + e.getMessage());
                    e.printStackTrace();
                }

            } else {
                logger.error("Error getting data for oid '" + oid + "'");
            }

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error(e.getMessage());
            logger.debug("Exception while connecting to endpoint '" + url + "'", e);
        }

        return null;
    }

    // TODO Think of a better name
    public Map<String, Unit<?>> getChannelIDs(String oid) {
        DataPointResponse dataPointResponse = getDataPointResponse(oid);

        Map<String, Unit<?>> channelIds = new HashMap<>();

        if (dataPointResponse == null)
            return channelIds;

        ArrayList<DataPointConfiguration> dataPointConfigurations = dataPointResponse.getDataPointConfigurations();

        for (DataPointConfiguration dpt : dataPointConfigurations) {
            String nextOid = oid + "/" + dpt.getIndex();

            if (dpt.getType() == 7) {
                channelIds.putAll(getChannelIDs(nextOid));
            } else {
                channelIds.put(nextOid, dpt.getUnit());
            }
        }

        return channelIds;
    }
}
