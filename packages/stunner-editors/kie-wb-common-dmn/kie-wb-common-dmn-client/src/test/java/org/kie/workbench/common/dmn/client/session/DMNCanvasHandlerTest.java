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

package org.kie.workbench.common.dmn.client.session;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNCanvasHandlerTest {

    @Mock
    private DMNCanvasHandler canvasHandler;

    @Mock
    private Element parent;

    @Mock
    private Element child;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private Shape shape;

    private final String parentUuid = "parent uuid";

    @Before
    public void setup() {
        when(parent.getUUID()).thenReturn(parentUuid);
        doCallRealMethod().when(canvasHandler).addChild(parent, child);
        doCallRealMethod().when(canvasHandler).updateDiagramId(any());
    }

    @Test
    public void testAddChildWhenItIsElementFromThisCanvas() {

        doReturn(false).when(canvasHandler).isCanvasRoot(parent);
        doReturn(canvas).when(canvasHandler).getCanvas();
        when(canvas.getShape(parentUuid)).thenReturn(shape);

        canvasHandler.addChild(parent, child);

        verify(canvasHandler).superAddChild(parent, child);
    }

    @Test
    public void testAddChildWhenItIsNotElementFromThisCanvas() {

        doReturn(false).when(canvasHandler).isCanvasRoot(parent);
        doReturn(canvas).when(canvasHandler).getCanvas();
        when(canvas.getShape(parentUuid)).thenReturn(null);

        canvasHandler.addChild(parent, child);

        verify(canvasHandler, never()).superAddChild(parent, child);
    }

    @Test
    public void testAddChildWhenItIsCanvasRoot() {

        doReturn(true).when(canvasHandler).isCanvasRoot(parent);
        doReturn(canvas).when(canvasHandler).getCanvas();
        when(canvas.getShape(parentUuid)).thenReturn(null);

        canvasHandler.addChild(parent, child);

        verify(canvasHandler).superAddChild(parent, child);
    }

    @Test
    public void testUpdateDiagramIdWhenDiagramIdIsNull() {

        final String diagramId = "diagram id";
        final HasContentDefinitionId hasContentDefinitionId = mock(HasContentDefinitionId.class);
        final Element element = getElementForTestUpdateDiagramId(diagramId,
                                                                 null,
                                                                 hasContentDefinitionId);

        canvasHandler.updateDiagramId(element);

        verify(hasContentDefinitionId).setDiagramId(diagramId);
    }

    @Test
    public void testUpdateDiagramIdWhenDiagramIdIsSet() {

        final String diagramId = "diagram id";
        final String currentDiagramId = "some set diagram";
        final HasContentDefinitionId hasContentDefinitionId = mock(HasContentDefinitionId.class);
        final Element element = getElementForTestUpdateDiagramId(diagramId,
                                                                 currentDiagramId,
                                                                 hasContentDefinitionId);

        canvasHandler.updateDiagramId(element);

        verify(hasContentDefinitionId, never()).setDiagramId(Mockito.<String>any());
    }

    @Test
    public void testUpdateDiagramIdWhenDiagramIdIsEmpty() {

        final String diagramId = "diagram id";
        final String currentDiagramId = "";
        final HasContentDefinitionId hasContentDefinitionId = mock(HasContentDefinitionId.class);
        final Element element = getElementForTestUpdateDiagramId(diagramId,
                                                                 currentDiagramId,
                                                                 hasContentDefinitionId);

        canvasHandler.updateDiagramId(element);

        verify(hasContentDefinitionId).setDiagramId(diagramId);
    }

    private Element getElementForTestUpdateDiagramId(final String diagramId,
                                                     final String currentDiagramId,
                                                     final HasContentDefinitionId hasContentDefinitionId) {
        final Element element = mock(Element.class);
        final Definition definition = mock(Definition.class);
        final GraphsProvider graphsProvider = mock(GraphsProvider.class);

        when(graphsProvider.getCurrentDiagramId()).thenReturn(diagramId);
        when(definition.getDefinition()).thenReturn(hasContentDefinitionId);
        when(element.getContent()).thenReturn(definition);
        when(hasContentDefinitionId.getDiagramId()).thenReturn(currentDiagramId);
        when(canvasHandler.getGraphsProvider()).thenReturn(graphsProvider);

        return element;
    }
}