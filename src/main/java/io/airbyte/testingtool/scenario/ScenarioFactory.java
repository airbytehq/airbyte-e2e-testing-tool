package io.airbyte.testingtool.scenario;

import io.airbyte.testingtool.argument_parser.RunArguments;
import io.airbyte.testingtool.scenario.action.ActionFactory;
import io.airbyte.testingtool.scenario.action.ScenarioAction;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfigAction;
import io.airbyte.testingtool.scenario.config.ScenarioConfigActionParameter;
import io.airbyte.testingtool.scenario.config.ScenarioConfigInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.InstanceFactory;
import io.airbyte.testingtool.scenario.parameter.ScenarioParameter;
import io.airbyte.testingtool.scenario.validator.ValidationService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScenarioFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioFactory.class);

  /**
   * Builds a test scenario using incoming arguments.
   *
   * @param runArguments main class run arguments
   * @return ready for execution test scenario
   */
  public static TestScenario getScenario(final RunArguments runArguments) {
    return buildScenario(runArguments.getScenarioConfig(), runArguments.getCredentials(), runArguments.getParams());
  }

  private static TestScenario buildScenario(final ScenarioConfig config, final Map<String, CredentialConfig> credentialConfigs,
      final Map<String, String> params) {
    if (ValidationService.validateScenarioRun(config, credentialConfigs, params)) {

      final Map<String, Instance> scenarioInstanceNameToInstanceMap = mapInstancesAndCredentials(config, credentialConfigs);
      final Map<String, ScenarioParameter> scenarioParameterMap = mapParameters(config, params);

      return TestScenario.builder()
          .scenarioName(config.getScenarioName())
          .preparationActions(getActions(config.getPreparationActions(), scenarioInstanceNameToInstanceMap, scenarioParameterMap))
          .scenarioActions(getActions(config.getScenarioActions(), scenarioInstanceNameToInstanceMap, scenarioParameterMap))
          .build();
    } else {
      throw new RuntimeException("The scenario failed the run validation.");
    }
  }

  private static Map<String, Instance> mapInstancesAndCredentials(final ScenarioConfig config,
      final Map<String, CredentialConfig> credentialConfigs) {
    final Map<String, Instance> resultMap = new HashMap<>();

    final Set<ScenarioConfigInstance> allInstances = getScenarioInstances(config);
    allInstances.forEach(scenarioConfigInstance -> {
      final var instance = InstanceFactory.getInstance(scenarioConfigInstance, credentialConfigs.get(scenarioConfigInstance.getInstanceName()));
      resultMap.put(instance.getInstanceName(), instance);
    });

    return resultMap;
  }

  private static Map<String, ScenarioParameter> mapParameters(final ScenarioConfig config,
      final Map<String, String> incomingParameters) {
    final Map<String, ScenarioParameter> resultMap = new HashMap<>();

    final Set<ScenarioConfigActionParameter> allParameters = getScenarioParameters(config);
    allParameters.forEach(scenarioConfigActionParameter -> {
      var scenarioParameter = new ScenarioParameter(scenarioConfigActionParameter);
      var parameterName = scenarioParameter.getParameterName();
      if (incomingParameters.containsKey(parameterName)) {
        scenarioParameter.setParameterValue(incomingParameters.get(parameterName));
      }
      resultMap.put(parameterName, scenarioParameter);
    });

    return resultMap;
  }

  private static Set<ScenarioConfigInstance> getScenarioInstances(final ScenarioConfig config) {
    return new HashSet<>(config.getUsedInstances());
  }

  private static Set<ScenarioConfigActionParameter> getScenarioParameters(final ScenarioConfig config) {
    var allParamNames = ScenarioUtils.getAllActions(config).stream().map(ScenarioConfigAction::getResultParameter)
        .filter(Objects::nonNull).collect(
            Collectors.toSet());
    ScenarioUtils.getAllActions(config).forEach(action -> allParamNames.addAll(action.getRequiredParameters()));
    return allParamNames;
  }

  private static SortedSet<ScenarioAction> getActions(final List<ScenarioConfigAction> actionConfigs,
      final Map<String, Instance> scenarioInstanceNameToInstanceMap, final Map<String, ScenarioParameter> params) {
    final SortedSet<ScenarioAction> actions = new TreeSet<>();
    actionConfigs.forEach(scenarioConfigAction ->
        actions.add(ActionFactory.getScenarioAction(actions.size(), scenarioConfigAction, scenarioInstanceNameToInstanceMap, params))
    );
    return actions;
  }
}
