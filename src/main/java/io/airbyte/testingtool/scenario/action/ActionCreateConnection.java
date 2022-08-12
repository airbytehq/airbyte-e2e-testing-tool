package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import lombok.Builder;

public class ActionCreateConnection extends ScenarioAction {

  private final AirbyteInstance airbyteInstance;
  private final AirbyteConnection connection;
  private final DestinationInstance destinationInstance;
  private final SourceInstance sourceInstance;

  @Builder
  public ActionCreateConnection(int order, AirbyteInstance airbyteInstance, AirbyteConnection connection, DestinationInstance destinationInstance, SourceInstance sourceInstance) {
    super(order);
    this.connection = connection;
    this.airbyteInstance = airbyteInstance;
    this.destinationInstance = destinationInstance;
    this.sourceInstance = sourceInstance;
  }

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
