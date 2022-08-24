package io.airbyte.testingtool.scenario.validator.validations;

import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfigAction;
import io.airbyte.testingtool.scenario.config.ScenarioConfigInstance;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

public class ValidationInstanceInitiation extends AbstractValidation {

  public ValidationInstanceInitiation(ScenarioConfig scenarioConfig) {
    super(scenarioConfig);
  }

  @Override
  protected String getValidationName() {
    return "Check that all instances have initialization action";
  }

  @Override
  protected void validateInternal(List<String> errors) {
    var initiatedInstanceNames = Stream.concat(getScenarioConfig().getScenarioActions().stream(),
            getScenarioConfig().getPreparationActions().stream())
        .map(ScenarioConfigAction::getResultInstance).collect(
            Collectors.toSet());
    var requiredInitializationInstanceNames = getScenarioConfig().getUsedInstances().stream()
        .filter(scenarioConfigInstance -> scenarioConfigInstance.getInstanceType().isInitializationIsRequired())
        .map(ScenarioConfigInstance::getInstanceName).collect(
            Collectors.toSet());

    String errorText = null;
    var notInitializedInstances = requiredInitializationInstanceNames.stream().filter(s -> !initiatedInstanceNames.contains(s))
        .collect(Collectors.joining(", "));
    if (StringUtils.isNotEmpty(notInitializedInstances)) {
      errorText = "Instance(s) that should be initialized by an action : " + notInitializedInstances;
    }
  }
}
