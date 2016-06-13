package de.tbelmega.cityprocessor;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

public class HttpConnectorTest {

    public static final String TEST_URI = "http://api.goeuro.com/api/v2/position/suggest/en/Berlin";


    /**
     * Test that server responds to sample request with status code 200 and a JSON-type entity.
     * @throws Exception
     */
    @Test
    public void testThat_serverRespondsAsExpected() throws Exception {
        //arrange
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(TEST_URI);
        HttpResponse response;

        //act
        try {
            response = httpClient.execute(httpGet);
        } finally {
            httpClient.close();
        }

        //assert
        int status = response.getStatusLine().getStatusCode();
        String mimeType = response.getEntity().getContentType().getValue();

        assertEquals("Unexpected status code: " + status, HttpStatus.SC_OK, status);
        assertTrue("Unexpected mime type: " + mimeType, mimeType.contains(ContentType.APPLICATION_JSON.getMimeType()));
    }

    /**
     * Test that sample request with HttpConnector class is successful (does not throw an exception).
     * @throws Exception
     */
    @Test
    public void testThat_httpConnectorGetsResponseString() throws Exception {
        //arrange
        HttpConnector connector = new HttpConnector();

        //act
        String result = connector.getResponseEntity("Berlin");

        //assert
        assertNotNull("Response string should not be null", result);
    }
}
