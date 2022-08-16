package io.airbyte.testingtool.scenario.instance;

import static io.airbyte.testingtool.scenario.config.CredentialConfig.InstanceCredTypes.AIRBYTE_CREDS;
import static io.airbyte.testingtool.scenario.config.CredentialConfig.InstanceCredTypes.DESTINATION_CREDS;
import static io.airbyte.testingtool.scenario.config.CredentialConfig.InstanceCredTypes.SOURCE_CREDS;

import io.airbyte.testingtool.scenario.config.CredentialConfig.InstanceCredTypes;
import lombok.Getter;

public enum InstanceTypes {

  AIRBYTE(AIRBYTE_CREDS, true),
  SOURCE(SOURCE_CREDS, true),
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
