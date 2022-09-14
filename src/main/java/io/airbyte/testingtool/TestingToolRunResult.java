package io.airbyte.testingtool;

import lombok.Builder;
import lombok.Getter;

@Builder
public class TestingToolRunResult {

  @Getter
  private final boolean isSuccessful;
  @Getter
  private final String runText;
}
