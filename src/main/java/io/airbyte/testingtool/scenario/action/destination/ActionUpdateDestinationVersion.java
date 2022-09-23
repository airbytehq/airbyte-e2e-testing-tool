package io.airbyte.testingtool.scenario.action.destination;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.parameter.ScenarioParameter;
import java.util.List;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scenario action. The action changes destination version to a specific value.
 */
public class ActionUpdateDestinationVersion extends AbstractDestinationAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      ActionUpdateDestinationVersion.class);

  private final ScenarioParameter version;

  @Builder
  public ActionUpdateDestinationVersion(int order, List<Instance> requiredInstances, Instance resultInstance, DestinationInstance destinationInstance,
      ScenarioParameter version) {
    super(order, requiredInstances, resultInstance, destinationInstance);
    this.version = version;
  }

  @Override
  protected void doActionInternal() throws Exception {
    updateDestinationVersion();
  }

  @Override
  protected String getContextInternal() {
    return "Destination version `" + version.getParameterValue() + "` -> **" + version.getParameterName() + "**";
  }

  @Override
  public String getActionName() {
    return "Update Destination version";
  }

  private void updateDestinationVersion() {
    LOGGER.info("Start updating Source version to  \"{}\"", version.getParameterValue());
    try {
      destinationInstance.setDockerImageTag(version.getParameterValue());
    } catch (ApiException e) {
      throw new RuntimeException(
          "Fail to set version `" + version.getParameterValue() + "` to the destination `" + destinationInstance.getAribyteDestinationTypeName()
              + "`!");
    }
  }
}
