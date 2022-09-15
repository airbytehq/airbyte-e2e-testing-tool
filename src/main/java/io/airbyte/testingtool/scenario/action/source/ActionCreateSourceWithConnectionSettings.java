package io.airbyte.testingtool.scenario.action.source;

import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceWithSettingsInstance;
import java.util.List;
import lombok.Builder;

public class ActionCreateSourceWithConnectionSettings extends ActionCreateSource {

  @Builder(builderMethodName = "actionCreateSourceWithConnectionSettingsBuilder")
  public ActionCreateSourceWithConnectionSettings(int order, List<Instance> requiredInstances,
      Instance resultInstance, AirbyteInstance airbyteInstance,
      SourceWithSettingsInstance sourceInstance) {
    super(order, requiredInstances, resultInstance, airbyteInstance, sourceInstance);
  }

  @Override
  public String getActionName() {
    return "Create source with connection settings";
  }

}
