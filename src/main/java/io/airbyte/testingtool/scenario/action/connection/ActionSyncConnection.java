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

  private long syncStart;
  private long syncEnd;
  private long bytes;
  private long records;
  private List<AttemptStreamStats> streamStats;
  private AttemptStats totalStats;

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
    metrics = Metrics.builder().bytes(bytes).records(records).startTime(syncStart).endTime(syncEnd).streamStats(streamStats)
        .totalStats(totalStats).build();
  }

  private void sync() throws ApiException, InterruptedException {
    JobInfoRead jir = connectionInstance.getAirbyteInstance().getAirbyteApi().getConnectionApi().syncConnection(connectionInstance.getConnectionRequestBody());
    JobWaiter.waitForJobFinish(connectionInstance.getAirbyteInstance().getAirbyteApi().getJobsApi(), jir.getJob().getId());
    JobInfoRead finishedJir = connectionInstance.getAirbyteInstance().getAirbyteApi().getJobsApi().getJobInfo(new JobIdRequestBody().id(jir.getJob().getId()));
    List<AttemptInfoRead> attempts = finishedJir.getAttempts();
    AttemptInfoRead lastAttempt = attempts.get(attempts.size() - 1);

    syncStart = lastAttempt.getAttempt().getCreatedAt();
    syncEnd = lastAttempt.getAttempt().getEndedAt();
    bytes = lastAttempt.getAttempt().getBytesSynced();
    records = lastAttempt.getAttempt().getRecordsSynced();
    streamStats = lastAttempt.getAttempt().getStreamStats();
    totalStats = lastAttempt.getAttempt().getTotalStats();
  }
}
