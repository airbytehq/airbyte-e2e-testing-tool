package io.airbyte.testingtool.scenario.validator.validations;

import io.airbyte.testingtool.scenario.config.ScenarioConfig;
import io.airbyte.testingtool.scenario.validator.ValidationResult;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class AbstractValidation {

  @Getter(AccessLevel.PROTECTED)
  private final ScenarioConfig scenarioConfig;

  private final List<String> errors = new ArrayList<>();

  public ValidationResult validate() {
    errors.clear();
    validateInternal(errors);
    return ValidationResult.builder()
        .validationName(getValidationName())
        .errorText(String.join("; ", errors))
        .build();
  }

  protected abstract String getValidationName();

  protected abstract void validateInternal(List<String> errors);

}
