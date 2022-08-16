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


public class AirbyteInstance extends InstanceWithCredentials {
  private static final Logger LOGGER = LoggerFactory.getLogger(AirbyteInstance.class);
  private static final String CLOUD_API_USER = "cloud-api";
  @Getter
  private AirbyteApiClient airbyteApi;

  @Builder
  public AirbyteInstance(String instanceName, CredentialConfig credentialConfig, String apiServerHost, int apiServerPort) {
    super(instanceName, credentialConfig);
    LOGGER.info("Creating Airbyte Config Api Client <" + instanceName + ">");
    airbyteApi = new AirbyteApiClient(new ApiClient()
        .setScheme("http")
        .setHost(apiServerHost)
        .setPort(apiServerPort)
        .setBasePath("/api")
        .setRequestInterceptor(builder -> {
          builder.setHeader("X-Endpoint-API-UserInfo", getAuthHeader(CLOUD_API_USER, true));
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
