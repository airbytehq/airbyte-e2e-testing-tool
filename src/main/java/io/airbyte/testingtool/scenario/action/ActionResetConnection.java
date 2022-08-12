package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import lombok.Builder;

public class ActionResetConnection extends ScenarioAction {

  private final AirbyteConnection connection;
  private final AirbyteInstance airbyteInstance;

  @Builder
  public ActionResetConnection(int order, AirbyteConnection connection, AirbyteInstance airbyteInstance) {
    super(order);
    this.connection = connection;
    this.airbyteInstance = airbyteInstance;
  }

  @Override
  public String getActionName() {
    return "Reset Connection";
  }

  @Override
  public void doActionInternal() {
    reset();
  }

  private void reset() {
  }

}
