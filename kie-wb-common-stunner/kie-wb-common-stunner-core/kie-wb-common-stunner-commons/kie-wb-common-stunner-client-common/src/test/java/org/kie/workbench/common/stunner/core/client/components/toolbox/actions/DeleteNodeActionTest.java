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

package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DeleteNodeActionTest {

    private static final String E_UUID = "e1";

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> commandFactory;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Node element;

    @Mock
    private Index<?, ?> graphIndex;

    @Mock
    private CanvasCommand<AbstractCanvasHandler> deleteNodeCommand;

    private DeleteNodeAction tested;

    @Before
    public void setup() throws Exception {
        when(commandFactory.deleteNode(eq(element))).thenReturn(deleteNodeCommand);
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(graphIndex.get(eq(E_UUID))).thenReturn(element);
        when(element.asNode()).thenReturn(element);
        this.tested = new DeleteNodeAction(translationService,
                                           sessionCommandManager,
                                           commandFactory,
                                           action -> true);
    }

    @Test
    public void testTitle() {
        tested.getTitle(canvasHandler,
                        E_UUID);
        verify(translationService,
               times(1)).getValue(eq(CoreTranslationMessages.DELETE));
    }

    @Test
    public void testAction() {
        final MouseClickEvent event = mock(MouseClickEvent.class);
        ToolboxAction<AbstractCanvasHandler> cascade =
                tested.onMouseClick(canvasHandler,
                                    E_UUID,
                                    event);
        assertEquals(tested,
                     cascade);
        verify(commandFactory,
               times(1)).deleteNode(eq(element));
        verify(sessionCommandManager,
               times(1)).execute(eq(canvasHandler),
                                 eq(deleteNodeCommand));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSkipAction() {
        this.tested = new DeleteNodeAction(translationService,
                                           sessionCommandManager,
                                           commandFactory,
                                           action -> false);
        final MouseClickEvent event = mock(MouseClickEvent.class);
        ToolboxAction<AbstractCanvasHandler> cascade =
                tested.onMouseClick(canvasHandler,
                                    E_UUID,
                                    event);
        assertEquals(tested,
                     cascade);
        verify(sessionCommandManager,
               never()).execute(eq(canvasHandler),
                                any(CanvasCommand.class));
    }
}
