package io.airbyte.testingtool.scenario.action.connection;

import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.Instance;
import java.util.List;
import lombok.Builder;

public class ActionDeleteConnection extends AbstractConnectionAction {

  @Builder
  public ActionDeleteConnection(int order, List<Instance> requiredInstances,
      Instance resultInstance,
      AirbyteConnection connection) {
    super(order, requiredInstances, resultInstance, connection);
  }

  @Override
  public String getActionName() {
    return "Drop connection";
  }

  @Override
  protected void doActionInternal() throws Exception {
    connectionInstance.getAirbyteInstance().getAirbyteApi().getConnectionApi().deleteConnection(connectionInstance.getConnectionRequestBody());
  }
}
