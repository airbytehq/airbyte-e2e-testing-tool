package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import lombok.Builder;

public class ActionSyncConnection extends ScenarioAction {

  private final AirbyteConnection connection;
  private final AirbyteInstance airbyteInstance;

  @Builder
  public ActionSyncConnection(int order, AirbyteConnection connection, AirbyteInstance airbyteInstance) {
    super(order);
    this.connection = connection;
    this.airbyteInstance = airbyteInstance;
  }

  @Override
  public String getActionName() {
    return "Sync Connection";
  }

  @Override
  public void doActionInternal() {
    sync();
  }

  private void sync() {
  }
}
