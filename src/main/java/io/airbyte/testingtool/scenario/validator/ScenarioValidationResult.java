package io.airbyte.testingtool.scenario.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScenarioValidationResult {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioValidationResult.class);

  @Getter
  private final String scenarioName;
  @Getter
  private final Map<String, ValidationResult> validationResults = new HashMap<>();

  public ScenarioValidationResult(String scenarioName) {
    this.scenarioName = scenarioName;
  }

  public void addValidationResult(final String validationName, final ValidationResult validationResult) {
    validationResults.putIfAbsent(validationName, validationResult);
  }

  public void printResults() {
    LOGGER.info("Scenario `" + scenarioName + "` validation result : \n" + getFormattedValidationResults());
  }

  public boolean isValidationSuccessful() {
    return validationResults.values().stream().noneMatch(ValidationResult::isFailed);
  }

  private String getFormattedValidationResults() {
    return getValidationResults(false).stream()
        .map(validationResult -> validationResult.getFormattedSummary(getLongestName()))
        .collect(Collectors.joining("\n"));
  }

  public List<ValidationResult> getValidationResults(boolean isOnlyErrors) {
    return validationResults.values().stream().filter(validationResult -> !isOnlyErrors || validationResult.isFailed()).toList();
  }

  private int getLongestName() {
    return validationResults.values().stream().map(validationResult -> validationResult.getValidationName().length())
        .max(Integer::compareTo).get();
  }
}
