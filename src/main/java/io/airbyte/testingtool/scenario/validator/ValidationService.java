package io.airbyte.testingtool.scenario.validator;

import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
    var longestName = results.stream().map(validationResult -> validationResult.getValidationName().length())
        .max(Integer::compareTo).get();
    LOGGER.info("Scenario `" + scenarioName + "` validation result : \n" + results.stream()
        .map(validationResult -> validationResult.getFormattedSummary(longestName))
        .collect(Collectors.joining("\n")));
  }

  private static ValidationResult validateUsedInstances(ScenarioConfig scenarioConfig) {
    return ValidationResult
        .builder()
        .validationName("Validate `UsedInstances` section")
        .build();
  }

  private static ValidationResult validateAllActionInstancesInUsedInstances(ScenarioConfig scenarioConfig) {
    return ValidationResult
        .builder()
        .validationName("Check that all action instances listed in the `UsedInstances` section")
        .build();
  }

  private static ValidationResult validateAllRequiredInstancesInitiated(ScenarioConfig scenarioConfig) {
    return ValidationResult
        .builder()
        .validationName("Check that all instances have initialization action")
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
