package io.airbyte.testingtool.scenario.config;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

public class ScenarioConfig {

  private Map<String, JsonNode> instanceCredentials;
  private Map<String, JsonNode> actionConfigs;

  public ScenarioConfig(Map<String, JsonNode> instanceCredentials, Map<String, JsonNode> actionConfigs) {
    this.instanceCredentials = instanceCredentials;
    this.actionConfigs = actionConfigs;
  }

  public Map<String, JsonNode> getInstanceCredentials() {
    return instanceCredentials;
  }

  public Map<String, JsonNode> getActionConfigs() {
    return actionConfigs;
  }
}
