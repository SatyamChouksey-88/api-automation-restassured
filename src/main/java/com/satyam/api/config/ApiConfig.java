package com.satyam.api.config;

import java.util.Optional;

/** Central config for ReqRes (live) or WireMock (offline {@code -Dmode=mock}). */
public final class ApiConfig {

  private static final String DEFAULT_LIVE_BASE = "https://reqres.in";

  public static final String API_PREFIX = "/api";

  private ApiConfig() {}

  public static boolean isMockMode() {
    return "mock".equalsIgnoreCase(System.getProperty("mode", "live"));
  }

  public static String getBaseUrl() {
    String override = System.getProperty("baseUrl");
    if (override != null && !override.isBlank()) {
      return override.trim();
    }
    String fromEnv = System.getenv("BASE_URL");
    if (fromEnv != null && !fromEnv.isBlank()) {
      return fromEnv.trim();
    }
    return DEFAULT_LIVE_BASE;
  }

  /** Optional ReqRes {@code x-api-key} (env {@code REQRES_API_KEY} or {@code -DreqresApiKey=...}). */
  public static Optional<String> reqresApiKey() {
    String key = System.getenv("REQRES_API_KEY");
    if (key == null || key.isBlank()) {
      key = System.getProperty("reqresApiKey");
    }
    return Optional.ofNullable(key).map(String::trim).filter(s -> !s.isEmpty());
  }
}
