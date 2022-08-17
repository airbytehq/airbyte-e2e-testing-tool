package io.airbyte.testingtool.scenario.action;

import static io.airbyte.api.client.model.generated.JobStatus.CANCELLED;
import static io.airbyte.api.client.model.generated.JobStatus.FAILED;
import static io.airbyte.api.client.model.generated.JobStatus.SUCCEEDED;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.ConnectionIdRequestBody;
import io.airbyte.api.client.model.generated.JobInfoRead;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionSyncConnection extends ScenarioAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionSyncConnection.class);

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
    waitForJobFinish(airbyteInstance.getAirbyteApi().getConnectionApi().syncConnection(connectionIdRequestBody));
  }

  private void waitForJobFinish(JobInfoRead jobInfoRead) throws InterruptedException {
    var job = jobInfoRead.getJob();
    LOGGER.info("Waiting job {} finish", job);
    while (!Arrays.asList(FAILED, SUCCEEDED, CANCELLED).contains(job.getStatus())) {
      this.wait(100);
    }
    LOGGER.info("Job {} finished", job);
  }
}
