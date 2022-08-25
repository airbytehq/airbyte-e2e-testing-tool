package io.airbyte.testingtool.credentials;

import io.airbyte.testingtool.argument_parser.Command;
import io.airbyte.testingtool.json.Jsons;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CredentialsService {

  private static final String LOCAL_SECRET_FOLDER = "secrets/";

  public static Map<String, CredentialConfig> getCredentials(final Command runCommand, final Map<String, String> args) throws IOException {
    return switch (runCommand) {
      case RUN_SCENARIO -> getCredentialsFromSecretService(args);
      case RUN_SCENARIO_LOCAL -> getCredentialsFromLocalSecrets(args);
      default -> null;
    };
  }

  public static Map<String, CredentialConfig> getCredentialsFromSecretService(final Map<String, String> args) {
    throw new RuntimeException("Secret service is not integrated yet!");
  }

  public static Map<String, CredentialConfig> getCredentialsFromLocalSecrets(final Map<String, String> args) throws IOException {
    Map<String, CredentialConfig> credentials = new HashMap<>();
    for (Entry<String, String> nameCredentialsMap : args.entrySet()) {
      credentials.put(nameCredentialsMap.getKey(), readLocalCredential(nameCredentialsMap.getValue()));
    }

    return credentials;
  }

  private static CredentialConfig readLocalCredential(final String fileName) throws IOException {
    var filePath = Path.of(LOCAL_SECRET_FOLDER + fileName);
    if (Files.exists(filePath)) {
      return Jsons.deserialize(Files.readString(filePath), CredentialConfig.class);
    } else {
      throw new RuntimeException("Local credential file (" + LOCAL_SECRET_FOLDER + fileName + ") not found!");
    }
  }
}
