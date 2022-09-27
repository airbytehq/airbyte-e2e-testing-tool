package io.airbyte.testingtool.scenario.instance;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.ConnectionIdRequestBody;
import java.util.UUID;

import io.airbyte.api.client.model.generated.ConnectionRead;
import io.airbyte.api.client.model.generated.ConnectionUpdate;
import io.airbyte.api.client.model.generated.OperationCreate;
import io.airbyte.api.client.model.generated.OperationRead;
import io.airbyte.api.client.model.generated.OperatorConfiguration;
import io.airbyte.api.client.model.generated.OperatorNormalization;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static io.airbyte.api.client.model.generated.OperatorType.NORMALIZATION;

public class AirbyteConnection extends Instance {

  @Getter
  @Setter
  private UUID connectionId;
  @Getter
  @Setter
  protected AirbyteApiInstance airbyteInstance;

  @Builder
  public AirbyteConnection(String instanceName) {
    super(instanceName);
  }

  @Override
  public InstanceTypes getInstanceType() {
    return InstanceTypes.CONNECTION;
  }

  public ConnectionIdRequestBody getConnectionRequestBody() {
    ConnectionIdRequestBody connectionIdRequestBody = new ConnectionIdRequestBody();
    connectionIdRequestBody.setConnectionId(connectionId);
    return connectionIdRequestBody;
  }

  public void setNormalization(boolean destinationSupportNormalization) throws ApiException {
    ConnectionIdRequestBody connectionIdRequestBody = new ConnectionIdRequestBody();
    connectionIdRequestBody.setConnectionId(getConnectionId());
    ConnectionRead connectionRead = airbyteInstance.getAirbyteApi().getConnectionApi().getConnection(connectionIdRequestBody);

    if (destinationSupportNormalization) {
      OperationCreate operationCreate = new OperationCreate();
      operationCreate.setWorkspaceId(airbyteInstance.getWorkspaceId());
      operationCreate.setName("option_name_" + UUID.randomUUID());
      OperatorConfiguration operatorConfiguration = new OperatorConfiguration();
      OperatorNormalization operatorNormalization = new OperatorNormalization();
      operatorNormalization.setOption(OperatorNormalization.OptionEnum.BASIC);
      operatorConfiguration.setNormalization(operatorNormalization);
      operatorConfiguration.setOperatorType(NORMALIZATION);
      operationCreate.setOperatorConfiguration(operatorConfiguration);

      OperationRead operationRead = airbyteInstance.getAirbyteApi()
              .getOperationApi()
              .createOperation(operationCreate);
      connectionRead.addOperationIdsItem(operationRead.getOperationId());
      airbyteInstance.getAirbyteApi().getConnectionApi().updateConnection(createConnectionUpdate(connectionRead));
    }
  }

  /* Helpers */

  private ConnectionUpdate createConnectionUpdate(ConnectionRead connectionRead) {
    ConnectionUpdate connectionUpdate = new ConnectionUpdate();
    connectionUpdate.setConnectionId(connectionRead.getConnectionId());
    connectionUpdate.setName(connectionRead.getName());
    connectionUpdate.setPrefix(connectionRead.getPrefix());
    connectionUpdate.setOperationIds(connectionRead.getOperationIds());
    connectionUpdate.setSchedule(connectionRead.getSchedule());
    connectionUpdate.setNamespaceDefinition(connectionRead.getNamespaceDefinition());
    connectionUpdate.setNamespaceFormat(connectionRead.getNamespaceFormat());
    connectionUpdate.setResourceRequirements(connectionRead.getResourceRequirements());
    connectionUpdate.setSourceCatalogId(connectionRead.getSourceCatalogId());
    connectionUpdate.setStatus(connectionRead.getStatus());
    connectionUpdate.setSyncCatalog(connectionRead.getSyncCatalog());
    return connectionUpdate;
  }

}
