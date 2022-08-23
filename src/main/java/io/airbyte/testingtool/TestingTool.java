package io.airbyte.testingtool;

import io.airbyte.testingtool.argument_parser.RunArgumentFactory;
import io.airbyte.testingtool.argument_parser.RunArguments;
import io.airbyte.testingtool.scenario.ScenarioFactory;
import io.airbyte.testingtool.scenario.helper.HelpService;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingTool {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestingTool.class);

  public static void main(String[] args) throws IOException {
    LOGGER.info("Testing tool started!");

    var runArguments = RunArgumentFactory.getArguments(args);
    LOGGER.info("Run arguments : \n{}", runArguments.toString());

    switch (runArguments.getRunCommand()) {
      case RUN_SCENARIO -> runScenario(runArguments);
      case RUN_HELP -> runHelp(runArguments);
      case RUN_FULL_HELP -> runFullHelp(runArguments);
    }

    LOGGER.info("Testing tool execution finished!");
  }

  private static void runScenario(RunArguments arguments) throws IOException {
    var scenario = ScenarioFactory.getScenario(arguments);
    LOGGER.info("Scenario [{}] is selected for execution.", scenario.getScenarioName());

    scenario.prepareScenario();
    LOGGER.info("Scenario preparation finished.");
    scenario.runScenario();
    LOGGER.info("Scenario execution finished.");

    scenario.printSummary();
  }

  private static void runHelp(RunArguments arguments) {
    LOGGER.info(HelpService.getHelp(arguments.getScenarioConfig()));
  }

  private static void runFullHelp(RunArguments arguments) {
    LOGGER.info(HelpService.getFullHelp(arguments.getScenarioConfig()));
  }

}
