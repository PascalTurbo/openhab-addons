package org.openhab.binding.ochsnerweb2com.internal;

import java.io.StringWriter;
import java.net.URI;

import javax.xml.bind.JAXBContext;
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

    private OchsnerWeb2ComConfiguration configuration;

    public OchsnerWeb2ComConnection(OchsnerWeb2ComBridgeHandler handler, HttpClient httpClient) {
        this.configuration = handler.getConfiguration();
        this.handler = handler;
        this.httpClient = httpClient;
    }

    // ToDo: Refactor an make it a generic method
    private static String buildRequestBody() {

        Reference reference = new Reference("/1", null);

        DataPointRequest dataPointRequest = new DataPointRequest(reference, 0, -1);

        Body requestBody = new Body(dataPointRequest);

        Envelope requestEnvelope = new Envelope(requestBody);

        String request = "";

        try {
            JAXBContext requestContext = JAXBContext.newInstance(Envelope.class);

            Marshaller requestMarshaller = requestContext.createMarshaller();
            requestMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter stringWriter = new StringWriter();
            requestMarshaller.marshal(requestEnvelope, stringWriter);

            request = stringWriter.toString();

            // TODO: Ugly hack! replace ns3 should not be the solution
            request = request.replace("ns3:", "");

            logger.debug("Request XML: {request}");

        } catch (Exception e) {
            logger.warn("Error while creating xml: {e.getMessage()}");
            e.printStackTrace();
        }

        return request;
    }

    // Test the connection with the configured connection parameters
    // Returns true, if the connection is successful,
    // else returns false
    public void testConnection() {

        // CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        // credentialsProvider.setCredentials(AuthScope.ANY,
        // new UsernamePasswordCredentials(configuration.username, configuration.password));

        String url = "http://" + configuration.hostname + "/ws";

        String request = buildRequestBody();

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
                        "Invalid username or password");
            }

            if (response.getStatus() == 200) {
                logger.debug("Successfully established connection.");
                handler.setStatusInfo(ThingStatus.ONLINE, ThingStatusDetail.NONE, "");
            }

        } catch (Exception e) {
            logger.debug(e.getMessage());
        }

        // ContentResponse response = httpClient.POST(url)

        // try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider)
        // .build()) {

        // HttpContext localContext = new BasicHttpContext();
        // HttpGet httpGet = new HttpGet(url);

        // HttpPost httpPost = new HttpPost(url);
        // // TODO: Ugly hack! replace ns3 should not be the solution
        // httpPost.setEntity(new StringEntity(request.replace("ns3:", ""), ContentType.APPLICATION_XML));
        // httpPost.setHeader("Content-Type", "text/xml; charset=utf-8");
        // httpPost.setHeader("SOAPAction", "http://ws01.lom.ch/soap/getDP");

        // CloseableHttpResponse response = httpClient.execute(httpPost, localContext);

        // if (response.getStatusLine().getStatusCode() == 401) {
        // logger.warn("Error while testing connection: Authentication Error.");
        // logger.debug(response.getStatusLine().toString());
        // handler.setStatusInfo(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
        // "Invalid username or password");
        // }
        // } catch (Exception e) {
        // logger.warn("Error while testing connection: {e.getMessage()}");
        // logger.debug(e.getMessage());
        // handler.setStatusInfo(ThingStatus.UNKNOWN, ThingStatusDetail.NONE, "Unknown error occured");
        // }
    }
}
