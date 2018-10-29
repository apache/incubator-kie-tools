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

package org.uberfire.experimental.service.definition.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.experimental.service.backend.BackendExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinitionProvider;

@Service
@ApplicationScoped
public class CDIBackendFeatureDefRegistry extends ExperimentalFeatureDefRegistryImpl implements BackendExperimentalFeatureDefRegistry {

    @Inject
    public CDIBackendFeatureDefRegistry(Instance<ExperimentalFeatureDefinitionProvider> definitionProviders) {
        for (ExperimentalFeatureDefinitionProvider definitionProvider : definitionProviders) {
            register(definitionProvider);
        }
    }

    @Override
    public Collection<ExperimentalFeatureDefinition> loadFeatureDefinitions(Collection<ExperimentalFeatureDefinition> clientDefinitions) {

        if(clientDefinitions != null) {
            clientDefinitions
                    .stream()
                    .filter(clientDefinition -> !features.containsKey(clientDefinition.getId()))
                    .forEach(this::register);
        }

        return new ArrayList<>(features.values());
    }
}
