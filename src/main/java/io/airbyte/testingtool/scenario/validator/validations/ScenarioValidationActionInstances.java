package io.airbyte.testingtool.scenario.validator.validations;

import io.airbyte.testingtool.scenario.ScenarioUtils;
import io.airbyte.testingtool.scenario.config.scenarios.ScenarioConfig;
import io.airbyte.testingtool.scenario.config.scenarios.ScenarioConfigInstance;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class ScenarioValidationActionInstances extends AbstractScenarioValidation {

  public ScenarioValidationActionInstances(ScenarioConfig scenarioConfig) {
    super(scenarioConfig);
  }

  @Override
  public String getValidationName() {
    return "Check that all action instances listed in the `UsedInstances` section";
  }

  @Override
  protected void validateInternal(List<String> errors) {
    Set<String> instanceFromActions = new HashSet<>();
    ScenarioUtils.getAllActions(getScenarioConfig())
        .forEach(scenarioConfigAction -> {
          if (StringUtils.isNotEmpty(scenarioConfigAction.getResultInstance())) {
            instanceFromActions.add(scenarioConfigAction.getResultInstance());
          }
          instanceFromActions.addAll(scenarioConfigAction.getRequiredInstances());
        });

    Set<String> describedInstance = getScenarioConfig().getUsedInstances().stream().map(ScenarioConfigInstance::getInstanceName)
        .collect(Collectors.toSet());

    var missingInstance = instanceFromActions.stream().filter(s -> !describedInstance.contains(s)).collect(Collectors.joining(", "));
    if (StringUtils.isNotEmpty(missingInstance)) {
      errors.add("Instance(s) used in the scenario actions but not described in the UsedInstances : " + missingInstance);
    }

    missingInstance = describedInstance.stream().filter(s -> !instanceFromActions.contains(s)).collect(Collectors.joining(", "));
    if (StringUtils.isNotEmpty(missingInstance)) {
      errors.add("Instance(s) described in the UsedInstances but not used in the scenario actions : " + missingInstance);
    }
  }
}
