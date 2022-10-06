package io.airbyte.testingtool.metrics;

import io.airbyte.api.client.model.generated.AttemptStats;
import io.airbyte.api.client.model.generated.AttemptStreamStats;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Metrics {
  private long startTime;
  private long endTime;
  private long bytes;
  private long records;
  private List<AttemptStreamStats> streamStats;
  private AttemptStats totalStats;

  @Override
  public String toString() {
    long time = (endTime - startTime);
    return "time (ms): " + time +
        "\nbytes=" + bytes +
        "\nrecords=" + records +
        "\nthroughput(bytes)=" + bytes/(double)time +
        "\nthroughput(records)=" + records/(double)time;
  }
}