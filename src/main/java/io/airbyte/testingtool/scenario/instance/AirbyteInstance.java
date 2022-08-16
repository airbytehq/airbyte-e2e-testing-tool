package io.airbyte.testingtool.scenario.instance;

import io.airbyte.api.client.AirbyteApiClient;
import io.airbyte.api.client.invoker.generated.ApiClient;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.Builder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Setter;

public class AirbyteInstance extends InstanceWithCredentials {
  private static final Logger LOGGER = LoggerFactory.getLogger(AirbyteInstance.class);
  private static final String API_USER = "cloud-api";
  private static final String API_HOST_NODE = "apiHost";
  private static final String API_PORT_NODE = "apiPort";
  private static final String API_PATH_NODE = "apiPath";
  private static final String API_SCHEME_NODE = "apiScheme";

  @Getter
  @Setter
  private AirbyteApiClient airbyteApi;

  @Builder
  public AirbyteInstance(String instanceName, CredentialConfig credentialConfig) {
    super(instanceName, credentialConfig);
    String apiServerHost = credentialConfig.getCredentialJson().get(API_HOST_NODE).textValue();
    int apiServerPort = Integer.valueOf(credentialConfig.getCredentialJson().get(API_PORT_NODE).textValue());
    String apiPath = credentialConfig.getCredentialJson().get(API_PATH_NODE).textValue();
    apiPath = apiPath == null ? "/api" : apiPath;
    String apiScheme = credentialConfig.getCredentialJson().get(API_SCHEME_NODE).textValue();
    apiScheme = apiScheme == null ? "http" : apiScheme;

    LOGGER.info("Creating Airbyte Config Api Client <" + instanceName + ">");
    airbyteApi = new AirbyteApiClient(new ApiClient()
        .setScheme(apiScheme)
        .setHost(apiServerHost)
        .setPort(apiServerPort)
        .setBasePath(apiPath)
        .setRequestInterceptor(builder -> {
          builder.setHeader("X-Endpoint-API-UserInfo", getAuthHeader(API_USER, true));
          builder.setHeader("User-Agent", "Airbyte-E2E-Testing-Tool");
        }));
  }

  @Override
  public InstanceTypes getInstanceType() {
    return InstanceTypes.AIRBYTE;
  }

  private static String getAuthHeader(String userId, final boolean verified) {
    final var payload = ("{\"user_id\": \"" + userId + "\", \"email_verified\":" + verified + "}").getBytes(StandardCharsets.UTF_8);
    return Base64.getEncoder().encodeToString(payload);
  }

}
