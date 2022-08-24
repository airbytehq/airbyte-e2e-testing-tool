package io.airbyte.testingtool.scenario.validator;

import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.validator.validations.AbstractValidation;
import io.airbyte.testingtool.scenario.validator.validations.ValidationActionInstanceRequirements;
import io.airbyte.testingtool.scenario.validator.validations.ValidationActionInstances;
import io.airbyte.testingtool.scenario.validator.validations.ValidationInstanceInitiation;
import io.airbyte.testingtool.scenario.validator.validations.ValidationUsedInstances;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);

  public static boolean validateScenarioConfig(final ScenarioConfig scenarioConfig) {
    List<AbstractValidation> validations = List.of(new ValidationActionInstances(scenarioConfig), new ValidationInstanceInitiation(scenarioConfig),
        new ValidationUsedInstances(scenarioConfig), new ValidationActionInstanceRequirements(scenarioConfig));

    var results = validations.stream().map(AbstractValidation::validate).toList();
    printResults(scenarioConfig.getScenarioName(), results);
    return results.stream().noneMatch(ValidationResult::isFailed);
  }

  private static void printResults(final String scenarioName, final List<ValidationResult> results) {
    LOGGER.info("Scenario `" + scenarioName + "` validation result : \n" + results.stream()
        .map(validationResult -> validationResult.getFormattedSummary(getLongestName(results)))
        .collect(Collectors.joining("\n")));
  }

  public static int getLongestName(final List<ValidationResult> results) {
    return results.stream().map(validationResult -> validationResult.getValidationName().length())
        .max(Integer::compareTo).get();
  }

}
