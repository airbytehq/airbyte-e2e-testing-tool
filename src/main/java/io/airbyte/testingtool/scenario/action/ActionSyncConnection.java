package io.airbyte.testingtool.scenario.action;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.ConnectionIdRequestBody;
import io.airbyte.api.client.model.generated.JobInfoRead;
import io.airbyte.testingtool.jobwaiter.JobWaiter;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import java.util.List;
import lombok.Builder;

public class ActionSyncConnection extends ScenarioAction {


  private final AirbyteConnection connection;
  private final AirbyteInstance airbyteInstance;

  @Builder
  public ActionSyncConnection(int order, List<Instance> requiredInstances, Instance resultInstance, AirbyteConnection connection,
      AirbyteInstance airbyteInstance) {
    super(order, requiredInstances, resultInstance);
    this.connection = connection;
    this.airbyteInstance = airbyteInstance;
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
    ConnectionIdRequestBody connectionIdRequestBody = new ConnectionIdRequestBody();
    connectionIdRequestBody.setConnectionId(connection.getConnectionId());
    JobInfoRead jobInfo = airbyteInstance.getAirbyteApi().getConnectionApi().syncConnection(connectionIdRequestBody);

    JobWaiter.waitForJobFinish(airbyteInstance.getAirbyteApi().getJobsApi(),
        jobInfo.getJob().getId());

    List<String> syncLog = jobInfo.getAttempts().get(jobInfo.getAttempts().size() - 1).getLogs().getLogLines();

    for (String logLine: syncLog) {
      if (logLine.startsWith("METRIC_SYNC_START_TIME")) {
        System.out.println("SYNC START RECORD:\n" + logLine);
      }
      else if (logLine.startsWith("METRIC_SYNC_END_TIME")) {
        System.out.println("SYNC END RECORD:\n" + logLine);
      }
    }
  }
}
