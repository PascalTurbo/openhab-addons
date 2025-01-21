package org.openhab.binding.ochsnerweb2com.internal;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.openhab.binding.ochsnerweb2com.internal.model.Body;
import org.openhab.binding.ochsnerweb2com.internal.model.DataPointRequest;
import org.openhab.binding.ochsnerweb2com.internal.model.Envelope;
import org.openhab.binding.ochsnerweb2com.internal.model.Reference;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OchsnerWeb2ComConnection {

    private final static Logger logger = LoggerFactory.getLogger(OchsnerWeb2ComHandler.class);

    private final OchsnerWeb2ComHandler handler;

    private OchsnerWeb2ComConfiguration configuration;

    public OchsnerWeb2ComConnection(OchsnerWeb2ComHandler handler) {
        this.configuration = handler.getConfiguration();
        this.handler = handler;
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

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(configuration.username, configuration.password));

        String url = "http://" + configuration.hostname + "/ws";

        String request = buildRequestBody();

        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider)
                .build()) {

            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet(url);

            HttpPost httpPost = new HttpPost(url);
            // TODO: Ugly hack! replace ns3 should not be the solution
            httpPost.setEntity(new StringEntity(request.replace("ns3:", ""), ContentType.APPLICATION_XML));
            httpPost.setHeader("Content-Type", "text/xml; charset=utf-8");
            httpPost.setHeader("SOAPAction", "http://ws01.lom.ch/soap/getDP");

            CloseableHttpResponse response = httpClient.execute(httpPost, localContext);

            if (response.getStatusLine().getStatusCode() == 401) {
                logger.warn("Error while testing connection: Authentication Error.");
                logger.debug(response.getStatusLine().toString());
                handler.setStatusInfo(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Invalid username or password");
            }
        } catch (Exception e) {
            logger.warn("Error while testing connection: {e.getMessage()}");
            logger.debug(e.getMessage());
            handler.setStatusInfo(ThingStatus.UNKNOWN, ThingStatusDetail.NONE, "Unknown error occured");
        }
    }
}
