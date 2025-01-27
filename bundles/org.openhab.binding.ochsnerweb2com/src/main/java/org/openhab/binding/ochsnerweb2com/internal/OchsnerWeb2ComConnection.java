package org.openhab.binding.ochsnerweb2com.internal;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.DigestAuthentication;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.openhab.binding.ochsnerweb2com.internal.model.Body;
import org.openhab.binding.ochsnerweb2com.internal.model.DataPointRequest;
import org.openhab.binding.ochsnerweb2com.internal.model.Envelope;
import org.openhab.binding.ochsnerweb2com.internal.model.Reference;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OchsnerWeb2ComConnection {

    private final static Logger logger = LoggerFactory.getLogger(OchsnerWeb2ComBridgeHandler.class);

    private final OchsnerWeb2ComBridgeHandler handler;

    private final HttpClient httpClient;

    private final OchsnerWeb2ComConfiguration configuration;

    public OchsnerWeb2ComConnection(OchsnerWeb2ComBridgeHandler handler, HttpClient httpClient) {
        this.configuration = handler.getConfiguration();
        this.handler = handler;
        this.httpClient = httpClient;
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

    // Test the connection with the configured connection parameters
    // Returns true, if the connection is successful,
    // else returns false
    public void testConnection() {

        // There is no known local tls encrypted api endpoint
        // ToDo: Support configuration of port and protocol as username.ochsner-web.com:444 is also a valid endpoint
        String url = "http://" + configuration.hostname + "/ws";

        String request = buildRequestBody("/1", 0, -1);

        AuthenticationStore authenticationStore = httpClient.getAuthenticationStore();

        try {
            URI uri = new URI(url);
            authenticationStore.addAuthentication(
                    new DigestAuthentication(uri, "RC7000", configuration.username, configuration.password));

            ContentResponse response = httpClient.POST(url).header(HttpHeader.CONTENT_TYPE, "text/xml; charset=utf-8")
                    .header("SOAPAction", "http://ws01.lom.ch/soap/getDP").content(new StringContentProvider(request))
                    .send();

            if (response.getStatus() == 401) {
                logger.warn("Error while testing connection: Authentication Error.");
                handler.setStatusInfo(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Authentication failed. Check username and password.");
            }

            if (response.getStatus() == 200) {
                logger.debug("Successfully established connection.");
                handler.setStatusInfo(ThingStatus.ONLINE, ThingStatusDetail.NONE,
                        "Successfully established connection.");
            }

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error(e.getMessage());
            logger.debug("Exception while connecting to endpoint '" + url + "'", e);
        } catch (URISyntaxException e) {
            logger.error("Error while creating URI: {}", e.getMessage());
        }
    }
}
