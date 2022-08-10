package io.airbyte.testingtool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingTool {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestingTool.class);

  public static void main(String[] args) {
    LOGGER.warn("Testing tool started!");
    LOGGER.warn("Testing tool finished!");
  }

}
