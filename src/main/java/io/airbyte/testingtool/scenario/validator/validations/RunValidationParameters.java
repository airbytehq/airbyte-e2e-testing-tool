package io.airbyte.testingtool.scenario.validator.validations;

import io.airbyte.testingtool.scenario.ScenarioUtils;
import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RunValidationParameters extends AbstractScenarioValidation {

  private final Map<String, String> params;

  public RunValidationParameters(ScenarioConfig scenarioConfig, Map<String, String> params) {
    super(scenarioConfig);
    this.params = params;
  }

  @Override
  public String getValidationName() {
    return "All required parameters exist";
  }

  @Override
  protected void validateInternal(List<String> errors) {
    var missingParams = ScenarioUtils.getAllRequiredParametersWithoutInitialization(getScenarioConfig()).stream()
        .filter(parameter -> !params.containsKey(parameter.getName()))
        .map(parameter ->
            parameter.getName() + " is required parameter by missing in the input params.")
        .collect(Collectors.toSet());

    if (!missingParams.isEmpty()) {
      errors.addAll(missingParams);
    }
  }
}
