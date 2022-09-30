package io.airbyte.testingtool.scenario;

import io.airbyte.testingtool.scenario.config.scenarios.ScenarioConfig;
import io.airbyte.testingtool.scenario.config.scenarios.ScenarioConfigAction;
import io.airbyte.testingtool.scenario.config.scenarios.ScenarioConfigActionParameter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScenarioUtils {

  public static List<ScenarioConfigAction> getAllActions(ScenarioConfig config) {
    return Stream.concat(config.getScenarioActions().stream(), config.getPreparationActions().stream()).toList();
  }

  public static List<ScenarioConfigActionParameter> getAllRequiredParametersWithoutInitialization(ScenarioConfig config) {
    var allResultParams = getAllResultParams(config);
    var allRequiredParams = getAllRequiredParams(config);

    return allRequiredParams.stream().filter(parameter -> !allResultParams.contains(parameter)).toList();
  }

  public static Set<ScenarioConfigActionParameter> getAllResultParams(ScenarioConfig config) {
    return getAllActions(config).stream().map(ScenarioConfigAction::getResultParameter).filter(Objects::nonNull).collect(Collectors.toSet());
  }

  public static Set<ScenarioConfigActionParameter> getAllRequiredParams(ScenarioConfig config) {
    Set<ScenarioConfigActionParameter> allRequiredParams = new HashSet<>();
    getAllActions(config).forEach(action -> allRequiredParams.addAll(action.getRequiredParameters()));
    return allRequiredParams;
  }
}
