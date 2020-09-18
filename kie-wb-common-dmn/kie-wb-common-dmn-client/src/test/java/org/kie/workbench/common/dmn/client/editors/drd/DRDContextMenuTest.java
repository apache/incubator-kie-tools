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

package org.kie.workbench.common.dmn.client.editors.drd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLBodyElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramTuple;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.uberfire.mvp.Command;

import static java.util.Arrays.asList;
import static org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenu.DRDACTIONS_CONTEXT_MENU_TITLE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DRDContextMenuTest {

    private DRDContextMenu drdContextMenu;

    @Mock
    private ContextMenu contextMenu;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private DRDContextMenuService drdContextMenuService;

    @Mock
    private Node<? extends Definition<?>, Edge> node;

    @Mock
    private HTMLElement element;

    @Mock
    private CSSStyleDeclaration styleDeclaration;

    @Mock
    private HTMLDocument htmlDocument;

    @Mock
    private HTMLBodyElement body;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Before
    public void setUp() {
        drdContextMenu = new DRDContextMenu(contextMenu, translationService, drdContextMenuService, dmnDiagramsSession);
    }

    @Test
    public void testGetTitle() {
        drdContextMenu.getTitle();

        verify(translationService).getValue(eq(DRDACTIONS_CONTEXT_MENU_TITLE));
    }

    @Test
    public void testGetElement() {
        drdContextMenu.getElement();

        verify(contextMenu).getElement();
    }

    @Test
    public void testShow() {
        drdContextMenu.show(new ArrayList<>());

        verify(contextMenu).show(any(Consumer.class));
    }

    @Test
    public void testContextMenuHandler() {

        final DMNDiagramTuple diagramTuple1 = new DMNDiagramTuple(mock(Diagram.class), new DMNDiagramElement());
        final DMNDiagramTuple diagramTuple2 = new DMNDiagramTuple(mock(Diagram.class), new DMNDiagramElement());
        final List<DMNDiagramTuple> diagrams = asList(diagramTuple1, diagramTuple2);
        final DMNDiagramElement diagramElement = mock(DMNDiagramElement.class);

        when(translationService.getValue(anyString())).thenReturn(StringUtils.EMPTY);
        when(drdContextMenuService.getDiagrams()).thenReturn(diagrams);
        when(dmnDiagramsSession.getDRGDiagramElement()).thenReturn(diagramElement);
        when(dmnDiagramsSession.getCurrentDMNDiagramElement()).thenReturn(Optional.of(diagramElement));

        drdContextMenu.setDRDContextMenuHandler(contextMenu, Collections.singletonList(node));

        verify(contextMenu).setHeaderMenu(anyString(), anyString());
        verify(contextMenu, times(4)).addTextMenuItem(anyString(), anyBoolean(), any(Command.class));
    }

    @Test
    public void testAppendContextMenuToTheDOM() {
        when(contextMenu.getElement()).thenReturn(element);
        Whitebox.setInternalState(element, "style", styleDeclaration);
        Whitebox.setInternalState(DomGlobal.class, "document", htmlDocument);
        Whitebox.setInternalState(htmlDocument, "body", body);

        drdContextMenu.appendContextMenuToTheDOM(10, 10);

        verify(body).appendChild(any(HTMLElement.class));
    }
}
