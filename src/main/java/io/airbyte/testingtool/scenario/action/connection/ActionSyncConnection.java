package io.airbyte.testingtool.scenario.action.connection;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.testingtool.jobwaiter.JobWaiter;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.Instance;
import java.util.List;
import lombok.Builder;

public class ActionSyncConnection extends AbstractConnectionAction {

  @Builder
  public ActionSyncConnection(int order, List<Instance> requiredInstances, Instance resultInstance, AirbyteConnection connection) {
    super(order, requiredInstances, resultInstance, connection);
  }

  @Override
  public String getActionName() {
    return "Sync Connection";
  }

  @Override
  public void doActionInternal() throws ApiException, InterruptedException {
    sync();
  }

  private void sync() throws ApiException, InterruptedException {
    JobWaiter.waitForJobFinish(connectionInstance.getAirbyteInstance().getAirbyteApi().getJobsApi(),
        connectionInstance.getAirbyteInstance().getAirbyteApi().getConnectionApi().syncConnection(connectionInstance.getConnectionRequestBody())
            .getJob().getId());
  }

}
