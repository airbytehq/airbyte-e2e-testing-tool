package io.airbyte.testingtool.scenario.action;

import static io.airbyte.testingtool.scenario.config.scenarios.ActionParameterTypes.DESTINATION_VERSION;
import static io.airbyte.testingtool.scenario.config.scenarios.ActionParameterTypes.SOURCE_VERSION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.AIRBYTE;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.CONNECTION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.DESTINATION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.SOURCE;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.SOURCE_WITH_CONNECTION_SETTINGS;

import io.airbyte.testingtool.scenario.action.airbyte.ActionConnectToAirbyteAPI;
import io.airbyte.testingtool.scenario.action.connection.ActionCreateConnection;
import io.airbyte.testingtool.scenario.action.connection.ActionCreateConnectionCustom;
import io.airbyte.testingtool.scenario.action.connection.ActionResetConnection;
import io.airbyte.testingtool.scenario.action.connection.ActionSyncConnection;
import io.airbyte.testingtool.scenario.action.destination.ActionCreateDestination;
import io.airbyte.testingtool.scenario.action.destination.ActionReadDestinationVersion;
import io.airbyte.testingtool.scenario.action.destination.ActionUpdateDestinationVersion;
import io.airbyte.testingtool.scenario.action.source.ActionCreateSource;
import io.airbyte.testingtool.scenario.action.source.ActionCreateSourceWithConnectionSettings;
import io.airbyte.testingtool.scenario.action.source.ActionReadSourceVersion;
import io.airbyte.testingtool.scenario.action.source.ActionUpdateSourceVersion;
import io.airbyte.testingtool.scenario.config.scenarios.ActionParameterTypes;
import io.airbyte.testingtool.scenario.config.scenarios.ScenarioConfigAction;
import io.airbyte.testingtool.scenario.config.scenarios.ScenarioConfigActionParameter;
import io.airbyte.testingtool.scenario.instance.AirbyteApiInstance;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.InstanceTypes;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import io.airbyte.testingtool.scenario.instance.SourceWithSettingsInstance;
import io.airbyte.testingtool.scenario.parameter.ScenarioParameter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class ActionFactory {

  /**
   * Builds a scenario action using scenario config, provided credentials and parameters.
   *
   * @param order                             sequence is very important for actions.
   * @param config                            scenario config file describes all logical instances and actions without any credentials.
   * @param scenarioInstanceNameToInstanceMap mapping of instances built using provided credentials.
   * @param params                            action special parameters.
   * @return scenario action with all required instances.
   */
  public static ScenarioAction getScenarioAction(int order, ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap,
      final Map<String, ScenarioParameter> params) {
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
      case READ_SOURCE_VERSION -> getActionReadSourceVersion(order, config, scenarioInstanceNameToInstanceMap, params);
      case READ_DESTINATION_VERSION -> getActionReadDestinationVersion(order, config, scenarioInstanceNameToInstanceMap, params);
      case DELETE_SOURCE -> null;
      case DELETE_CONNECTION -> null;
      case DELETE_DESTINATION -> null;
    };
  }

  private static Instance linkInstance(String instanceName,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return linkInstance(instanceName, scenarioInstanceNameToInstanceMap, Instance.class);
  }

  private static <T extends Instance> T linkInstance(String instanceName,
      Map<String, Instance> scenarioInstanceNameToInstanceMap, Class<T> clazz) {
    return clazz.cast(scenarioInstanceNameToInstanceMap.get(instanceName));
  }

  private static <T extends Instance> T linkInstanceByType(InstanceTypes type,
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

  private static List<Instance> getRequiredInstances(ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return scenarioInstanceNameToInstanceMap.entrySet().stream()
        .filter(
            nameInstanceEntry -> config.getRequiredInstances().contains(nameInstanceEntry.getKey()))
        .map(
            Entry::getValue).toList();
  }

  private static ScenarioParameter linkParam(ScenarioConfigActionParameter scenarioConfigActionParameter,
      final Map<String, ScenarioParameter> incomingParameters) {
    return incomingParameters.get(scenarioConfigActionParameter.getName());
  }

  private static ScenarioParameter linkParamByType(ActionParameterTypes type, List<ScenarioConfigActionParameter> scenarioConfigActionParameters,
      final Map<String, ScenarioParameter> incomingParameters) {
    Optional<ScenarioConfigActionParameter> scenarioConfigParameter = scenarioConfigActionParameters
        .stream()
        .filter(parameter -> type.equals(parameter.getType()))
        .findFirst();
    return incomingParameters.get(scenarioConfigParameter.get().getName());
  }

  private static ActionConnectToAirbyteAPI getActionConnectToAirbyteAPI(int order,
      ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return ActionConnectToAirbyteAPI
        .builder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .airbyteApiInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap,
            AirbyteApiInstance.class))
        .build();
  }

  private static ActionCreateSource getActionCreateSource(int order, ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return ActionCreateSource
        .builder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .airbyteApiInstance(linkInstanceByType(AIRBYTE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteApiInstance.class))
        .sourceInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap,
            SourceInstance.class))
        .build();
  }

  private static ActionCreateSourceWithConnectionSettings getActionCreateSourceWithSettings(int order, ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return ActionCreateSourceWithConnectionSettings
        .actionCreateSourceWithConnectionSettingsBuilder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .airbyteApiInstance(linkInstanceByType(AIRBYTE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteApiInstance.class))
        .sourceInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap,
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
        .resultInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .airbyteApiInstance(linkInstanceByType(AIRBYTE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteApiInstance.class))
        .destinationInstance(
            linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap,
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
        .resultInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .destinationInstance(
            linkInstanceByType(DESTINATION, config.getRequiredInstances(),
                scenarioInstanceNameToInstanceMap, DestinationInstance.class))
        .sourceInstance(linkInstanceByType(SOURCE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, SourceInstance.class))
        .connectionInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap,
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
        .resultInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .destinationInstance(
            linkInstanceByType(DESTINATION, config.getRequiredInstances(),
                scenarioInstanceNameToInstanceMap, DestinationInstance.class))
        .sourceInstance(linkInstanceByType(SOURCE_WITH_CONNECTION_SETTINGS, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, SourceWithSettingsInstance.class))
        .connectionInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap,
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
        .resultInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .connection(linkInstanceByType(CONNECTION, config.getRequiredInstances(),
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
        .resultInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .connection(linkInstanceByType(CONNECTION, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, AirbyteConnection.class))
        .build();
  }


  private static ScenarioAction getActionUpdateSourceVersion(int order, ScenarioConfigAction config,
      Map<String, Instance> scenarioInstanceNameToInstanceMap, final Map<String, ScenarioParameter> params) {
    return ActionUpdateSourceVersion
        .builder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .sourceInstance(linkInstanceByType(SOURCE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, SourceInstance.class))
        .version(linkParamByType(SOURCE_VERSION, config.getRequiredParameters(), params))
        .build();
  }

  private static ScenarioAction getActionUpdateDestinationVersion(int order,
      final ScenarioConfigAction config, final Map<String, Instance> scenarioInstanceNameToInstanceMap,
      final Map<String, ScenarioParameter> params) {
    return ActionUpdateDestinationVersion
        .builder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .destinationInstance(
            linkInstanceByType(DESTINATION, config.getRequiredInstances(),
                scenarioInstanceNameToInstanceMap, DestinationInstance.class))
        .version(linkParamByType(DESTINATION_VERSION, config.getRequiredParameters(), params))
        .build();
  }

  private static ScenarioAction getActionReadSourceVersion(int order,
      final ScenarioConfigAction config, final Map<String, Instance> scenarioInstanceNameToInstanceMap, final Map<String, ScenarioParameter> params) {
    return ActionReadSourceVersion
        .builder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .sourceInstance(linkInstanceByType(SOURCE, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, SourceInstance.class))
        .version(linkParam(config.getResultParameter(), params))
        .build();
  }

  private static ScenarioAction getActionReadDestinationVersion(int order,
      final ScenarioConfigAction config, final Map<String, Instance> scenarioInstanceNameToInstanceMap, final Map<String, ScenarioParameter> params) {
    return ActionReadDestinationVersion
        .builder()
        .order(order)
        .requiredInstances(getRequiredInstances(config, scenarioInstanceNameToInstanceMap))
        .resultInstance(linkInstance(config.getResultInstance(), scenarioInstanceNameToInstanceMap))
        .destinationInstance(linkInstanceByType(DESTINATION, config.getRequiredInstances(),
            scenarioInstanceNameToInstanceMap, DestinationInstance.class))
        .version(linkParam(config.getResultParameter(), params))
        .build();
  }

}
