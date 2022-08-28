package io.airbyte.testingtool.credentials;

import com.google.cloud.secretmanager.v1.ProjectName;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import io.airbyte.testingtool.json.Jsons;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

public class CredentialsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CredentialsService.class);
  private static final String PROJECT_ID = "projectId";

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

  public static void getSecrets(String secretName) throws IOException {
    var projectId = getProjectIdForSecretManager();
    try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
      ProjectName projectName = ProjectName.of(projectId);
      SecretManagerServiceClient.ListSecretsPagedResponse pagedResponse = client.listSecrets(projectName);
//      var optionalSecret = StreamSupport.stream(pagedResponse.iterateAll().spliterator(), false)
//              .filter(secret -> secretName.equals(secret.getName()))
//              .findFirst();
      var a = StreamSupport.stream(pagedResponse.iterateAll().spliterator(), false).toList();
      LOGGER.error("aaa");
    }
  }

  private static String getProjectIdForSecretManager() throws IOException {
      final String secrets = Files.readString(Path.of("secrets/config.json"));
      var secretsNode = Jsons.deserialize(secrets);
      return secretsNode.get(PROJECT_ID).asText();
  }
}
