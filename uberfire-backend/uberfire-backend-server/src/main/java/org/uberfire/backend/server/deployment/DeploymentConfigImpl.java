package org.uberfire.backend.server.deployment;

import org.uberfire.backend.deployment.DeploymentConfig;

public class DeploymentConfigImpl implements DeploymentConfig {

    private String identifier;
    private Object deploymentUnit;

    public DeploymentConfigImpl(String identifier, Object deploymentUnit) {
        this.identifier = identifier;
        this.deploymentUnit = deploymentUnit;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Object getDeploymentUnit() {
        return deploymentUnit;
    }
}
