package io.airbyte.testingtool.scenario.action;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.ConnectionCreate;
import io.airbyte.api.client.model.generated.ConnectionRead;
import io.airbyte.api.client.model.generated.ConnectionStatus;
import io.airbyte.api.client.model.generated.NamespaceDefinitionType;
import io.airbyte.api.model.generated.ConnectionIdRequestBody;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionCreateConnection extends ScenarioAction {
  private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateConnection.class);

  private final AirbyteInstance airbyteInstance;
  private final AirbyteConnection connection;
  private final DestinationInstance destinationInstance;
  private final SourceInstance sourceInstance;

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
  public void doActionInternal() {
    createConnection();
  }

  private void createConnection() {
    try {
      ConnectionCreate connectionConfig = new ConnectionCreate()
          .status(ConnectionStatus.ACTIVE)
          .sourceId(sourceInstance.getId())
          .destinationId(destinationInstance.getId())
//          .syncCatalog(connection.getSyncCatalog())
//          .schedule(connection.getSchedule())
//          .operationIds(connection.getOperationIds())
          .name(connection.getName())
          .namespaceDefinition(NamespaceDefinitionType.CUSTOMFORMAT)
          .namespaceFormat("output_namespace_${SOURCE_NAMESPACE}")
          .prefix("output_table_");
      ConnectionRead connectionRead = airbyteInstance.getAirbyteApi().getConnectionApi().createConnection(connectionConfig);
      connection.setConnection(connectionRead);
    }
    catch (ApiException e) {
      LOGGER.error("Error creating connection", e);
    }
  }
}
