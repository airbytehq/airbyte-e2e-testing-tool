package io.airbyte.testingtool.scenario.instance;

import static io.airbyte.testingtool.scenario.config.credentials.CredentialConfig.InstanceCredTypes.AIRBYTE_CREDS;
import static io.airbyte.testingtool.scenario.config.credentials.CredentialConfig.InstanceCredTypes.DESTINATION_CREDS;
import static io.airbyte.testingtool.scenario.config.credentials.CredentialConfig.InstanceCredTypes.SOURCE_CREDS;
import static io.airbyte.testingtool.scenario.config.credentials.CredentialConfig.InstanceCredTypes.SOURCE_CREDS_WITH_CONN_SETTINGS;

import io.airbyte.testingtool.scenario.config.credentials.CredentialConfig.InstanceCredTypes;
import lombok.Getter;

public enum InstanceTypes {

  AIRBYTE(AIRBYTE_CREDS, true),
  SOURCE(SOURCE_CREDS, true),
  SOURCE_WITH_CONNECTION_SETTINGS(SOURCE_CREDS_WITH_CONN_SETTINGS, true),
  DESTINATION(DESTINATION_CREDS, true),
  CONNECTION(null, true);

  @Getter
  private final InstanceCredTypes requiredCredentials;
  @Getter
  private final boolean initializationIsRequired;

  InstanceTypes(InstanceCredTypes requiredCredentials, boolean initializationIsRequired) {
    this.requiredCredentials = requiredCredentials;
    this.initializationIsRequired = initializationIsRequired;
  }

  public boolean isCredentialsRequired() {
    return requiredCredentials != null;
  }

}
