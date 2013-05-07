package org.uberfire.backend.server.deployment;

import org.uberfire.backend.deployment.DeploymentConfig;
import org.uberfire.backend.deployment.DeploymentConfigFactory;
import org.uberfire.backend.server.config.ConfigGroup;

public class DeploymentConfigFactoryImpl implements DeploymentConfigFactory {

    @Override
    public DeploymentConfig newDeployment(ConfigGroup groupConfig) {
        return new DeploymentConfigImpl(groupConfig.getName(), groupConfig.getConfigItem("unit").getValue());
    }
}
