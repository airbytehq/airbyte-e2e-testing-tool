package io.airbyte.testingtool.scenario.action.source;

import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import java.util.List;
import lombok.Builder;

public class ActionDeleteSource extends AbstractSourceAction {

  @Builder
  public ActionDeleteSource(int order, List<Instance> requiredInstances,
      Instance resultInstance, SourceInstance sourceInstance) {
    super(order, requiredInstances, resultInstance, sourceInstance);
  }

  @Override
  protected void doActionInternal() throws Exception {
    sourceInstance.getAirbyteApiInstance().getAirbyteApi().getSourceApi().deleteSource(sourceInstance.getSourceIdRequestBody());
  }

  @Override
  public String getActionName() {
    return "Delete source";
  }
}
