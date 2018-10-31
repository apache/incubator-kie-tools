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

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.experimental.service.backend.BackendExperimentalFeaturesRegistryService;
import org.uberfire.experimental.service.backend.impl.ExperimentalFeaturesSessionImpl;
import org.uberfire.experimental.service.events.NonPortableExperimentalFeatureModifiedEvent;
import org.uberfire.experimental.service.events.PortableExperimentalFeatureModifiedEvent;
import org.uberfire.experimental.service.registry.ExperimentalFeature;
import org.uberfire.experimental.service.registry.ExperimentalFeaturesRegistry;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;

@ApplicationScoped
public class ClientExperimentalFeaturesRegistryServiceImpl implements ClientExperimentalFeaturesRegistryService {

    private Caller<BackendExperimentalFeaturesRegistryService> backendService;
    private Event<NonPortableExperimentalFeatureModifiedEvent> event;

    private ExperimentalFeaturesSessionImpl session;

    @Inject
    public ClientExperimentalFeaturesRegistryServiceImpl(Caller<BackendExperimentalFeaturesRegistryService> backendService, Event<NonPortableExperimentalFeatureModifiedEvent> event) {
        this.backendService = backendService;
        this.event = event;
    }

    @Override
    public void loadRegistry() {
        backendService.call((RemoteCallback<ExperimentalFeaturesSessionImpl>) experimentalFeaturesSession -> session = experimentalFeaturesSession).getExperimentalFeaturesSession();
    }

    @Override
    public ExperimentalFeaturesRegistry getFeaturesRegistry() {
        return session.getFeaturesRegistry();
    }

    @Override
    public boolean isFeatureEnabled(String featureId) {

        // If framework is enabled we must check the feature state
        if(isExperimentalEnabled()) {
            return getFeaturesRegistry().isFeatureEnabled(featureId);
        }

        // If framework is disabled only non experimental features are enabled
        return !getFeaturesRegistry().getFeature(featureId).isPresent();
    }

    @Override
    public void updateExperimentalFeature(String featureId, boolean enabled) {
        if (isExperimentalEnabled()) {

            Optional<ExperimentalFeature> optional = session.getFeaturesRegistry().getFeature(featureId);

            if (optional.isPresent()) {
                ExperimentalFeatureImpl feature = (ExperimentalFeatureImpl) optional.get();

                if (feature.isEnabled() != enabled) {

                    feature.setEnabled(enabled);

                    event.fire(new NonPortableExperimentalFeatureModifiedEvent(feature));
                }
            }
        }
    }

    @Override
    public Boolean isExperimentalEnabled() {
        return session.isExperimentalFeaturesEnabled();
    }

    public void onGlobalFeatureModified(@Observes PortableExperimentalFeatureModifiedEvent event) {
        updateExperimentalFeature(event.getFeature().getFeatureId(), event.getFeature().isEnabled());
    }
}
