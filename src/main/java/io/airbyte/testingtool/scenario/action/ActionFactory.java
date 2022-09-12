package io.airbyte.testingtool.scenario.action;

import static io.airbyte.testingtool.scenario.config.ActionParameterTypes.DESTINATION_VERSION;
import static io.airbyte.testingtool.scenario.config.ActionParameterTypes.SOURCE_VERSION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.AIRBYTE;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.CONNECTION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.DESTINATION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.SOURCE;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.SOURCE_WITH_CONNECTION_SETTINGS;

import io.airbyte.testingtool.scenario.config.ActionParameterTypes;
import io.airbyte.testingtool.scenario.config.ScenarioConfigAction;
import io.airbyte.testingtool.scenario.config.ScenarioConfigActionParameter;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.InstanceTypes;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import io.airbyte.testingtool.scenario.instance.SourceWithSettingsInstance;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class ActionFactory {

  /**
   * Builds a scenario action using scenario config, provided credentials and parameters.
   * @param order                             sequence is very important for actions.
   * @param config                            scenario config file describes all logical instances and actions without any credentials.
   * @param scenarioInstanceNameToInstanceMap mapping of instances built using provided credentials.
   * @param params                            action special parameters.
   * @return                                  scenario action with all required instances.
   */
  public static ScenarioAction getScenarioAction(int order, ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap,
      final Map<String, String> params) {
    return switch (config.getAction()) {
      case CONNECT_AIRBYTE_API -> getActionConnectToAirbyteAPI(order, config,
          scenarioInstanceNameToInstanceMap);
      case RESET_CONNECTION -> getActionResetConnection(order, config,
          scenarioInstanceNameToInstanceMap);
      case SYNC_CONNECTION -> getActionSyncConnection(order, config,
          scenarioInstanceNameToInstanceMap);
      case CREATE_SOURCE -> getActionCreateSource(order, config, scenarioInstanceNameToInstanceMap);
      case CREATE_SOURCE_WITH_CONN_SETTINGS -> getActionCreateSourceWithSettings(order, config, scenarioInstanceNameToInstanceMap);
      case CREATE_DESTINATION -> getActionCreateDestination(order, config,
          scenarioInstanceNameToInstanceMap);
      case CREATE_CONNECTION -> getActionCreateConnection(order, config,
          scenarioInstanceNameToInstanceMap);
      case CREATE_CUSTOM_CONNECTION -> getActionCreateConnectionCustom(order, config,
          scenarioInstanceNameToInstanceMap);
      case UPDATE_SOURCE_VERSION -> getActionUpdateSourceVersion(order, config,
          scenarioInstanceNameToInstanceMap, params);
      case UPDATE_DESTINATION_VERSION -> getActionUpdateDestinationVersion(order, config,
          scenarioInstanceNameToInstanceMap, params);
    };
  }

  private static <T extends Instance> T getInstanceByType(InstanceTypes type,
      List<String> scenarioConfigInstanceNames,
      Map<String, Instance> scenarioInstanceNameToInstanceMap, Class<T> clazz) {

    var filteredList = scenarioConfigInstanceNames.stream()
        .map(scenarioInstanceNameToInstanceMap::get)
        .filter(instance -> instance.getInstanceType().equals(type))
        .toList();
    if (filteredList.size() == 1) {
      return clazz.cast(filteredList.get(0));
    } else {
      throw new RuntimeException(
          "Found " + (filteredList.size() > 1 ? "multiply(" + filteredList.size() + ") values "
              : "zero values ") + "for type " + type);
    }
  }

  private static Instance getInstance(String instanceName,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return getInstance(instanceName, scenarioInstanceNameToInstanceMap, Instance.class);
  }

  private static <T extends Instance> T getInstance(String instanceName,
      Map<String, Instance> scenarioInstanceNameToInstanceMap, Class<T> clazz) {
    return clazz.cast(scenarioInstanceNameToInstanceMap.get(instanceName));
  }

  private static List<Instance> getRequiredInstances(ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return scenarioInstanceNameToInstanceMap.entrySet().stream()
        .filter(
            nameInstanceEntry -> config.getRequiredInstances().contains(nameInstanceEntry.getKey()))
        .map(
            Entry::getValue).toList();
  }

  private static String getParamByType(ActionParameterTypes types, ScenarioConfigAction config,
      final Map<String, String> params) {
    Optional<ScenarioConfigActionParameter> scenarioParameter = config.getRequiredParameters()
        .stream()
        .filter(parameter -> types.equals(parameter.getType()))
        .findFirst();

    return scenarioParameter.map(parameter -> params.get(parameter.getName())).orElse(null);

  }

  private static ActionConnectToAirbyteAPI getActionConnectToAirbyteAPI(int order,
      ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return ActionConnectToAirbyteAPI
        .builder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .airbyteInstance(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap,
            AirbyteInstance.class))
        .build();
  }

  private static ActionCreateSource getActionCreateSource(int order, ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return ActionCreateSource
        .builder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .airbyteInstance(getInstanceByType(AIRBYTE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteInstance.class))
        .sourceInstance(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap,
            SourceInstance.class))
        .build();
  }

  private static ActionCreateSourceWithConnectionSettings getActionCreateSourceWithSettings(int order, ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return ActionCreateSourceWithConnectionSettings
        .actionCreateSourceWithConnectionSettingsBuilder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .airbyteInstance(getInstanceByType(AIRBYTE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteInstance.class))
        .sourceInstance(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap,
            SourceWithSettingsInstance.class))
        .build();
  }

  private static ActionCreateDestination getActionCreateDestination(int order,
      ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return ActionCreateDestination
        .builder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .airbyteInstance(getInstanceByType(AIRBYTE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteInstance.class))
        .destinationInstance(
            getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap,
                DestinationInstance.class))
        .build();
  }

  private static ActionCreateConnection getActionCreateConnection(int order,
      ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return ActionCreateConnection
        .builder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .airbyteInstance(getInstanceByType(AIRBYTE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteInstance.class))
        .destinationInstance(
            getInstanceByType(DESTINATION, config.getRequiredInstances(),
                scenarioInstanceNameToInstanceMap, DestinationInstance.class))
        .sourceInstance(getInstanceByType(SOURCE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, SourceInstance.class))
        .connection(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap,
            AirbyteConnection.class))
        .build();
  }

  private static ActionCreateConnection getActionCreateConnectionCustom(int order,
      ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return ActionCreateConnectionCustom
        .actionCreateConnectionCustomBuilder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .airbyteInstance(getInstanceByType(AIRBYTE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteInstance.class))
        .destinationInstance(
            getInstanceByType(DESTINATION, config.getRequiredInstances(),
                scenarioInstanceNameToInstanceMap, DestinationInstance.class))
        .sourceInstance(getInstanceByType(SOURCE_WITH_CONNECTION_SETTINGS, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, SourceWithSettingsInstance.class))
        .connection(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap,
            AirbyteConnection.class))
        .build();
  }

  private static ActionSyncConnection getActionSyncConnection(int order,
      ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return ActionSyncConnection
        .builder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .airbyteInstance(getInstanceByType(AIRBYTE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteInstance.class))
        .connection(getInstanceByType(CONNECTION, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteConnection.class))
        .build();
  }

  private static ActionResetConnection getActionResetConnection(int order,
      ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return ActionResetConnection
        .builder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .airbyteInstance(getInstanceByType(AIRBYTE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteInstance.class))
        .connection(getInstanceByType(CONNECTION, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteConnection.class))
        .build();
  }


  private static ScenarioAction getActionUpdateSourceVersion(int order, ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap, final Map<String, String> params) {
    return ActionUpdateSourceVersion
        .builder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .airbyteInstance(getInstanceByType(AIRBYTE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteInstance.class))
        .sourceInstance(getInstanceByType(SOURCE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, SourceInstance.class))
        .version(getParamByType(SOURCE_VERSION, config, params))
        .build();
  }

  private static ScenarioAction getActionUpdateDestinationVersion(int order,
      ScenarioConfigAction config, Map<String, Instance> scenarioInstanceNameToInstanceMap,
      final Map<String, String> params) {
    return ActionUpdateDestinationVersion
        .builder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(getInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .airbyteInstance(getInstanceByType(AIRBYTE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteInstance.class))
        .destinationInstance(
            getInstanceByType(DESTINATION, config.getRequiredInstances(),
                scenarioInstanceNameToInstanceMap, DestinationInstance.class))
        .version(getParamByType(DESTINATION_VERSION, config, params))
        .build();
  }

}
