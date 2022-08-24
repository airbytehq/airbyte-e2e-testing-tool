package io.airbyte.testingtool.scenario.validator.validations;

import io.airbyte.testingtool.scenario.config.ActionParameterTypes;
import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfigAction;
import io.airbyte.testingtool.scenario.config.ScenarioConfigActionParameter;
import io.airbyte.testingtool.scenario.config.ScenarioConfigInstance;
import io.airbyte.testingtool.scenario.instance.InstanceTypes;
import java.util.List;

public class ValidationActionInstanceRequirements extends AbstractValidation {

  public ValidationActionInstanceRequirements(ScenarioConfig scenarioConfig) {
    super(scenarioConfig);
  }

  @Override
  protected String getValidationName() {
    return "Action should have all mandatory attributes";
  }

  @Override
  protected void validateInternal(List<String> errors) {
    getScenarioConfig().getPreparationActions().forEach(action -> {
      checkRequiredInstances(action, errors);
      checkResultInstance(action, errors);
      checkParameters(action, errors);
    });
  }

  private List<InstanceTypes> getInstanceTypesByNames(List<String> instanceNames) {
    return getScenarioConfig().getUsedInstances().stream().filter(instance -> instanceNames.contains(instance.getInstanceName())).map(
        ScenarioConfigInstance::getInstanceType).toList();
  }

  private List<ActionParameterTypes> getParamTypes(ScenarioConfigAction action) {
    return action.getRequiredParameters().stream().map(ScenarioConfigActionParameter::getType).toList();
  }

  private void checkRequiredInstances(ScenarioConfigAction action, List<String> errors) {
    var actionType = action.getAction();
    if (actionType.isInstanceRequired()) {
      var definedInstances = getInstanceTypesByNames(action.getRequiredInstances());
      if (!definedInstances.containsAll(actionType.getRequiredInstances())) {
        errors.add(
            "The action " + action.getAction().name() + " should have required instances : " + actionType.getRequiredInstances() + " but have : "
                + definedInstances);
      }
    }
  }

  private void checkResultInstance(ScenarioConfigAction action, List<String> errors) {
    var actionType = action.getAction();
    if (actionType.isResultInstance()) {
      var definedInstances = getInstanceTypesByNames(List.of(action.getResultInstance()));
      var requiredInstance = List.of(actionType.getResultInstance());
      if (!definedInstances.containsAll(requiredInstance)) {
        errors.add(
            "The action " + action.getAction().name() + " should have result instance : " + requiredInstance + " but have : " + definedInstances);
      }
    }
  }

  private void checkParameters(ScenarioConfigAction action, List<String> errors) {
    var actionType = action.getAction();
    if (actionType.isParameterRequired()) {
      var definedParameters = getParamTypes(action);
      if (!definedParameters.containsAll(action.getRequiredParameters())) {
        errors.add("The action " + action.getAction().name() + " should have result parameters : " + action.getRequiredParameters() + " but have : "
            + definedParameters);
      }
    }
  }

}
