package io.airbyte.testingtool.scenario;

import io.airbyte.testingtool.metrics.Metrics;
import io.airbyte.testingtool.scenario.action.ScenarioAction;
import java.util.Map;
import java.util.SortedSet;
import lombok.Builder;
import lombok.Getter;
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

  public String getTextSummary() {
    return String.format("""
            ### %s Scenario `%s` execution is finished %s
            #### Preparation actions :
            | Preparation action | Result | Context | Duration |
            |:---|:---:|:---|:---:|
            %s
            #### Scenario actions    :
            | Scenario action | Result | Context | Duration |
            |:---|:---:|:---|:---:|
            %s
            """, (isFailed ? ":x:" : ":heavy_check_mark:"), scenarioName, (isFailed ? " with errors!" : "successfully."),
        getActionSummaryText(preparationActions),
        getActionSummaryText(scenarioActions));
  }

  private String getActionSummaryText(SortedSet<ScenarioAction> actions) {
    StringBuilder summary = new StringBuilder();
    actions.forEach(action ->
      summary.append("| ").append(action.getActionName())
          .append(" | ").append(action.getStatus().getName())
          .append(" | ").append(action.getContext())
          .append(" | **").append(action.getDurationSec()).append(" sec** |\n").append(getMetrics(action))
    );
    return summary.toString();
  }

  private String getMetrics(ScenarioAction action) {

    if (action.getMetrics() == null) {
      return "";
    }
    else {
      return action.getMetrics().toString();
    }
  }

}
