/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.shape.impl;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.ConnectorViewStub;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.IsConnector;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.definition.shape.MutableShapeDef;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConnectorShapeTest {

    @Mock
    private MutableShapeDef shapeDef;

    @Mock
    private ShapeStateHelper shapeStateHelper;

    private ConnectorShape tested;
    private ShapeView<?> shapeView;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        shapeView = spy(new ConnectorViewStub());
        this.tested = new ConnectorShape(shapeDef,
                                         shapeView,
                                         shapeStateHelper);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testApplyState() {
        tested.applyState(ShapeState.NONE);
        verify(shapeStateHelper,
               never()).save(any(Predicate.class));
        verify(shapeStateHelper,
               times(1)).applyState(eq(ShapeState.NONE));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testApplyConnections() {
        final Object def = mock(Object.class);
        final Edge<ViewConnector<Object>, Node> edge = new EdgeImpl<>("uuid1");
        final ViewConnectorImpl<Object> content =
                new ViewConnectorImpl<>(def,
                                        new BoundsImpl(
                                                new BoundImpl(0d,
                                                              0d),
                                                new BoundImpl(15d,
                                                              40d)
                                        ));
        Connection sourceConnection = mock(Connection.class);
        Connection targetConnection = mock(Connection.class);
        content.setSourceConnection(sourceConnection);
        content.setTargetConnection(targetConnection);
        edge.setContent(content);
        final ShapeView<?> source = mock(ShapeView.class);
        final ShapeView<?> target = mock(ShapeView.class);
        tested.applyConnections(edge,
                                source,
                                target,
                                MutationContext.STATIC);
        verify(((IsConnector) shapeView),
               times(1)).connect(eq(source),
                                 eq(sourceConnection),
                                 eq(target),
                                 eq(targetConnection));
    }
}
