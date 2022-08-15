package io.airbyte.testingtool.scenario.config;

import io.airbyte.testingtool.json.Jsons;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScenarioConfigService {

  public static ScenarioConfig getConfig(String[] args) throws IOException {
    final String fullConfigAsString = Files.readString(Path.of("src/main/resources/scenarios/poc-scenario.json"));
    return Jsons.deserialize(fullConfigAsString, ScenarioConfig.class);
  }

}
