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

package org.uberfire.experimental.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.experimental.service.backend.BackendExperimentalFeaturesRegistryService;
import org.uberfire.experimental.service.backend.ExperimentalFeaturesSession;
import org.uberfire.experimental.service.backend.impl.ExperimentalFeaturesSessionImpl;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;
import org.uberfire.experimental.service.editor.EditableExperimentalFeature;
import org.uberfire.experimental.service.editor.FeaturesEditorService;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeaturesRegistryImpl;
import org.uberfire.experimental.service.storage.ExperimentalFeaturesStorage;

@Service
@ApplicationScoped
public class BackendExperimentalFeaturesRegistryServiceImpl implements ExperimentalFeaturesRegistryService,
                                                                       BackendExperimentalFeaturesRegistryService,
                                                                       FeaturesEditorService {

    public static final String EXPERIMENTAL_FEATURES_PROPERTY_NAME = "appformer.experimental.features";

    private final ExperimentalFeatureDefRegistry defRegistry;

    private ExperimentalFeaturesStorage globalStorage;


    @Inject
    public BackendExperimentalFeaturesRegistryServiceImpl(final ExperimentalFeatureDefRegistry defRegistry, @Named("global") final ExperimentalFeaturesStorage globalStorage) {
        this.defRegistry = defRegistry;
        this.globalStorage = globalStorage;
    }

    @Override
    public ExperimentalFeaturesRegistryImpl getFeaturesRegistry() {
        return loadRegistry();
    }

    @Override
    public Boolean isExperimentalEnabled() {
        return Boolean.parseBoolean(System.getProperty(EXPERIMENTAL_FEATURES_PROPERTY_NAME, "false"));
    }

    @Override
    public boolean isFeatureEnabled(String featureId) {

        ExperimentalFeaturesRegistryImpl registry = getFeaturesRegistry();

        if(!registry.getFeature(featureId).isPresent()) {
            return true;
        }

        return isExperimentalEnabled() && getFeaturesRegistry().isFeatureEnabled(featureId);
    }

    @Override
    public void save(EditableExperimentalFeature editableFeature) {

        if(!isExperimentalEnabled()) {
            throw new IllegalStateException("Impossible edit feature '" + editableFeature.getFeatureId() + "': Experimental Framework is disabled");
        }

        Optional<ExperimentalFeatureDefinition> optional = Optional.ofNullable(defRegistry.getFeatureById(editableFeature.getFeatureId()));

        if (optional.isPresent()) {
            ExperimentalFeatureImpl feature = new ExperimentalFeatureImpl(editableFeature.getFeatureId(), editableFeature.isEnabled());

            globalStorage.store(feature);
        } else {
            throw new IllegalArgumentException("Cannot find ExperimentalFeature '" + editableFeature.getFeatureId() + "'");
        }
    }

    private ExperimentalFeaturesRegistryImpl loadRegistry() {
        List<ExperimentalFeatureImpl> features = new ArrayList<>();

        features.addAll(globalStorage.getFeatures());

        return new ExperimentalFeaturesRegistryImpl(features);
    }

    @Override
    public ExperimentalFeaturesSession getExperimentalFeaturesSession() {
        return new ExperimentalFeaturesSessionImpl(isExperimentalEnabled(), getFeaturesRegistry());
    }
}
