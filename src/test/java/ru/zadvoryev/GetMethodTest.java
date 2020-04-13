package ru.zadvoryev;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.*;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
/*
 * 2) Implement following scenario for this endpoint using java:
 * Verify that user can search only member of his own company.
 */
public class GetMethodTest {

    @Rule
    WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(8090);
        wireMockServer.start();
        setupStub();
    }

    @AfterEach
    public void stop() {
        wireMockServer.stop();
    }

    public void setupStub() {
        configureFor("localhost", 8090);
        stubFor(get(urlEqualTo("/company/100/users"))
                .withBasicAuth("jack@gmail.com", "12345")
                .willReturn(aResponse().withStatus(200)
                        .withBodyFile("get-all-users-in-company-response.json")));
        stubFor(get(urlPathMatching("/company/100/users"))
                .withQueryParam("name", matching("([a-zA-Z]+)"))
                .withBasicAuth("jack@gmail.com", "12345")
                .willReturn(aResponse().withStatus(200)
                        .withBodyFile("get-user-by-name-response.json")));
        stubFor(get(urlEqualTo("/company/100/users"))
                .withBasicAuth("vasya@mail.ru", "123456")
                .willReturn(aResponse().withStatus(403)
                        .withBodyFile("get-error-403-response.json")));
        stubFor(get(urlPathMatching("/company/100/users"))
                .withQueryParam("name", matching("([a-zA-Z]+)"))
                .withBasicAuth("vasya@mail.ru", "123456")
                .willReturn(aResponse().withStatus(403)
                        .withBodyFile("get-error-403-response.json")));
    }

    @Test
    @DisplayName("Test will receive users data if that user search member of his own company")
    public void getAllUsersDataByNameHisOwnCompany() {

        final String encodedString = "amFja0BnbWFpbC5jb206MTIzNDU=";
        final int expectedSize = 3;
        final int expectedStatusCode = 200;

        Response response = given()
                .header("Authorization", "Basic " + encodedString)
                .when()
                .get("http://localhost:8090/company/100/users");

        int actualStatusCode = response.statusCode();
        List<Object> users = response.jsonPath().getList("users");

        Assert.assertEquals(expectedStatusCode, actualStatusCode);
        Assert.assertEquals(expectedSize, users.size());
    }


    @Test
    @DisplayName("Test will receive user data if that user search member of his own company")
    public void getUserDataByNameHisOwnCompany() {

        final String encodedString = "amFja0BnbWFpbC5jb206MTIzNDU=";
        final String expectedName = "Bob";
        final int expectedStatusCode = 200;

        Response response = given()
                .header("Authorization", "Basic " + encodedString)
                .when()
                .get("http://localhost:8090/company/100/users?name=Bob");

        int actualStatusCode = response.statusCode();
        String actualName = response.jsonPath().get("users.name[0]");

        Assert.assertEquals(expectedStatusCode, actualStatusCode);
        Assert.assertEquals(expectedName, actualName);
    }

    @Test
    @DisplayName("Test will not receive users data if that user search member of not his own company")
    public void getAllUsersDataByNameNotHisOwnCompany() {

        final String encodedString = "dmFzeWFAbWFpbC5ydToxMjM0NTY=";
        final String expectedMessage = "Access denied for user";
        final int expectedStatusCode = 403;

        Response response = given()
                .header("Authorization", "Basic " + encodedString)
                .when()
                .get("http://localhost:8090/company/100/users");

        int actualStatusCode = response.statusCode();
        String actualMessage = response.jsonPath().get("message");

        Assert.assertEquals(expectedStatusCode, actualStatusCode);
        Assert.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Test will not receive user data if that user search member of not his own company")
    public void getUserDataByNameNotHisOwnCompany() {

        final String encodedString = "dmFzeWFAbWFpbC5ydToxMjM0NTY=";
        final String expectedMessage = "Access denied for user";
        final int expectedStatusCode = 403;

        Response response = given()
                .header("Authorization", "Basic " + encodedString)
                .when()
                .get("http://localhost:8090/company/100/users?name=Bob");

        int actualStatusCode = response.statusCode();
        String actualMessage = response.jsonPath().get("message");

        Assert.assertEquals(expectedStatusCode, actualStatusCode);
        Assert.assertEquals(expectedMessage, actualMessage);
    }
}
