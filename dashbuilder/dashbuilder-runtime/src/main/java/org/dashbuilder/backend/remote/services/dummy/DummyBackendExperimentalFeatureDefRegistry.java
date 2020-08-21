/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.backend.remote.services.dummy;

import java.util.Collection;
import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.experimental.service.backend.BackendExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;

@Service
@ApplicationScoped
public class DummyBackendExperimentalFeatureDefRegistry implements BackendExperimentalFeatureDefRegistry {

    @Override
    public ExperimentalFeatureDefinition getFeatureById(String definitionId) {
        return null;
    }

    @Override
    public Collection<ExperimentalFeatureDefinition> getAllFeatures() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ExperimentalFeatureDefinition> getGlobalFeatures() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ExperimentalFeatureDefinition> getUserFeatures() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ExperimentalFeatureDefinition> loadFeatureDefinitions(Collection<ExperimentalFeatureDefinition> clientDefinitions) {
        return Collections.emptyList();
    }

}