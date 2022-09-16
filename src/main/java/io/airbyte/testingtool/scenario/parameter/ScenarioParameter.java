package io.airbyte.testingtool.scenario.parameter;

import io.airbyte.testingtool.scenario.config.ActionParameterTypes;
import io.airbyte.testingtool.scenario.config.ScenarioConfigActionParameter;
import lombok.Getter;
import lombok.Setter;

public class ScenarioParameter {

  @Getter
  private final String parameterName;
  @Getter
  private final ActionParameterTypes parameterType;
  @Setter
  private String parameterValue;

  public ScenarioParameter(ScenarioConfigActionParameter scenarioParameter) {
    this.parameterName = scenarioParameter.getName();
    this.parameterType = scenarioParameter.getType();
  }

  public String getParameterValue() {
    if (parameterValue != null) {
      return parameterValue;
    } else {
      throw new RuntimeException(String.format("Parameter %s(%s) is not set yet!", parameterName, parameterType));
    }
  }
}
