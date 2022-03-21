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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourBridge;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourCustomSelectorPositionProvider.PositionProviderFunction;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Tutorial;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers.GuidedTourGraphObserver;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers.GuidedTourGridObserver;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers.GraphElementsPositionProviderFactory;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers.HTMLElementsPositionProviderFactory;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorial;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedTourBridgeInitializerTest {

    @Mock
    private GuidedTourGraphObserver graphObserver;

    @Mock
    private GuidedTourGridObserver gridObserver;

    @Mock
    private GraphElementsPositionProviderFactory graphPositionUtils;

    @Mock
    private HTMLElementsPositionProviderFactory htmlPositionUtils;

    @Mock
    private GuidedTourBridge monitorBridge;

    @Mock
    private DMNTutorial dmnTutorial;

    @Test
    public void testInit() {
        final PositionProviderFunction graphProvider = mock(PositionProviderFunction.class);
        final PositionProviderFunction htmlProvider = mock(PositionProviderFunction.class);
        final Tutorial tutorial = mock(Tutorial.class);
        final GuidedTourBridgeInitializer bridgeInitializer = new GuidedTourBridgeInitializer(graphObserver, gridObserver, graphPositionUtils, htmlPositionUtils, monitorBridge, dmnTutorial);

        when(graphPositionUtils.createPositionProvider()).thenReturn(graphProvider);
        when(htmlPositionUtils.createPositionProvider()).thenReturn(htmlProvider);
        when(dmnTutorial.getTutorial()).thenReturn(tutorial);

        bridgeInitializer.init();

        verify(monitorBridge).registerPositionProvider("DMNEditorGraph", graphProvider);
        verify(monitorBridge).registerPositionProvider("DMNEditorHTMLElement", htmlProvider);
        verify(monitorBridge).registerObserver(graphObserver);
        verify(monitorBridge).registerObserver(gridObserver);
        verify(monitorBridge).registerTutorial(tutorial);
    }
}
