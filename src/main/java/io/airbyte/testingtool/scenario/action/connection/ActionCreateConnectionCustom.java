package io.airbyte.testingtool.scenario.action.connection;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.api.client.model.generated.ConnectionCreate;
import io.airbyte.testingtool.scenario.config.settings.ConnectionSettings;
import io.airbyte.testingtool.scenario.instance.AirbyteConnection;
import io.airbyte.testingtool.scenario.instance.AirbyteInstance;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceWithSettingsInstance;
import java.util.List;
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
    return super.getConnectionCreateConfig().name(getSettings().getConnectionName()); // @TODO A. Korotkov
  }

  private ConnectionSettings getSettings() {
    return ((SourceWithSettingsInstance)this.sourceInstance).getConnectionSettings();
  }
}
