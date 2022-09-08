package io.airbyte.testingtool.scenario.config;

import io.airbyte.testingtool.json.Jsons;
import io.airbyte.testingtool.scenario.validator.ValidationService;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScenarioConfigService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioConfigService.class);
  private static final String SCENARIO_PATH = "/scenarios";

  @Getter
  private final static Map<String, ScenarioConfig> scenarioConfigs;

  public static ScenarioConfig getConfig(String scenarioName) {
    if (scenarioConfigs.containsKey(scenarioName)) {
      var scenarioConfig = scenarioConfigs.get(scenarioName);
      if (ValidationService.validateScenarioConfig(scenarioConfig)) {
        return scenarioConfig;
      } else {
        throw new RuntimeException("The scenario " + scenarioName + " is invalid!");
      }
    } else {
      throw new RuntimeException("The scenario " + scenarioName + " not found!");
    }
  }

  static {
    scenarioConfigs = new HashMap<>();
    try (var paths = Files.walk(getPath())) {
      paths.filter(Files::isRegularFile).forEach(path -> {
        try {
          String fullConfigAsString = Files.readString(path);
          var scenarioConfig = Jsons.deserialize(fullConfigAsString, ScenarioConfig.class);
          scenarioConfigs.put(scenarioConfig.getScenarioName(), scenarioConfig);
        } catch (IOException e) {
          LOGGER.error("Fail to parse scenario config file : {}!", path);
        }
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Path getPath() throws IOException, URISyntaxException {
    URI uri = ScenarioConfigService.class.getResource(SCENARIO_PATH).toURI();
    if ("jar".equals(uri.getScheme())) {
      FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
      return fileSystem.getPath(SCENARIO_PATH);
    } else {
      return Paths.get(uri);
    }
  }

}
