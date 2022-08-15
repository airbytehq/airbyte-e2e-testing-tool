package io.airbyte.testingtool.scenario.instance;

import io.airbyte.api.client.AirbyteApiClient;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import lombok.Builder;

public class AirbyteInstance extends InstanceWithCredentials {

  private AirbyteApiClient client;

  @Builder
  public AirbyteInstance(String instanceName, CredentialConfig credentialConfig) {
    super(instanceName, credentialConfig);
  }

  @Override
  public InstanceTypes getInstanceType() {
    return InstanceTypes.AIRBYTE;
  }

}
