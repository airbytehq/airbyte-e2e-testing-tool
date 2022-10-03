package io.airbyte.testingtool.utils;

import java.util.Objects;
import org.testcontainers.containers.GenericContainer;

public class HostPortResolver {

  public static String resolveHost(GenericContainer<?> container) {
    return System.getProperty("os.name").toLowerCase().startsWith("mac")
        ? getIpAddress(container)
        : container.getHost();
  }

  public static int resolvePort(GenericContainer<?> container) {
    return System.getProperty("os.name").toLowerCase().startsWith("mac") ? container.getExposedPorts().get(0)
        : container.getFirstMappedPort();
  }

  public static String resolveIpAddress(GenericContainer<?> container) {
    return getIpAddress(container);
  }

  private static String getIpAddress(GenericContainer<?> container) {
    return Objects.requireNonNull(container.getContainerInfo()
        .getNetworkSettings()
        .getNetworks()
        .entrySet().stream()
        .findFirst()
        .get().getValue().getIpAddress());
  }

}