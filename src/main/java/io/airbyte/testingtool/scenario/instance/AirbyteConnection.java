package io.airbyte.testingtool.scenario.instance;

import lombok.Builder;

public class AirbyteConnection extends Instance implements InstanceRequireInitialization {

  @Builder
  public AirbyteConnection(String instanceName) {
    super(instanceName);
  }

  @Override
  public InstanceTypes getInstanceType() {
    return InstanceTypes.CONNECTION;
  }

  public void configureConnection(AirbyteInstance airbyteInstance, SourceInstance sourceInstance, DestinationInstance destinationInstance) {

  }

}
