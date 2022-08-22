package io.airbyte.testingtool.argument_parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArgumentParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArgumentParser.class);
    private static final String PARSING_ARGUMENT_CHAR = "=";
    private static final String COMMAND_START_CHAR = "/";

    public static Map<String, String> argumentParser(String[] args) {
        return Stream.of(args).collect(Collectors.toMap(ArgumentParser::getArgumentKey, ArgumentParser::getArgumentValue));
    }

    private static String getArgumentKey(String arg) {
        return arg.startsWith(COMMAND_START_CHAR) ? arg : splitAndGetArgumentPart(arg, true);
    }

    private static String getArgumentValue(String arg) {
        return arg.startsWith(COMMAND_START_CHAR) ? arg : splitAndGetArgumentPart(arg, false);
    }

    private static String splitAndGetArgumentPart(String arg, boolean isKey) {
        return isKey ? List.of(arg.split(PARSING_ARGUMENT_CHAR)).get(0) : List.of(arg.split(PARSING_ARGUMENT_CHAR)).get(1);
    }
}
