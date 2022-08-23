package io.airbyte.testingtool.argument_parser;

import io.airbyte.testingtool.scenario.config.CredentialConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import java.util.Map;
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

}
