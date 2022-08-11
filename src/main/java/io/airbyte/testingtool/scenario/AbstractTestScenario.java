package io.airbyte.testingtool.scenario;

import io.airbyte.testingtool.scenario.action.AbstractScenarioAction;
import java.util.SortedSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTestScenario {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTestScenario.class);
  protected SortedSet<AbstractScenarioAction> actions;

  public abstract String getScenarioName();

  public abstract void prepareScenario();

  public String runScenario() {
    StringBuilder summary = new StringBuilder();
    for (AbstractScenarioAction action : actions) {
      boolean isActionSuccessful = action.doAction();
      summary.append(StringUtils.rightPad("  ["+action.getActionName()+"]", 30)).append(" : ").append(action.getResultSummary()).append("\n");
      if (!isActionSuccessful) {
        LOGGER.error("Scenario execution stops due to action [{}] failure.", action.getActionName());
        break;
      }
    }

    return summary.toString();
  }

}
