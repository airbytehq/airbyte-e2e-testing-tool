package io.airbyte.testingtool.scenario.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ActionStatuses {
  NOT_EXECUTED(false, ":white_circle:"),
  OK(false, ":green_circle:"),
  FAILED(true, ":red_circle:"),
  SKIPPED(false, ":white_circle:");

  @Getter
  private final boolean isFailure;
  @Getter
  private final String name;

}
