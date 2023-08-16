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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNShape;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FixedBoundsPropertyReadersTest {

    private static final double RESOLUTION_FACTOR = 1d;
    private static final String CATCH_EVENT_ID = "ce";
    private static final String BOUNDARY_EVENT_ID = "be";
    private static final String THROW_EVENT_ID = "te";
    private static final String GATEWAY_EVENT_ID = "gw";

    @Mock
    private CatchEvent catchEvent;

    @Mock
    private BoundaryEvent boundaryEvent;

    @Mock
    private ThrowEvent throwEvent;

    @Mock
    private Gateway gateway;

    @Mock
    private BPMNDiagram diagram;

    @Mock
    private DefinitionResolver definitionResolver;

    @Mock
    private BPMNShape catchEventShape;

    @Mock
    private BPMNShape boundaryEventShape;

    @Mock
    private BPMNShape throwEventShape;

    @Mock
    private BPMNShape gatewayEventShape;

    private CatchEventPropertyReader catchEventPropertyReader;
    private BoundaryEventPropertyReader boundaryEventPropertyReader;
    private ThrowEventPropertyReader throwEventPropertyReader;
    private GatewayPropertyReader gatewayPropertyReader;

    @Before
    public void setup() {
        when(definitionResolver.getResolutionFactor()).thenReturn(RESOLUTION_FACTOR);
        when(catchEvent.getId()).thenReturn(CATCH_EVENT_ID);
        when(boundaryEvent.getId()).thenReturn(BOUNDARY_EVENT_ID);
        when(throwEvent.getId()).thenReturn(THROW_EVENT_ID);
        when(gateway.getId()).thenReturn(GATEWAY_EVENT_ID);
        when(definitionResolver.getShape(eq(CATCH_EVENT_ID))).thenReturn(catchEventShape);
        when(definitionResolver.getShape(eq(BOUNDARY_EVENT_ID))).thenReturn(boundaryEventShape);
        when(definitionResolver.getShape(eq(THROW_EVENT_ID))).thenReturn(throwEventShape);
        when(definitionResolver.getShape(eq(GATEWAY_EVENT_ID))).thenReturn(gatewayEventShape);
        this.catchEventPropertyReader = new CatchEventPropertyReader(catchEvent, diagram, definitionResolver);
        this.boundaryEventPropertyReader = spy(new BoundaryEventPropertyReader(boundaryEvent, diagram, definitionResolver));
        this.throwEventPropertyReader = new ThrowEventPropertyReader(throwEvent, diagram, definitionResolver);
        this.gatewayPropertyReader = new GatewayPropertyReader(gateway, diagram, gatewayEventShape, RESOLUTION_FACTOR);
    }

    @Test
    public void testBounds() {
        org.eclipse.dd.dc.Bounds catchEventShapeBounds = mockBounds(1f, 2f, 123.2f, 322.3f);
        when(catchEventShape.getBounds()).thenReturn(catchEventShapeBounds);
        org.eclipse.dd.dc.Bounds throwEventShapeBounds = mockBounds(5f, 6f, 2144.67f, 853.34f);
        when(throwEventShape.getBounds()).thenReturn(throwEventShapeBounds);
        doReturn(Point2D.create(3d, 4d)).when(boundaryEventPropertyReader).getDockerInfo();
        org.eclipse.dd.dc.Bounds gatewayEventShapeBounds = mockBounds(7f, 8f, 4543.2f, 25.3f);
        when(gatewayEventShape.getBounds()).thenReturn(gatewayEventShapeBounds);
        Bounds catchEventBounds = catchEventPropertyReader.getBounds();
        Bounds boundaryEventBounds = boundaryEventPropertyReader.getBounds();
        Bounds throwEventBounds = throwEventPropertyReader.getBounds();
        Bounds gatewayEventBounds = gatewayPropertyReader.getBounds();
        assertEquals(Bounds.create(1, 2, 57, 58), catchEventBounds);
        assertEquals(Bounds.create(3, 4, 59, 60), boundaryEventBounds);
        assertEquals(Bounds.create(5, 6, 61, 62), throwEventBounds);
        assertEquals(Bounds.create(7, 8, 63, 64), gatewayEventBounds);
    }

    private static org.eclipse.dd.dc.Bounds mockBounds(float x,
                                                       float y,
                                                       float width,
                                                       float height) {
        org.eclipse.dd.dc.Bounds bounds = mock(org.eclipse.dd.dc.Bounds.class);
        when(bounds.getX()).thenReturn(x);
        when(bounds.getY()).thenReturn(y);
        when(bounds.getWidth()).thenReturn(width);
        when(bounds.getHeight()).thenReturn(height);
        return bounds;
    }
}
