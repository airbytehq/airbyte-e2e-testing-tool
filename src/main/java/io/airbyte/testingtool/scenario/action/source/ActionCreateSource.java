package io.airbyte.testingtool.scenario.action.source;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.SourceCreate;
import io.airbyte.testingtool.scenario.instance.AirbyteApiInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import java.util.List;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionCreateSource extends AbstractSourceAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateSource.class);
  protected final AirbyteApiInstance airbyteApiInstance;

  @Builder
  public ActionCreateSource(int order, List<Instance> requiredInstances, Instance resultInstance, SourceInstance sourceInstance,
      AirbyteApiInstance airbyteApiInstance) {
    super(order, requiredInstances, resultInstance, sourceInstance);
    this.airbyteApiInstance = airbyteApiInstance;
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
    createSource.setWorkspaceId(airbyteApiInstance.getWorkspaceId());
    var definitionName = sourceInstance.getCredentialConfig().getInstanceType();
    createSource.setSourceDefinitionId(airbyteApiInstance.getSourceDefinitionId(definitionName));
    try {
      var createdSource = airbyteApiInstance.getAirbyteApi().getSourceApi().createSource(createSource);
      sourceInstance.setId(createdSource.getSourceId());
      sourceInstance.setAirbyteApiInstance(airbyteApiInstance);
      LOGGER.info("New source \"{}\" successfully created.", definitionName);
    } catch (ApiException e) {
      throw new RuntimeException("Fail to create new source", e);
    }
  }
}
