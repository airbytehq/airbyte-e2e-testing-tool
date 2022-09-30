package io.airbyte.testingtool.scenario.instance;

import io.airbyte.testingtool.scenario.config.credentials.CredentialConfig;
import io.airbyte.testingtool.scenario.config.scenarios.ScenarioConfigInstance;

public class InstanceFactory {

  public static Instance getInstance(ScenarioConfigInstance instanceConfig) {
    return getInstance(instanceConfig, null);
  }

  public static Instance getInstance(ScenarioConfigInstance instanceConfig, CredentialConfig creds) {
    try {
      return switch (instanceConfig.getInstanceType()) {
        case AIRBYTE -> AirbyteApiInstance.builder().instanceName(instanceConfig.getInstanceName()).credentialConfig(creds).build();
        case CONNECTION -> AirbyteConnection.builder().instanceName(instanceConfig.getInstanceName()).build();
        case SOURCE -> SourceInstance.builder().instanceName(instanceConfig.getInstanceName()).credentialConfig(creds).build();
        case SOURCE_WITH_CONNECTION_SETTINGS ->
            SourceWithSettingsInstance.sourceWithSettingsInstanceBuilder().instanceName(instanceConfig.getInstanceName()).credentialConfig(creds)
                .build();
        case DESTINATION -> DestinationInstance.builder().instanceName(instanceConfig.getInstanceName()).credentialConfig(creds).build();
      };
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
