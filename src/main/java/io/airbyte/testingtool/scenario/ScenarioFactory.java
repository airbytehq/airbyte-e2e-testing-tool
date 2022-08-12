package io.airbyte.testingtool.scenario;

import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfigService;
import java.io.IOException;

public class ScenarioFactory {

  public static TestScenario getScenario(ScenarioConfig config) {
    return null;
  }

  public static TestScenario getScenario(String[] args) throws IOException {
    return getScenario(ScenarioConfigService.getConfig(args));
  }

}
