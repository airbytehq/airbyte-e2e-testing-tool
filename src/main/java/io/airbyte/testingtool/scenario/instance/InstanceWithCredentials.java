package io.airbyte.testingtool.scenario.instance;

import io.airbyte.testingtool.scenario.config.CredentialConfig;
import io.airbyte.testingtool.scenario.config.CredentialConfig.InstanceCredTypes;
import lombok.Getter;

@Getter
public abstract class InstanceWithCredentials extends Instance {

  protected CredentialConfig credentialConfig;

  public InstanceCredTypes getRequiredCredentialType() {
    return getInstanceType().getRequiredCredentials();
  }

  public InstanceWithCredentials(String instanceName, CredentialConfig credentialConfig) {
    super(instanceName);
    if (credentialConfig.getCredentialType().equals(getRequiredCredentialType())) {
      this.credentialConfig = credentialConfig;
    } else {
      throw new RuntimeException("Unexpected credential type! Expected : " + getRequiredCredentialType() + ", actual : " + credentialConfig.getCredentialType());
    }
  }
}
