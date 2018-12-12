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

package org.kie.workbench.common.stunner.core.client.canvas.controls.drag;

import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractControlTest {

    @Mock
    AbstractCanvasHandler canvasHandler;

    @Mock
    AbstractCanvas canvas;

    @Mock
    Diagram diagram;

    @Mock
    Metadata metadata;

    @Mock
    Node element;

    @Mock
    Shape<?> shape;

    @Mock
    HasEventHandlers<ShapeViewExtStub, Object> shapeEventHandler;

    @Mock
    HasControlPoints<ShapeViewExtStub> hasControlPoints;

    ShapeViewExtStub shapeView;

    static final String ELEMENT_UUID = "element-uuid1";
    static final String ROOT_UUID = "root-uuid1";

    public void setUp() {
        shapeView = new ShapeViewExtStub(shapeEventHandler,
                                         hasControlPoints);

        when(element.getUUID()).thenReturn(ELEMENT_UUID);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn(ROOT_UUID);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(canvas.getShape(eq(ELEMENT_UUID))).thenReturn(shape);
        when(canvas.getShapes()).thenReturn(Collections.singletonList(shape));
        when(shape.getUUID()).thenReturn(ELEMENT_UUID);
        when(shape.getShapeView()).thenReturn(shapeView);

        when(shapeEventHandler.supports(eq(ViewEventType.MOUSE_CLICK))).thenReturn(true);
        when(shapeEventHandler.supports(eq(ViewEventType.MOUSE_ENTER))).thenReturn(true);
        when(shapeEventHandler.supports(eq(ViewEventType.MOUSE_EXIT))).thenReturn(true);
        when(shapeEventHandler.supports(eq(ViewEventType.DRAG))).thenReturn(true);
    }

    @Ignore
    @Test
    public void testPlaceHolderForMissingTests() {
        // placeholder for missing test too keep it track in maven report and Jenkins
    }
}
