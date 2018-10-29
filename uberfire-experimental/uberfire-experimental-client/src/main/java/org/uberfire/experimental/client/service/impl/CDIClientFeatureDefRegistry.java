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

package org.uberfire.experimental.client.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesDefRegistry;
import org.uberfire.experimental.service.backend.BackendExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinitionProvider;
import org.uberfire.experimental.service.definition.impl.ExperimentalFeatureDefRegistryImpl;

@ApplicationScoped
public class CDIClientFeatureDefRegistry extends ExperimentalFeatureDefRegistryImpl implements ClientExperimentalFeaturesDefRegistry {

    private Caller<BackendExperimentalFeatureDefRegistry> backendRegistry;

    @Inject
    public CDIClientFeatureDefRegistry(Caller<BackendExperimentalFeatureDefRegistry> backendRegistry) {
        this.backendRegistry = backendRegistry;
    }

    @Override
    public void loadRegistry() {
        Collection<SyncBeanDef<ExperimentalFeatureDefinitionProvider>> providers = IOC.getBeanManager().lookupBeans(ExperimentalFeatureDefinitionProvider.class);

        List<ExperimentalFeatureDefinition> clientFeatures = providers.stream()
                .map(SyncBeanDef::newInstance)
                .map(ExperimentalFeatureDefinitionProvider::getDefinition)
                .collect(Collectors.toList());

        backendRegistry.call(new RemoteCallback<Collection<ExperimentalFeatureDefinition>>() {
            @Override
            public void callback(Collection<ExperimentalFeatureDefinition> response) {
                response.forEach(featureDefinition -> register(featureDefinition));
            }
        }).loadFeatureDefinitions(clientFeatures);
    }
}
