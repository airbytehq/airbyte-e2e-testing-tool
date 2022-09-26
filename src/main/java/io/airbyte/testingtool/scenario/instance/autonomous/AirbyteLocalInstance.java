package io.airbyte.testingtool.scenario.instance.autonomous;

import com.fasterxml.jackson.databind.JsonNode;
import io.airbyte.testingtool.scenario.config.credentials.CredentialConfig.InstanceCredTypes;

public class AirbyteLocalInstance implements LocalInstance{

  @Override
  public JsonNode startLocalInstance() {
    return null; // @TODO A.Korotkov please provide local creds to the instance here
  }

  @Override
  public InstanceCredTypes getInstanceCredType() {
    return InstanceCredTypes.AIRBYTE_CREDS;
  }
}
