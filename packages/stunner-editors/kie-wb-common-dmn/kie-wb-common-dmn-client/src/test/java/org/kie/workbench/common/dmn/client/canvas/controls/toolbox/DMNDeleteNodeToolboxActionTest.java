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

package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import java.util.Collections;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DeleteNodeConfirmation;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDeleteNodeToolboxActionTest {

    private DMNDeleteNodeToolboxAction toolboxAction;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private ManagedInstance<DefaultCanvasCommandFactory> commandFactories;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private EventSourceMock<CanvasClearSelectionEvent> clearSelectionEvent;

    @Mock
    private DeleteNodeConfirmation deleteNodeConfirmation;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Index graphIndex;

    @Before
    public void setup() {
        toolboxAction = spy(new DMNDeleteNodeToolboxAction(translationService,
                                                           sessionCommandManager,
                                                           commandFactories,
                                                           definitionUtils,
                                                           clearSelectionEvent,
                                                           deleteNodeConfirmation));

        doNothing().when(toolboxAction).superOnMouseClick(any(), any(), any());
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
    }

    @Test
    public void testOnMouseClickWhenNodeRequiresDeletionConfirmation() {

        final String nodeUuid = "node uuid";
        final Element element = mock(Element.class);
        final Node node = mock(Node.class);
        final MouseClickEvent event = new MouseClickEvent(0, 0, 0, 0);

        when(element.asNode()).thenReturn(node);
        when(graphIndex.get(nodeUuid)).thenReturn(element);
        when(deleteNodeConfirmation.requiresDeletionConfirmation(Collections.singleton(node))).thenReturn(true);

        toolboxAction.onMouseClick(canvasHandler, nodeUuid, event);

        verify(deleteNodeConfirmation).confirmDeletion(any(), any(), any());
        verify(toolboxAction, never()).superOnMouseClick(canvasHandler, nodeUuid, event);
    }

    @Test
    public void testOnMouseClickWhenNodeDoesNotRequiresDeletionConfirmation() {

        final String nodeUuid = "node uuid";
        final Element element = mock(Element.class);
        final Node node = mock(Node.class);
        final MouseClickEvent event = new MouseClickEvent(0, 0, 0, 0);

        when(element.asNode()).thenReturn(node);
        when(graphIndex.get(nodeUuid)).thenReturn(element);
        when(deleteNodeConfirmation.requiresDeletionConfirmation(Collections.singleton(node))).thenReturn(false);

        toolboxAction.onMouseClick(canvasHandler, nodeUuid, event);

        verify(deleteNodeConfirmation, never()).confirmDeletion(any(), any(), any());
        verify(toolboxAction).superOnMouseClick(canvasHandler, nodeUuid, event);
    }
}
