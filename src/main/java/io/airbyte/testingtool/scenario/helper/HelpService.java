package io.airbyte.testingtool.scenario.helper;

import static io.airbyte.testingtool.argument_parser.Command.RUN_FULL_HELP;
import static io.airbyte.testingtool.argument_parser.Command.RUN_HELP;
import static io.airbyte.testingtool.argument_parser.Command.RUN_SCENARIO;
import static io.airbyte.testingtool.argument_parser.Command.RUN_SCENARIO_LOCAL;
import static io.airbyte.testingtool.argument_parser.RunArgumentFactory.SCENARIO_NAME_ARGUMENT;

import io.airbyte.testingtool.argument_parser.Command;
import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfigAction;
import io.airbyte.testingtool.scenario.config.ScenarioConfigActionParameter;
import io.airbyte.testingtool.scenario.config.ScenarioConfigInstance;
import io.airbyte.testingtool.scenario.config.ScenarioConfigService;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

public class HelpService {

  public static String getHelp(final ScenarioConfig scenarioConfig) {
    var helpTextBuilder = new StringBuilder();
    addHeader(scenarioConfig, helpTextBuilder);
    addCallExample(scenarioConfig, helpTextBuilder);
    addFullHelpExample(scenarioConfig.getScenarioName(), helpTextBuilder);
    addRequiredInstances(scenarioConfig, helpTextBuilder);
    addRequiredParameters(scenarioConfig, helpTextBuilder);

    return helpTextBuilder.toString();
  }

  public static String getFullHelp(final ScenarioConfig scenarioConfig) {
    var helpTextBuilder = new StringBuilder();
    addHeader(scenarioConfig, helpTextBuilder);
    addCallExample(scenarioConfig, helpTextBuilder);
    addShortHelpExample(scenarioConfig.getScenarioName(), helpTextBuilder);
    addRequiredInstances(scenarioConfig, helpTextBuilder);
    addRequiredParameters(scenarioConfig, helpTextBuilder);
    addScenarioActions(scenarioConfig, helpTextBuilder);

    return helpTextBuilder.toString();
  }

  public static String getScenarioListHelp() {
    var helpTextBuilder = new StringBuilder();

    helpTextBuilder.append("#### Call help examples :\n");

    String putScenarioName = "<scenario_name>";
    addShortHelpExample(putScenarioName, helpTextBuilder);
    addFullHelpExample(putScenarioName, helpTextBuilder);

    helpTextBuilder.append("#### Scenario validation sings :\n")
        .append("- :heavy_check_mark: - the scenario is valid and ready for use.\n")
        .append("- :x: - the scenario is invalid. You can get a list of errors by a help command.\n");

    helpTextBuilder.append("#### Available scenarios :\n");
    ScenarioConfigService.getScenarioConfigs().entrySet().stream().sorted(Entry.comparingByKey())
        .forEach(pairNameConfig -> helpTextBuilder.append("- ").append(pairNameConfig.getKey()).append(" ")
            .append(getValidationSign(pairNameConfig.getKey())).append("\n").append(getScenarioDescription(pairNameConfig.getValue())));

    return helpTextBuilder.toString();
  }

  private static String getScenarioDescription(final ScenarioConfig config) {
    return (StringUtils.isNotEmpty(config.getScenarioDescription()) ? "_:information_source: " + config.getScenarioDescription() + "_\n" : "");
  }


  public static String getHelpLine(final Command helpCommand, final String scenarioName) {
    return "`" + helpCommand.getCommand() + " " + SCENARIO_NAME_ARGUMENT + "=\"" + scenarioName + "\"`";
  }

  private static void addHeader(final ScenarioConfig scenarioConfig, final StringBuilder builder) {
    builder.append("#### Scenario `").append(scenarioConfig.getScenarioName()).append("`. ")
        .append(getValidationSign(scenarioConfig.getScenarioName())).append("\n");
    builder.append(getScenarioDescription(scenarioConfig));
    addErrors(scenarioConfig, builder);
  }

  private static void addErrors(final ScenarioConfig scenarioConfig, final StringBuilder builder) {
    var scenarioValidationResult = ScenarioConfigService.getScenarioValidationResult(scenarioConfig.getScenarioName());
    if (!scenarioValidationResult.isValidationSuccessful()) {
      builder.append("#### :warning: Failed validations :\n")
          .append("| Validation | Result |\n")
          .append("|---|---|\n");
      scenarioValidationResult.getValidationResults(true).forEach(
          validationResult -> builder.append("| ").append(validationResult.getValidationName()).append(" | ").append(validationResult.getErrorText())
              .append(" |\n"));
    }
  }

  private static String getValidationSign(final String scenarioName) {
    return (ScenarioConfigService.getScenarioValidationResult(scenarioName).isValidationSuccessful() ? ":heavy_check_mark:" : ":x:");
  }

  private static void addCallExample(final ScenarioConfig scenarioConfig, final StringBuilder builder) {
    builder.append("\n#### Call examples :\n");
    builder.append("- **Run scenario**   : ");
    addCallLine(RUN_SCENARIO, scenarioConfig, builder);
    builder.append("- **Run scenario with local creds**  : ");
    addCallLine(RUN_SCENARIO_LOCAL, scenarioConfig, builder);
    builder.append("_Note! The local scenario run requires file names in the folder `secrets\\`. For example, `some_cred_1=local_file.json`_\n");
  }

  private static void addCallLine(final Command runCommand, final ScenarioConfig scenarioConfig, final StringBuilder builder) {
    builder.append("`").append(runCommand.getCommand())
        .append(" ").append(SCENARIO_NAME_ARGUMENT).append("=\"").append(scenarioConfig.getScenarioName()).append("\" ")
        .append(getCallInstanceArgs(scenarioConfig))
        .append(getCallParamArgs(scenarioConfig))
        .append("`").append("\n");
  }

  private static void addShortHelpExample(final String scenarioName, final StringBuilder builder) {
    builder.append("- **Get short help** : ").append(getHelpLine(RUN_HELP, scenarioName)).append("\n");
  }

  private static void addFullHelpExample(final String scenarioName, final StringBuilder builder) {
    builder.append("- **Get full help**  : ").append(getHelpLine(RUN_FULL_HELP, scenarioName)).append("\n");
  }

  private static String getCallInstanceArgs(final ScenarioConfig scenarioConfig) {
    return StringUtils.trim(scenarioConfig.getUsedInstances().stream().map(HelpService::getCredArgLine).collect(Collectors.joining(" ")));
  }

  private static String getCallParamArgs(final ScenarioConfig scenarioConfig) {
    var allActions = Stream.concat(scenarioConfig.getPreparationActions().stream(), scenarioConfig.getScenarioActions().stream());
    Set<String> allParamNames = new HashSet<>();
    allActions.forEach(action -> allParamNames.addAll(action.getRequiredParameters().stream().map(ScenarioConfigActionParameter::getName).collect(
        Collectors.toSet())));
    return " " + StringUtils.trim(allParamNames.stream().map(HelpService::getParamArgLine).collect(Collectors.joining(" ")));
  }

  private static String getCredArgLine(final ScenarioConfigInstance instance) {
    return (instance.getInstanceType().isCredentialsRequired() ? instance.getInstanceName() + "=<put_credential_name>" : "");
  }

  private static String getParamArgLine(final String parameterName) {
    return parameterName + "=<parameter_value>";
  }

  private static void addRequiredInstances(final ScenarioConfig scenarioConfig, final StringBuilder builder) {
    builder.append("\n").append("#### Instances in the scenario").append("\n");
    scenarioConfig.getUsedInstances().forEach(scenarioConfigInstance -> builder.append(getInstanceText(scenarioConfigInstance)).append("\n"));
  }

  private static String getInstanceText(final ScenarioConfigInstance instance) {
    return String.format("""
        - name : `%s`
        type : `%s` %s
        """, instance.getInstanceName(), instance.getInstanceType().name(), getInstanceCredentialLine(instance));
  }

  private static String getInstanceCredentialLine(final ScenarioConfigInstance instance) {
    return (instance.getInstanceType().isCredentialsRequired() ? "\ncredentials : `" + instance.getInstanceType().getRequiredCredentials() + "`"
        : "");
  }

  private static void addRequiredParameters(final ScenarioConfig scenarioConfig, final StringBuilder builder) {
    var allActions = Stream.concat(scenarioConfig.getPreparationActions().stream(), scenarioConfig.getScenarioActions().stream());
    Set<ScenarioConfigActionParameter> requiredParams = new HashSet<>();
    allActions.forEach(action -> {
      if (!action.getRequiredParameters().isEmpty()) {
        requiredParams.addAll(action.getRequiredParameters());
      }
    });
    if (!requiredParams.isEmpty()) {
      builder.append("#### Parameters in the scenario").append("\n");
      requiredParams.forEach(parameter -> builder.append(getParameterText(parameter)).append("\n"));
    }
  }

  private static String getParameterText(final ScenarioConfigActionParameter parameter) {
    return String.format("""
        - name : `%s`
        type : `%s`
        """, parameter.getName(), parameter.getType().value());
  }

  private static void addScenarioActions(final ScenarioConfig scenarioConfig, final StringBuilder builder) {
    builder.append("#### Scenario actions").append("\n")
        .append("##### Preparation actions :").append("\n")
        .append(getActions(scenarioConfig.getPreparationActions()))
        .append("\n").append("##### Scenario actions :").append("\n")
        .append(getActions(scenarioConfig.getScenarioActions()));
  }

  private static String getActions(final List<ScenarioConfigAction> scenarioConfig) {
    return scenarioConfig.stream().map(scenarioConfigAction -> String.format("""
                - action : `%s`%s%s%s
                """, scenarioConfigAction.getAction().name(), getRequiredInstancesLine(scenarioConfigAction), getResultInstanceLine(scenarioConfigAction),
            getRequiredParameters(scenarioConfigAction)))
        .collect(Collectors.joining("\n"));
  }

  private static String getRequiredInstancesLine(final ScenarioConfigAction action) {
    return (!action.getRequiredInstances().isEmpty() ? "\nrequiredInstances : `" + action.getRequiredInstances() + "`" : "");
  }

  private static String getResultInstanceLine(final ScenarioConfigAction action) {
    return (StringUtils.isNotEmpty(action.getResultInstance()) ? "\nresultInstance : `" + action.getResultInstance() + "`" : "");
  }

  private static String getRequiredParameters(final ScenarioConfigAction action) {
    return (!action.getRequiredParameters().isEmpty() ? "\nrequiredParameters : `[" + action.getRequiredParameters().stream()
        .map(parameter -> parameter.getName()).collect(
            Collectors.joining(", ")) + "]`" : "");
  }

}
