package io.airbyte.testingtool.scenario.action.connection;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.AirbyteStreamAndConfiguration;
import io.airbyte.api.client.model.generated.ConnectionCreate;
import io.airbyte.testingtool.scenario.config.settings.AirbyteCatalogSettings;
import io.airbyte.testingtool.scenario.config.settings.AirbyteStreamAndConfigSettings;
import io.airbyte.testingtool.scenario.config.settings.ConnectionSettings;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceWithSettingsInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Builder;

public class ActionCreateConnectionCustom extends ActionCreateConnection{

  @Builder(builderMethodName = "actionCreateConnectionCustomBuilder")
  public ActionCreateConnectionCustom(int order, List<Instance> requiredInstances,
      Instance resultInstance, AirbyteInstance airbyteInstance,
      AirbyteConnection connection,
      DestinationInstance destinationInstance,
      SourceWithSettingsInstance sourceInstance) {
    super(order, requiredInstances, resultInstance, airbyteInstance, connection, destinationInstance, sourceInstance);
  }

  @Override
  public String getActionName() {
    return "Create custom connection";
  }

  @Override
  protected ConnectionCreate getConnectionCreateConfig() throws ApiException {
    var defaultAirbyteCatalog = sourceInstance.discoverSourceSchema();
    var customStreams = connectionStreamsCustomization(defaultAirbyteCatalog.getStreams(),
            getSettings().getSyncCatalogConfig().getStreams());
    defaultAirbyteCatalog.getStreams().clear();
    defaultAirbyteCatalog.getStreams().addAll(customStreams);
    return super.getConnectionCreateConfig()
            .name(getSettings().getConnectionName())
            .syncCatalog(defaultAirbyteCatalog); // @TODO A. Korotkov
  }

  private ConnectionSettings getSettings() {
    return ((SourceWithSettingsInstance)this.sourceInstance).getConnectionSettings();
  }

  private List<AirbyteStreamAndConfiguration> connectionStreamsCustomization(List<AirbyteStreamAndConfiguration> defaultStreams,
                                                                             List<AirbyteStreamAndConfigSettings> customStreams) {
    List<AirbyteStreamAndConfiguration> customCatalogStreams = new ArrayList<>();
    defaultStreams.forEach(defaultStream -> {
      if (Objects.nonNull(Objects.requireNonNull(defaultStream.getStream()).getName())) {
        var streamName = Objects.requireNonNull(defaultStream.getStream()).getName();
        var streamNamespace = Objects.requireNonNull(defaultStream.getStream()).getNamespace();
        var optionalStreamCustom = customStreams.stream().filter(customStream -> customStream.getStream().getName().equals(streamName) &&
                (Objects.nonNull(customStream.getStream().getNamespace()) || Objects.nonNull(streamNamespace) ||
                        customStream.getStream().getNamespace().equals(streamName)))
                .findFirst();
        if (optionalStreamCustom.isPresent()) {
          var streamCustom = optionalStreamCustom.get();
          var stream = defaultStream;
          Objects.requireNonNull(stream.getConfig()).setSelected(streamCustom.getConfig().getSelected());
          Objects.requireNonNull(stream.getConfig()).setSyncMode(streamCustom.getConfig().getSyncMode());
          Objects.requireNonNull(stream.getConfig()).setCursorField(streamCustom.getConfig().getCursorField());
          Objects.requireNonNull(stream.getConfig()).setPrimaryKey(streamCustom.getConfig().getPrimaryKey());
          Objects.requireNonNull(stream.getConfig()).setDestinationSyncMode(streamCustom.getConfig().getDestinationSyncMode());
          Objects.requireNonNull(stream.getConfig()).setAliasName(streamCustom.getConfig().getAliasName());
          customCatalogStreams.add(stream);
        } else {
          customCatalogStreams.add(defaultStream);
        }
      }
    });
    return customCatalogStreams;
  }
}
