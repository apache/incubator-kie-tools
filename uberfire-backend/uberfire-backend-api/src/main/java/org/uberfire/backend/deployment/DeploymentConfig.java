package org.uberfire.backend.deployment;

public interface DeploymentConfig {

    String getIdentifier();

    Object getDeploymentUnit();
}
