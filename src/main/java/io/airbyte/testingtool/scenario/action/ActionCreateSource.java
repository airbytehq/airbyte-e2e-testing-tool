package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import java.util.List;
import lombok.Builder;

public class ActionCreateSource extends ScenarioAction {

  private final AirbyteInstance airbyteInstance;
  private final SourceInstance sourceInstance;

  @Builder
  public ActionCreateSource(int order, List<Instance> requiredInstances, Instance resultInstance, AirbyteInstance airbyteInstance,
      SourceInstance sourceInstance) {
    super(order, requiredInstances, resultInstance);
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
