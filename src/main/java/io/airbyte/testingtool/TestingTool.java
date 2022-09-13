package io.airbyte.testingtool;

import io.airbyte.testingtool.argument_parser.RunArgumentFactory;
import io.airbyte.testingtool.argument_parser.RunArguments;
import io.airbyte.testingtool.scenario.ScenarioFactory;
import io.airbyte.testingtool.scenario.helper.HelpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TestingTool {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestingTool.class);

  public static void main(String[] args) throws IOException {
    LOGGER.info("Testing tool started!");

    var runArguments = RunArgumentFactory.getArguments(args);
    LOGGER.info("Run arguments : \n{}", runArguments.toString());

    switch (runArguments.getRunCommand()) {
      case RUN_SCENARIO, RUN_SCENARIO_LOCAL -> runScenario(runArguments);
      case RUN_HELP -> runHelp(runArguments);
      case RUN_FULL_HELP -> runFullHelp(runArguments);
      case RUN_LIST_SCENARIOS -> runScenarioListHelp();
    }

    LOGGER.info("Testing tool execution finished!");
  }

  private static void runScenario(final RunArguments arguments) {
    var scenario = ScenarioFactory.getScenario(arguments);
    LOGGER.info("Scenario [{}] is selected for execution.", scenario.getScenarioName());

    scenario.prepareScenario();
    LOGGER.info("Scenario preparation finished.");
    scenario.runScenario();
    LOGGER.info("Scenario execution finished.");

    scenario.printSummary();
  }

  private static void runHelp(final RunArguments arguments) {
    LOGGER.info(HelpService.getHelp(arguments.getScenarioConfig()));
  }

  private static void runFullHelp(final RunArguments arguments) {
    LOGGER.info(HelpService.getFullHelp(arguments.getScenarioConfig()));
  }

  private static void runScenarioListHelp() {
    LOGGER.info(HelpService.getScenarioListHelp());
  }

}
