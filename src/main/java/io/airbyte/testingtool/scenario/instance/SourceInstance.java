package io.airbyte.testingtool.scenario.instance;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.AirbyteCatalog;
import io.airbyte.api.client.model.generated.SourceDiscoverSchemaRequestBody;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class SourceInstance extends InstanceWithCredentials {

  @Getter
  @Setter
  protected UUID id;

  @Getter
  @Setter
  protected AirbyteInstance airbyteInstance;

  @Builder
  public SourceInstance(String instanceName, CredentialConfig credentialConfig) {
    super(instanceName, credentialConfig);
  }

  @Override
  public InstanceTypes getInstanceType() {
    return InstanceTypes.SOURCE;
  }

  public AirbyteCatalog discoverSourceSchema() throws ApiException {
    if (isInitialized()) {
      return airbyteInstance.getAirbyteApi().getSourceApi().discoverSchemaForSource(new SourceDiscoverSchemaRequestBody().sourceId(id)).getCatalog();
    } else {
      throw new RuntimeException("The source should be initialized by an action first!");
    }
  }

}
