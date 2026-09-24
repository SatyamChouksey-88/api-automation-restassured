package com.satyam.api.dataproviders;

import com.fasterxml.jackson.databind.JsonNode;
import com.satyam.api.models.UserRequest;
import com.satyam.api.utils.JsonResource;
import org.testng.annotations.DataProvider;

public class UserDataProvider {

  @DataProvider(name = "createUsers")
  public Object[][] createUsers() {
    JsonNode root = JsonResource.readTree("testdata/create-users.json");
    JsonNode users = root.get("users");
    Object[][] rows = new Object[users.size()][1];
    for (int i = 0; i < users.size(); i++) {
      JsonNode u = users.get(i);
      rows[i][0] = new UserRequest(u.get("name").asText(), u.get("job").asText());
    }
    return rows;
  }
}
