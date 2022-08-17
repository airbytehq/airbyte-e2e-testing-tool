package io.airbyte.testingtool.scenario.instance;

import io.airbyte.testingtool.scenario.config.CredentialConfig;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class DestinationInstance extends InstanceWithCredentials {

  @Getter
  @Setter
  private UUID id;

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
