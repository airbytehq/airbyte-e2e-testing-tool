package io.airbyte.testingtool.scenario.validator.validations;

import io.airbyte.testingtool.scenario.config.CredentialConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import java.util.List;
import java.util.Map;

public class RunValidationCredentials extends AbstractScenarioValidation {

  private final Map<String, CredentialConfig> credentialConfigs;

  public RunValidationCredentials(ScenarioConfig scenarioConfig, Map<String, CredentialConfig> credentialConfigs) {
    super(scenarioConfig);
    this.credentialConfigs = credentialConfigs;
  }

  @Override
  protected String getValidationName() {
    return "All required credentials exist";
  }

  @Override
  protected void validateInternal(List<String> errors) {
    var instancesWithoutCredentials = getScenarioConfig().getUsedInstances().stream()
        .filter(instance -> instance.getInstanceType().isCredentialsRequired() && !credentialConfigs.containsKey(instance.getInstanceName()))
        .toList();
    if (!instancesWithoutCredentials.isEmpty()) {
      instancesWithoutCredentials.forEach(instance -> errors.add("Instance " + instance.getInstanceName() + " doesn't get all required credentials : " + instance.getInstanceType().getRequiredCredentials().value()));
    }
  }
}
