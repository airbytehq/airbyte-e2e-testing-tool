package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import lombok.Builder;

@Builder
public class ResetConnection extends AbstractScenarioAction {

  private final AirbyteConnection connection;
  private final AirbyteInstance airbyteInstance;

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

  @Override
  public int compareTo(Integer o) {
    return 0;
  }
}
