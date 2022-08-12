package io.airbyte.testingtool.scenario.instance;

import io.airbyte.testingtool.scenario.config.CredentialConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfigInstance;

public class InstanceFactory {

  public static Instance getInstance(ScenarioConfigInstance instanceConfig) {
    return getInstance(instanceConfig, null);
  }

  public static Instance getInstance(ScenarioConfigInstance instanceConfig, CredentialConfig creds) {
    return switch (instanceConfig.getInstanceType()) {
      case AIRBYTE -> AirbyteInstance.builder().instanceName(instanceConfig.getInstanceName()).credentialConfig(creds).build();
      case CONNECTION -> AirbyteConnection.builder().instanceName(instanceConfig.getInstanceName()).build();
      case SOURCE -> SourceInstance.builder().instanceName(instanceConfig.getInstanceName()).credentialConfig(creds).build();
      case DESTINATION -> DestinationInstance.builder().instanceName(instanceConfig.getInstanceName()).credentialConfig(creds).build();
    };
  }

}
