package io.airbyte.testingtool.scenario;

import io.airbyte.testingtool.scenario.action.ScenarioAction;
import java.util.SortedSet;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Builder
public class TestScenario {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestScenario.class);

  @Getter
  protected String scenarioName;
  @Getter
  protected boolean isFailed;
  protected SortedSet<ScenarioAction> preparationActions;
  protected SortedSet<ScenarioAction> scenarioActions;

  public void prepareScenario() {
    executeActions(preparationActions);
  }

  public void runScenario() {
    executeActions(scenarioActions);
  }

  private void executeActions(SortedSet<ScenarioAction> actions) {
    if (!isFailed) {
      for (ScenarioAction action : actions) {
        action.doAction();
        if (action.getStatus().isFailure()) {
          LOGGER.error("Scenario execution stops due to action [{}] failure.", action.getActionName());
          isFailed = true;
          break;
        }
      }
    }
  }

  public void printSummary() {
    LOGGER.info("""
            Scenario `{}` execution is finished {}.
            Preparation actions :
            {}
            Scenario actions    :
            {}
            """, scenarioName, (isFailed ? " with errors!" : "successfully"), getActionSummaryText(preparationActions),
        getActionSummaryText(scenarioActions));
  }

  private String getActionSummaryText(SortedSet<ScenarioAction> actions) {
    StringBuilder summary = new StringBuilder();
    var longestName = actions.stream().map(action -> action.getActionName().length()).max(Integer::compare).orElse(0);
    actions.forEach(action -> {
      summary.append(StringUtils.rightPad("  [" + action.getActionName() + "]", longestName + 5)).append(" : ").append(action.getStatus().name());
      var actionText = action.getResultSummary();
      summary.append((StringUtils.isNotEmpty(actionText) ? " - " + actionText : "")).append("\n");
    });
    return summary.toString();
  }

}
