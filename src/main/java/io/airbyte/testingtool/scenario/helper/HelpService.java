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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

public class HelpService {

  public static String getHelp(ScenarioConfig scenarioConfig) {
    var helpTextBuilder = new StringBuilder();
    addCallExample(scenarioConfig, helpTextBuilder);
    addFullHelpExample(scenarioConfig.getScenarioName(), helpTextBuilder);
    addRequiredInstances(scenarioConfig, helpTextBuilder);
    addRequiredParameters(scenarioConfig, helpTextBuilder);

    return helpTextBuilder.toString();
  }

  public static String getFullHelp(ScenarioConfig scenarioConfig) {
    var helpTextBuilder = new StringBuilder();
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

    helpTextBuilder.append("#### Available scenarios :\n");
    ScenarioConfigService.getScenarioConfigs().keySet().forEach(scenarioName -> helpTextBuilder.append("- ").append(scenarioName).append("\n"));

    return helpTextBuilder.toString();
  }

  public static String getHelpLine(Command helpCommand, String scenarioName) {
    return "`" + helpCommand.getCommand() + " " + SCENARIO_NAME_ARGUMENT + "=\"" + scenarioName + "\"`";
  }

  private static void addCallExample(ScenarioConfig scenarioConfig, StringBuilder builder) {
    builder.append("#### Scenario `").append(scenarioConfig.getScenarioName()).append("` example call").append("\n");
    builder.append("- **Run scenario**   : ");
    addCallLine(RUN_SCENARIO, scenarioConfig, builder);
    builder.append("- **Run scenario with local creds**  : ");
    addCallLine(RUN_SCENARIO_LOCAL, scenarioConfig, builder);
    builder.append("_Note! The local scenario run requires file names in the folder `secrets\\`. For example, `some_cred_1=local_file.json`_\n");
  }

  private static void addCallLine(Command runCommand, ScenarioConfig scenarioConfig, StringBuilder builder) {
    builder.append("`").append(runCommand.getCommand())
        .append(" ").append(SCENARIO_NAME_ARGUMENT).append("=\"").append(scenarioConfig.getScenarioName()).append("\" ")
        .append(getCallInstanceArgs(scenarioConfig))
        .append(getCallParamArgs(scenarioConfig))
        .append("`").append("\n");
  }

  private static void addShortHelpExample(String scenarioName, StringBuilder builder) {
    builder.append("- **Get short help** : ").append(getHelpLine(RUN_HELP, scenarioName)).append("\n");
  }

  private static void addFullHelpExample(String scenarioName, StringBuilder builder) {
    builder.append("- **Get full help**  : ").append(getHelpLine(RUN_FULL_HELP, scenarioName)).append("\n");
  }

  private static String getCallInstanceArgs(ScenarioConfig scenarioConfig) {
    return StringUtils.trim(scenarioConfig.getUsedInstances().stream().map(HelpService::getCredArgLine).collect(Collectors.joining(" ")));
  }

  private static String getCallParamArgs(ScenarioConfig scenarioConfig) {
    var allActions = Stream.concat(scenarioConfig.getPreparationActions().stream(), scenarioConfig.getScenarioActions().stream());
    Set<String> allParamNames = new HashSet<>();
    allActions.forEach(action -> allParamNames.addAll(action.getRequiredParameters().stream().map(ScenarioConfigActionParameter::getName).collect(
        Collectors.toSet())));
    return " " + StringUtils.trim(allParamNames.stream().map(HelpService::getParamArgLine).collect(Collectors.joining(" ")));
  }

  private static String getCredArgLine(ScenarioConfigInstance instance) {
    return (instance.getInstanceType().isCredentialsRequired() ? instance.getInstanceName() + "=<put_credential_name>" : "");
  }

  private static String getParamArgLine(String parameterName) {
    return parameterName + "=<parameter_value>";
  }

  private static void addRequiredInstances(ScenarioConfig scenarioConfig, StringBuilder builder) {
    builder.append("\n").append("#### Instances in the scenario").append("\n");
    scenarioConfig.getUsedInstances().forEach(scenarioConfigInstance -> builder.append(getInstanceText(scenarioConfigInstance)).append("\n"));
  }

  private static String getInstanceText(ScenarioConfigInstance instance) {
    return String.format("""
        - name : `%s`
        type : `%s` %s
        """, instance.getInstanceName(), instance.getInstanceType().name(), getInstanceCredentialLine(instance));
  }

  private static String getInstanceCredentialLine(ScenarioConfigInstance instance) {
    return (instance.getInstanceType().isCredentialsRequired() ? "\ncredentials : `" + instance.getInstanceType().getRequiredCredentials() + "`"
        : "");
  }

  private static void addRequiredParameters(ScenarioConfig scenarioConfig, StringBuilder builder) {
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

  private static String getParameterText(ScenarioConfigActionParameter parameter) {
    return String.format("""
        - name : `%s`
        type : `%s`
        """, parameter.getName(), parameter.getType().value());
  }

  private static void addScenarioActions(ScenarioConfig scenarioConfig, StringBuilder builder) {
    builder.append("#### Scenario actions").append("\n")
        .append("##### Preparation actions :").append("\n")
        .append(getActions(scenarioConfig.getPreparationActions()))
        .append("\n").append("##### Scenario actions :").append("\n")
        .append(getActions(scenarioConfig.getScenarioActions()));
  }

  private static String getActions(List<ScenarioConfigAction> scenarioConfig) {
    return scenarioConfig.stream().map(scenarioConfigAction -> String.format("""
            - action : `%s`%s%s%s
            """, scenarioConfigAction.getAction().name(), getRequiredInstancesLine(scenarioConfigAction), getResultInstanceLine(scenarioConfigAction), getRequiredParameters(scenarioConfigAction)))
        .collect(Collectors.joining("\n"));
  }

  private static String getRequiredInstancesLine(ScenarioConfigAction action) {
    return (!action.getRequiredInstances().isEmpty() ? "\nrequiredInstances : `" + action.getRequiredInstances() + "`" : "");
  }

  private static String getResultInstanceLine(ScenarioConfigAction action) {
    return (StringUtils.isNotEmpty(action.getResultInstance()) ? "\nresultInstance : `" + action.getResultInstance() + "`" : "");
  }

  private static String getRequiredParameters(ScenarioConfigAction action) {
    return (!action.getRequiredParameters().isEmpty() ? "\nrequiredParameters : `[" + action.getRequiredParameters().stream().map(parameter -> parameter.getName()).collect(
        Collectors.joining(", ")) + "]`" : "");
  }

}
