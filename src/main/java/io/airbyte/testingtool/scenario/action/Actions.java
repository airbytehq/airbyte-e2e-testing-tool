package io.airbyte.testingtool.scenario.action;

import static io.airbyte.testingtool.scenario.config.scenarios.ActionParameterTypes.DESTINATION_VERSION;
import static io.airbyte.testingtool.scenario.config.scenarios.ActionParameterTypes.SOURCE_VERSION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.AIRBYTE;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.CONNECTION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.DESTINATION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.SOURCE;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.SOURCE_WITH_CONNECTION_SETTINGS;

import io.airbyte.testingtool.scenario.config.scenarios.ActionParameterTypes;
import io.airbyte.testingtool.scenario.instance.InstanceTypes;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

public enum Actions {

  // Airbyte
  CONNECT_AIRBYTE_API(null, AIRBYTE, null, null),

  // Connection
  CREATE_CONNECTION(List.of(SOURCE, DESTINATION), CONNECTION, null, null),
  CREATE_CUSTOM_CONNECTION(List.of(SOURCE_WITH_CONNECTION_SETTINGS, DESTINATION), CONNECTION, null, null),
  RESET_CONNECTION(List.of(CONNECTION), null, null, null),
  SYNC_CONNECTION(List.of(CONNECTION), null, null, null),
  DELETE_CONNECTION(List.of(CONNECTION), null, null, null),

  // Source
  CREATE_SOURCE(List.of(AIRBYTE), SOURCE, null, null),
  CREATE_SOURCE_WITH_CONN_SETTINGS(List.of(AIRBYTE), SOURCE_WITH_CONNECTION_SETTINGS, null, null),
  UPDATE_SOURCE_VERSION(List.of(SOURCE), null, List.of(SOURCE_VERSION), null),
  READ_SOURCE_VERSION(List.of(SOURCE), null, null, SOURCE_VERSION),
  DELETE_SOURCE(List.of(SOURCE), null, null, null),

  // Destination
  CREATE_DESTINATION(List.of(AIRBYTE), DESTINATION, null, null),
  UPDATE_DESTINATION_VERSION(List.of(DESTINATION), null, List.of(DESTINATION_VERSION), null),
  READ_DESTINATION_VERSION(List.of(DESTINATION), null, null, DESTINATION_VERSION),
  DELETE_DESTINATION(List.of(DESTINATION), null, null, null);

  @Getter
  private final List<InstanceTypes> requiredInstances;
  @Getter
  private final InstanceTypes resultInstance;
  @Getter
  private final List<ActionParameterTypes> requiredParameters;
  @Getter
  private final ActionParameterTypes resultParameter;

  Actions(List<InstanceTypes> requiredInstances, InstanceTypes resultInstance,
      List<ActionParameterTypes> requiredParameters, ActionParameterTypes resultParameter) {
    this.requiredInstances = (requiredInstances == null ? Collections.emptyList()
        : requiredInstances);
    this.resultInstance = resultInstance;
    this.requiredParameters = (requiredParameters == null ? Collections.emptyList()
        : requiredParameters);
    this.resultParameter = resultParameter;
  }

  public boolean isInstanceRequired() {
    return !requiredInstances.isEmpty();
  }

  public boolean isResultInstance() {
    return resultInstance != null;
  }

  public boolean isParameterRequired() {
    return !requiredParameters.isEmpty();
  }

  public boolean isResultParameter() {
    return resultInstance != null;
  }

}
