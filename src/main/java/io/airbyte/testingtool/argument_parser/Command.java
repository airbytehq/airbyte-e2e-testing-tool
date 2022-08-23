package io.airbyte.testingtool.argument_parser;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

public enum Command {
    RUN_SCENARIO("/run-scenario"),
    RUN_HELP("/run-help");

    private static final Logger LOGGER = LoggerFactory.getLogger(Command.class);

    @Getter
    private final String command;

    Command(String command) {
        this.command = command;
    }

    public static String getCommandName(String command) {
        var optionalCommand = EnumSet.allOf(Command.class).stream()
                .filter(com -> com.getCommand().equals(command))
                .findFirst();
        if (optionalCommand.isPresent()) {
            return optionalCommand.get().name();
        } else {
            LOGGER.error("Command \"{}\" does not support", command);
            throw new RuntimeException(String.format("Command \"%s\" does not support", command));
        }
    }

    public static Command getCommandByName(String name) {
        var optionalCommand = EnumSet.allOf(Command.class).stream()
                .filter(com -> com.name().equals(name))
                .findFirst();
        if (optionalCommand.isPresent()) {
            return optionalCommand.get();
        } else {
            LOGGER.error("Command with name - \"{}\" does not support", name);
            throw new RuntimeException(String.format("Command with name - \"%s\" does not support", name));
        }
    }
}
