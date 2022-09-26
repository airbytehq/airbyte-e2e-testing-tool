package io.airbyte.testingtool.scenario.instance.autonomous;

import com.fasterxml.jackson.databind.JsonNode;
import io.airbyte.testingtool.scenario.config.credentials.CredentialConfig.InstanceCredTypes;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AutonomousInstances {

  AIRBYTE(AirbyteLocalInstance.class);

  private final Class<? extends LocalInstance> localInstanceClass;

  public JsonNode startLocalInstance(InstanceCredTypes expectedInstanceCredType)
      throws Exception {
    var localInstance = localInstanceClass.getDeclaredConstructor().newInstance();
    if (localInstance.getInstanceCredType().equals(expectedInstanceCredType)) {
      return localInstance.startLocalInstance();
    } else {
      throw new RuntimeException(
          "The credential contains mismatch between credential type `" + expectedInstanceCredType + "` and autonomous instance providing cred type `"
              + localInstance.getInstanceCredType() + "`");
    }
  }

}
