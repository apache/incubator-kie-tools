package org.uberfire.backend.server.deployment;

import org.uberfire.backend.deployment.DeploymentConfig;
import org.uberfire.backend.server.config.ConfigGroup;

public interface DeploymentConfigFactory {

    DeploymentConfig newDeployment(ConfigGroup groupConfig);
}
