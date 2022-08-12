package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import lombok.Builder;

public class ActionCreateSource extends ScenarioAction {

  private final AirbyteInstance airbyteInstance;
  private final SourceInstance sourceInstance;

  @Builder
  public ActionCreateSource(int order, AirbyteInstance airbyteInstance, SourceInstance sourceInstance) {
    super(order);
    this.airbyteInstance = airbyteInstance;
    this.sourceInstance = sourceInstance;
  }

  @Override
  public String getActionName() {
    return "Create Source";
  }

  @Override
  public void doActionInternal() {
    createSource();
  }

  private void createSource() {
  }
}
