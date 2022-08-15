package io.airbyte.testingtool.scenario.instance;

import io.airbyte.testingtool.scenario.config.CredentialConfig;
import lombok.Builder;

public class DestinationInstance extends InstanceWithCredentials {

  @Builder
  public DestinationInstance(String instanceName, CredentialConfig credentialConfig) {
    super(instanceName, credentialConfig);
  }

  @Override
  public InstanceTypes getInstanceType() {
    return InstanceTypes.DESTINATION;
  }

  public void configureDestination(AirbyteInstance airbyteInstance) {

  }
}
