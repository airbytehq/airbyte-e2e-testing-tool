package io.airbyte.testingtool;

import io.airbyte.testingtool.scenario.ScenarioFactory;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingTool {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestingTool.class);

  public static void main(String[] args) throws IOException {

    LOGGER.info("Testing tool started!");

    var scenario = ScenarioFactory.getScenario(args);
    LOGGER.info("Scenario [{}] is selected for execution.", scenario.getScenarioName());

    scenario.prepareScenario();
    LOGGER.info("Scenario preparation finished.");
    scenario.runScenario();
    LOGGER.info("Scenario execution finished.");

    scenario.printSummary();

    LOGGER.info("Testing tool execution finished!");
  }

}
