package org.uberfire.backend.server.deployment;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.backend.deployment.DeploymentConfig;
import org.uberfire.backend.deployment.DeploymentConfigService;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;

@ApplicationScoped
public class DeploymentConfigServiceImpl implements DeploymentConfigService {

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @Inject
    private DeploymentConfigFactory deploymentFactory;

    private Map<String, DeploymentConfig> registeredDeployments = new HashMap<String, DeploymentConfig>();

    @PostConstruct
    public void loadGroups() {
        Collection<ConfigGroup> deployments = configurationService.getConfiguration(ConfigType.DEPLOYMENT);
        if (deployments != null) {
            for (ConfigGroup deploymentConfig : deployments) {
                DeploymentConfig deployment = deploymentFactory.newDeployment(deploymentConfig);
                registeredDeployments.put(deployment.getIdentifier(), deployment);
            }
        }
    }

    @Override
    public void addDeployment(String identifier, Object deploymentUnit) {
        ConfigGroup deploymentConfig = configurationFactory.newConfigGroup(ConfigType.DEPLOYMENT, identifier, "");
        deploymentConfig.addConfigItem(configurationFactory.newConfigItem("unit", deploymentUnit));

        configurationService.addConfiguration(deploymentConfig);

        DeploymentConfig deployment = deploymentFactory.newDeployment(deploymentConfig);
        registeredDeployments.put(deployment.getIdentifier(), deployment);
    }

    @Override
    public void removeDeployment(String identifier) {
        ConfigGroup deploymentConfig = configurationFactory.newConfigGroup(ConfigType.DEPLOYMENT, identifier, "");
        configurationService.removeConfiguration(deploymentConfig);

        registeredDeployments.remove(identifier);

    }

    @Override
    public DeploymentConfig getDeployment(String identifier) {
        return registeredDeployments.get(identifier);
    }

    @Override
    public Collection<DeploymentConfig> getDeployments() {
        return Collections.unmodifiableCollection(registeredDeployments.values());
    }
}
