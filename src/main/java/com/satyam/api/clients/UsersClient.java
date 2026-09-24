package com.satyam.api.clients;

import static io.restassured.RestAssured.given;

import com.satyam.api.config.ApiConfig;
import com.satyam.api.models.UserRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Thin REST Assured wrapper around ReqRes /api/users resources.
 * Specs stay free of raw URLs and repeated headers.
 */
public class UsersClient {

  private RequestSpecification baseSpec() {
    return given()
        .baseUri(ApiConfig.BASE_URL)
        .basePath(ApiConfig.API_PREFIX)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON);
  }

  public Response listUsers(int page) {
    return baseSpec().queryParam("page", page).when().get("/users");
  }

  public Response getUser(int id) {
    return baseSpec().when().get("/users/{id}", id);
  }

  public Response createUser(UserRequest body) {
    return baseSpec().body(body).when().post("/users");
  }

  public Response updateUserPut(int id, UserRequest body) {
    return baseSpec().body(body).when().put("/users/{id}", id);
  }

  public Response updateUserPatch(int id, UserRequest body) {
    return baseSpec().body(body).when().patch("/users/{id}", id);
  }

  public Response deleteUser(int id) {
    return baseSpec().when().delete("/users/{id}", id);
  }

  public Response register(Object body) {
    return baseSpec().body(body).when().post("/register");
  }

  public Response login(Object body) {
    return baseSpec().body(body).when().post("/login");
  }
}
