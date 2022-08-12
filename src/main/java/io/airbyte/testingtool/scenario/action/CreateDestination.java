package io.airbyte.testingtool.scenario.action;

import com.fasterxml.jackson.databind.JsonNode;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import lombok.Builder;

@Builder
public class CreateDestination extends AbstractScenarioAction {

  private final AirbyteConnection connection;
  private final JsonNode destinationConfig;

  @Override
  public String getActionName() {
    return "Create Destination";
  }

  @Override
  public void doActionInternal() {
    createDestination();
  }

  private void createDestination() {
  }
}
