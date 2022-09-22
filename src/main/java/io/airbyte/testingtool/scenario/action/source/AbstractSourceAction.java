package io.airbyte.testingtool.scenario.action.source;

import io.airbyte.testingtool.scenario.action.ScenarioAction;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import java.util.List;

abstract class AbstractSourceAction extends ScenarioAction {

  protected final SourceInstance sourceInstance;

  public AbstractSourceAction(int order, List<Instance> requiredInstances, Instance resultInstance,
      SourceInstance sourceInstance) {
    super(order, requiredInstances, resultInstance);
    this.sourceInstance = sourceInstance;
  }

  @Override
  protected String getContextInternal() {
    return "Source name : **" + sourceInstance.getInstanceName() + "**";
  }
}
