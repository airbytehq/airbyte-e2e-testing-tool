package io.airbyte.testingtool.metrics.impl;

import io.airbyte.testingtool.metrics.Metric;

public abstract class MetricImpl implements Metric {

  @Override
  public String getStringValue() {
    return toString();
  }
}
