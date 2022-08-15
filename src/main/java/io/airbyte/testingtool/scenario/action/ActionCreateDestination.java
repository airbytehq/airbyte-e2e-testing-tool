package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import lombok.Builder;

public class ActionCreateDestination extends ScenarioAction {

  private final AirbyteInstance airbyteInstance;
  private final DestinationInstance destinationInstance;

  @Builder
  public ActionCreateDestination(int order, AirbyteInstance airbyteInstance, DestinationInstance destinationInstance) {
    super(order);
    this.airbyteInstance = airbyteInstance;
    this.destinationInstance = destinationInstance;
  }

  @Override
  public String getActionName() {
    return "Create Destination";
  }

  @Override
  public void doActionInternal() {
    createDestination();
  }

  private void createDestination() {

  }
}
