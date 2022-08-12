package io.airbyte.testingtool.scenario;

import io.airbyte.testingtool.scenario.action.AbstractScenarioAction;
import java.util.SortedSet;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Builder
public class TestScenario {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestScenario.class);

  @Getter protected String scenarioName;
  protected boolean isFailed;
  protected SortedSet<AbstractScenarioAction> preparationActions;
  protected SortedSet<AbstractScenarioAction> scenarioActions;

  public String prepareScenario() {
    return executeActions(preparationActions);
  }

  public String runScenario() {
    return executeActions(scenarioActions);
  }

  private String executeActions(SortedSet<AbstractScenarioAction> actions) {
    if (isFailed) {
      return "Skip execution of actions " + actions.stream().map(AbstractScenarioAction::getActionName).collect(Collectors.joining(", ")) + " due to previous failure.";
    }
    else {
      StringBuilder summary = new StringBuilder();
      for (AbstractScenarioAction action : actions) {
        boolean isActionSuccessful = action.doAction();
        summary.append(StringUtils.rightPad("  [" + action.getActionName() + "]", 30)).append(" : ").append(action.getResultSummary()).append("\n");
        if (!isActionSuccessful) {
          LOGGER.error("Scenario execution stops due to action [{}] failure.", action.getActionName());
          isFailed = true;
          break;
        }
      }

      return summary.toString();
    }
  }

}
