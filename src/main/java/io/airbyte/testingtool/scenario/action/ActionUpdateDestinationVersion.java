package io.airbyte.testingtool.scenario.action;

import io.airbyte.api.client.generated.DestinationDefinitionApi;
import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.DestinationDefinitionUpdate;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.parameter.ScenarioParameter;
import java.util.List;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scenario action. The action changes destination version to a specific value.
 */
public class ActionUpdateDestinationVersion extends ScenarioAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      ActionUpdateDestinationVersion.class);

  private final AirbyteInstance airbyteInstance;
  private final DestinationInstance destinationInstance;
  private final ScenarioParameter version;

  @Builder
  public ActionUpdateDestinationVersion(int order, List<Instance> requiredInstances,
      Instance resultInstance, AirbyteInstance airbyteInstance,
      DestinationInstance destinationInstance, ScenarioParameter version) {
    super(order, requiredInstances, resultInstance);
    this.airbyteInstance = airbyteInstance;
    this.destinationInstance = destinationInstance;
    this.version = version;
  }

  @Override
  protected void doActionInternal() throws Exception {
    updateDestinationVersion();
  }

  @Override
  public String getActionName() {
    return "Update Destination version";
  }

  private void updateDestinationVersion() throws ApiException {
    LOGGER.info("Start updating Source version to  \"{}\"", version.getParameterValue());

    DestinationDefinitionApi destinationDefinitionApi = airbyteInstance.getAirbyteApi()
        .getDestinationDefinitionApi();
    var definitionName = destinationInstance.getCredentialConfig().getInstanceType();

    DestinationDefinitionUpdate destinationDefinitionUpdate = new DestinationDefinitionUpdate();
    destinationDefinitionUpdate.setDestinationDefinitionId(
        airbyteInstance.getDestinationDefinitionId(definitionName));
    destinationDefinitionUpdate.setDockerImageTag(version.getParameterValue());

    destinationDefinitionApi.updateDestinationDefinition(destinationDefinitionUpdate);

    LOGGER.info("Destination version \"{}\" was updated", version.getParameterValue());
  }
}
