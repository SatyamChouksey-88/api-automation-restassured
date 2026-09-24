package com.satyam.api.tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.satyam.api.clients.UsersClient;
import com.satyam.api.dataproviders.UserDataProvider;
import com.satyam.api.models.UserRequest;
import com.satyam.api.models.UserResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Epic("ReqRes Users API")
@Feature("CRUD + contract")
public class UsersApiTest {

  private UsersClient users;

  @BeforeClass
  public void setUp() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    RestAssured.config =
        RestAssuredConfig.config()
            .objectMapperConfig(
                ObjectMapperConfig.objectMapperConfig()
                    .jackson2ObjectMapperFactory((type, s) -> mapper));
    users = new UsersClient();
  }

  @Test(priority = 1)
  @Description("GET /users/{id} returns a single known user")
  public void getSingleUser_returns200AndExpectedEmail() {
    users
        .getUser(2)
        .then()
        .statusCode(200)
        .body("data.id", equalTo(2))
        .body("data.email", equalTo("janet.weaver@reqres.in"))
        .body("data.first_name", equalTo("Janet"));
  }

  @Test(priority = 2)
  @Description("GET /users/{id} response matches JSON schema")
  public void getSingleUser_matchesJsonSchema() {
    users
        .getUser(2)
        .then()
        .statusCode(200)
        .body(matchesJsonSchemaInClasspath("schemas/single-user-schema.json"));
  }

  @Test(priority = 3)
  @Description("GET /users?page=2 returns a page of users")
  public void listUsers_page2_returnsDataArray() {
    users
        .listUsers(2)
        .then()
        .statusCode(200)
        .body("page", equalTo(2))
        .body("data", not(empty()))
        .body("data.size()", greaterThan(0));
  }

  @Test(priority = 4)
  @Description("GET /users list response matches JSON schema")
  public void listUsers_matchesJsonSchema() {
    users
        .listUsers(1)
        .then()
        .statusCode(200)
        .body(matchesJsonSchemaInClasspath("schemas/user-list-schema.json"));
  }

  @Test(priority = 5, dataProvider = "createUsers", dataProviderClass = UserDataProvider.class)
  @Description("POST /users creates a user (data-driven from JSON)")
  public void createUser_returns201WithId(UserRequest request) {
    Response response = users.createUser(request);
    response.then().statusCode(201).body("id", notNullValue()).body("createdAt", notNullValue());

    UserResponse body = response.as(UserResponse.class);
    Assert.assertEquals(body.getName(), request.getName());
    Assert.assertEquals(body.getJob(), request.getJob());
    Assert.assertNotNull(body.getId());
  }

  @Test(priority = 6)
  @Description("PUT /users/{id} updates name and job")
  public void updateUserPut_returns200() {
    UserRequest request = new UserRequest("Morpheus", "Zion Resident");
    users
        .updateUserPut(2, request)
        .then()
        .statusCode(200)
        .body("name", equalTo("Morpheus"))
        .body("job", equalTo("Zion Resident"))
        .body("updatedAt", notNullValue());
  }

  @Test(priority = 7)
  @Description("PATCH /users/{id} partially updates job")
  public void updateUserPatch_returns200() {
    UserRequest request = new UserRequest(null, "Team Lead");
    users
        .updateUserPatch(2, request)
        .then()
        .statusCode(200)
        .body("job", equalTo("Team Lead"))
        .body("updatedAt", notNullValue());
  }

  @Test(priority = 8)
  @Description("DELETE /users/{id} returns 204")
  public void deleteUser_returns204() {
    users.deleteUser(2).then().statusCode(204);
  }

  @Test(priority = 9)
  @Description("GET missing user returns 404")
  public void getMissingUser_returns404() {
    users.getUser(23).then().statusCode(404);
  }

  @Test(priority = 10)
  @Description("POST /register without password returns 400")
  public void register_missingPassword_returns400() {
    users
        .register(Map.of("email", "sydney@fife"))
        .then()
        .statusCode(400)
        .body("error", equalTo("Missing password"));
  }

  @Test(priority = 11)
  @Description("POST /login without password returns 400")
  public void login_missingPassword_returns400() {
    users
        .login(Map.of("email", "peter@klaven"))
        .then()
        .statusCode(400)
        .body("error", equalTo("Missing password"));
  }

  @Test(priority = 12)
  @Description("POST /register with valid demo credentials returns token")
  public void register_valid_returnsToken() {
    users
        .register(Map.of("email", "eve.holt@reqres.in", "password", "pistol"))
        .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("token", notNullValue());
  }

  @Test(priority = 13)
  @Description("POST /login with valid demo credentials returns token")
  public void login_valid_returnsToken() {
    users
        .login(Map.of("email", "eve.holt@reqres.in", "password", "cityslicka"))
        .then()
        .statusCode(200)
        .body("token", notNullValue());
  }

  @Test(priority = 14)
  @Description("GET /users page query returns consistent per_page")
  public void listUsers_hasExpectedPerPage() {
    users.listUsers(1).then().statusCode(200).body("per_page", equalTo(6));
  }

  @Test(priority = 15)
  @Description("Create then deserialize POJO round-trip")
  public void createUser_pojoRoundTrip() {
    UserRequest request = new UserRequest("Neo", "The One");
    UserResponse created = users.createUser(request).then().statusCode(201).extract().as(UserResponse.class);
    Assert.assertEquals(created.getName(), "Neo");
    Assert.assertEquals(created.getJob(), "The One");
    Assert.assertFalse(created.getId().isBlank());
  }
}
