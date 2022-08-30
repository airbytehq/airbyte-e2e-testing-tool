package io.airbyte.testingtool.scenario.validator;

import io.airbyte.testingtool.scenario.config.CredentialConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.validator.validations.AbstractScenarioValidation;
import io.airbyte.testingtool.scenario.validator.validations.RunValidationCredentials;
import io.airbyte.testingtool.scenario.validator.validations.RunValidationParameters;
import io.airbyte.testingtool.scenario.validator.validations.ScenarioValidationActionInstanceRequirements;
import io.airbyte.testingtool.scenario.validator.validations.ScenarioValidationActionInstances;
import io.airbyte.testingtool.scenario.validator.validations.ScenarioValidationInstanceInitiation;
import io.airbyte.testingtool.scenario.validator.validations.ScenarioValidationUsedInstances;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validation service provides different validations for scenarios and guarantees their consistency.
 */
public class ValidationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);

  /**
   * Validates scenario config consistency.
   * For example, if the scenario has some action that requires a source instance, there should be an action that provides(initializes) this instance.
   * @param scenarioConfig Scenario config
   * @return               is the scenario config valid?
   */
  public static boolean validateScenarioConfig(final ScenarioConfig scenarioConfig) {
    LOGGER.info("Validating scenario config.");
    return runValidation(scenarioConfig.getScenarioName(), List.of(new ScenarioValidationActionInstances(scenarioConfig), new ScenarioValidationInstanceInitiation(scenarioConfig),
        new ScenarioValidationUsedInstances(scenarioConfig), new ScenarioValidationActionInstanceRequirements(scenarioConfig)));
  }

  /**
   * Checks that provided input contains all required params and credentials for the specific scenario.
   * If you pass this validation during scenario run, you can be sure that there is enough to execute the scenario.
   * For example, if scenario has action with a source instance, there should be provided a credential file for the source instance.
   * @param scenarioConfig    Scenario config
   * @param credentialConfigs list of provided credentials
   * @param params            list of provided parameters
   * @return                  Are the scenario requirements satisfied?
   */
  public static boolean validateScenarioRun(final ScenarioConfig scenarioConfig, final Map<String, CredentialConfig> credentialConfigs,
      final Map<String, String> params) {
    LOGGER.info("Validating scenario run configuration.");
    return runValidation(scenarioConfig.getScenarioName(), List.of(new RunValidationCredentials(scenarioConfig, credentialConfigs), new RunValidationParameters(scenarioConfig, params)));
  }

  private static boolean runValidation(final String scenarioName, final List<AbstractScenarioValidation> validations) {
    var results = validations.stream().map(AbstractScenarioValidation::validate).toList();
    printResults(scenarioName, results);
    return results.stream().noneMatch(ValidationResult::isFailed);
  }

  private static void printResults(final String scenarioName, final List<ValidationResult> results) {
    LOGGER.info("Scenario `" + scenarioName + "` validation result : \n" + results.stream()
        .map(validationResult -> validationResult.getFormattedSummary(getLongestName(results)))
        .collect(Collectors.joining("\n")));
  }

  private static int getLongestName(final List<ValidationResult> results) {
    return results.stream().map(validationResult -> validationResult.getValidationName().length())
        .max(Integer::compareTo).get();
  }

}
