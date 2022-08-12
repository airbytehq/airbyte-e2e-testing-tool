package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.config.ScenarioConfigAction;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import java.util.Map;

public class ActionFactory {

  public static ScenarioAction getScenarioAction(int order, ScenarioConfigAction config, Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    return switch (config.getAction()) {
      case RESET_CONNECTION -> getActionResetConnection(order, config, scenarioInstanceNameToInstanceMap);
      case SYNC_CONNECTION -> getActionSyncConnection(order, config, scenarioInstanceNameToInstanceMap);
      case CREATE_SOURCE -> getActionCreateSource(order, config, scenarioInstanceNameToInstanceMap);
      case CREATE_DESTINATION -> getActionCreateDestination(order, config, scenarioInstanceNameToInstanceMap);
      case CREATE_CONNECTION -> getActionCreateConnection(order, config, scenarioInstanceNameToInstanceMap);
    };
  }

  private static ActionCreateSource getActionCreateSource(int order, ScenarioConfigAction config, Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    var builder = ActionCreateSource.builder().order(order);
    config.getRequiredInstances().forEach(scenarioConfigInstance -> {
      switch (scenarioConfigInstance.getInstanceType()) {
        case SOURCE -> builder.sourceInstance((SourceInstance) scenarioInstanceNameToInstanceMap.get(scenarioConfigInstance.getInstanceName()));
        case AIRBYTE -> builder.airbyteInstance((AirbyteInstance) scenarioInstanceNameToInstanceMap.get(scenarioConfigInstance.getInstanceName()));
      }
    });

    return builder.build();
  }

  private static ActionCreateDestination getActionCreateDestination(int order, ScenarioConfigAction config, Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    var builder = ActionCreateDestination.builder().order(order);
    config.getRequiredInstances().forEach(scenarioConfigInstance -> {
      switch (scenarioConfigInstance.getInstanceType()) {
        case DESTINATION -> builder.destinationInstance((DestinationInstance) scenarioInstanceNameToInstanceMap.get(scenarioConfigInstance.getInstanceName()));
        case AIRBYTE -> builder.airbyteInstance((AirbyteInstance) scenarioInstanceNameToInstanceMap.get(scenarioConfigInstance.getInstanceName()));
      }
    });

    return builder.build();
  }

  private static ActionCreateConnection getActionCreateConnection(int order, ScenarioConfigAction config, Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    var builder = ActionCreateConnection.builder().order(order);
    config.getRequiredInstances().forEach(scenarioConfigInstance -> {
      switch (scenarioConfigInstance.getInstanceType()) {
        case DESTINATION -> builder.destinationInstance((DestinationInstance) scenarioInstanceNameToInstanceMap.get(scenarioConfigInstance.getInstanceName()));
        case SOURCE -> builder.sourceInstance((SourceInstance) scenarioInstanceNameToInstanceMap.get(scenarioConfigInstance.getInstanceName()));
        case CONNECTION -> builder.connection((AirbyteConnection) scenarioInstanceNameToInstanceMap.get(scenarioConfigInstance.getInstanceName()));
        case AIRBYTE -> builder.airbyteInstance((AirbyteInstance) scenarioInstanceNameToInstanceMap.get(scenarioConfigInstance.getInstanceName()));
      }
    });

    return builder.build();
  }

  private static ActionSyncConnection getActionSyncConnection(int order, ScenarioConfigAction config, Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    var builder = ActionSyncConnection.builder().order(order);
    config.getRequiredInstances().forEach(scenarioConfigInstance -> {
      switch (scenarioConfigInstance.getInstanceType()) {
        case CONNECTION -> builder.connection((AirbyteConnection) scenarioInstanceNameToInstanceMap.get(scenarioConfigInstance.getInstanceName()));
        case AIRBYTE -> builder.airbyteInstance((AirbyteInstance) scenarioInstanceNameToInstanceMap.get(scenarioConfigInstance.getInstanceName()));
      }
    });

    return builder.build();
  }

  private static ActionResetConnection getActionResetConnection(int order, ScenarioConfigAction config, Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    var builder = ActionResetConnection.builder().order(order);
    config.getRequiredInstances().forEach(scenarioConfigInstance -> {
      switch (scenarioConfigInstance.getInstanceType()) {
        case CONNECTION -> builder.connection((AirbyteConnection) scenarioInstanceNameToInstanceMap.get(scenarioConfigInstance.getInstanceName()));
        case AIRBYTE -> builder.airbyteInstance((AirbyteInstance) scenarioInstanceNameToInstanceMap.get(scenarioConfigInstance.getInstanceName()));
      }
    });

    return builder.build();
  }

}
