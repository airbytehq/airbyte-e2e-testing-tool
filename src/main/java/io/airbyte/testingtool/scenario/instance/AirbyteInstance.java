package io.airbyte.testingtool.scenario.instance;

import io.airbyte.api.client.AirbyteApiClient;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class AirbyteInstance extends InstanceWithCredentials {

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
