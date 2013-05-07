package org.uberfire.backend.deployment;

import org.uberfire.backend.server.config.ConfigGroup;

public interface DeploymentConfigFactory {

    DeploymentConfig newDeployment(ConfigGroup groupConfig);
}
