package io.airbyte.testingtool;

import io.airbyte.testingtool.scenario.ScenarioFactory;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingTool {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestingTool.class);

  public static void main(String[] args) throws IOException {

    LOGGER.warn("Testing tool started!");

    var scenario = ScenarioFactory.getScenario(args);
    LOGGER.info("Scenario [{}] is selected for execution.", scenario.getScenarioName());

    var preparationSummary = scenario.prepareScenario();
    LOGGER.info("""
        Scenario preparation is finished.\s
        Summary :\s
        {}""", preparationSummary);
    LOGGER.info("Scenario preparation is finished.");

    var executionSummary = scenario.runScenario();
    LOGGER.info("""
        Scenario execution is finished.\s
        Summary :\s
        {}""", executionSummary);

    LOGGER.warn("Testing tool finished!");
  }

}
