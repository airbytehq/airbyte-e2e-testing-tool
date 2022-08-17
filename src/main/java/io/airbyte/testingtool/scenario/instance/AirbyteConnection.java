package io.airbyte.testingtool.scenario.instance;

import io.airbyte.api.client.model.generated.AirbyteCatalog;
import io.airbyte.api.client.model.generated.ConnectionRead;
import io.airbyte.api.client.model.generated.ConnectionSchedule;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class AirbyteConnection extends Instance {

  @Getter
  @Setter
  private ConnectionRead connection;

  @Getter
  @Setter
  private String name;

  @Getter
  @Setter
  private AirbyteCatalog syncCatalog;

  @Getter
  @Setter
  private ConnectionSchedule schedule;

  @Getter
  @Setter
  private List<UUID> operationIds;

  @Builder
  public AirbyteConnection(String instanceName) {
    super(instanceName);
  }

  @Override
  public InstanceTypes getInstanceType() {
    return InstanceTypes.CONNECTION;
  }

  public void configureConnection(AirbyteInstance airbyteInstance, SourceInstance sourceInstance, DestinationInstance destinationInstance) {

  }

}
