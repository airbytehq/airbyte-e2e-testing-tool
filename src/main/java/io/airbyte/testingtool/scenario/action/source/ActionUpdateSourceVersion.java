package io.airbyte.testingtool.scenario.action.source;

import io.airbyte.api.client.invoker.generated.ApiException;
import io.airbyte.testingtool.scenario.action.ScenarioAction;
import io.airbyte.testingtool.scenario.instance.Instance;
import io.airbyte.testingtool.scenario.instance.SourceInstance;
import io.airbyte.testingtool.scenario.parameter.ScenarioParameter;
import java.util.List;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Scenario action. The action changes source version to a specific value.
 */
public class ActionUpdateSourceVersion extends ScenarioAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateSourceVersion.class);

  private final SourceInstance sourceInstance;
  private final ScenarioParameter version;

  @Builder
  public ActionUpdateSourceVersion(int order, List<Instance> requiredInstances,
      Instance resultInstance, SourceInstance sourceInstance,
      ScenarioParameter version) {
    super(order, requiredInstances, resultInstance);
    this.sourceInstance = sourceInstance;
    this.version = version;
  }

  @Override
  protected void doActionInternal() throws Exception {
    updateSourceVersion();
  }

  @Override
  public String getActionName() {
    return "Update Source version";
  }

  private void updateSourceVersion() {
    LOGGER.info("Start updating Source version to  \"{}\"", version.getParameterValue());
    try {
      sourceInstance.setDockerImageTag(version.getParameterValue());
    } catch (ApiException e) {
      throw new RuntimeException(
          "Fail to set version `" + version.getParameterValue() + "` to the source `" + sourceInstance.getAribyteSourceTypeName() + "`!");
    }
    context = "New Source version `" + version.getParameterValue() + "` from (**" + version.getParameterName() + "**)";
    LOGGER.info(context);
  }
}
