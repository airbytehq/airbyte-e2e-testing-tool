package io.airbyte.testingtool.scenario.action.source;

import io.airbyte.testingtool.scenario.action.ScenarioAction;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import io.airbyte.testingtool.scenario.parameter.ScenarioParameter;
import java.util.List;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionReadSourceVersion extends ScenarioAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionReadSourceVersion.class);

  private final SourceInstance sourceInstance;
  private final ScenarioParameter version;

  @Builder
  public ActionReadSourceVersion(int order, List<Instance> requiredInstances,
      Instance resultInstance, SourceInstance sourceInstance, ScenarioParameter version) {
    super(order, requiredInstances, resultInstance);
    this.sourceInstance = sourceInstance;
    this.version = version;
  }

  @Override
  protected void doActionInternal() throws Exception {
    version.setParameterValue(sourceInstance.getDockerImageTag());
    LOGGER.info("Read source version \"{}\" into {} parameter.", version.getParameterValue(), version.getParameterName());
    context = "Source version `" + version.getParameterValue() + "` -> **" + version.getParameterName() + "**";
  }

  @Override
  public String getActionName() {
    return "Read source version";
  }
}
