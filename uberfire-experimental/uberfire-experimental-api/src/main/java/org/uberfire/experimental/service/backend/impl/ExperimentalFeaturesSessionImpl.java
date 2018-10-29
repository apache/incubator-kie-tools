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

package org.uberfire.experimental.service.backend.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.experimental.service.backend.ExperimentalFeaturesSession;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeaturesRegistryImpl;

@Portable
public class ExperimentalFeaturesSessionImpl implements ExperimentalFeaturesSession {

    private boolean experimentalFeaturesEnabled;
    private ExperimentalFeaturesRegistryImpl registry;

    public ExperimentalFeaturesSessionImpl() {
    }

    public ExperimentalFeaturesSessionImpl(@MapsTo("experimentalFeaturesEnabled") boolean experimentalFeaturesEnabled, @MapsTo("registry") ExperimentalFeaturesRegistryImpl registry) {
        this.experimentalFeaturesEnabled = experimentalFeaturesEnabled;
        this.registry = registry;
    }

    @Override
    public ExperimentalFeaturesRegistryImpl getFeaturesRegistry() {
        return registry;
    }

    public void setRegistry(ExperimentalFeaturesRegistryImpl registry) {
        this.registry = registry;
    }

    public void setExperimentalFeaturesEnabled(boolean experimentalFeaturesEnabled) {
        this.experimentalFeaturesEnabled = experimentalFeaturesEnabled;
    }

    @Override
    public boolean isExperimentalFeaturesEnabled() {
        return experimentalFeaturesEnabled;
    }
}
