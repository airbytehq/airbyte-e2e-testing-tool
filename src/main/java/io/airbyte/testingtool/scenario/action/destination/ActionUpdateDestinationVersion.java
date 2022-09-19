package io.airbyte.testingtool.scenario.action.destination;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.testingtool.scenario.action.ScenarioAction;
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
public class ActionUpdateDestinationVersion extends ScenarioAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      ActionUpdateDestinationVersion.class);

  private final DestinationInstance destinationInstance;
  private final ScenarioParameter version;

  @Builder
  public ActionUpdateDestinationVersion(int order, List<Instance> requiredInstances,
      Instance resultInstance, DestinationInstance destinationInstance, ScenarioParameter version) {
    super(order, requiredInstances, resultInstance);
    this.destinationInstance = destinationInstance;
    this.version = version;
  }

  @Override
  protected void doActionInternal() throws Exception {
    updateDestinationVersion();
  }

  @Override
  public String getActionName() {
    return "Update Destination version";
  }

  private void updateDestinationVersion() throws ApiException {
    LOGGER.info("Start updating Source version to  \"{}\"", version.getParameterValue());
    destinationInstance.setDockerImageTag(version.getParameterValue());
    context = "New destination version `" + version.getParameterValue() + "` from (**" + version.getParameterName() + "**)";
    LOGGER.info(context);
  }
}
