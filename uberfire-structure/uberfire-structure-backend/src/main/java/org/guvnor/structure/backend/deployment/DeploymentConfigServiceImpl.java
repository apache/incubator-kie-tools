/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.backend.deployment;

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

import org.guvnor.structure.backend.config.Added;
import org.guvnor.structure.backend.config.Removed;
import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.deployment.DeploymentConfig;
import org.guvnor.structure.deployment.DeploymentConfigService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.deployment.DeploymentConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DeploymentConfigServiceImpl implements DeploymentConfigService {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentConfigServiceImpl.class);

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
                registeredDeployments.put(deployment.getIdentifier(),
                                          deployment);
            }
        }
    }

    @Override
    public void addDeployment(String identifier,
                              Object deploymentUnit) {
        ConfigGroup deploymentConfig = configurationFactory.newConfigGroup(ConfigType.DEPLOYMENT,
                                                                           identifier,
                                                                           "");
        deploymentConfig.addConfigItem(configurationFactory.newConfigItem("unit",
                                                                          deploymentUnit));

        configurationService.addConfiguration(deploymentConfig);

        DeploymentConfig deployment = deploymentFactory.newDeployment(deploymentConfig);
        registeredDeployments.put(deployment.getIdentifier(),
                                  deployment);
    }

    @Override
    public void removeDeployment(String identifier) {
        ConfigGroup deploymentConfig = configurationFactory.newConfigGroup(ConfigType.DEPLOYMENT,
                                                                           identifier,
                                                                           "");
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
        logger.debug("Received deployment changed event, processing...");
        Collection<ConfigGroup> deployments = configurationService.getConfiguration(ConfigType.DEPLOYMENT);
        if (deployments != null) {
            List<String> processedDeployments = new ArrayList<String>();
            for (ConfigGroup deploymentConfig : deployments) {
                String name = deploymentConfig.getName();

                if (!this.registeredDeployments.containsKey(name)) {
                    try {
                        logger.debug("New deployment {} has been discovered and will be deployed",
                                     name);
                        // add it to registered deployments
                        DeploymentConfig deployment = deploymentFactory.newDeployment(deploymentConfig);
                        // trigger deployment of new element
                        addedDeploymentEvent.fire(new DeploymentConfigChangedEvent(deployment.getDeploymentUnit()));
                        registeredDeployments.put(deployment.getIdentifier(),
                                                  deployment);
                        logger.debug("Deployment {} deployed successfully",
                                     name);
                    } catch (RuntimeException e) {
                        logger.warn("Deployment {} failed to deploy due to {}",
                                    name,
                                    e.getMessage(),
                                    e);
                    }
                }

                processedDeployments.add(name);
            }

            Set<String> registeredDeploymedIds = registeredDeployments.keySet();
            // process undeploy
            for (String identifier : registeredDeploymedIds) {
                if (!processedDeployments.contains(identifier)) {
                    try {
                        logger.debug("New deployment {} has been discovered and will be deployed",
                                     identifier);
                        DeploymentConfig deployment = registeredDeployments.remove(identifier);

                        // trigger undeloyment as it was removed
                        removedDeploymentEvent.fire(new DeploymentConfigChangedEvent(deployment.getDeploymentUnit()));
                        logger.debug("Deployment {} undeployed successfully",
                                     identifier);
                    } catch (RuntimeException e) {
                        logger.warn("Undeployment {} failed to deploy due to {}",
                                    identifier,
                                    e.getMessage(),
                                    e);
                    }
                }
            }
        }
    }
}
