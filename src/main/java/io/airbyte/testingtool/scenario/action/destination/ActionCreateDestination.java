package io.airbyte.testingtool.scenario.action.destination;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.DestinationCreate;
import io.airbyte.testingtool.scenario.instance.AirbyteApiInstance;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import java.util.List;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionCreateDestination extends AbstractDestinationAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateDestination.class);

  protected final AirbyteApiInstance airbyteApiInstance;

  @Builder
  public ActionCreateDestination(int order, List<Instance> requiredInstances, Instance resultInstance, DestinationInstance destinationInstance,
      AirbyteApiInstance airbyteApiInstance) {
    super(order, requiredInstances, resultInstance, destinationInstance);
    this.airbyteApiInstance = airbyteApiInstance;
  }

  @Override
  public String getActionName() {
    return "Create Destination";
  }

  @Override
  public void doActionInternal() throws ApiException {
    createDestination();
  }

  private void createDestination() {
    DestinationCreate createDestination = new DestinationCreate();
    createDestination.setConnectionConfiguration(destinationInstance.getCredentialConfig().getCredentialJson());
    createDestination.setName(destinationInstance.getInstanceName());
    createDestination.setWorkspaceId(airbyteApiInstance.getWorkspaceId());
    var definitionName = destinationInstance.getCredentialConfig().getInstanceType();
    createDestination.setDestinationDefinitionId(airbyteApiInstance.getDestinationDefinitionId(definitionName));
    try {
      var createdDestination = airbyteApiInstance.getAirbyteApi().getDestinationApi().createDestination(createDestination);
      destinationInstance.setId(createdDestination.getDestinationId());
      destinationInstance.setAirbyteApiInstance(airbyteApiInstance);
      LOGGER.info("New destination \"{}\" successfully created.", definitionName);
    } catch (ApiException e) {
      throw new RuntimeException("Fail to create new destination", e);
    }
  }
}
