package io.airbyte.testingtool.scenario.instance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class Instance {

  protected String instanceName;

  public abstract InstanceTypes getInstanceType();

}
