package io.airbyte.testingtool.scenario.action.connection;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.testingtool.jobwaiter.JobWaiter;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.Instance;
import java.util.List;
import lombok.Builder;

public class ActionResetConnection extends AbstractConnectionAction {

  @Builder
  public ActionResetConnection(int order, List<Instance> requiredInstances, Instance resultInstance, AirbyteConnection connection) {
    super(order, requiredInstances, resultInstance, connection);
  }

  @Override
  public String getActionName() {
    return "Reset Connection";
  }

  @Override
  public void doActionInternal() throws ApiException, InterruptedException {
    reset();
  }

  private void reset() throws ApiException, InterruptedException {
    JobWaiter.waitForJobFinish(connectionInstance.getAirbyteInstance().getAirbyteApi().getJobsApi(),
        connectionInstance.getAirbyteInstance().getAirbyteApi().getConnectionApi().resetConnection(connectionInstance.getConnectionRequestBody())
            .getJob().getId());
  }

}
