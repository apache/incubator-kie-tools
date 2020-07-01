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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers;

import java.util.Optional;

import org.appformer.kogito.bridge.client.guided.tour.GuidedTourBridge;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;
import org.jboss.errai.ioc.client.api.Disposer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourUtils;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourActions.CREATED;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourActions.REMOVED;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourActions.UPDATED;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuidedTourGraphObserverTest {

    @Mock
    private Disposer<GuidedTourGraphObserver> disposer;

    @Mock
    private GuidedTourUtils guidedTourUtils;

    @Mock
    private GuidedTourBridge bridge;

    private GuidedTourGraphObserverFake observer;

    @Before
    public void setup() {
        observer = spy(new GuidedTourGraphObserverFake(disposer, guidedTourUtils));
    }

    @Test
    public void testOnCanvasElementAddedEvent() {
        final CanvasElementAddedEvent event = new CanvasElementAddedEvent(null, null);
        final UserInteraction userInteraction = mock(UserInteraction.class);
        final NodeImpl<View> node = new NodeImpl<>("0000");
        final String nodeName = "Decision-1";

        when(guidedTourUtils.getName(event)).thenReturn(Optional.of(nodeName));

        doReturn(userInteraction).when(observer).buildUserInteraction(CREATED.name(), nodeName);

        observer.onCanvasElementAddedEvent(event);

        verify(bridge).refresh(userInteraction);
    }

    @Test
    public void testOnCanvasElementUpdatedEvent() {
        final CanvasElementUpdatedEvent event = new CanvasElementUpdatedEvent(null, null);
        final UserInteraction userInteraction = mock(UserInteraction.class);
        final NodeImpl<View> node = new NodeImpl<>("0000");
        final String nodeName = "Decision-1";

        when(guidedTourUtils.getName(event)).thenReturn(Optional.of(nodeName));

        doReturn(userInteraction).when(observer).buildUserInteraction(UPDATED.name(), nodeName);

        observer.onCanvasElementUpdatedEvent(event);

        verify(bridge).refresh(userInteraction);
    }

    @Test
    public void testOnAbstractCanvasElementRemovedEvent() {
        final CanvasElementRemovedEvent event = new CanvasElementRemovedEvent(null, null);
        final UserInteraction userInteraction = mock(UserInteraction.class);
        final NodeImpl<View> node = new NodeImpl<>("0000");
        final String nodeName = "Decision-1";

        when(guidedTourUtils.getName(event)).thenReturn(Optional.of(nodeName));

        doReturn(userInteraction).when(observer).buildUserInteraction(REMOVED.name(), nodeName);

        observer.onAbstractCanvasElementRemovedEvent(event);

        verify(bridge).refresh(userInteraction);
    }

    class GuidedTourGraphObserverFake extends GuidedTourGraphObserver {

        GuidedTourGraphObserverFake(final Disposer<GuidedTourGraphObserver> disposer,
                                    final GuidedTourUtils guidedTourUtils) {
            super(disposer, guidedTourUtils);
        }

        @Override
        protected Optional<GuidedTourBridge> getMonitorBridge() {
            return Optional.of(bridge);
        }
    }
}
