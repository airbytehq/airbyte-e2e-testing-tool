package io.airbyte.testingtool.scenario.action;

import com.fasterxml.jackson.databind.JsonNode;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import lombok.Builder;

@Builder
public class CreateSource extends AbstractScenarioAction {

  private final AirbyteConnection connection;
  private final JsonNode sourceConfig;

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
