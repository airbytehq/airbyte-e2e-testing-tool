package io.airbyte.testingtool.scenario.action;

import lombok.Getter;

public enum ActionStatuses {
  NOT_EXECUTED(false),
  OK(false),
  FAILED(true),
  SKIPPED(false);

  @Getter
  private boolean isFailure;

  ActionStatuses(boolean isFailure) {
    this.isFailure = isFailure;
  }
}
