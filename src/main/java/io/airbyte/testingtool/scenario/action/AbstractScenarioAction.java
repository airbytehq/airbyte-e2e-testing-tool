package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.TestingTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractScenarioAction implements Comparable<Integer> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractScenarioAction.class);

  private final int order;
  private String resultSummary = "Not executed";
  private boolean isExecuted = false;

  public AbstractScenarioAction(int order) {
    this.order = order;
  }

  public int getOrder() {
    return order;
  }

  @Override
  public int compareTo(Integer o) {
    return Integer.compare(order, o);
  }

  public boolean doAction() {
    boolean result = true;
    if (isRepeatable() || !isExecuted) {
      try {
        doActionInternal();
        resultSummary = getSuccessfulExecutionSummary();
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

  protected String getSuccessfulExecutionSummary() {
    return "Ok";
  }

  protected abstract void doActionInternal();

  public abstract String getActionName();

  public boolean isRepeatable() {
    return true;
  }

  public String getResultSummary() {
    return resultSummary;
  }

}
