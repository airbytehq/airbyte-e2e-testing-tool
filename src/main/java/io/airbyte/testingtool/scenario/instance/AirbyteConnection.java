package io.airbyte.testingtool.scenario.instance;

import io.airbyte.api.client.model.generated.ConnectionIdRequestBody;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

}
