package io.airbyte.testingtool.scenario.action.connection;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.ConnectionCreate;
import io.airbyte.api.client.model.generated.ConnectionRead;
import io.airbyte.api.client.model.generated.ConnectionStatus;
import io.airbyte.api.client.model.generated.NamespaceDefinitionType;
import io.airbyte.testingtool.scenario.instance.AirbyteApiInstance;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActionCreateConnection extends AbstractConnectionAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateConnection.class);

  protected final DestinationInstance destinationInstance;
  protected final SourceInstance sourceInstance;

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
    if (getNormalizationFlag() && destinationInstance.isSupportNormalization()) {
      connectionInstance.setNormalization();
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

  protected boolean getNormalizationFlag() {
    return true;
  }

}
