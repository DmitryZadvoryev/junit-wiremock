import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static helper.ConvertToString.convertInputStreamToString;
import static org.junit.Assert.assertEquals;

public class GetMethodTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    public CloseableHttpClient httpClient = HttpClients.createDefault();

    @Before
    public void initTestData() {
        wireMockRule.start();
        configureFor("localhost", 8080);
        stubFor(get(urlEqualTo("/company/777/users?name=lzergin"))
                .willReturn(aResponse().withStatus(200).withBody("lzergin")));
    }


    @Test
    public void ExampleTest() throws IOException {
        HttpGet request = new HttpGet("http://localhost:8080/company/777/users?name=lzergin");
        HttpResponse httpResponse = httpClient.execute(request);
        InputStream content = httpResponse.getEntity().getContent();
        String result = convertInputStreamToString(content);
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
        assertEquals("lzergin", result);

    }

    @After
    public void stopServer() {
        wireMockRule.stop();
    }

}
