package io.airbyte.testingtool.scenario.validator.validations;

import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfigActionParameter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    var failedActions = Stream.concat(getScenarioConfig().getScenarioActions().stream(), getScenarioConfig().getPreparationActions().stream())
        .map(action -> {
          var missingParams = action.getRequiredParameters().stream().map(ScenarioConfigActionParameter::getName)
              .filter(name -> !params.containsKey(name)).collect(
                  Collectors.toSet());
          return (missingParams.isEmpty() ? null : action.getAction().name() + " action requires params : " + missingParams);
        }).filter(Objects::nonNull).collect(Collectors.toSet());

    if (!failedActions.isEmpty()) {
      errors.addAll(failedActions);
    }
  }
}
