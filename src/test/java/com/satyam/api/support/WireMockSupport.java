package com.satyam.api.support;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.satyam.api.config.ApiConfig;

/** Starts WireMock with ReqRes-shaped stubs when {@code -Dmode=mock}. */
public final class WireMockSupport {

  private static WireMockServer server;

  private WireMockSupport() {}

  public static void startIfMockMode() {
    if (!ApiConfig.isMockMode() || server != null) {
      return;
    }
    server =
        new WireMockServer(
            WireMockConfiguration.wireMockConfig().dynamicPort().globalTemplating(true));
    server.start();
    System.setProperty("baseUrl", "http://localhost:" + server.port());
    registerStubs();
  }

  public static void stopIfStarted() {
    if (server != null) {
      server.stop();
      server = null;
    }
  }

  private static void registerStubs() {
    String user2 =
        """
        {"data":{"id":2,"email":"janet.weaver@reqres.in","first_name":"Janet","last_name":"Weaver","avatar":"https://reqres.in/img/faces/2-image.jpg"},"support":{"url":"https://reqres.in","text":"mock"}}
        """;
    String listPage1 =
        """
        {"page":1,"per_page":6,"total":12,"total_pages":2,"data":[{"id":1,"email":"george.bluth@reqres.in","first_name":"George","last_name":"Bluth","avatar":"https://reqres.in/img/faces/1-image.jpg"},{"id":2,"email":"janet.weaver@reqres.in","first_name":"Janet","last_name":"Weaver","avatar":"https://reqres.in/img/faces/2-image.jpg"}],"support":{"url":"https://reqres.in","text":"mock"}}
        """;
    String listPage2 =
        """
        {"page":2,"per_page":6,"total":12,"total_pages":2,"data":[{"id":7,"email":"michael.lawson@reqres.in","first_name":"Michael","last_name":"Lawson","avatar":"https://reqres.in/img/faces/7-image.jpg"}],"support":{"url":"https://reqres.in","text":"mock"}}
        """;
    String emptyPage =
        """
        {"page":999,"per_page":6,"total":12,"total_pages":2,"data":[],"support":{"url":"https://reqres.in","text":"mock"}}
        """;

    server.stubFor(
        get(urlPathEqualTo("/api/users/2"))
            .willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(user2)));
    server.stubFor(
        get(urlPathEqualTo("/api/users/23"))
            .willReturn(aResponse().withStatus(404).withHeader("Content-Type", "application/json")));
    server.stubFor(
        get(urlPathMatching("/api/users"))
            .withQueryParam("page", equalTo("1"))
            .willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(listPage1)));
    server.stubFor(
        get(urlPathMatching("/api/users"))
            .withQueryParam("page", equalTo("2"))
            .willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(listPage2)));
    server.stubFor(
        get(urlPathMatching("/api/users"))
            .withQueryParam("page", equalTo("999"))
            .willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(emptyPage)));
    server.stubFor(
        post(urlPathEqualTo("/api/users"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"name\":\"{{jsonPath request.body '$.name'}}\",\"job\":\"{{jsonPath request.body '$.job'}}\",\"id\":\"mock-id\",\"createdAt\":\"2026-01-01T00:00:00.000Z\"}")
                    .withTransformers("response-template")));
    server.stubFor(
        post(urlPathEqualTo("/api/register"))
            .withRequestBody(equalToJson("{\"email\":\"sydney@fife\"}", true, true))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"error\":\"Missing password\"}")));
    server.stubFor(
        post(urlPathEqualTo("/api/register"))
            .withRequestBody(equalToJson("{\"password\":\"onlypassword\"}", true, true))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"error\":\"Missing email\"}")));
    server.stubFor(
        post(urlPathEqualTo("/api/login"))
            .withRequestBody(equalToJson("{\"email\":\"peter@klaven\"}", true, true))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"error\":\"Missing password\"}")));
    server.stubFor(
        post(urlPathEqualTo("/api/login"))
            .withRequestBody(
                equalToJson(
                    "{\"email\":\"unknown-user@reqres.invalid\",\"password\":\"wrong-password\"}", true, true))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"error\":\"User not found\"}")));
    server.stubFor(
        put(urlPathMatching("/api/users/.*"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"name\":\"Morpheus\",\"job\":\"Zion Resident\",\"updatedAt\":\"2026-01-01T00:00:00.000Z\"}")));
    server.stubFor(
        patch(urlPathMatching("/api/users/.*"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"job\":\"Team Lead\",\"updatedAt\":\"2026-01-01T00:00:00.000Z\"}")));
    server.stubFor(delete(urlPathMatching("/api/users/.*")).willReturn(aResponse().withStatus(204)));
    server.stubFor(
        post(urlPathEqualTo("/api/register"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":1,\"token\":\"mock-token\"}")));
    server.stubFor(
        post(urlPathEqualTo("/api/login"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"token\":\"mock-token\"}")));
  }
}
