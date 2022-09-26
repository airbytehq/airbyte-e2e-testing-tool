package io.airbyte.testingtool.scenario.instance;

import io.airbyte.api.client.generated.DestinationDefinitionApi;
import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.DestinationDefinitionIdRequestBody;
import io.airbyte.api.client.model.generated.DestinationDefinitionUpdate;
import io.airbyte.api.client.model.generated.DestinationIdRequestBody;
import io.airbyte.testingtool.scenario.config.credentials.CredentialConfig;
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
  protected AirbyteApiInstance airbyteApiInstance;

  @Builder
  public DestinationInstance(String instanceName, CredentialConfig credentialConfig, UUID id) {
    super(instanceName, credentialConfig);
    this.id = id;
  }

  @Override
  public InstanceTypes getInstanceType() {
    return InstanceTypes.DESTINATION;
  }

  public String getAribyteDestinationTypeName() {
    return getCredentialConfig().getInstanceType();
  }

  public void setDockerImageTag(String version) throws ApiException {
    DestinationDefinitionApi destinationDefinitionApi = airbyteApiInstance.getAirbyteApi()
        .getDestinationDefinitionApi();

    DestinationDefinitionUpdate destinationDefinitionUpdate = new DestinationDefinitionUpdate();
    destinationDefinitionUpdate.setDestinationDefinitionId(
        airbyteApiInstance.getDestinationDefinitionId(getAribyteDestinationTypeName()));
    destinationDefinitionUpdate.setDockerImageTag(version);

    destinationDefinitionApi.updateDestinationDefinition(destinationDefinitionUpdate);
  }

  public String getDockerImageTag() throws ApiException {
    DestinationDefinitionApi destinationDefinitionApi = airbyteApiInstance.getAirbyteApi()
        .getDestinationDefinitionApi();
    var destinationDefinitionRead = destinationDefinitionApi.getDestinationDefinition(getDestinationDefinitionIdRequestBody());
    return destinationDefinitionRead.getDockerImageTag();
  }

  public DestinationDefinitionIdRequestBody getDestinationDefinitionIdRequestBody() {
    var destinationDefinition = new DestinationDefinitionIdRequestBody();
    destinationDefinition.setDestinationDefinitionId(airbyteApiInstance.getDestinationDefinitionId(getAribyteDestinationTypeName()));
    return destinationDefinition;
  }

  public DestinationIdRequestBody getDestinationIdRequestBody() {
    var destinationDefinition = new DestinationIdRequestBody();
    destinationDefinition.setDestinationId(airbyteApiInstance.getDestinationDefinitionId(getAribyteDestinationTypeName()));
    return destinationDefinition;
  }
}
