package ru.zadvoryev;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static ru.zadvoryev.Constants.NOT_FOUND;
import static ru.zadvoryev.Constants.OK;


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
        SetupStubHepler.setSetup(wireMockServer);
    }

    /*
     * Test will get user data if name param is in lower case
     */
    @Test
    @DisplayName("Test №1 will get user data if name param is in lower case")
    public void getUserDataNameParamLowerCase() {
        final String expectedName = "Bob";
        Response response = given()
                .when()
                .contentType("application/json")
                .get("http://localhost:8090/company/100/users?name=bob");
        verify(getRequestedFor(urlPathMatching("/company/100/users"))
                .withQueryParam("name", equalTo("bob")));
        String actualName = response.jsonPath().get("name");
        int statusCode = response.getStatusCode();
        Assert.assertEquals(OK, statusCode);
        Assert.assertEquals(expectedName, actualName);
    }

    /*
     * Test will get user data if name param is in upper case
     */
    @Test
    @DisplayName("Test №2 will get user data if name param is in upper case")
    public void getUserDataNameParamUpperCase() {
        final String expectedName = "Bob";
        Response response = given()
                .when()
                .contentType("application/json")
                .get("http://localhost:8090/company/100/users?name=BOB");
        verify(getRequestedFor(urlPathMatching("/company/100/users"))
                .withQueryParam("name", equalTo("BOB")));
        String actualName = response.jsonPath().get("name");
        int statusCode = response.getStatusCode();
        Assert.assertEquals(OK, statusCode);
        Assert.assertEquals(expectedName, actualName);
    }

    /*
     * Test will get users data if name param is in empty
     */
    @Test
    @DisplayName("Test №3 will get users data if name param is in empty")
    public void getUsersDataIfNameParamEmpty() {
        final int expected = 3;
        Response response = given()
                .when()
                .contentType("application/json")
                .get("http://localhost:8090/company/100/users?name=");
        verify(getRequestedFor(urlPathMatching("/company/100/users"))
                .withQueryParam("name", equalTo("")));
        List<String> actual = response.jsonPath().getList("users");
        int statusCode = response.getStatusCode();
        Assert.assertEquals(OK, statusCode);
        Assert.assertEquals(expected, actual.size());
    }

    /*
     * Test will receive error message if name does not exist
     */
    @Test
    @DisplayName("Test №4 will receive error message if name does not exist")
    public void getErrorMessageNameDoesNotExist() {
        final String expectedMessage = "User not found";
        Response response = given()
                .when()
                .contentType("application/json")
                .get("http://localhost:8090/company/100/users?name=Tommy");
        verify(getRequestedFor(urlEqualTo("/company/100/users?name=Tommy")));
        String actualMessage = response.jsonPath().get("message");
        int statusCode = response.getStatusCode();
        Assert.assertEquals(OK, statusCode);
        Assert.assertEquals(expectedMessage, actualMessage);
    }

    /*
       Test will receive all users in the company
   */
    @Test
    @DisplayName("Test №5 will receive all users in the company")
    public void getAllUsersInCompany() {
        final int expected = 3;
        Response response = given()
                .when()
                .contentType("application/json")
                .get("http://localhost:8090/company/100/users");
        verify(getRequestedFor(urlEqualTo("/company/100/users")));
        List<String> actual = response.jsonPath().getList("users");
        int statusCode = response.getStatusCode();
        Assert.assertEquals(OK, statusCode);
        Assert.assertEquals(expected, actual.size());
    }


    /*
        Test will receive error message if company does not exist
     */
    @Test
    @DisplayName("Test №6 will receive error message if company does not exist")
    public void getErrorMessageIfCompanyIdDoesNotExist() {
        final String expectedMessage = "Company not found";
        Response response = given()
                .when()
                .contentType("application/json")
                .get("http://localhost:8090/company/333/users");
        verify(getRequestedFor(urlEqualTo("/company/333/users")));
        String actualMessage = response.jsonPath().get("message");
        int statusCode = response.getStatusCode();
        Assert.assertEquals(NOT_FOUND, statusCode);
        Assert.assertEquals(expectedMessage, actualMessage);
    }


    /*
        There are not users in the company
     */
    @Test
    @DisplayName("Test №7 There are not users in the company")
    public void getThereAreNotUsersInTheCompany() {
        Response response = given()
                .when()
                .contentType("application/json")
                .get("http://localhost:8090/company/111/users");
        verify(getRequestedFor(urlEqualTo("/company/111/users")));
        List<String> actual = response.jsonPath().getList("users");
        int statusCode = response.getStatusCode();
        Assert.assertEquals(OK, statusCode);
        Assert.assertTrue(actual.isEmpty());
    }

    /*
       Test will receive company data by id
    */
    @Test
    @DisplayName("Test №8 will receive company data by id")
    public void getCompanyDataById() {
        final String expectedName = "OOO Magnit";
        Response response = given()
                .when()
                .contentType("application/json")
                .get("http://localhost:8090/company/100");
        verify(getRequestedFor(urlEqualTo("/company/100")));
        String actual = response.jsonPath().get("name");
        int statusCode = response.getStatusCode();
        Assert.assertEquals(OK, statusCode);
        Assert.assertEquals(expectedName, actual);
    }
}

