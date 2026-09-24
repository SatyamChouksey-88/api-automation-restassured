package com.satyam.api.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

public final class JsonResource {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private JsonResource() {}

  public static JsonNode readTree(String classpathLocation) {
    try (InputStream in = JsonResource.class.getClassLoader().getResourceAsStream(classpathLocation)) {
      if (in == null) {
        throw new IllegalArgumentException("Missing classpath resource: " + classpathLocation);
      }
      return MAPPER.readTree(in);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read " + classpathLocation, e);
    }
  }

  public static ObjectMapper mapper() {
    return MAPPER;
  }
}
