package io.airbyte.testingtool.scenario.action.connection;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.ConnectionCreate;
import io.airbyte.api.client.model.generated.ConnectionRead;
import io.airbyte.api.client.model.generated.ConnectionStatus;
import io.airbyte.api.client.model.generated.NamespaceDefinitionType;
import io.airbyte.testingtool.scenario.action.ScenarioAction;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import java.util.List;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionCreateConnection extends ScenarioAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateConnection.class);

  protected final AirbyteInstance airbyteInstance;
  protected final AirbyteConnection connection;
  protected final DestinationInstance destinationInstance;
  protected final SourceInstance sourceInstance;

  @Builder
  public ActionCreateConnection(int order, List<Instance> requiredInstances, Instance resultInstance, AirbyteInstance airbyteInstance,
      AirbyteConnection connection, DestinationInstance destinationInstance, SourceInstance sourceInstance) {
    super(order, requiredInstances, resultInstance);
    this.airbyteInstance = airbyteInstance;
    this.connection = connection;
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
  }

  private void createConnection() throws ApiException {
    ConnectionRead connectionRead = airbyteInstance.getAirbyteApi().getConnectionApi().createConnection(getConnectionCreateConfig());
    connection.setConnectionId(connectionRead.getConnectionId());
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
}
