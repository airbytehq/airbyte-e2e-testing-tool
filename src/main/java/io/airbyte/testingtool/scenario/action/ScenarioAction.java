package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.instance.Instance;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ScenarioAction implements Comparable<ScenarioAction> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioAction.class);

  protected final int order;
  private final List<Instance> requiredInstances;
  private final Instance resultInstance;
  private String resultSummary = "Not executed";
  private boolean isExecuted = false;

  public ScenarioAction(int order, List<Instance> requiredInstances, Instance resultInstance) {
    this.order = order;
    this.requiredInstances = requiredInstances;
    this.resultInstance = resultInstance;
  }

  public int getOrder() {
    return order;
  }

  @Override
  public int compareTo(ScenarioAction o) {
    return Integer.compare(order, o.getOrder());
  }

  public boolean doAction() {
    boolean result = true;
    if (isRepeatable() || !isExecuted) {
      try {
        checkAllRequiredInstancesInitialized();
        doActionInternal();
        resultSummary = getSuccessfulExecutionSummary();
        resultInstance.setInitialized(true);
      } catch (Exception e) {
        resultSummary = "Execution failed with exception : " + e.getMessage();
        result = false;
      }
      isExecuted = true;
    } else {
      LOGGER.error("Action {} can't be executed one more time!", getActionName());
      result = false;
    }
    return result;
  }

  private void checkAllRequiredInstancesInitialized() {
    var notInitializedInstances = requiredInstances.stream()
        .filter(instance -> instance.getInstanceType().isInitializationIsRequired() && !instance.isInitialized()).map(Instance::getInstanceName)
        .toList();
    if (!notInitializedInstances.isEmpty()) {
      throw new RuntimeException("Not all required instances are initialized : " + String.join(", ", notInitializedInstances));
    }
  }

  protected String getSuccessfulExecutionSummary() {
    return "Ok";
  }

  protected abstract void doActionInternal() throws Exception;

  public abstract String getActionName();

  public boolean isRepeatable() {
    return true;
  }

  public String getResultSummary() {
    return resultSummary;
  }

}
