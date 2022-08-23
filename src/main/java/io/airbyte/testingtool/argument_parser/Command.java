package io.airbyte.testingtool.argument_parser;

import java.util.EnumSet;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Command {
  RUN_SCENARIO("/run-scenario"),
  RUN_HELP("/help"),
  RUN_FULL_HELP("/help-full"),
  ;

  private static final Logger LOGGER = LoggerFactory.getLogger(Command.class);

  @Getter
  private final String command;

  Command(String command) {
    this.command = command;
  }

  public static Command getCommand(String command) {
    var optionalCommand = EnumSet.allOf(Command.class).stream()
        .filter(com -> com.command.equals(command))
        .findFirst();
    if (optionalCommand.isPresent()) {
      return optionalCommand.get();
    } else {
      LOGGER.error("Command with name - \"{}\" does not support", command);
      throw new RuntimeException(String.format("Command with name - \"%s\" does not support", command));
    }
  }
}
