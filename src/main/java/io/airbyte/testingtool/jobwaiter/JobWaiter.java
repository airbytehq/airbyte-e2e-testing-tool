package io.airbyte.testingtool.jobwaiter;

import static io.airbyte.api.client.model.generated.JobStatus.CANCELLED;
import static io.airbyte.api.client.model.generated.JobStatus.FAILED;
import static io.airbyte.api.client.model.generated.JobStatus.SUCCEEDED;

import io.airbyte.api.client.generated.JobsApi;
import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.JobIdRequestBody;
import io.airbyte.api.client.model.generated.JobRead;
import io.airbyte.api.client.model.generated.JobStatus;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobWaiter {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobWaiter.class);
  private static final long defaultWaitMillis = TimeUnit.SECONDS.toMillis(1);
  private static final long defaultTimeOutMillis = TimeUnit.MINUTES.toMillis(30);

  public static void waitForJobFinish(JobsApi jobApi, long airbyteJobId) throws InterruptedException, ApiException {
    waitForJobFinish(jobApi, airbyteJobId, defaultWaitMillis);
  }

  public static void waitForJobFinish(JobsApi jobApi, long airbyteJobId, long checkEveryMillis) throws InterruptedException, ApiException {
    waitForJobFinish(jobApi, airbyteJobId, checkEveryMillis, defaultTimeOutMillis);
  }

  public static void waitForJobFinish(JobsApi jobApi, long airbyteJobId, long checkEveryMillis, long timeOutMillis)
      throws InterruptedException, ApiException {
    LOGGER.info("Waiting for job id : {}", airbyteJobId);
    JobStatus resultStatus;
    long count = 0;
    while (true) {
      var job = getJobRead(jobApi, airbyteJobId);
      if (Arrays.asList(FAILED, SUCCEEDED, CANCELLED).contains(job.getStatus())) {
        resultStatus = job.getStatus();
        break;
      } else {
        Thread.sleep(checkEveryMillis);
        count += checkEveryMillis;
        if (count >= timeOutMillis) {
          throw new RuntimeException("Job execution timeout. Job details : \n" + job);
        }
      }
    }
    LOGGER.info("Job id {} finished with status {}", airbyteJobId, resultStatus);

    if (resultStatus != SUCCEEDED) {
      throw new RuntimeException("Job id " + airbyteJobId + " failed with status " + resultStatus);
    }
  }

  private static JobRead getJobRead(JobsApi jobApi, long airbyteJobId) throws ApiException {
    return jobApi.getJobInfo(new JobIdRequestBody().id(airbyteJobId)).getJob();
  }

}
