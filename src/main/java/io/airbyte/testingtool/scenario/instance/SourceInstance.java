package io.airbyte.testingtool.scenario.instance;

import io.airbyte.testingtool.scenario.config.CredentialConfig;
import io.airbyte.testingtool.scenario.config.CredentialConfig.InstanceCredTypes;
import lombok.Builder;

public class SourceInstance extends InstanceWithCredentials {

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
