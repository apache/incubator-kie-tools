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
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MorphNodeActionTest {

    private static final String E_UUID = "e1";
    private static final String SSID_UUID = "ss1";
    private static final String MORPH_TARGET_ID = "mt1";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> commandFactory;

    @Mock
    private EventSourceMock<CanvasSelectionEvent> canvasElementSelectedEvent;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private Layer layer;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Node<? extends Definition<?>, Edge> element;

    @Mock
    private MorphDefinition morphDefinition;

    @Mock
    private Index<?, ?> graphIndex;

    private MorphNodeAction tested;
    private CanvasCommand<AbstractCanvasHandler> morphNodeCommand;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(commandFactory.morphNode(eq(element),
                                      eq(morphDefinition),
                                      eq(MORPH_TARGET_ID),
                                      eq(SSID_UUID)))
                .thenReturn(morphNodeCommand);
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getLayer()).thenReturn(layer);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getShapeSetId()).thenReturn(SSID_UUID);
        when(graphIndex.get(eq(E_UUID))).thenReturn(element);
        when(element.asNode()).thenReturn((Node) element);
        this.tested = new MorphNodeAction(definitionUtils,
                                          sessionCommandManager,
                                          commandFactory,
                                          translationService,
                                          canvasElementSelectedEvent)
                .setMorphDefinition(morphDefinition)
                .setTargetDefinitionId(MORPH_TARGET_ID);
    }

    @Test
    public void testTitle() {
        tested.getTitle(canvasHandler,
                        E_UUID);
        verify(translationService,
               times(1)).getValue(eq(MorphNodeAction.KEY_TITLE));
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
               times(1)).morphNode(eq(element),
                                   eq(morphDefinition),
                                   eq(MORPH_TARGET_ID),
                                   eq(SSID_UUID));
        verify(sessionCommandManager,
               times(1)).execute(eq(canvasHandler),
                                 eq(morphNodeCommand));
        final ArgumentCaptor<CanvasSelectionEvent> eventArgumentCaptor =
                ArgumentCaptor.forClass(CanvasSelectionEvent.class);
        verify(canvasElementSelectedEvent,
               times(1)).fire(eventArgumentCaptor.capture());
        final CanvasSelectionEvent eCaptured = eventArgumentCaptor.getValue();
        assertEquals(E_UUID,
                     eCaptured.getIdentifiers().iterator().next());
    }
}
