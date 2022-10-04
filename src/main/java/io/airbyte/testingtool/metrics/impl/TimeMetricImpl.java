package io.airbyte.testingtool.metrics.impl;

import io.airbyte.testingtool.metrics.TimeMetric;

public class TimeMetricImpl extends MetricImpl implements TimeMetric {
  private long startTime;
  private long endTime;

  @Override
  public void setStartTime(long start) {
    this.startTime = start;
  }

  @Override
  public void setEndTime(long end) {
    this.endTime = end;
  }

  @Override
  public String toString() {
    return String.valueOf(endTime - startTime);
  }
}
