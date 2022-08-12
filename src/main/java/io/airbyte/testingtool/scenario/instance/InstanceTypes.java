package io.airbyte.testingtool.scenario.instance;

import static io.airbyte.testingtool.scenario.config.CredentialConfig.InstanceCredTypes.AIRBYTE_CREDS;
import static io.airbyte.testingtool.scenario.config.CredentialConfig.InstanceCredTypes.DESTINATION_CREDS;
import static io.airbyte.testingtool.scenario.config.CredentialConfig.InstanceCredTypes.SOURCE_CREDS;

import io.airbyte.testingtool.scenario.config.CredentialConfig.InstanceCredTypes;

public enum InstanceTypes {

  AIRBYTE(AIRBYTE_CREDS),
  SOURCE(SOURCE_CREDS),
  DESTINATION(DESTINATION_CREDS),
  CONNECTION(null);

  private final InstanceCredTypes requiredCredentials;

  InstanceTypes(InstanceCredTypes requiredCredentials) {
    this.requiredCredentials = requiredCredentials;
  }

  public boolean isCredentialsRequired() {
    return requiredCredentials != null;
  }

  public InstanceCredTypes getRequiredCredentials() {
    return requiredCredentials;
  }
}
