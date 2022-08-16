package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import lombok.Builder;

public class ActionConnectToAirbyteAPI extends ScenarioAction {

  private final AirbyteInstance airbyteInstance;

  @Builder
  public ActionConnectToAirbyteAPI(int order, AirbyteInstance airbyteInstance) {
    super(order);
    this.airbyteInstance = airbyteInstance;
  }

  @Override
  public String getActionName() {
    return "Connect to Airbyte API";
  }

  @Override
  public void doActionInternal() {
    connectToApi();
  }

  private void connectToApi() {
    var creds = airbyteInstance.getCredentialConfig();

    airbyteInstance.setAirbyteApi(null);
  }
}
