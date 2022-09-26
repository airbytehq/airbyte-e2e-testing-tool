package io.airbyte.testingtool.scenario.action.connection;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.ConnectionCreate;
import io.airbyte.api.client.model.generated.ConnectionIdRequestBody;
import io.airbyte.api.client.model.generated.ConnectionRead;
import io.airbyte.api.client.model.generated.ConnectionStatus;
import io.airbyte.api.client.model.generated.ConnectionUpdate;
import io.airbyte.api.client.model.generated.DestinationDefinitionIdWithWorkspaceId;
import io.airbyte.api.client.model.generated.NamespaceDefinitionType;
import io.airbyte.api.client.model.generated.OperationCreate;
import io.airbyte.api.client.model.generated.OperationRead;
import io.airbyte.api.client.model.generated.OperatorConfiguration;
import io.airbyte.api.client.model.generated.OperatorNormalization;
import io.airbyte.testingtool.scenario.instance.AirbyteApiInstance;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import static io.airbyte.api.client.model.generated.OperatorType.NORMALIZATION;

public class ActionCreateConnection extends AbstractConnectionAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateConnection.class);

  protected final DestinationInstance destinationInstance;
  protected final SourceInstance sourceInstance;

  protected boolean useNormalization = true;

  @Builder
  public ActionCreateConnection(int order, List<Instance> requiredInstances, Instance resultInstance, AirbyteConnection connectionInstance,
      DestinationInstance destinationInstance, SourceInstance sourceInstance) {
    super(order, requiredInstances, resultInstance, connectionInstance);
    this.destinationInstance = destinationInstance;
    this.sourceInstance = sourceInstance;
  }

  @Override
  public String getActionName() {
    return "Create Connection";
  }

  @Override
  public void doActionInternal() throws ApiException {
    createConnection();
    if (useNormalization) {
      updateConnectionWithNormalization(connectionInstance.getConnectionId());
    }
  }

  public void updateConnectionWithNormalization(UUID connectionId) throws ApiException {
    var airbyteApiInstance = getAirbyteApiInstance();
    ConnectionIdRequestBody connectionIdRequestBody = new ConnectionIdRequestBody();
    connectionIdRequestBody.setConnectionId(connectionId);
    ConnectionRead connectionRead = airbyteApiInstance.getAirbyteApi().getConnectionApi().getConnection(connectionIdRequestBody);

    var destinationDefinitionId = destinationInstance.getDestinationDefinitionIdRequestBody()
            .getDestinationDefinitionId();
    DestinationDefinitionIdWithWorkspaceId definitionIdWithWorkspaceId = new DestinationDefinitionIdWithWorkspaceId();
    definitionIdWithWorkspaceId.setDestinationDefinitionId(destinationDefinitionId);
    definitionIdWithWorkspaceId.setWorkspaceId(airbyteApiInstance.getWorkspaceId());
    var destinationDefinitionSpecification = airbyteApiInstance.getAirbyteApi()
            .getDestinationDefinitionSpecificationApi()
            .getDestinationDefinitionSpecification(definitionIdWithWorkspaceId);

    if (Boolean.TRUE.equals(destinationDefinitionSpecification.getSupportsNormalization())) {
      OperationCreate operationCreate = new OperationCreate();
      operationCreate.setWorkspaceId(airbyteApiInstance.getWorkspaceId());
      operationCreate.setName("option_name_" + UUID.randomUUID());
      OperatorConfiguration operatorConfiguration = new OperatorConfiguration();
      OperatorNormalization operatorNormalization = new OperatorNormalization();
      operatorNormalization.setOption(OperatorNormalization.OptionEnum.BASIC);
      operatorConfiguration.setNormalization(operatorNormalization);
      operatorConfiguration.setOperatorType(NORMALIZATION);
      operationCreate.setOperatorConfiguration(operatorConfiguration);

      OperationRead operationRead = airbyteApiInstance.getAirbyteApi()
              .getOperationApi()
              .createOperation(operationCreate);
      connectionRead.addOperationIdsItem(operationRead.getOperationId());
      airbyteApiInstance.getAirbyteApi().getConnectionApi().updateConnection(createConnectionUpdate(connectionRead));
    }
  }

  private void createConnection() throws ApiException {
    var airbyteApiInstance = getAirbyteApiInstance();
    ConnectionRead connectionRead = airbyteApiInstance.getAirbyteApi().getConnectionApi().createConnection(getConnectionCreateConfig());
    connectionInstance.setAirbyteInstance(airbyteApiInstance);
    connectionInstance.setConnectionId(connectionRead.getConnectionId());
  }

  protected AirbyteApiInstance getAirbyteApiInstance() {
    var sourceApi = sourceInstance.getAirbyteApiInstance();
    var destinationApi = destinationInstance.getAirbyteApiInstance();
    if (sourceApi.equals(destinationApi)) {
      return sourceApi;
    } else {
      throw new RuntimeException(
          "Unable to create connection between Source and Destination from different Airbyte instances. " + sourceApi.getInstanceName() + " <> "
              + destinationApi.getInstanceName());
    }
  }

  protected ConnectionCreate getConnectionCreateConfig() throws ApiException {
    return new ConnectionCreate()
        .status(ConnectionStatus.ACTIVE)
        .sourceId(sourceInstance.getId())
        .destinationId(destinationInstance.getId())
        .syncCatalog(sourceInstance.discoverSourceSchema())
        .namespaceDefinition(NamespaceDefinitionType.CUSTOMFORMAT)
        .namespaceFormat("output_namespace_${SOURCE_NAMESPACE}")
        .prefix("output_table_");
  }

  protected void updateUseNormalizationFlag(boolean value) {
    this.useNormalization = value;
  }

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
