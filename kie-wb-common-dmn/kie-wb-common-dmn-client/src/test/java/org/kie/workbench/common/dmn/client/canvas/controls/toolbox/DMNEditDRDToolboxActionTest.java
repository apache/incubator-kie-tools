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

package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

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
import org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNEditDRDToolboxAction.DRDACTIONS_CONTEXT_MENU_TITLE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNEditDRDToolboxActionTest {

    private static final String UUID = "UUID";

    private DMNEditDRDToolboxAction dmnEditDRDToolboxAction;

    private Element node;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Index<?, ?> graphIndex;

    @Mock
    private ContextMenu drdContextMenu;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private MouseClickEvent mouseClickEvent;

    @Before
    public void setUp() {
        node = new NodeImpl(UUID);
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(graphIndex.get(eq(UUID))).thenReturn(node);

        dmnEditDRDToolboxAction = new DMNEditDRDToolboxAction(drdContextMenu, translationService);
    }

    @Test
    public void testGetGlyph() {
        assertThat(dmnEditDRDToolboxAction.getGlyph(canvasHandler, UUID)).isNotNull();
        assertThat(dmnEditDRDToolboxAction.getGlyph(canvasHandler, UUID)).isInstanceOf(ImageDataUriGlyph.class);
    }

    @Test
    public void testGetTitle() {
        dmnEditDRDToolboxAction.getTitle(canvasHandler, UUID);

        verify(translationService,
               times(1)).getValue(eq(DRDACTIONS_CONTEXT_MENU_TITLE));
    }

    @Test
    public void testOnMouseClick() {
        final HTMLElement htmlElement = new HTMLElement();
        htmlElement.style = new CSSStyleDeclaration();
        final HTMLDocument htmlDocument = new HTMLDocument();
        htmlDocument.body = new HTMLBodyElement();
        Whitebox.setInternalState(DomGlobal.class, "document", htmlDocument);
        when(drdContextMenu.getElement()).thenReturn(htmlElement);

        dmnEditDRDToolboxAction.onMouseClick(canvasHandler, UUID, mouseClickEvent);

        verify(drdContextMenu, times(1)).show(any(Consumer.class));
    }

    @Test
    public void testContextMenuHandler() {
        when(translationService.getValue(anyString())).thenReturn(StringUtils.EMPTY);
        dmnEditDRDToolboxAction.contextMenuHandler(drdContextMenu, node);

        verify(drdContextMenu, times(1)).setHeaderMenu(anyString(), anyString());
        verify(drdContextMenu, times(3)).addTextMenuItem(anyString(), anyBoolean(), any(Command.class));
    }
}
