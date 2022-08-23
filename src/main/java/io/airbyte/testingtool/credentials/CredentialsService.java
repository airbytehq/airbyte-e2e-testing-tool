package io.airbyte.testingtool.credentials;

import io.airbyte.testingtool.json.Jsons;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CredentialsService {

  public static Map<String, CredentialConfig> getCreds(final Map<String, String> args) throws IOException {
    final String airbyte = Files.readString(Path.of("secrets/airbyte_creds.json"));
    final String source = Files.readString(Path.of("secrets/source_creds.json"));
    final String dest = Files.readString(Path.of("secrets/destination_creds.json"));

    Map<String, CredentialConfig> credentials = new HashMap<>();
    credentials.put("airbyte_1", Jsons.deserialize(airbyte, CredentialConfig.class));
    credentials.put("source_1", Jsons.deserialize(source, CredentialConfig.class));
    credentials.put("destination_1", Jsons.deserialize(dest, CredentialConfig.class));

    return credentials;
  }
}
