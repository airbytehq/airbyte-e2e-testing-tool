package io.airbyte.testingtool.argument_parser;

import static io.airbyte.testingtool.argument_parser.ArgumentParser.argumentParser;

import io.airbyte.testingtool.credentials.CredentialsService;
import io.airbyte.testingtool.scenario.config.ScenarioConfigInstance;
import io.airbyte.testingtool.scenario.config.ScenarioConfigService;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class RunArgumentFactory {

  public final static String RUN_COMMAND_ARGUMENT = "run-command";
  public final static String SCENARIO_NAME_ARGUMENT = "name";

  public static RunArguments getArguments(String[] args) throws IOException {
    var arguments = argumentParser(args);

    if (arguments.isEmpty()) {
      return getDefault();
    }

    var runCommand = Command.getCommand(getMandatoryValue(RUN_COMMAND_ARGUMENT, arguments));
    var scenarioName = getMandatoryValue(SCENARIO_NAME_ARGUMENT, arguments);
    var scenarioConfig = ScenarioConfigService.getConfig(scenarioName);

    var instanceWithCredentials = scenarioConfig.getUsedInstances().stream().filter(instance -> instance.getInstanceType().isCredentialsRequired())
        .map(
            ScenarioConfigInstance::getInstanceName).collect(
            Collectors.toSet());
    var creds = CredentialsService.getCreds(
        arguments.entrySet().stream().filter(x -> instanceWithCredentials.contains(x.getKey())).collect(Collectors.toMap(Entry::getKey,
            Entry::getValue)));
    var params = arguments.entrySet().stream().filter(
            x -> !instanceWithCredentials.contains(x.getKey()) && !x.getKey().equals(RUN_COMMAND_ARGUMENT) && !x.getKey().equals(SCENARIO_NAME_ARGUMENT))
        .collect(Collectors.toMap(Entry::getKey,
            Entry::getValue));

    return RunArguments.builder()
        .runCommand(runCommand)
        .scenarioConfig(scenarioConfig)
        .credentials(creds)
        .params(params)
        .build();
  }

  // @TODO remove when we stop using hardcoded inputs
  private static RunArguments getDefault() throws IOException {
    return RunArguments.builder()
        .runCommand(Command.RUN_SCENARIO)
        .scenarioConfig(ScenarioConfigService.getConfig("Simple sync scenario"))
        .credentials(CredentialsService.getCreds(Collections.emptyMap()))
        .params(Collections.emptyMap())
        .build();
  }

  private static String getMandatoryValue(String key, Map<String, String> map) {
    if (map.containsKey(key)) {
      return map.get(key);
    } else {
      throw new RuntimeException("The parameter " + key + " is mandatory and should be in the arguments! Arguments : " + map);
    }
  }

}
