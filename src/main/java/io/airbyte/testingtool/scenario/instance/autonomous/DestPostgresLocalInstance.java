package io.airbyte.testingtool.scenario.instance.autonomous;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.airbyte.testingtool.json.Jsons;
import io.airbyte.testingtool.scenario.config.credentials.CredentialConfig.InstanceCredTypes;
import io.airbyte.testingtool.utils.HostPortResolver;
import java.util.function.Supplier;
import org.testcontainers.containers.PostgreSQLContainer;

class DestPostgresLocalInstance extends TestContainerLocalInstance<PostgreSQLContainer<?>> {

  @Override
  protected JsonNode getInstanceConfig(PostgreSQLContainer<?> instance) {
    return Jsons.jsonNode(ImmutableMap.builder()
        .put("host", HostPortResolver.resolveHost(instance))
        .put("username", instance.getUsername())
        .put("password", instance.getPassword())
        .put("schema", "public")
        .put("port", HostPortResolver.resolvePort(instance))
        .put("database", instance.getDatabaseName())
        .put("ssl", false)
        .put("tunnel_method", Jsons.deserialize("{\"tunnel_method\":\"NO_TUNNEL\"}"))
        .build());
  }

  @Override
  protected Supplier<PostgreSQLContainer<?>> getContainerSupplier() {
    return () -> new PostgreSQLContainer<>("postgres:13-alpine");
  }

  @Override
  public InstanceCredTypes getInstanceCredType() {
    return InstanceCredTypes.DESTINATION_CREDS;
  }

}
