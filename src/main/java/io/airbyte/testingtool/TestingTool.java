package io.airbyte.testingtool;

import io.airbyte.testingtool.argument_parser.RunArgumentFactory;
import io.airbyte.testingtool.argument_parser.RunArguments;
import io.airbyte.testingtool.scenario.ScenarioFactory;
import io.airbyte.testingtool.scenario.helper.HelpService;
import io.airbyte.testingtool.scenario.validator.ValidationService;
import java.io.IOException;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingTool {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestingTool.class);

  public static void main(String[] args) throws IOException {
    LOGGER.info("Testing tool started!");

    var runArguments = RunArgumentFactory.getArguments(args);
    LOGGER.info("Run arguments : \n{}", runArguments.toString());

    var result = switch (runArguments.getRunCommand()) {
      case RUN_SCENARIO, RUN_SCENARIO_LOCAL -> runScenario(runArguments);
      case RUN_HELP -> runHelp(runArguments);
      case RUN_FULL_HELP -> runFullHelp(runArguments);
      case RUN_LIST_SCENARIOS -> runScenarioListHelp();
    };

    LOGGER.info("Testing tool execution finished!");

    // @TODO Mariia Khokh. Please put result.getRunText() into result file
  }

  private static TestingToolRunResult runScenario(final RunArguments arguments) {
    return runInternal(() -> {
      ValidationService.validateScenarioConfig(arguments.getScenarioConfig());
      var scenario = ScenarioFactory.getScenario(arguments);
      LOGGER.info("Scenario [{}] is selected for execution.", scenario.getScenarioName());

      scenario.prepareScenario();
      LOGGER.info("Scenario preparation finished.");
      scenario.runScenario();
      LOGGER.info("Scenario execution finished.");

      return scenario.getTextSummary();
    });
  }

  private static TestingToolRunResult runHelp(final RunArguments arguments) {
    return runInternal(() -> HelpService.getHelp(arguments.getScenarioConfig()));
  }

  private static TestingToolRunResult runFullHelp(final RunArguments arguments) {
    return runInternal(() -> HelpService.getFullHelp(arguments.getScenarioConfig()));
  }

  private static TestingToolRunResult runScenarioListHelp() {
    return runInternal(HelpService::getScenarioListHelp);
  }

  private static TestingToolRunResult runInternal(Supplier<String> command) {
    TestingToolRunResult result;
    try {
      result = getResult(true, command.get());
    } catch (Exception e) {
      result = getResult(false, "#### Execution failed with error :\n" + e.getMessage());
    }
    LOGGER.info(result.getRunText());
    return result;
  }

  private static TestingToolRunResult getResult(final boolean isSuccessful, final String runText) {
    return TestingToolRunResult.builder()
        .isSuccessful(isSuccessful)
        .runText(runText)
        .build();
  }

}
