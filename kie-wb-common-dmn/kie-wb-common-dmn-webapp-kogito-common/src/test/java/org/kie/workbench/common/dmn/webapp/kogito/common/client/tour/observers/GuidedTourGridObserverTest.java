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
import java.util.stream.Stream;

import org.appformer.kogito.bridge.client.guided.tour.GuidedTourBridge;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;
import org.jboss.errai.ioc.client.api.Disposer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourUtils;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourActions.CREATED;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourActions.UPDATED;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuidedTourGridObserverTest {

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private GuidedTourUtils guidedTourUtils;

    @Mock
    private Disposer<GuidedTourGridObserver> disposer;

    @Mock
    private GuidedTourBridge bridge;

    @Mock
    private UserInteraction userInteraction;

    @Mock
    private Node node1;

    @Mock
    private Node node2;

    private String uuid1 = "uuid1";

    private String uuid2 = "uuid2";

    private GuidedTourGridObserverFake observer;

    @Before
    public void setup() {
        observer = spy(new GuidedTourGridObserverFake(disposer));

        when(node1.getUUID()).thenReturn(uuid1);
        when(node2.getUUID()).thenReturn(uuid2);
        when(dmnGraphUtils.getNodeStream()).thenReturn(Stream.of(node1, node2));
        when(guidedTourUtils.getName(node1)).thenReturn("Decision-1");
        when(guidedTourUtils.getName(node2)).thenReturn("Decision-2");
    }

    @Test
    public void testOnEditExpressionEvent() {
        final EditExpressionEvent event = mock(EditExpressionEvent.class);

        when(event.getNodeUUID()).thenReturn(uuid1);
        doReturn(userInteraction).when(observer).buildUserInteraction(CREATED.name(), "BOXED_EXPRESSION:::Decision-1");

        observer.onEditExpressionEvent(event);

        verify(bridge).refresh(userInteraction);
    }

    @Test
    public void testOnExpressionEditorChanged() {
        final ExpressionEditorChanged event = new ExpressionEditorChanged(uuid2);

        doReturn(userInteraction).when(observer).buildUserInteraction(UPDATED.name(), "BOXED_EXPRESSION:::Decision-2");

        observer.onExpressionEditorChanged(event);

        verify(bridge).refresh(userInteraction);
    }

    class GuidedTourGridObserverFake extends GuidedTourGridObserver {

        GuidedTourGridObserverFake(final Disposer<GuidedTourGridObserver> disposer) {
            super(disposer, dmnGraphUtils, guidedTourUtils);
        }

        @Override
        protected Optional<GuidedTourBridge> getMonitorBridge() {
            return Optional.of(bridge);
        }
    }
}
