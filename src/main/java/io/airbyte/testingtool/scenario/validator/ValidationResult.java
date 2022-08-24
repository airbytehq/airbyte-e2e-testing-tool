package io.airbyte.testingtool.scenario.validator;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Builder
@Getter
public class ValidationResult {

  private String validationName;
  private String errorText;

  public boolean isFailed() {
    return errorText != null && !errorText.isEmpty();
  }

  public String getFormattedSummary(int longestValidationName) {
    return StringUtils.rightPad("  [" + this.getValidationName() + "]", longestValidationName + 5) + " : "
        + StringUtils.rightPad((this.isFailed() ? "FAILED!" : "Ok"), 8) + (this.isFailed() ? " : " + errorText : "");
  }
}
