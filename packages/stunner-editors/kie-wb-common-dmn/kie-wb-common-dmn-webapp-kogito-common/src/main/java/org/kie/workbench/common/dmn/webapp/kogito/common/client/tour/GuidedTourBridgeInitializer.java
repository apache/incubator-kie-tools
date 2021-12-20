/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.tour;

import javax.inject.Inject;

import org.appformer.kogito.bridge.client.guided.tour.GuidedTourBridge;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers.GuidedTourGraphObserver;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers.GuidedTourGridObserver;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers.GraphElementsPositionProviderFactory;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers.HTMLElementsPositionProviderFactory;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorial;

/**
 * Initializes the Guided Tour bridge, by registering custom position providers, observers, and the DMN tutorial.
 */
public class GuidedTourBridgeInitializer {

    private final GuidedTourGraphObserver graphObserver;

    private final GuidedTourGridObserver gridObserver;

    private final GraphElementsPositionProviderFactory graphPositionUtils;

    private final HTMLElementsPositionProviderFactory htmlPositionUtils;

    private final GuidedTourBridge monitorBridge;

    private final DMNTutorial dmnTutorial;

    @Inject
    public GuidedTourBridgeInitializer(final GuidedTourGraphObserver graphObserver,
                                       final GuidedTourGridObserver gridObserver,
                                       final GraphElementsPositionProviderFactory graphPositionUtils,
                                       final HTMLElementsPositionProviderFactory htmlPositionUtils,
                                       final GuidedTourBridge monitorBridge,
                                       final DMNTutorial dmnTutorial) {
        this.graphObserver = graphObserver;
        this.gridObserver = gridObserver;
        this.graphPositionUtils = graphPositionUtils;
        this.htmlPositionUtils = htmlPositionUtils;
        this.monitorBridge = monitorBridge;
        this.dmnTutorial = dmnTutorial;
    }

    public void init() {
        registerPositionProviders();
        registerObservers();
        registerTutorials();
    }

    private void registerPositionProviders() {
        monitorBridge.registerPositionProvider("DMNEditorGraph", graphPositionUtils.createPositionProvider());
        monitorBridge.registerPositionProvider("DMNEditorHTMLElement", htmlPositionUtils.createPositionProvider());
    }

    private void registerObservers() {
        monitorBridge.registerObserver(graphObserver);
        monitorBridge.registerObserver(gridObserver);
    }

    private void registerTutorials() {
        monitorBridge.registerTutorial(dmnTutorial.getTutorial());
    }
}
