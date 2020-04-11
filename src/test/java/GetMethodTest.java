import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static io.restassured.RestAssured.given;


public class GetMethodTest {

    @Rule
    WireMockServer wireMockServer;

    final int OK = HttpStatus.SC_OK;
    final int NOT_FOUND = HttpStatus.SC_NOT_FOUND;
    final int FORBIDDEN = HttpStatus.SC_FORBIDDEN;

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

