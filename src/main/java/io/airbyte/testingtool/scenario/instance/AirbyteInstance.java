package io.airbyte.testingtool.scenario.instance;

import io.airbyte.api.client.AirbyteApiClient;
import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.testingtool.scenario.config.CredentialConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;

public class AirbyteInstance extends InstanceWithCredentials {
  private static final Logger LOGGER = LoggerFactory.getLogger(AirbyteInstance.class);

  private UUID workspaceId;

  @Getter
  @Setter
  private AirbyteApiClient airbyteApi;

  @Builder
  public AirbyteInstance(String instanceName, CredentialConfig credentialConfig) {
    super(instanceName, credentialConfig);
  }

  @Override
  public InstanceTypes getInstanceType() {
    return InstanceTypes.AIRBYTE;
  }

  public UUID getWorkspaceId() {
    if (Objects.isNull(workspaceId)) {
      workspaceId = getWorkspaceIdFromAirbyteApi();
    }
    return workspaceId;
  }

  public UUID getSourceDefinitionId(String definitionName) {
    try {
      var sourceDefinitionsList = airbyteApi.getSourceDefinitionApi()
              .listSourceDefinitions()
              .getSourceDefinitions();
      var optionalSourceDefinition = sourceDefinitionsList
              .stream()
              .filter(sourceDefinition -> definitionName.equals(sourceDefinition.getName()))
              .findFirst();
      if (optionalSourceDefinition.isPresent()) {
        return optionalSourceDefinition.get().getSourceDefinitionId();
      } else {
        LOGGER.error("There are no source definition available with name \"{}\"", definitionName);
        throw new RuntimeException(String.format("There are no source definition available with name \"%s\"", definitionName));
      }
    } catch (ApiException e) {
      LOGGER.error("Fail to airbyte client connect");
      throw new RuntimeException("Fail to airbyte client connect", e);
    }
  }

  public UUID getDestinationDefinitionId(String definitionName) {
    try {
      var destinationDefinitionsList = airbyteApi.getDestinationDefinitionApi()
              .listDestinationDefinitions()
              .getDestinationDefinitions();
      var optionalDestinationDefinition = destinationDefinitionsList
              .stream()
              .filter(destinationDefinition -> definitionName.equals(destinationDefinition.getName()))
              .findFirst();
      if (optionalDestinationDefinition.isPresent()) {
        return optionalDestinationDefinition.get().getDestinationDefinitionId();
      } else {
        LOGGER.error("There are no destination definition available with name \"{}\"", definitionName);
        throw new RuntimeException(String.format("There are no destination definition available with name \"%s\"", definitionName));
      }
    } catch (ApiException e) {
      LOGGER.error("Fail to airbyte client connect");
      throw new RuntimeException("Fail to airbyte client connect", e);
    }
  }

  /* Helpers */

  private UUID getWorkspaceIdFromAirbyteApi() {
    try {
      // todo: Now, we use the first workspace from the list. In the future, it should be replaced, and get workspace by slug name.
      var optionalWorkspaceReader = airbyteApi.getWorkspaceApi()
              .listWorkspaces()
              .getWorkspaces()
              .stream()
              .findFirst();
      if (optionalWorkspaceReader.isPresent()) {
        return optionalWorkspaceReader.get().getWorkspaceId();
      } else {
        LOGGER.error("There are no workspaces available");
        throw new RuntimeException("There are no workspaces available");
      }
    } catch (ApiException e) {
      LOGGER.error("Fail to airbyte client connect");
      throw new RuntimeException("Fail to airbyte client connect", e);
    }
  }
}
