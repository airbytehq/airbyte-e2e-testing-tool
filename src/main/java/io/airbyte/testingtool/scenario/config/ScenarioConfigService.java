package io.airbyte.testingtool.scenario.config;

import io.airbyte.testingtool.json.Jsons;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScenarioConfigService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioConfigService.class);

  private final static Map<String, ScenarioConfig> scenarioConfigs;

  public static ScenarioConfig getConfig(String scenarioName) {
    if (scenarioConfigs.containsKey(scenarioName)) {
      return scenarioConfigs.get(scenarioName);
    } else {
      throw new RuntimeException("The scenario " + scenarioName + " not found!");
    }
  }

  static {
    scenarioConfigs = new HashMap<>();
    try (var paths = Files.walk(Paths.get("src/main/resources/scenarios/"))) {
      paths.filter(Files::isRegularFile).forEach(path -> {
        try {
          String fullConfigAsString = Files.readString(path);
          var scenarioConfig = Jsons.deserialize(fullConfigAsString, ScenarioConfig.class);
          scenarioConfigs.put(scenarioConfig.getScenarioName(), scenarioConfig);
        } catch (IOException e) {
          LOGGER.error("Fail to parse scenario config file : {}!", path);
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
