package io.airbyte.testingtool.scenario.instance;

import io.airbyte.api.client.generated.DestinationDefinitionApi;
import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.DestinationDefinitionIdRequestBody;
import io.airbyte.api.client.model.generated.DestinationDefinitionUpdate;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class DestinationInstance extends InstanceWithCredentials {

  @Getter
  @Setter
  private UUID id;

  @Getter
  @Setter
  protected AirbyteInstance airbyteInstance;

  @Builder
  public DestinationInstance(String instanceName, CredentialConfig credentialConfig, UUID id, AirbyteInstance airbyteInstance) {
    super(instanceName, credentialConfig);
    this.id = id;
    this.airbyteInstance = airbyteInstance;
  }

  @Override
  public InstanceTypes getInstanceType() {
    return InstanceTypes.DESTINATION;
  }

  public String getAribyteDestinationTypeName() {
    return getCredentialConfig().getInstanceType();
  }

  public void setDockerImageTag(String version) throws ApiException {
    DestinationDefinitionApi destinationDefinitionApi = airbyteInstance.getAirbyteApi()
        .getDestinationDefinitionApi();

    DestinationDefinitionUpdate destinationDefinitionUpdate = new DestinationDefinitionUpdate();
    destinationDefinitionUpdate.setDestinationDefinitionId(
        airbyteInstance.getDestinationDefinitionId(getAribyteDestinationTypeName()));
    destinationDefinitionUpdate.setDockerImageTag(version);

    destinationDefinitionApi.updateDestinationDefinition(destinationDefinitionUpdate);
  }

  public String getDockerImageTag() throws ApiException {
    DestinationDefinitionApi destinationDefinitionApi = airbyteInstance.getAirbyteApi()
        .getDestinationDefinitionApi();
    var destinationDefinition = new DestinationDefinitionIdRequestBody();
    destinationDefinition.setDestinationDefinitionId(airbyteInstance.getDestinationDefinitionId(getAribyteDestinationTypeName()));
    var destinationDefinitionRead = destinationDefinitionApi.getDestinationDefinition(destinationDefinition);
    return destinationDefinitionRead.getDockerImageTag();
  }
}
