/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.experimental.client.workbench.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.type.impl.ClientTypeRegistryImpl;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.experimental.service.registry.ExperimentalFeature;
import org.uberfire.experimental.service.registry.ExperimentalFeaturesRegistry;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@Specializes
public class ExperimentalAwareClientTypeRegistryImpl extends ClientTypeRegistryImpl {

    private ClientExperimentalFeaturesRegistryService registryService;

    private Map<Class<?>, String> resourceTypeToExperimentalFeature = new HashMap<>();

    @Inject
    public ExperimentalAwareClientTypeRegistryImpl(SyncBeanManager iocManager, ClientExperimentalFeaturesRegistryService registryService) {
        super(iocManager);
        this.registryService = registryService;
    }

    public void init() {
        super.init();

        Collection<SyncBeanDef<ResourceTypeDefinition>> resourceTypeDefs = iocManager.lookupBeans(ResourceTypeDefinition.class);

        List<SyncBeanDef<ResourceTypeDefinition>> clientResourceTypes = resourceTypeDefs.stream()
                .filter(def -> def.isAssignableTo(ClientResourceType.class))
                .collect(Collectors.toList());

        List<SyncBeanDef<ResourceTypeDefinition>> resourceTypes = resourceTypeDefs.stream()
                .filter(def -> !def.isAssignableTo(ClientResourceType.class))
                .collect(Collectors.toList());

        clientResourceTypes.stream()
                .forEach(clientResourceDef -> {
                    for (Iterator<SyncBeanDef<ResourceTypeDefinition>> it = resourceTypes.iterator(); it.hasNext(); ) {
                        SyncBeanDef<ResourceTypeDefinition> resourceDef = it.next();
                        if (clientResourceDef.isAssignableTo(resourceDef.getBeanClass())) {
                            Optional<ExperimentalFeature> optional = getFeatureForResourceType(resourceDef, clientResourceDef);
                            if (optional.isPresent()) {
                                ExperimentalFeature feature = optional.get();
                                resourceTypeToExperimentalFeature.put(resourceDef.getBeanClass(), feature.getFeatureId());
                                resourceTypeToExperimentalFeature.put(clientResourceDef.getBeanClass(), feature.getFeatureId());
                                it.remove();
                            }
                        }
                    }
                });
    }

    private Optional<ExperimentalFeature> getFeatureForResourceType(SyncBeanDef<ResourceTypeDefinition> resourceType, SyncBeanDef<ResourceTypeDefinition> clientResourceType) {
        ExperimentalFeaturesRegistry registry = registryService.getFeaturesRegistry();

        Optional<ExperimentalFeature> optional = registry.getFeature(resourceType.getBeanClass().getName());

        if(optional.isPresent()) {
            return optional;
        }

        return registry.getFeature(clientResourceType.getBeanClass().getName());
    }

    @Override
    public boolean isEnabled(ClientResourceType resourceType) {
        if (super.isEnabled(resourceType)) {

            resourceType = Factory.maybeUnwrapProxy(resourceType);

            Optional<String> optional = Optional.ofNullable(resourceTypeToExperimentalFeature.get(resourceType.getClass()));

            if (optional.isPresent()) {
                return registryService.isFeatureEnabled(optional.get());
            }

            return true;
        }
        return false;
    }
}
