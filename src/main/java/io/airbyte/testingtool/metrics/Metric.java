package io.airbyte.testingtool.metrics;

public interface Metric {
  String TIME_METRIC = "time";
  String THROUGHPUT_METRIC = "throughput";

  String getStringValue();
}
