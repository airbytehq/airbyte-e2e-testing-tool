package io.airbyte.testingtool.scenario.helper;

import static io.airbyte.testingtool.argument_parser.Command.RUN_FULL_HELP;
import static io.airbyte.testingtool.argument_parser.Command.RUN_HELP;
import static io.airbyte.testingtool.argument_parser.Command.RUN_SCENARIO;

import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfigAction;
import io.airbyte.testingtool.scenario.config.ScenarioConfigInstance;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class HelpService {

  public static String getHelp(ScenarioConfig scenarioConfig) {
    var helpTextBuilder = new StringBuilder();
    addCallExample(scenarioConfig, helpTextBuilder);
    addFullHelpExample(scenarioConfig, helpTextBuilder);
    addRequiredInstances(scenarioConfig, helpTextBuilder);

    return helpTextBuilder.toString();
  }

  public static String getFullHelp(ScenarioConfig scenarioConfig) {
    var helpTextBuilder = new StringBuilder();
    addCallExample(scenarioConfig, helpTextBuilder);
    addShortHelpExample(scenarioConfig, helpTextBuilder);
    addRequiredInstances(scenarioConfig, helpTextBuilder);
    addScenarioActions(scenarioConfig, helpTextBuilder);

    return helpTextBuilder.toString();
  }

  private static void addCallExample(ScenarioConfig scenarioConfig, StringBuilder builder) {
    builder.append("#### Scenario `").append(scenarioConfig.getScenarioName()).append("` example call").append("\n");
    builder.append("Run scenario   : `").append(RUN_SCENARIO.getCommand()).append(" name=\"").append(scenarioConfig.getScenarioName()).append("\" ").append(getCallArgs(scenarioConfig)).append("`").append("\n");
  }

  private static void addShortHelpExample(ScenarioConfig scenarioConfig, StringBuilder builder) {
    builder.append("Get short help : `").append(RUN_HELP.getCommand()).append(" name=\"").append(scenarioConfig.getScenarioName()).append("\"").append("`").append("\n");
  }

  private static void addFullHelpExample(ScenarioConfig scenarioConfig, StringBuilder builder) {
    builder.append("Get full help  : `").append(RUN_FULL_HELP.getCommand()).append(" name=\"").append(scenarioConfig.getScenarioName()).append("\"").append("`").append("\n");
  }

  private static String getCallArgs(ScenarioConfig scenarioConfig) {
    return StringUtils.trim(scenarioConfig.getUsedInstances().stream().map(HelpService::getCredArgLine).collect(Collectors.joining(" ")));
  }

  private static String getCredArgLine(ScenarioConfigInstance instance) {
    return (instance.getInstanceType().isCredentialsRequired() ? instance.getInstanceName() + "=<put_credential_name>" : "");
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
    return (instance.getInstanceType().isCredentialsRequired() ? "\ncredentials : `" + instance.getInstanceType().getRequiredCredentials() + "`" : "");
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
        - action : `%s`%s%s
        """, scenarioConfigAction.getAction().name(), getRequiredInstancesLine(scenarioConfigAction), getResultInstanceLine(scenarioConfigAction))).collect(Collectors.joining("\n"));
  }

  private static String getRequiredInstancesLine(ScenarioConfigAction action) {
    return (!action.getRequiredInstances().isEmpty() ? "\nrequiredInstances : `" + action.getRequiredInstances() + "`" : "");
  }

  private static String getResultInstanceLine(ScenarioConfigAction action) {
    return (StringUtils.isNotEmpty(action.getResultInstance()) ? "\nresultInstance : `" + action.getResultInstance() + "`" : "");
  }

}
