package io.airbyte.testingtool.scenario.instance;

import io.airbyte.api.client.AirbyteApiClient;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import lombok.Builder;
import lombok.Getter;

public class AirbyteInstance extends InstanceWithCredentials {

  @Getter
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
