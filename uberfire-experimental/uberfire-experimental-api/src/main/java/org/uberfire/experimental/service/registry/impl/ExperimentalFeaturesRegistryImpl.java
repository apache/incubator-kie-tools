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

package org.uberfire.experimental.service.registry.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.experimental.service.registry.ExperimentalFeature;
import org.uberfire.experimental.service.registry.ExperimentalFeaturesRegistry;

@Portable
public class ExperimentalFeaturesRegistryImpl implements ExperimentalFeaturesRegistry {

    private List<ExperimentalFeatureImpl> features;

    public ExperimentalFeaturesRegistryImpl(@MapsTo("features") List<ExperimentalFeatureImpl> features) {
        this.features = features;
    }

    @Override
    public Optional<ExperimentalFeature> getFeature(String featureId) {
        return features.stream()
                .filter(feature -> feature.getFeatureId().equals(featureId))
                .map(feature -> (ExperimentalFeature) feature)
                .findAny();
    }

    @Override
    public boolean isFeatureEnabled(String featureId) {

        Optional<ExperimentalFeature> optional = getFeature(featureId);

        if (optional.isPresent()) {
            return optional.get().isEnabled();
        }

        return true;
    }

    @Override
    public Collection<ExperimentalFeature> getAllFeatures() {
        return Collections.unmodifiableList(features);
    }
}
