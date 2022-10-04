package io.airbyte.testingtool.scenario.action.connection;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.AttemptInfoRead;
import io.airbyte.api.client.model.generated.JobIdRequestBody;
import io.airbyte.api.client.model.generated.JobInfoRead;
import io.airbyte.testingtool.jobwaiter.JobWaiter;
import io.airbyte.testingtool.metrics.Metric;
import io.airbyte.testingtool.metrics.ThroughputMetric;
import io.airbyte.testingtool.metrics.TimeMetric;
import io.airbyte.testingtool.metrics.impl.ThroughputMetricImpl;
import io.airbyte.testingtool.metrics.impl.TimeMetricImpl;
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
    JobInfoRead jir = connectionInstance.getAirbyteInstance().getAirbyteApi().getConnectionApi().syncConnection(connectionInstance.getConnectionRequestBody());
    JobWaiter.waitForJobFinish(connectionInstance.getAirbyteInstance().getAirbyteApi().getJobsApi(), jir.getJob().getId());
    JobInfoRead finishedJir = connectionInstance.getAirbyteInstance().getAirbyteApi().getJobsApi().getJobInfo(new JobIdRequestBody().id(jir.getJob().getId()));
    List<AttemptInfoRead> attempts = finishedJir.getAttempts();
    AttemptInfoRead lastAttempt = attempts.get(attempts.size() - 1);

    long syncStart = lastAttempt.getAttempt().getCreatedAt();
    long syncEnd = lastAttempt.getAttempt().getEndedAt();
    long bytes = lastAttempt.getAttempt().getBytesSynced();

    TimeMetric timeMetric = new TimeMetricImpl();
    timeMetric.setStartTime(syncStart);
    timeMetric.setEndTime(syncEnd);

    double throughput = bytes/(double)(syncEnd - syncStart);
    ThroughputMetric throughputMetric = new ThroughputMetricImpl();
    throughputMetric.setThroughput(throughput);

    setMetric(Metric.TIME_METRIC, timeMetric);
    setMetric(Metric.THROUGHPUT_METRIC, throughputMetric);
  }

}
