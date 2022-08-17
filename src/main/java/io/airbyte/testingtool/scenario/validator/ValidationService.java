package io.airbyte.testingtool.scenario.validator;

import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfigAction;
import io.airbyte.testingtool.scenario.config.ScenarioConfigInstance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);

  public static boolean validateScenarioConfig(ScenarioConfig scenarioConfig) {
    var results = Arrays.asList(validateUsedInstances(scenarioConfig), validateAllActionInstancesInUsedInstances(scenarioConfig),
        validateAllRequiredInstancesInitiated(scenarioConfig));
    printResults(scenarioConfig.getScenarioName(), results);
    return results.stream().noneMatch(ValidationResult::isFailed);
  }

  private static void printResults(String scenarioName, List<ValidationResult> results) {
    LOGGER.info("Scenario `" + scenarioName + "` validation result : \n" + results.stream()
        .map(validationResult -> validationResult.getFormattedSummary(getLongestName(results)))
        .collect(Collectors.joining("\n")));
  }

  public static int getLongestName(List<ValidationResult> results) {
    return results.stream().map(validationResult -> validationResult.getValidationName().length())
        .max(Integer::compareTo).get();
  }

  private static ValidationResult validateUsedInstances(ScenarioConfig scenarioConfig) {
    List<String> errors = new ArrayList<>();
    var instances = scenarioConfig.getUsedInstances();

    if (new HashSet<>(instances).size() != instances.size()) {
      errors.add("Instance duplicates found by key Name+Type!");
    }

    if (instances.stream().map(ScenarioConfigInstance::getInstanceName).collect(Collectors.toSet()).size() != instances.size()) {
      errors.add("Instance name is not unique!");
    }

    return ValidationResult
        .builder()
        .validationName("Validate `UsedInstances` section")
        .errorText(String.join("; ", errors))
        .build();
  }

  private static ValidationResult validateAllActionInstancesInUsedInstances(ScenarioConfig scenarioConfig) {
    Set<String> instanceFromActions = new HashSet<>();
    Stream.concat(scenarioConfig.getScenarioActions().stream(), scenarioConfig.getPreparationActions().stream()).forEach(scenarioConfigAction -> {
      if (StringUtils.isNotEmpty(scenarioConfigAction.getResultInstance())) {
        instanceFromActions.add(scenarioConfigAction.getResultInstance());
      }
      instanceFromActions.addAll(scenarioConfigAction.getRequiredInstances());
    });

    Set<String> describedInstance = scenarioConfig.getUsedInstances().stream().map(ScenarioConfigInstance::getInstanceName)
        .collect(Collectors.toSet());

    List<String> errors = new ArrayList<>();
    var missingInstance = instanceFromActions.stream().filter(s -> !describedInstance.contains(s)).collect(Collectors.joining(", "));
    if (StringUtils.isNotEmpty(missingInstance)) {
      errors.add("Instance(s) used in the scenario actions but not described in the UsedInstances : " + missingInstance);
    }

    missingInstance = describedInstance.stream().filter(s -> !instanceFromActions.contains(s)).collect(Collectors.joining(", "));
    if (StringUtils.isNotEmpty(missingInstance)) {
      errors.add("Instance(s) described in the UsedInstances but not used in the scenario actions : " + missingInstance);
    }

    return ValidationResult
        .builder()
        .validationName("Check that all action instances listed in the `UsedInstances` section")
        .errorText(String.join("; ", errors))
        .build();
  }

  private static ValidationResult validateAllRequiredInstancesInitiated(ScenarioConfig scenarioConfig) {
    var initiatedInstanceNames = Stream.concat(scenarioConfig.getScenarioActions().stream(), scenarioConfig.getPreparationActions().stream())
        .map(ScenarioConfigAction::getResultInstance).collect(
            Collectors.toSet());
    var requiredInitializationInstanceNames = scenarioConfig.getUsedInstances().stream()
        .filter(scenarioConfigInstance -> scenarioConfigInstance.getInstanceType().isInitializationIsRequired())
        .map(ScenarioConfigInstance::getInstanceName).collect(
            Collectors.toSet());

    String errorText = null;
    var notInitializedInstances = requiredInitializationInstanceNames.stream().filter(s -> !initiatedInstanceNames.contains(s))
        .collect(Collectors.joining(", "));
    if (StringUtils.isNotEmpty(notInitializedInstances)) {
      errorText = "Instance(s) that should be initialized by an action : " + notInitializedInstances;
    }

    return ValidationResult
        .builder()
        .validationName("Check that all instances have initialization action")
        .errorText(errorText)
        .build();
  }

  @Builder
  @Getter
  private static class ValidationResult {

    private String validationName;
    private String errorText;

    public boolean isFailed() {
      return errorText != null && !errorText.isEmpty();
    }

    public String getFormattedSummary(int longestValidationName) {
      return StringUtils.rightPad("  [" + this.getValidationName() + "]", longestValidationName + 5) + " : "
          + StringUtils.rightPad((this.isFailed() ? "FAILED!" : "Ok"), 8) + (this.isFailed() ? " : " + errorText : "");
    }
  }

}
