package io.airbyte.testingtool.scenario.action.source;

import io.airbyte.api.client.generated.SourceDefinitionApi;
import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.SourceDefinitionUpdate;
import io.airbyte.testingtool.scenario.action.ScenarioAction;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import io.airbyte.testingtool.scenario.parameter.ScenarioParameter;
import java.util.List;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Scenario action. The action changes source version to a specific value.
 */
public class ActionUpdateSourceVersion extends ScenarioAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateSourceVersion.class);

  private final AirbyteInstance airbyteInstance;
  private final SourceInstance sourceInstance;
  private final ScenarioParameter version;

  @Builder
  public ActionUpdateSourceVersion(int order, List<Instance> requiredInstances,
      Instance resultInstance, AirbyteInstance airbyteInstance, SourceInstance sourceInstance,
      ScenarioParameter version) {
    super(order, requiredInstances, resultInstance);
    this.airbyteInstance = airbyteInstance;
    this.sourceInstance = sourceInstance;
    this.version = version;
  }

  @Override
  protected void doActionInternal() throws Exception {
    updateSourceVersion();
  }

  @Override
  public String getActionName() {
    return "Update Source version";
  }

  private void updateSourceVersion() throws ApiException {
    LOGGER.info("Start updating Source version to  \"{}\"", version.getParameterValue());

    SourceDefinitionApi sourceDefinitionApi = airbyteInstance.getAirbyteApi()
        .getSourceDefinitionApi();
    var definitionName = sourceInstance.getCredentialConfig().getInstanceType();

    SourceDefinitionUpdate sourceDefinitionUpdate = new SourceDefinitionUpdate();
    sourceDefinitionUpdate.setSourceDefinitionId(
        airbyteInstance.getSourceDefinitionId(definitionName));
    sourceDefinitionUpdate.setDockerImageTag(version.getParameterValue());

    sourceDefinitionApi.updateSourceDefinition(sourceDefinitionUpdate);

    LOGGER.info("Source version \"{}\" was updated", version.getParameterValue());
    context = "New Source version `"+version.getParameterValue()+"` from (**" + version.getParameterName() + "**)";
  }
}
