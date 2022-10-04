package io.airbyte.testingtool.metrics;

public interface TimeMetric extends Metric {

  void setStartTime(long start);
  void setEndTime(long end);
}
