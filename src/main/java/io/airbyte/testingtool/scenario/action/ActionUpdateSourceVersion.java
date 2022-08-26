package io.airbyte.testingtool.scenario.action;

import io.airbyte.api.client.generated.SourceDefinitionApi;
import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.SourceDefinitionUpdate;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import java.util.List;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ActionUpdateSourceVersion extends ScenarioAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateSourceVersion.class);

  private final AirbyteInstance airbyteInstance;
  private final SourceInstance sourceInstance;
  private final String version;

  @Builder
  public ActionUpdateSourceVersion(int order, List<Instance> requiredInstances,
      Instance resultInstance, AirbyteInstance airbyteInstance, SourceInstance sourceInstance,
      String version) {
    super(order, requiredInstances, resultInstance);
    this.airbyteInstance = airbyteInstance;
    this.sourceInstance = sourceInstance;
    this.version = version;
  }

  @Override
  protected void doActionInternal() throws Exception {
    createUpdateSourceVersion();
  }

  @Override
  public String getActionName() {
    return "Update Source version";
  }

  private void createUpdateSourceVersion() throws ApiException {
    LOGGER.info("Start updating Source version to  \"{}\"", version);

    SourceDefinitionApi sourceDefinitionApi = airbyteInstance.getAirbyteApi()
        .getSourceDefinitionApi();
    var definitionName = sourceInstance.getCredentialConfig().getInstanceType();

    SourceDefinitionUpdate sourceDefinitionUpdate = new SourceDefinitionUpdate();
    sourceDefinitionUpdate.setSourceDefinitionId(
        airbyteInstance.getSourceDefinitionId(definitionName));
    sourceDefinitionUpdate.setDockerImageTag(version);

    sourceDefinitionApi.updateSourceDefinition(sourceDefinitionUpdate);

    LOGGER.info("Source version \"{}\" was updated", version);
  }
}
