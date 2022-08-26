package io.airbyte.testingtool.scenario.action;

import static io.airbyte.testingtool.scenario.config.ActionParameterTypes.DESTINATION_VERSION;
import static io.airbyte.testingtool.scenario.config.ActionParameterTypes.SOURCE_VERSION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.AIRBYTE;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.CONNECTION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.DESTINATION;
import static io.airbyte.testingtool.scenario.instance.InstanceTypes.SOURCE;

import io.airbyte.testingtool.scenario.config.ActionParameterTypes;
import io.airbyte.testingtool.scenario.instance.InstanceTypes;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

public enum Actions {

  CONNECT_AIRBYTE_API(null, AIRBYTE, null),
  RESET_CONNECTION(List.of(AIRBYTE, CONNECTION), null, null),
  SYNC_CONNECTION(List.of(AIRBYTE, CONNECTION), null, null),
  CREATE_SOURCE(List.of(AIRBYTE), SOURCE, null),
  CREATE_DESTINATION(List.of(AIRBYTE), DESTINATION, null),
  UPDATE_SOURCE_VERSION(List.of(AIRBYTE, SOURCE), null, List.of(SOURCE_VERSION)),
  UPDATE_DESTINATION_VERSION(List.of(AIRBYTE, DESTINATION), null, List.of(DESTINATION_VERSION)),
  CREATE_CONNECTION(List.of(AIRBYTE, SOURCE, DESTINATION), CONNECTION, null);

  @Getter
  private final List<InstanceTypes> requiredInstances;
  @Getter
  private final InstanceTypes resultInstance;
  @Getter
  private final List<ActionParameterTypes> requiredParameters;

  Actions(List<InstanceTypes> requiredInstances, InstanceTypes resultInstance,
      List<ActionParameterTypes> requiredParameters) {
    this.requiredInstances = (requiredInstances == null ? Collections.emptyList()
        : requiredInstances);
    this.resultInstance = resultInstance;
    this.requiredParameters = (requiredParameters == null ? Collections.emptyList()
        : requiredParameters);
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

}
