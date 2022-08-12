package io.airbyte.testingtool.scenario.instance;

import io.airbyte.testingtool.scenario.config.CredentialConfig;
import lombok.Builder;

public class AirbyteInstance extends InstanceWithCredentials {

  @Builder
  public AirbyteInstance(String instanceName, CredentialConfig credentialConfig) {
    super(instanceName, credentialConfig);
  }

  @Override
  public InstanceTypes getInstanceType() {
    return InstanceTypes.AIRBYTE;
  }

}
