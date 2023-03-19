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

package org.appformer.kogito.bridge.client.guided.tour;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.appformer.kogito.bridge.client.guided.tour.GuidedTourCustomSelectorPositionProvider.PositionProviderFunction;
import org.appformer.kogito.bridge.client.guided.tour.observers.GlobalHTMLObserver;
import org.appformer.kogito.bridge.client.guided.tour.service.GuidedTourService;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Tutorial;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;

/**
 * Provides a bridge between the GWT code and the the native JavaScript implementations.
 */
@ApplicationScoped
public class GuidedTourBridge {

    private final GuidedTourService guidedTourService;

    private final GlobalHTMLObserver globalHTMLObserver;

    List<GuidedTourObserver> observers = new ArrayList<>();

    @Inject
    public GuidedTourBridge(final GuidedTourService guidedTourService,
                            final GlobalHTMLObserver globalHTMLObserver) {
        this.guidedTourService = guidedTourService;
        this.globalHTMLObserver = globalHTMLObserver;
    }

    @PostConstruct
    public void init() {
        registerObserver(globalHTMLObserver);
    }

    /**
     * Refreshes the Guided Tour component by calling the {@link GuidedTourService}.
     */
    public void refresh(final UserInteraction userInteraction) {
        getGuidedTourService().refresh(userInteraction);
    }

    /**
     * Registers a tutorial into the Guided Tour component by calling the {@link GuidedTourService}.
     */
    public void registerTutorial(final Tutorial tutorial) {
        getGuidedTourService().registerTutorial(tutorial);
    }

    /**
     * Register a new observer into the {@link GuidedTourBridge}.
     */
    public void registerObserver(final GuidedTourObserver observer) {
        observer.setMonitorBridge(this);
        observers.add(observer);
    }

    /**
     * Register a position provider into the {@link GuidedTourCustomSelectorPositionProvider}.
     */
    public void registerPositionProvider(final String type,
                                         final PositionProviderFunction positionProviderFunction) {
        getPositionProviderInstance().registerPositionProvider(type, positionProviderFunction);
    }

    private GuidedTourService getGuidedTourService() {
        if (guidedTourService.isEnabled()) {
            return guidedTourService;
        }
        disposeObservers();
        return GuidedTourService.DEFAULT;
    }

    private void disposeObservers() {
        observers.forEach(GuidedTourObserver::dispose);
    }

    GuidedTourCustomSelectorPositionProvider getPositionProviderInstance() {
        return GuidedTourCustomSelectorPositionProvider.getInstance();
    }
}
