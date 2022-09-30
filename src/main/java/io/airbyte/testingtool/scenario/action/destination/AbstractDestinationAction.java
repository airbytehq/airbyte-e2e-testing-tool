package io.airbyte.testingtool.scenario.action.destination;

import io.airbyte.testingtool.scenario.action.ScenarioAction;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import java.util.List;

abstract class AbstractDestinationAction extends ScenarioAction {

  protected final DestinationInstance destinationInstance;

  public AbstractDestinationAction(int order, List<Instance> requiredInstances, Instance resultInstance,
      DestinationInstance destinationInstance) {
    super(order, requiredInstances, resultInstance);
    this.destinationInstance = destinationInstance;
  }

  @Override
  protected String getContextInternal() {
    return "Destination name : **" + destinationInstance.getInstanceName() + "**";
  }


}
