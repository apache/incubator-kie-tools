/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.shape.impl;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.ConnectorViewStub;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.IsConnector;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeViewDef;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConnectorShapeTest {

    private static Bounds BOUNDS = Bounds.create(0d, 0d, 15d, 40d);

    @Mock
    private ShapeViewDef shapeDef;

    @Mock
    private BiConsumer<Object, ShapeView> viewHandler;

    @Mock
    private ShapeStateHandler shapeStateHandler;

    private ConnectorShape tested;
    private ShapeView<?> shapeView;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        shapeView = spy(new ConnectorViewStub());
        when(shapeDef.fontHandler()).thenReturn(Optional.empty());
        when(shapeDef.titleHandler()).thenReturn(Optional.empty());
        when(shapeDef.sizeHandler()).thenReturn(Optional.empty());
        when(shapeDef.viewHandler()).thenReturn(viewHandler);
        this.tested = new ConnectorShape(shapeDef,
                                         shapeView,
                                         shapeStateHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testApplyState() {
        tested.applyState(ShapeState.NONE);
        verify(shapeStateHandler,
               never()).shapeAttributesChanged();
        verify(shapeStateHandler,
               times(1)).applyState(eq(ShapeState.NONE));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testApplyConnections() {
        final Object def = mock(Object.class);
        final Edge<ViewConnector<Object>, Node> edge = new EdgeImpl<>("uuid1");
        final ViewConnectorImpl<Object> content =
                new ViewConnectorImpl<>(def, BOUNDS);
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

    @Test
    @SuppressWarnings("unchecked")
    public void testApplyConnectionsEvenNoSourceOrTarget() {
        final Object def = mock(Object.class);
        final Edge<ViewConnector<Object>, Node> edge = new EdgeImpl<>("uuid1");
        final ViewConnectorImpl<Object> content =
                new ViewConnectorImpl<>(def, BOUNDS);
        Connection sourceConnection = mock(Connection.class);
        Connection targetConnection = mock(Connection.class);
        content.setSourceConnection(sourceConnection);
        content.setTargetConnection(targetConnection);
        edge.setContent(content);
        tested.applyConnections(edge,
                                null,
                                null,
                                MutationContext.STATIC);
        verify(((IsConnector) shapeView),
               times(1)).connect(eq(null),
                                 eq(sourceConnection),
                                 eq(null),
                                 eq(targetConnection));
    }
}
