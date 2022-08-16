package io.airbyte.testingtool.scenario.instance;

import io.airbyte.api.client.AirbyteApiClient;
import io.airbyte.api.client.invoker.generated.ApiClient;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirbyteInstance extends InstanceWithCredentials {
  private static final Logger LOGGER = LoggerFactory.getLogger(AirbyteInstance.class);

  @Getter
  @Setter
  private AirbyteApiClient airbyteApi;

  @Builder
  public AirbyteInstance(String instanceName, CredentialConfig credentialConfig) {
    super(instanceName, credentialConfig);
  }

  @Override
  public InstanceTypes getInstanceType() {
    return InstanceTypes.AIRBYTE;
  }
}
