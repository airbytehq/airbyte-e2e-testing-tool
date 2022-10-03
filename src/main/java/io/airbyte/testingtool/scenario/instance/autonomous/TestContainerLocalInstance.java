package io.airbyte.testingtool.scenario.instance.autonomous;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.function.Supplier;
import org.testcontainers.containers.GenericContainer;

public abstract class TestContainerLocalInstance<T extends GenericContainer<?>> implements LocalInstance {

  protected abstract JsonNode getInstanceConfig(T instance);

  protected abstract Supplier<T> getContainerSupplier();

  @Override
  public final JsonNode startLocalInstance() {
    var instance = startInstance(getContainerSupplier());
    return getInstanceConfig(instance);
  }

  private T startInstance(Supplier<T> supplier) {
    T instance = supplier.get();
    instance.start();
    return instance;
  }
}
