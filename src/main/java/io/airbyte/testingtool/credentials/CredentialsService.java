package io.airbyte.testingtool.credentials;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.secretmanager.v1.SecretName;
import com.google.cloud.secretmanager.v1.SecretVersion;
import io.airbyte.testingtool.argument_parser.Command;
import io.airbyte.testingtool.json.Jsons;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import io.airbyte.testingtool.scenario.config.ServiceAccountConfig;
import io.airbyte.testingtool.scenario.instance.InstanceWithCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
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
    var projectName = getServiceAccountConfig().getProjectId();
    Credentials myCredentials = ServiceAccountCredentials.fromStream(
            new FileInputStream(LOCAL_SECRET_FOLDER + SERVICE_ACCOUNT_CREDENTIAL_FILE));
    SecretManagerServiceSettings secretManagerServiceSettings =
            SecretManagerServiceSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
                    .build();
    try (SecretManagerServiceClient client = SecretManagerServiceClient.create(secretManagerServiceSettings)) {
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

  private static ServiceAccountConfig getServiceAccountConfig() {
    if (Objects.isNull(serviceAccountConfig)) {
      Path pathToServiceAccountCredentialFile = Path.of(LOCAL_SECRET_FOLDER + SERVICE_ACCOUNT_CREDENTIAL_FILE);
      try {
        String fullConfigAsString = Files.readString(pathToServiceAccountCredentialFile);
        serviceAccountConfig = Jsons.deserialize(fullConfigAsString, ServiceAccountConfig.class);
        return serviceAccountConfig;
      } catch (IOException e) {
        String errorMessage = String.format("Fail to parse \"%s\" config file!", pathToServiceAccountCredentialFile);
        LOGGER.error(errorMessage);
        throw new RuntimeException(errorMessage);
      }
    } else {
      return serviceAccountConfig;
    }
  }

  public static <T> T extractSettingsFromConfig(InstanceWithCredentials instance, Class<T> settingsType) {
    var settings = instance.getCredentialConfig().getAdditionalSettings();
    if (settings == null) {
      throw new RuntimeException("The credential config has no settings!");
    } else {
      return settingsType.cast(settings);
    }
  }

}
