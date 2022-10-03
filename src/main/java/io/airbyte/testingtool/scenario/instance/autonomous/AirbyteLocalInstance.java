package io.airbyte.testingtool.scenario.instance.autonomous;

import com.fasterxml.jackson.databind.JsonNode;
import io.airbyte.testingtool.json.Jsons;
import io.airbyte.testingtool.scenario.config.credentials.CredentialConfig.InstanceCredTypes;

class AirbyteLocalInstance implements LocalInstance {

  @Override
  public JsonNode startLocalInstance() {
    var defaultJsonCreds = "{\"apiHost\" : \"172.17.0.1\", \"apiPort\" : \"8000\", \"apiScheme\" : \"http\"}";
    return Jsons.deserialize(defaultJsonCreds);
  }

  @Override
  public InstanceCredTypes getInstanceCredType() {
    return InstanceCredTypes.AIRBYTE_CREDS;
  }
}
