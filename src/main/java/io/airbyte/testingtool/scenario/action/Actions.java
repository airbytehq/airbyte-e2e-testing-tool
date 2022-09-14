package io.airbyte.testingtool.scenario.action;

import static io.airbyte.testingtool.scenario.config.ActionParameterTypes.DESTINATION_VERSION;
import static io.airbyte.testingtool.scenario.config.ActionParameterTypes.SOURCE_VERSION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.AIRBYTE;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.CONNECTION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.DESTINATION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.SOURCE;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.SOURCE_WITH_CONNECTION_SETTINGS;

import io.airbyte.testingtool.scenario.config.ActionParameterTypes;
import io.airbyte.testingtool.scenario.instance.InstanceTypes;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

public enum Actions {

  CONNECT_AIRBYTE_API(null, AIRBYTE, null, null),
  RESET_CONNECTION(List.of(AIRBYTE, CONNECTION), null, null, null),
  SYNC_CONNECTION(List.of(AIRBYTE, CONNECTION), null, null, null),
  CREATE_SOURCE(List.of(AIRBYTE), SOURCE, null, null),
  CREATE_SOURCE_WITH_CONN_SETTINGS(List.of(AIRBYTE), SOURCE_WITH_CONNECTION_SETTINGS, null, null),
  CREATE_DESTINATION(List.of(AIRBYTE), DESTINATION, null, null),
  UPDATE_SOURCE_VERSION(List.of(AIRBYTE, SOURCE), null, List.of(SOURCE_VERSION), null),
  UPDATE_DESTINATION_VERSION(List.of(AIRBYTE, DESTINATION), null, List.of(DESTINATION_VERSION), null),
  CREATE_CONNECTION(List.of(AIRBYTE, SOURCE, DESTINATION), CONNECTION, null, null),
  CREATE_CUSTOM_CONNECTION(List.of(AIRBYTE, SOURCE_WITH_CONNECTION_SETTINGS, DESTINATION), CONNECTION, null, null),
  READ_SOURCE_VERSION(List.of(AIRBYTE, SOURCE), null, null, SOURCE_VERSION);

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
