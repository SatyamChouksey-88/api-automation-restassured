package com.satyam.api.config;

/**
 * Central config for the public ReqRes demo API.
 * Override with -DbaseUrl=... or env BASE_URL if needed.
 */
public final class ApiConfig {
  public static final String BASE_URL =
      System.getProperty(
          "baseUrl",
          System.getenv().getOrDefault("BASE_URL", "https://reqres.in"));

  public static final String API_PREFIX = "/api";

  private ApiConfig() {}
}
