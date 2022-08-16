package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import java.util.List;
import lombok.Builder;

public class ActionCreateConnection extends ScenarioAction {

  private final AirbyteInstance airbyteInstance;
  private final AirbyteConnection connection;
  private final DestinationInstance destinationInstance;
  private final SourceInstance sourceInstance;

  @Builder
  public ActionCreateConnection(int order, List<Instance> requiredInstances, Instance resultInstance, AirbyteInstance airbyteInstance,
      AirbyteConnection connection, DestinationInstance destinationInstance, SourceInstance sourceInstance) {
    super(order, requiredInstances, resultInstance);
    this.airbyteInstance = airbyteInstance;
    this.connection = connection;
    this.destinationInstance = destinationInstance;
    this.sourceInstance = sourceInstance;
  }

  @Override
  public String getActionName() {
    return "Create Connection";
  }

  @Override
  public void doActionInternal() {
    createDestination();
  }

  private void createDestination() {
  }
}
