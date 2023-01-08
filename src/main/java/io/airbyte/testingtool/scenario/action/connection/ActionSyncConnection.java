package io.airbyte.testingtool.scenario.action.connection;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.AttemptInfoRead;
import io.airbyte.api.client.model.generated.AttemptStats;
import io.airbyte.api.client.model.generated.AttemptStreamStats;
import io.airbyte.api.client.model.generated.JobIdRequestBody;
import io.airbyte.api.client.model.generated.JobInfoRead;
import io.airbyte.testingtool.jobwaiter.JobWaiter;
import io.airbyte.testingtool.metrics.Metrics;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.Instance;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
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
    JobInfoRead jir = connectionInstance.getAirbyteInstance().getAirbyteApi().getConnectionApi().syncConnection(connectionInstance.getConnectionRequestBody());
    metrics = JobWaiter.waitForJobFinish(connectionInstance.getAirbyteInstance().getAirbyteApi().getJobsApi(), jir.getJob().getId());
  }
}
