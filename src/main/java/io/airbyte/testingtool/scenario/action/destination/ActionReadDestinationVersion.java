package io.airbyte.testingtool.scenario.action.destination;

import io.airbyte.api.client.generated.DestinationDefinitionApi;
import io.airbyte.api.client.model.generated.DestinationDefinitionIdRequestBody;
import io.airbyte.testingtool.scenario.action.ScenarioAction;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.parameter.ScenarioParameter;
import java.util.List;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionReadDestinationVersion extends ScenarioAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionReadDestinationVersion.class);

  private final AirbyteInstance airbyteInstance;
  private final DestinationInstance destinationInstance;
  private final ScenarioParameter version;

  @Builder
  public ActionReadDestinationVersion(int order, List<Instance> requiredInstances,
      Instance resultInstance, AirbyteInstance airbyteInstance, DestinationInstance destinationInstance, ScenarioParameter version) {
    super(order, requiredInstances, resultInstance);
    this.airbyteInstance = airbyteInstance;
    this.destinationInstance = destinationInstance;
    this.version = version;
  }

  @Override
  protected void doActionInternal() throws Exception {
    DestinationDefinitionApi destinationDefinitionApi = airbyteInstance.getAirbyteApi()
        .getDestinationDefinitionApi();
    var destinationDefinition = new DestinationDefinitionIdRequestBody();
    var definitionName = destinationInstance.getCredentialConfig().getInstanceType();
    destinationDefinition.setDestinationDefinitionId(airbyteInstance.getDestinationDefinitionId(definitionName));
    var destinationDefinitionRead = destinationDefinitionApi.getDestinationDefinition(destinationDefinition);
    version.setParameterValue(destinationDefinitionRead.getDockerImageTag());
    LOGGER.info("Read destination version \"{}\" into {} parameter.", version.getParameterValue(), version.getParameterName());
    context = "Destination version `"+version.getParameterValue()+"` -> **" + version.getParameterName() + "**";
  }

  @Override
  public String getActionName() {
    return "Read destination version";
  }
}
