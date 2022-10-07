package io.airbyte.testingtool.metrics;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.airbyte.api.client.model.generated.AttemptStats;
import io.airbyte.api.client.model.generated.AttemptStreamStats;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Metrics implements Serializable {
  private static final long serialVersionUID = 1L;

  private long startTime;
  private long endTime;
  private long bytes;
  private long records;
  private List<AttemptStreamStats> streamStats;
  private AttemptStats totalStats;

  @Override
  public String toString() {
    long time = (endTime - startTime);
    double throughputBytes = time == 0 ? 0 : 1000*bytes/(double)time;
    double throughputRecords = time == 0 ? 0 : 1000*records/(double)time;

    StringBuffer scalabilityMetrics = new StringBuffer();

    if (streamStats != null) {
      for (AttemptStreamStats ass : streamStats) {
        scalabilityMetrics.append("Stream: " + ass.getStreamName())
            .append("\n    recordsEmitted: ").append(ass.getStats().getRecordsEmitted())
            .append("\n    bytesEmitted: ").append(ass.getStats().getBytesEmitted())
            .append("\n    stateMessagesEmitted: ").append(ass.getStats().getStateMessagesEmitted())
            .append("\n    recordsCommitted: ").append(ass.getStats().getRecordsCommitted());
      }
    }

    String performanceMetric = totalStats == null ? "" : new StringBuffer()
        .append("\n    recordsEmitted: ").append(totalStats.getRecordsEmitted())
        .append("\n    bytesEmitted: ").append(totalStats.getBytesEmitted())
        .append("\n    stateMessagesEmitted: ").append(totalStats.getStateMessagesEmitted())
        .append("\n    recordsCommitted: ").append(totalStats.getRecordsCommitted()).toString();

    StringBuffer result = new StringBuffer("time (ms): " + time +
        "\nbytes=" + bytes +
        "\nrecords=" + records +
        "\nthroughput(bytes/sec)=" +  throughputBytes +
        "\nthroughput(records/sec)=" + throughputRecords);

    if (streamStats != null) {
      result.append("\nscalability:\n==========\n" + scalabilityMetrics.toString() +
          "\n==========");
    }

    if (totalStats != null) {
      result.append("\nperformance=" + performanceMetric.toString());
    }

    return result.toString();
  }
}