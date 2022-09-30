package io.airbyte.testingtool.scenario.action.destination;

import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import java.util.List;
import lombok.Builder;

public class ActionDeleteDestination extends AbstractDestinationAction {

  @Builder
  public ActionDeleteDestination(int order, List<Instance> requiredInstances, Instance resultInstance, DestinationInstance destinationInstance) {
    super(order, requiredInstances, resultInstance, destinationInstance);
  }

  @Override
  protected void doActionInternal() throws Exception {
    destinationInstance.getAirbyteApiInstance().getAirbyteApi().getDestinationApi()
        .deleteDestination(destinationInstance.getDestinationIdRequestBody());
  }

  @Override
  public String getActionName() {
    return "Delete destination";
  }
}
