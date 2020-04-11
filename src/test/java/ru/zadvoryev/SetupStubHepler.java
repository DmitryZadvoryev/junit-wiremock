package ru.zadvoryev;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static ru.zadvoryev.Constants.*;

public class SetupStubHepler {

    public static WireMockServer setSetup(WireMockServer wireMockServer){
        stubFor(get(urlPathMatching("/company/100/users"))
                .withQueryParam("name", matching("[a-zA-Z]+"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(OK)
                        .withBodyFile("get-user-by-name-response.json")));
        stubFor(get(urlPathMatching("/company/100/users"))
                .withQueryParam("name", equalTo(""))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(OK)
                        .withBodyFile("get-all-users-in-company-response.json")));
        stubFor(get(urlEqualTo("/company/100/users?name=Tommy"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(OK)
                        .withBodyFile("get-error-message-if-name-does-not-exist-response.json")));
        stubFor(get(urlEqualTo("/company/100/users"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(OK)
                        .withBodyFile("get-all-users-in-company-response.json")));
        stubFor(get(urlEqualTo("/company/111/users"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(OK)
                        .withBodyFile("get-there-are-not-users-in-company-response.json")));

        stubFor(get(urlEqualTo("/company/333/users"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(NOT_FOUND)
                        .withBodyFile("get-error-message-if-company-id-does-not-exist-response.json")));
        stubFor(get(urlEqualTo("/company/100"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(OK)
                        .withBodyFile("get-company-data-by-id-response.json")));

        return wireMockServer;
    }
}
