package io.airbyte.testingtool.metrics;

public interface ThroughputMetric extends Metric {
  void setThroughput(double throughput);
}
