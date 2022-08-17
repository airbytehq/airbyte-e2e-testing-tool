package io.airbyte.testingtool.scenario.instance;

import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Instance {

  protected String instanceName;
  @Setter
  protected boolean isInitialized;

  public Instance(String instanceName) {
    this.instanceName = instanceName;
  }

  public abstract InstanceTypes getInstanceType();

  public boolean isInitialized() {
    return (!getInstanceType().isInitializationIsRequired() || isInitialized);
  }

}
