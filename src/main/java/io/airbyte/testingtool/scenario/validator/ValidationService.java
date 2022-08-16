package io.airbyte.testingtool.scenario.validator;

import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfigAction;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;

public class ValidationService {

  public static void validateScenarioConfig(ScenarioConfig scenarioConfig) {
    var actions = scenarioConfig.getScenarioActions();
    Set<String> actionInstances = actions.stream().map(ScenarioConfigAction::getResultInstance).collect(Collectors.toSet());
    actions.forEach(scenarioConfigAction -> actionInstances.addAll(scenarioConfigAction.getRequiredInstances()));
  }

  private static ValidationResult validateUsedInstances(ScenarioConfig scenarioConfig) {
    return null;
  }

  private static ValidationResult validateAllActionInstancesInUsedInstances(ScenarioConfig scenarioConfig) {
    return null;
  }

  private static ValidationResult validateAllRequiredInstancesInitiated(ScenarioConfig scenarioConfig) {
    return null;
  }

  private class ValidationResult {

  }

}
