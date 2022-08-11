package io.airbyte.testingtool.scenario;

import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfigService;

public class ScenarioFactory {

  public static AbstractTestScenario getScenario(ScenarioConfig config) {
    return null;
  }

  public static AbstractTestScenario getScenario(String[] args) {
    return getScenario(ScenarioConfigService.getConfig(args));
  }

}
