package io.airbyte.testingtool.scenario.instance;

import io.airbyte.testingtool.scenario.config.CredentialConfig;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class SourceInstance extends InstanceWithCredentials {

  @Getter
  @Setter
  private UUID id;

  @Builder
  public SourceInstance(String instanceName, CredentialConfig credentialConfig) {
    super(instanceName, credentialConfig);
  }

  @Override
  public InstanceTypes getInstanceType() {
    return InstanceTypes.SOURCE;
  }


  public void configureDestination(AirbyteInstance airbyteInstance) {

  }
}
