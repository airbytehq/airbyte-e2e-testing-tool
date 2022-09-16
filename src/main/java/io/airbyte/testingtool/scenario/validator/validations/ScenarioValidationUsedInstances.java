package io.airbyte.testingtool.scenario.validator.validations;

import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfigInstance;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ScenarioValidationUsedInstances extends AbstractScenarioValidation {

  public ScenarioValidationUsedInstances(ScenarioConfig scenarioConfig) {
    super(scenarioConfig);
  }

  @Override
  public String getValidationName() {
    return "Validate `UsedInstances` section";
  }

  @Override
  protected void validateInternal(List<String> errors) {
    var instances = getScenarioConfig().getUsedInstances();

    if (new HashSet<>(instances).size() != instances.size()) {
      errors.add("Instance duplicates found by key Name+Type!");
    }

    if (instances.stream().map(ScenarioConfigInstance::getInstanceName).collect(Collectors.toSet()).size() != instances.size()) {
      errors.add("Instance name is not unique!");
    }
  }
}
