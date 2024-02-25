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

import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.HTMLBodyElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenu;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
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
    private DRDContextMenu drdContextMenu;

    @Mock
    private MouseClickEvent mouseClickEvent;

    @Before
    public void setUp() {
        node = new NodeImpl(UUID);
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(graphIndex.get(eq(UUID))).thenReturn(node);

        dmnEditDRDToolboxAction = new DMNEditDRDToolboxAction(drdContextMenu);
    }

    @Test
    public void testGetGlyph() {
        assertThat(dmnEditDRDToolboxAction.getGlyph(canvasHandler, UUID)).isNotNull();
        assertThat(dmnEditDRDToolboxAction.getGlyph(canvasHandler, UUID)).isInstanceOf(ImageDataUriGlyph.class);
    }

    @Test
    public void testGetTitle() {
        final String title = "TITLE";

        when(drdContextMenu.getTitle()).thenReturn(title);

        assertThat(dmnEditDRDToolboxAction.getTitle(canvasHandler, UUID)).isEqualTo(title);
    }

    @Test
    public void testOnMouseClick() throws NoSuchFieldException, IllegalAccessException {
        final HTMLElement htmlElement = new HTMLElement();
        htmlElement.style = new CSSStyleDeclaration();
        final HTMLDocument htmlDocument = new HTMLDocument();
        htmlDocument.body = new HTMLBodyElement();

        when(drdContextMenu.getElement()).thenReturn(htmlElement);

        dmnEditDRDToolboxAction.onMouseClick(canvasHandler, UUID, mouseClickEvent);

        verify(drdContextMenu, times(1)).show(Mockito.<Collection>any());
    }

}
