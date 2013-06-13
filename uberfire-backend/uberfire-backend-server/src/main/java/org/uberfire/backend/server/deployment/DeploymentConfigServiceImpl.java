package org.uberfire.backend.server.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.backend.deployment.DeploymentConfig;
import org.uberfire.backend.deployment.DeploymentConfigService;
import org.uberfire.backend.server.config.Added;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;
import org.uberfire.backend.server.config.Removed;
import org.uberfire.backend.server.config.SystemRepositoryChangedEvent;

@ApplicationScoped
public class DeploymentConfigServiceImpl implements DeploymentConfigService {

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @Inject
    private DeploymentConfigFactory deploymentFactory;
    @Inject
    @Added
    private Event<DeploymentConfigChangedEvent> addedDeploymentEvent;
    @Inject
    @Removed
    private Event<DeploymentConfigChangedEvent> removedDeploymentEvent;


    private Map<String, DeploymentConfig> registeredDeployments = new ConcurrentHashMap<String, DeploymentConfig>();

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

    public void updateRegisteredDeployments(@Observes SystemRepositoryChangedEvent changedEvent) {
        Collection<ConfigGroup> deployments = configurationService.getConfiguration(ConfigType.DEPLOYMENT);
        if (deployments != null) {
            List<String> processedDeployments = new ArrayList<String>();
            for (ConfigGroup deploymentConfig : deployments) {
                String name = deploymentConfig.getName();

                if (!this.registeredDeployments.containsKey(name)) {
                    // add it to registered deployments
                    DeploymentConfig deployment = deploymentFactory.newDeployment(deploymentConfig);
                    // trigger deployment of new element
                    addedDeploymentEvent.fire(new DeploymentConfigChangedEvent(deployment.getDeploymentUnit()));
                    registeredDeployments.put(deployment.getIdentifier(), deployment);
                }

                processedDeployments.add(name);

            }

            Set<String> registeredDeploymedIds = registeredDeployments.keySet();
            // process undeploy
            for (String identifier : registeredDeploymedIds) {
                if (!processedDeployments.contains(identifier)) {
                    DeploymentConfig deployment = registeredDeployments.remove(identifier);

                    // trigger undeloyment as it was removed
                    removedDeploymentEvent.fire(new DeploymentConfigChangedEvent(deployment.getDeploymentUnit()));
                }
            }
        }
    }
}
