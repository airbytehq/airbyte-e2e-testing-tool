package io.airbyte.testingtool.scenario.action;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.DestinationCreate;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActionCreateDestination extends ScenarioAction {
  private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateDestination.class);

  private final AirbyteInstance airbyteInstance;
  private final DestinationInstance destinationInstance;

  @Builder
  public ActionCreateDestination(int order, List<Instance> requiredInstances, Instance resultInstance, AirbyteInstance airbyteInstance,
      DestinationInstance destinationInstance) {
    super(order, requiredInstances, resultInstance);
    this.airbyteInstance = airbyteInstance;
    this.destinationInstance = destinationInstance;
  }

  @Override
  public String getActionName() {
    return "Create Destination";
  }

  @Override
  public void doActionInternal() {
    createDestination();
  }

  private void createDestination() {
    DestinationCreate createDestination = new DestinationCreate();
    createDestination.setConnectionConfiguration(destinationInstance.getCredentialConfig().getCredentialJson());
    createDestination.setName(destinationInstance.getInstanceName());
    createDestination.setWorkspaceId(airbyteInstance.getWorkspaceId());
    var definitionName = destinationInstance.getCredentialConfig().getInstanceType();
    createDestination.setDestinationDefinitionId(airbyteInstance.getDestinationDefinitionId(definitionName));
    try {
      var createdDestination = airbyteInstance.getAirbyteApi().getDestinationApi().createDestination(createDestination);
      destinationInstance.setId(createdDestination.getDestinationId());
      LOGGER.info("New destination \"{}\" successfully created.", definitionName);
    } catch (ApiException e) {
      throw new RuntimeException("Fail to create new destination", e);
    }
  }
}
