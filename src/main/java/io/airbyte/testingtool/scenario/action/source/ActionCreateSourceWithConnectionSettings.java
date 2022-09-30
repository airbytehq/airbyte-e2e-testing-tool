package io.airbyte.testingtool.scenario.action.source;

import io.airbyte.testingtool.scenario.instance.AirbyteApiInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceWithSettingsInstance;
import java.util.List;
import lombok.Builder;

public class ActionCreateSourceWithConnectionSettings extends ActionCreateSource {

  @Builder(builderMethodName = "actionCreateSourceWithConnectionSettingsBuilder")
  public ActionCreateSourceWithConnectionSettings(int order, List<Instance> requiredInstances,
      Instance resultInstance, AirbyteApiInstance airbyteApiInstance,
      SourceWithSettingsInstance sourceInstance) {
    super(order, requiredInstances, resultInstance, sourceInstance, airbyteApiInstance);
  }

  @Override
  public String getActionName() {
    return "Create source with connection settings";
  }

}
