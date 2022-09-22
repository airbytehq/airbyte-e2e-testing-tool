package io.airbyte.testingtool.scenario.action.connection;

import io.airbyte.testingtool.scenario.action.ScenarioAction;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.Instance;
import java.util.List;

abstract class AbstractConnectionAction extends ScenarioAction {

  protected final AirbyteConnection connectionInstance;

  public AbstractConnectionAction(int order, List<Instance> requiredInstances,
      Instance resultInstance, AirbyteConnection connectionInstance) {
    super(order, requiredInstances, resultInstance);
    this.connectionInstance = connectionInstance;
  }

  @Override
  protected String getContextInternal() {
    return "Connection name : **" + connectionInstance.getInstanceName() + "**";
  }

}
