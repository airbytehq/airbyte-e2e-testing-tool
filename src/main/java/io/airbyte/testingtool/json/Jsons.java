/*
 * Copyright (c) 2022 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.testingtool.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class Jsons {

  // Object Mapper is thread-safe
  private static final ObjectMapper OBJECT_MAPPER = initMapper();
  private static final ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writer(new JsonPrettyPrinter());

  private static ObjectMapper initMapper() {
    final ObjectMapper result = new ObjectMapper().registerModule(new JavaTimeModule());
    result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return result;
  }

  public static <T> String serialize(final T object) {
    try {
      return OBJECT_MAPPER.writeValueAsString(object);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T deserialize(final String jsonString, final Class<T> klass) {
    try {
      return OBJECT_MAPPER.readValue(jsonString, klass);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T convertValue(final Object object, final Class<T> klass) {
    return OBJECT_MAPPER.convertValue(object, klass);
  }

  public static JsonNode deserialize(final String jsonString) {
    try {
      return OBJECT_MAPPER.readTree(jsonString);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> Optional<T> tryDeserialize(final String jsonString, final Class<T> klass) {
    try {
      return Optional.of(OBJECT_MAPPER.readValue(jsonString, klass));
    } catch (final Throwable e) {
      return Optional.empty();
    }
  }

  public static Optional<JsonNode> tryDeserialize(final String jsonString) {
    try {
      return Optional.of(OBJECT_MAPPER.readTree(jsonString));
    } catch (final Throwable e) {
      return Optional.empty();
    }
  }

  public static <T> JsonNode jsonNode(final T object) {
    return OBJECT_MAPPER.valueToTree(object);
  }

  public static JsonNode emptyObject() {
    return jsonNode(Collections.emptyMap());
  }

  public static ArrayNode arrayNode() {
    return OBJECT_MAPPER.createArrayNode();
  }

  public static <T> T object(final JsonNode jsonNode, final Class<T> klass) {
    return OBJECT_MAPPER.convertValue(jsonNode, klass);
  }

  public static <T> T object(final JsonNode jsonNode, final TypeReference<T> typeReference) {
    return OBJECT_MAPPER.convertValue(jsonNode, typeReference);
  }

  public static <T> Optional<T> tryObject(final JsonNode jsonNode, final Class<T> klass) {
    try {
      return Optional.of(OBJECT_MAPPER.convertValue(jsonNode, klass));
    } catch (final Exception e) {
      return Optional.empty();
    }
  }

  public static <T> Optional<T> tryObject(final JsonNode jsonNode, final TypeReference<T> typeReference) {
    try {
      return Optional.of(OBJECT_MAPPER.convertValue(jsonNode, typeReference));
    } catch (final Exception e) {
      return Optional.empty();
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T clone(final T object) {
    return (T) deserialize(serialize(object), object.getClass());
  }

  public static String toPrettyString(final JsonNode jsonNode) {
    try {
      return OBJECT_WRITER.writeValueAsString(jsonNode) + "\n";
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * By the Jackson DefaultPrettyPrinter prints objects with an extra space as follows: {"name" :
   * "airbyte"}. We prefer {"name": "airbyte"}.
   */
  private static class JsonPrettyPrinter extends DefaultPrettyPrinter {

    // this method has to be overridden because in the superclass it checks that it is an instance of
    // DefaultPrettyPrinter (which is no longer the case in this inherited class).
    @Override
    public DefaultPrettyPrinter createInstance() {
      return new DefaultPrettyPrinter(this);
    }

    // override the method that inserts the extra space.
    @Override
    public DefaultPrettyPrinter withSeparators(final Separators separators) {
      _separators = separators;
      _objectFieldValueSeparatorWithSpaces = separators.getObjectFieldValueSeparator() + " ";
      return this;
    }

  }

}
