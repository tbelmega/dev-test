package de.tbelmega.cityprocessor;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import org.apache.log4j.*;

public class HttpConnector {

    private static final Logger LOG = Logger.getLogger(HttpConnector.class);
    public static final String URI = "http://api.goeuro.com/api/v2/position/suggest/en/";

    /**
     * Send a request to the server to GET the data for the specified city.
     * @param cityName
     * @return
     * @throws IOException
     */
    public String getResponseEntity(String cityName) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(URI + cityName);

            LOG.info("Executing request " + httpGet.getRequestLine());

            ResponseHandler<String> responseHandler = new CustomResponseHandler();
            return httpClient.execute(httpGet, responseHandler);
        } finally {
            httpClient.close();
        }
    }



    private static class CustomResponseHandler implements ResponseHandler<String> {

        /**
         * Extract the payload from the HttpResponse.
         * @param response
         * @return
         * @throws IOException
         */
        public String handleResponse(HttpResponse response) throws IOException {
            LOG.info("Response: " + response.getStatusLine());
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();

            validateResponse(status, entity);
            String result = EntityUtils.toString(entity);

            LOG.info(result);
            return result;
        }

        /**
         * Response should have status code 200, a not-null entity and the MIME-Typ application/json.
         * @param status
         * @param entity
         * @throws ClientProtocolException
         */
        private void validateResponse(int status, HttpEntity entity) throws ClientProtocolException {
            if (! isSuccessStatus(status)) {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
            if (entity == null) {
                throw new ClientProtocolException("Entity is null.");
            }

            String mimeType = entity.getContentType().getValue();
            if (!mimeType.contains(ContentType.APPLICATION_JSON.getMimeType())){
                throw new ClientProtocolException("Unexpected mime type: " + mimeType);
            }
        }

        private boolean isSuccessStatus(int status) {
            return status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES;
        }

    }
}
