package io.airbyte.testingtool.scenario.instance.autonomous.mysql;

import static io.airbyte.testingtool.scenario.instance.autonomous.mysql.utils.MysqlUserGrantUtils.setUpUserGrants;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.airbyte.testingtool.json.Jsons;
import io.airbyte.testingtool.scenario.config.credentials.CredentialConfig.InstanceCredTypes;
import io.airbyte.testingtool.scenario.instance.autonomous.TestContainerLocalInstance;
import io.airbyte.testingtool.utils.HostPortResolver;
import java.util.function.Supplier;
import org.testcontainers.containers.MySQLContainer;

public class SourceMysqlLocalInstance extends TestContainerLocalInstance<MySQLContainer<?>> {

  @Override
  public InstanceCredTypes getInstanceCredType() {
    return InstanceCredTypes.SOURCE_CREDS;
  }

  @Override
  protected JsonNode getInstanceConfig(MySQLContainer<?> instance) {
    setUpUserGrants(instance);
    final JsonNode replicationMethod = Jsons.jsonNode(ImmutableMap.builder()
        .put("method", "STANDARD")
        .build());
    return Jsons.jsonNode(ImmutableMap.builder()
        .put("host", HostPortResolver.resolveHost(instance))
        .put("username", instance.getUsername())
        .put("password", instance.getPassword())
        .put("port", HostPortResolver.resolvePort(instance))
        .put("database", instance.getDatabaseName())
        .put("replication_method", replicationMethod)
        .put("ssl", false)
        .build());
  }

  @Override
  protected Supplier<MySQLContainer<?>> getContainerSupplier() {
    return () -> new MySQLContainer<>("mysql:8.0");
  }
}
