package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import lombok.Builder;

@Builder
public class CreateConnection extends AbstractScenarioAction {

  private final AirbyteConnection connection;
  private final DestinationInstance destinationInstance;
  private final SourceInstance sourceInstance;

  @Override
  public String getActionName() {
    return "Create Connection";
  }

  @Override
  public void doActionInternal() {
    createDestination();
  }

  private void createDestination() {
  }
}
