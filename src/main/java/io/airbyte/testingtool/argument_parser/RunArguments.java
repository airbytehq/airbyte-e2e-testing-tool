package io.airbyte.testingtool.argument_parser;

import io.airbyte.testingtool.scenario.config.CredentialConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(access = AccessLevel.PACKAGE)
@Getter
public class RunArguments {

  private final Command runCommand;
  private final ScenarioConfig scenarioConfig;
  private final Map<String, CredentialConfig> credentials;
  private final Map<String, String> params;

  @Override
  public String toString() {
    return String.format(""" 
        Run arguments:
          Run command          : %s
          Scenario config name : %s
          Credentials          : %s
          Parameters           : %s
        """, runCommand.name(), scenarioConfig.getScenarioName(), credentials.entrySet().stream().collect(Collectors.toMap(
        Entry::getKey, x -> x.getValue().getCredentialName())), params);
  }
}
