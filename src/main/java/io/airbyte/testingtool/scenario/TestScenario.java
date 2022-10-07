package io.airbyte.testingtool.scenario;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.airbyte.testingtool.metrics.Metrics;
import io.airbyte.testingtool.scenario.action.ScenarioAction;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
    storingMetrics(scenarioActions);
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

  private void storingMetrics(SortedSet<ScenarioAction> actions) {
    ObjectMapper mapper = new ObjectMapper();

    for (ScenarioAction action: actions) {
      try {
        Metrics actionMetrics = action.getMetrics();

        if (actionMetrics != null) {
          File output = new File(System.getenv("user.dir") + File.pathSeparator + "metrics" + File.pathSeparator +
              action.getActionName() + "_" + System.currentTimeMillis() + ".json");

          if (!output.exists()) {
            output.getParentFile().mkdirs();
            output.createNewFile();
          }

          try (DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(output)))) {
            String jsonString = mapper.writeValueAsString(actionMetrics);
            outputStream.writeUTF(jsonString);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private String getActionSummaryText(SortedSet<ScenarioAction> actions) {
    StringBuilder summary = new StringBuilder();
    actions.forEach(action ->
      summary.append("| ").append(action.getActionName())
          .append(" | ").append(action.getStatus().getName())
          .append(" | ").append(action.getContext())
          .append(" | **").append(action.getDurationSec()).append(" sec** |\n")
          .append(getMetrics(action))
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
