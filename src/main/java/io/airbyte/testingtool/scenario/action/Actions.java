package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.config.CredentialConfig.InstanceCredTypes;

public enum Actions {

  RESET_CONNECTION(null),
  SYNC_CONNECTION(null),
  CREATE_SOURCE(InstanceCredTypes.SOURCE_CREDS),
  CREATE_DESTINATION(InstanceCredTypes.DESTINATION_CREDS),
  CREATE_CONNECTION(null);

  private final InstanceCredTypes requiredCreds;

  Actions(InstanceCredTypes requiredCreds) {
    this.requiredCreds = requiredCreds;
  }

  public InstanceCredTypes getRequiredCreds() {
    return requiredCreds;
  }

  public boolean isCredRequired() {
    return requiredCreds != null;
  }
}
