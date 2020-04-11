import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class SetupStubHepler {

    public static WireMockServer setSetup(WireMockServer wireMockServer){

        return wireMockServer;
    }
}
