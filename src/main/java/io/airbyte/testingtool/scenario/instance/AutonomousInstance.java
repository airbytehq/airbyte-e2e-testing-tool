package io.airbyte.testingtool.scenario.instance;

import io.airbyte.testingtool.scenario.config.credentials.CredentialConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * If instance extends this class it becomes optional instance parameter.
 */
public abstract class AutonomousInstance extends InstanceWithCredentials {

  private static final Logger LOGGER = LoggerFactory.getLogger(AutonomousInstance.class);

  public AutonomousInstance(String instanceName, CredentialConfig credentialConfig) throws Exception {
    super(instanceName, credentialConfig);
    var autonomousInstance = credentialConfig.getLocalInstanceType();
    if (autonomousInstance != null) {
      credentialConfig.setCredentialJson(autonomousInstance.startLocalInstance(credentialConfig.getCredentialType()));
      LOGGER.info("Local instance {} is up and running.", this.instanceName);
    }
  }
}
