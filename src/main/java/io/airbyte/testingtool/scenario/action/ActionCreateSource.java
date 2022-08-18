package io.airbyte.testingtool.scenario.action;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.SourceCreate;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import java.util.List;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionCreateSource extends ScenarioAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateSource.class);

  private final AirbyteInstance airbyteInstance;
  private final SourceInstance sourceInstance;

  @Builder
  public ActionCreateSource(int order, List<Instance> requiredInstances, Instance resultInstance, AirbyteInstance airbyteInstance,
      SourceInstance sourceInstance) {
    super(order, requiredInstances, resultInstance);
    this.airbyteInstance = airbyteInstance;
    this.sourceInstance = sourceInstance;
  }

  @Override
  public String getActionName() {
    return "Create Source";
  }

  @Override
  public void doActionInternal() {
    createSource();
  }

  private void createSource() {
    SourceCreate createSource = new SourceCreate();
    createSource.setConnectionConfiguration(sourceInstance.getCredentialConfig().getCredentialJson());
    createSource.setName(sourceInstance.getInstanceName());
    createSource.setWorkspaceId(airbyteInstance.getWorkspaceId());
    var definitionName = sourceInstance.getCredentialConfig().getInstanceType();
    createSource.setSourceDefinitionId(airbyteInstance.getSourceDefinitionId(definitionName));
    try {
      var createdSource = airbyteInstance.getAirbyteApi().getSourceApi().createSource(createSource);
      sourceInstance.setId(createdSource.getSourceId());
      sourceInstance.setAirbyteInstance(airbyteInstance);
      LOGGER.info("New source \"{}\" successfully created.", definitionName);
    } catch (ApiException e) {
      throw new RuntimeException("Fail to create new source", e);
    }
  }
}
