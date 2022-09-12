package io.airbyte.testingtool.scenario.instance;

import io.airbyte.testingtool.credentials.CredentialsService;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import io.airbyte.testingtool.scenario.config.settings.ConnectionSettings;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class SourceWithSettingsInstance extends SourceInstance {

  @Getter
  @Setter
  private ConnectionSettings connectionSettings;

  @Builder(builderMethodName = "sourceWithSettingsInstanceBuilder")
  public SourceWithSettingsInstance(String instanceName, CredentialConfig credentialConfig) {
    super(instanceName, credentialConfig);
    this.connectionSettings = CredentialsService.extractSettingsFromConfig(this, ConnectionSettings.class);
  }

  @Override
  public InstanceTypes getInstanceType() {
    return InstanceTypes.SOURCE_WITH_CONNECTION_SETTINGS;
  }
}
