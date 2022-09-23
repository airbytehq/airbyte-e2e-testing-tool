package io.airbyte.testingtool.scenario.action.destination;

import io.airbyte.testingtool.scenario.instance.DestinationInstance;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.parameter.ScenarioParameter;
import java.util.List;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionReadDestinationVersion extends AbstractDestinationAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionReadDestinationVersion.class);

  private final ScenarioParameter version;

  @Builder
  public ActionReadDestinationVersion(int order, List<Instance> requiredInstances, Instance resultInstance, DestinationInstance destinationInstance,
      ScenarioParameter version) {
    super(order, requiredInstances, resultInstance, destinationInstance);
    this.version = version;
  }

  @Override
  protected void doActionInternal() throws Exception {
    version.setParameterValue(destinationInstance.getDockerImageTag());
    LOGGER.info("Read destination version \"{}\" into {} parameter.", version.getParameterValue(), version.getParameterName());
  }

  @Override
  protected String getContextInternal() {
    return "Destination version `" + version.getParameterValue() + "` -> **" + version.getParameterName() + "**";
  }

  @Override
  public String getActionName() {
    return "Read destination version";
  }
}
