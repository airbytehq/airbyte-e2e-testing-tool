package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import lombok.Builder;

@Builder
public class SyncConnection extends AbstractScenarioAction {

  private final AirbyteConnection connection;
  private final AirbyteInstance airbyteInstance;

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
