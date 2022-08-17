package io.airbyte.testingtool.scenario.instance;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class AirbyteConnection extends Instance {

  @Getter
  @Setter
  private UUID connectionId;

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
