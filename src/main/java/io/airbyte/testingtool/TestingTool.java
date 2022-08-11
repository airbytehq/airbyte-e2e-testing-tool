package io.airbyte.testingtool;

import io.airbyte.testingtool.scenario.ScenarioFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingTool {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestingTool.class);

  public static void main(String[] args) {
    LOGGER.warn("Testing tool started!");

    var scenario = ScenarioFactory.getScenario(args);
    LOGGER.info("Scenario [{}] is selected for execution.", scenario.getScenarioName());

    scenario.prepareScenario();
    LOGGER.info("Scenario preparation is finished.");

    var executionSummary = scenario.runScenario();
    LOGGER.info("""
        Scenario execution is finished.\s
        Summary :\s
        {}""", executionSummary);

    LOGGER.warn("Testing tool finished!");
  }

}
