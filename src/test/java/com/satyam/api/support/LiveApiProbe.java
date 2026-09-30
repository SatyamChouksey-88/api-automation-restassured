package com.satyam.api.support;

import static io.restassured.RestAssured.given;

import com.satyam.api.config.ApiConfig;
import io.restassured.response.Response;
import org.testng.SkipException;

/** Preflight live ReqRes before the suite runs (skipped in {@code -Dmode=mock}). */
public final class LiveApiProbe {

  private LiveApiProbe() {}

  public static void ensureLiveApiReachableOrSkip() {
    if (ApiConfig.isMockMode()) {
      return;
    }
    if (ApiConfig.reqresApiKey().isPresent()) {
      return;
    }

    Response response =
        given()
            .baseUri(ApiConfig.getBaseUrl())
            .basePath(ApiConfig.API_PREFIX)
            .accept("application/json")
            .when()
            .get("/users/2")
            .andReturn();

    int status = response.getStatusCode();
    if (status >= 200 && status < 300) {
      return;
    }

    throw new SkipException(
        "Live ReqRes preflight failed (HTTP "
            + status
            + "). Set REQRES_API_KEY (GitHub secret or env), or run offline: mvn test -Dmode=mock");
  }
}
