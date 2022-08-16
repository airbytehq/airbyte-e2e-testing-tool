package io.airbyte.testingtool.scenario;

import io.airbyte.testingtool.json.Jsons;
import io.airbyte.testingtool.scenario.action.ActionFactory;
import io.airbyte.testingtool.scenario.action.ScenarioAction;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.config.ScenarioConfigAction;
import io.airbyte.testingtool.scenario.config.ScenarioConfigInstance;
import io.airbyte.testingtool.scenario.config.ScenarioConfigService;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.InstanceFactory;
import io.airbyte.testingtool.scenario.validator.ValidationService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScenarioFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioFactory.class);

  public static TestScenario getScenario(ScenarioConfig config, List<CredentialConfig> credentialConfigs) {
    Map<String, Instance> scenarioInstanceNameToInstanceMap = mapInstancesAndCredentials(config, credentialConfigs);

    return TestScenario.builder()
        .scenarioName(config.getScenarioName())
        .preparationActions(getActions(config.getPreparationActions(), scenarioInstanceNameToInstanceMap))
        .scenarioActions(getActions(config.getScenarioActions(), scenarioInstanceNameToInstanceMap))
        .build();
  }

  private static Map<String, Instance> mapInstancesAndCredentials(ScenarioConfig config, List<CredentialConfig> credentialConfigs) {
    Map<String, Instance> resultMap = new HashMap<>();

    Set<ScenarioConfigInstance> allInstances = getScenarioInstances(config);
    allInstances.forEach(scenarioConfigInstance -> {
      var instance = InstanceFactory.getInstance(scenarioConfigInstance, getCorrespondingConfigAndRemove(scenarioConfigInstance, credentialConfigs));
      resultMap.put(instance.getInstanceName(), instance);
    });

    return resultMap;
  }

  private static Set<ScenarioConfigInstance> getScenarioInstances(ScenarioConfig config) {
    ValidationService.validateScenarioConfig(config);
    return new HashSet<>(config.getUsedInstances());
  }

  private static CredentialConfig getCorrespondingConfigAndRemove(ScenarioConfigInstance instanceConfig, List<CredentialConfig> credentialConfigs) {
    var cred = credentialConfigs.stream()
        .filter(credentialConfig -> credentialConfig.getCredentialType().equals(instanceConfig.getInstanceType().getRequiredCredentials()))
        .findFirst().orElse(null);
    if (cred != null) {
      credentialConfigs.remove(cred);
      LOGGER.info("Instance {} is mapped with credential config {}.", instanceConfig.getInstanceName(), cred.getCredentialName());
    }
    return cred;
  }

  public static TestScenario getScenario(String[] args) throws IOException {
    return getScenario(ScenarioConfigService.getConfig(args), getCreds(args));
  }

  private static SortedSet<ScenarioAction> getActions(List<ScenarioConfigAction> actionConfigs,
      Map<String, Instance> scenarioInstanceNameToInstanceMap) {
    SortedSet<ScenarioAction> actions = new TreeSet<>();
    actionConfigs.forEach(scenarioConfigAction ->
        actions.add(ActionFactory.getScenarioAction(actions.size(), scenarioConfigAction, scenarioInstanceNameToInstanceMap))
    );
    return actions;
  }

  private static List<CredentialConfig> getCreds(String[] args) throws IOException {
    final String airbyte = Files.readString(Path.of("secrets/airbyte_local_creds.json"));
    final String source = Files.readString(Path.of("secrets/postgres_test_source_creds.json"));
    final String dest = Files.readString(Path.of("secrets/postgres_test_dest_creds.json"));

    return new ArrayList<>(
        Arrays.asList(Jsons.deserialize(source, CredentialConfig.class), Jsons.deserialize(dest, CredentialConfig.class),
            Jsons.deserialize(airbyte, CredentialConfig.class)));
  }

}