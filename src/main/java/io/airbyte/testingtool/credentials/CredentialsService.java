package io.airbyte.testingtool.credentials;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretName;
import com.google.cloud.secretmanager.v1.SecretVersion;
import io.airbyte.testingtool.argument_parser.Command;
import io.airbyte.testingtool.json.Jsons;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import io.airbyte.testingtool.scenario.config.ServiceAccountConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.StreamSupport;

public class CredentialsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CredentialsService.class);
  private static final String LOCAL_SECRET_FOLDER = "secrets/";
  private static final String SERVICE_ACCOUNT_CREDENTIAL_FILE = "service_account_credentials.json";

  private static ServiceAccountConfig serviceAccountConfig;

  public static Map<String, CredentialConfig> getCredentials(final Command runCommand, final Map<String, String> args) throws IOException {
    return switch (runCommand) {
      case RUN_SCENARIO -> getCredentialsFromSecretService(args);
      case RUN_SCENARIO_LOCAL -> getCredentialsFromLocalSecrets(args);
      default -> null;
    };
  }

  public static Map<String, CredentialConfig> getCredentialsFromSecretService(final Map<String, String> args) throws IOException {
    Map<String, CredentialConfig> credentials = new HashMap<>();
    for (Entry<String, String> nameCredentialsMap : args.entrySet()) {
      credentials.put(nameCredentialsMap.getKey(), readSecretManagerCredential(nameCredentialsMap.getValue()));
    }

    return credentials;
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

  private static CredentialConfig readSecretManagerCredential(final String secretName) throws IOException {
    var projectName = serviceAccountConfig.getProjectId();
    try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
      SecretName secretNameForGetActiveVersion = SecretName.of(projectName, secretName);
      SecretManagerServiceClient.ListSecretVersionsPagedResponse versionList = client.listSecretVersions(secretNameForGetActiveVersion);
      var optionSecretWithVersion = StreamSupport.stream(versionList.iterateAll().spliterator(), false)
              .filter(secretVersion -> secretVersion.getState().equals(SecretVersion.State.ENABLED))
              .findFirst();

      if (optionSecretWithVersion.isEmpty()) {
        throw new RuntimeException(String.format("Driver could not find active version of \"%s\" secret.", secretName));
      }
      var secretNameWithVersion = optionSecretWithVersion.get().getName();
      AccessSecretVersionResponse response = client.accessSecretVersion(secretNameWithVersion);
      return Jsons.deserialize(response.getPayload().getData().toStringUtf8(), CredentialConfig.class);
    }
  }

  static {
    try {
      String fullConfigAsString = Files.readString(Path.of(LOCAL_SECRET_FOLDER + SERVICE_ACCOUNT_CREDENTIAL_FILE));
      serviceAccountConfig = Jsons.deserialize(fullConfigAsString, ServiceAccountConfig.class);
    } catch (IOException e) {
      LOGGER.error("Fail to parse \"{}\" config file!", SERVICE_ACCOUNT_CREDENTIAL_FILE);
    }
  }

}
