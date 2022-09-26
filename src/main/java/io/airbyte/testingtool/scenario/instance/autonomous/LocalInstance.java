package io.airbyte.testingtool.scenario.instance.autonomous;

import com.fasterxml.jackson.databind.JsonNode;
import io.airbyte.testingtool.scenario.config.credentials.CredentialConfig.InstanceCredTypes;

public interface LocalInstance {

  /**
   * Starts local instance.
   *
   * @return Json with credentials to the instance. We place it into `CredentialConfig:credentialJson`
   */
  JsonNode startLocalInstance();

  InstanceCredTypes getInstanceCredType();

}
