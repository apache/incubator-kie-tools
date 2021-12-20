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

import org.appformer.kogito.bridge.client.guided.tour.GuidedTourCustomSelectorPositionProvider.PositionProviderFunction;
import org.appformer.kogito.bridge.client.guided.tour.observers.GlobalHTMLObserver;
import org.appformer.kogito.bridge.client.guided.tour.service.GuidedTourService;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Tutorial;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;
import org.jboss.errai.ioc.client.api.Disposer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuidedTourBridgeTest {

    @Mock
    private GuidedTourService service;

    @Mock
    private GlobalHTMLObserverFake observer;

    private GuidedTourBridge bridge;

    @Before
    public void setup() {
        bridge = spy(new GuidedTourBridge(service, observer));
        bridge.init();
    }

    @Test
    public void testInit() {
        // 'init' is called on setup
        assertEquals(singletonList(observer), bridge.observers);
    }

    @Test
    public void testRefresh() {
        final UserInteraction userInteraction = mock(UserInteraction.class);
        when(service.isEnabled()).thenReturn(true);

        bridge.refresh(userInteraction);

        verify(service).refresh(userInteraction);
    }

    @Test
    public void testRegisterTutorial() {
        final Tutorial tutorial = mock(Tutorial.class);
        when(service.isEnabled()).thenReturn(true);

        bridge.registerTutorial(tutorial);

        verify(service).registerTutorial(tutorial);
    }

    @Test
    public void testRefreshWhenBridgeIsNotEnabled() {
        final UserInteraction userInteraction = mock(UserInteraction.class);
        when(service.isEnabled()).thenReturn(false);

        bridge.refresh(userInteraction);

        verify(observer).dispose();
    }

    @Test
    public void testRegisterTutorialWhenBridgeIsNotEnabled() {
        final Tutorial tutorial = mock(Tutorial.class);
        when(service.isEnabled()).thenReturn(false);

        bridge.registerTutorial(tutorial);

        verify(observer).dispose();
    }

    @Test
    public void testRegisterObserver() {
        final GuidedTourObserver observer = mock(GuidedTourObserver.class);

        bridge.registerObserver(observer);

        verify(observer).setMonitorBridge(bridge);
        assertEquals(asList(this.observer, observer), bridge.observers);
    }

    @Test
    public void testRegisterPositionProvider() {
        final GuidedTourCustomSelectorPositionProvider positionProvider = mock(GuidedTourCustomSelectorPositionProvider.class);
        final PositionProviderFunction positionProviderFunction = mock(PositionProviderFunction.class);
        final String type = "type";

        doReturn(positionProvider).when(bridge).getPositionProviderInstance();

        bridge.registerPositionProvider(type, positionProviderFunction);

        verify(positionProvider).registerPositionProvider(type, positionProviderFunction);
    }

    class GlobalHTMLObserverFake extends GlobalHTMLObserver {

        GlobalHTMLObserverFake(final Disposer<GlobalHTMLObserver> selfDisposer) {
            super(selfDisposer);
        }

        @Override
        protected void dispose() {
            super.dispose();
        }
    }
}
