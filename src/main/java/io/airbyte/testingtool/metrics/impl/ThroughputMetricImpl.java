package io.airbyte.testingtool.metrics.impl;

import io.airbyte.testingtool.metrics.ThroughputMetric;

public class ThroughputMetricImpl extends MetricImpl implements ThroughputMetric {

  private double throughput;

  @Override
  public void setThroughput(double throughput) {
    this.throughput = throughput;
  }

  @Override
  public String toString() {
    return String.valueOf(throughput);
  }
}
