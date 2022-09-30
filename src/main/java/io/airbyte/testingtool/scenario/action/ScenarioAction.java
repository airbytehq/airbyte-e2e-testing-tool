package io.airbyte.testingtool.scenario.action;

import io.airbyte.testingtool.scenario.instance.Instance;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ScenarioAction implements Comparable<ScenarioAction> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioAction.class);
  @Getter
  public final int order;
  private final List<Instance> requiredInstances;
  private final Instance resultInstance;
  @Getter
  private String context;
  private Duration duration;
  @Getter
  private ActionStatuses status;
  private boolean isExecuted = false;

  public ScenarioAction(int order, List<Instance> requiredInstances, Instance resultInstance) {
    this.order = order;
    this.requiredInstances = requiredInstances;
    this.resultInstance = resultInstance;
    this.status = ActionStatuses.NOT_EXECUTED;
  }

  @Override
  public int compareTo(ScenarioAction o) {
    return Integer.compare(order, o.getOrder());
  }

  public void doAction() {
    Instant start = Instant.now();
    if (isRepeatable() || !isExecuted) {
      try {
        checkAllRequiredInstancesInitialized();
        doActionInternal();
        status = ActionStatuses.OK;
        markResultInstanceAsInitialized();
        context = getContextInternal();
        LOGGER.info("Action `{}` context : {}", getActionName(), context);
      } catch (Exception e) {
        status = ActionStatuses.FAILED;
        context = e.getMessage();
      }
      isExecuted = true;
    } else {
      LOGGER.error("Action {} can't be executed one more time!", getActionName());
    }
    duration = Duration.between(start, Instant.now());
  }

  protected abstract String getContextInternal();

  private void markResultInstanceAsInitialized() {
    if (resultInstance != null) {
      resultInstance.setInitialized(true);
    }
  }

  private void checkAllRequiredInstancesInitialized() {
    var notInitializedInstances = requiredInstances.stream()
        .filter(instance -> instance.getInstanceType().isInitializationIsRequired() && !instance.isInitialized()).map(Instance::getInstanceName)
        .toList();
    if (!notInitializedInstances.isEmpty()) {
      throw new RuntimeException("Not all required instances are initialized : " + String.join(", ", notInitializedInstances));
    }
  }

  protected abstract void doActionInternal() throws Exception;

  public abstract String getActionName();

  public boolean isRepeatable() {
    return true;
  }

  public long getDurationSec() {
    return (duration != null ? duration.getSeconds() : 0);
  }

}
