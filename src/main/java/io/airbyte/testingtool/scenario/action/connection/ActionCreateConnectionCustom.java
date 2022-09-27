package io.airbyte.testingtool.scenario.action.connection;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.AirbyteCatalog;
import io.airbyte.api.client.model.generated.ConnectionCreate;
import io.airbyte.api.client.model.generated.SourceDiscoverSchemaRequestBody;
import io.airbyte.testingtool.scenario.config.settings.AirbyteStreamAndConfigSettings;
import io.airbyte.testingtool.scenario.config.settings.ConnectionSettings;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import io.airbyte.testingtool.scenario.instance.SourceWithSettingsInstance;
import lombok.Builder;

import java.util.List;
import java.util.Objects;

public class ActionCreateConnectionCustom extends ActionCreateConnection {

  @Builder(builderMethodName = "actionCreateConnectionCustomBuilder")
  public ActionCreateConnectionCustom(int order, List<Instance> requiredInstances, Instance resultInstance, AirbyteConnection connectionInstance,
      DestinationInstance destinationInstance, SourceInstance sourceInstance) {
    super(order, requiredInstances, resultInstance, connectionInstance, destinationInstance, sourceInstance);
  }

  @Override
  public String getActionName() {
    return "Create custom connection";
  }

  @Override
  protected ConnectionCreate getConnectionCreateConfig() throws ApiException {
    var requestBody = new SourceDiscoverSchemaRequestBody();
    requestBody.setSourceId(sourceInstance.getId());
    requestBody.setDisableCache(true);
    var sourceDiscoverSchema = getAirbyteApiInstance().getAirbyteApi()
        .getSourceApi()
        .discoverSchemaForSourceWithHttpInfo(requestBody);
    var catalog = sourceDiscoverSchema.getData().getCatalog();
    customizationCatalog(catalog, getSettings().getSyncCatalogConfig().getStreams());
    return super.getConnectionCreateConfig()
        .name(getSettings().getConnectionName())
        .syncCatalog(catalog);
  }

  @Override
  protected boolean getNormalizationFlag() {
    return Objects.nonNull(getSettings().getNormalization()) ? getSettings().getNormalization() : true;
  }

  private ConnectionSettings getSettings() {
    return ((SourceWithSettingsInstance) this.sourceInstance).getConnectionSettings();
  }

  private void customizationCatalog(AirbyteCatalog catalog,
      List<AirbyteStreamAndConfigSettings> customStreams) {
    catalog.getStreams().forEach(defaultStream -> {
      if (Objects.nonNull(Objects.requireNonNull(defaultStream.getStream()).getName())) {
        var streamName = Objects.requireNonNull(defaultStream.getStream()).getName();
        var streamNamespace = Objects.requireNonNull(defaultStream.getStream()).getNamespace();
        var optionalStreamCustom = customStreams.stream().filter(customStream -> customStream.getStream().getName().equals(streamName) &&
                (Objects.nonNull(customStream.getStream().getNamespace()) || Objects.nonNull(streamNamespace) ||
                    customStream.getStream().getNamespace().equals(streamName)))
            .findFirst();
        if (optionalStreamCustom.isPresent()) {
          var streamCustom = optionalStreamCustom.get();
          if (Objects.nonNull(streamCustom.getConfig().getAliasName())) {
            Objects.requireNonNull(defaultStream.getConfig()).setAliasName(streamCustom.getConfig().getAliasName());
          }
          if (Objects.nonNull(streamCustom.getConfig().getSyncMode())) {
            Objects.requireNonNull(defaultStream.getConfig()).setSyncMode(streamCustom.getConfig().getSyncMode());
          }
          if (Objects.nonNull(streamCustom.getConfig().getDestinationSyncMode())) {
            Objects.requireNonNull(defaultStream.getConfig()).setDestinationSyncMode(streamCustom.getConfig().getDestinationSyncMode());
          }
          if (Objects.nonNull(streamCustom.getConfig().getCursorField())) {
            Objects.requireNonNull(defaultStream.getConfig()).setCursorField(streamCustom.getConfig().getCursorField());
          }
          if (Objects.nonNull(streamCustom.getConfig().getPrimaryKey())) {
            Objects.requireNonNull(defaultStream.getConfig()).setPrimaryKey(streamCustom.getConfig().getPrimaryKey());
          }
        } else {
          Objects.requireNonNull(defaultStream.getConfig()).setSelected(false);
        }
      }
    });
  }
}
