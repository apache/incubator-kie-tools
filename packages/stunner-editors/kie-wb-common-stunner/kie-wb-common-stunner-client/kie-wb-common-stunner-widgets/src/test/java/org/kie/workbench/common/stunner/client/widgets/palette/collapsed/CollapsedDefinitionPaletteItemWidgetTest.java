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


package org.kie.workbench.common.stunner.client.widgets.palette.collapsed;

import java.util.function.Consumer;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.CollapsedDefaultPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteItemMouseEvent;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollapsedDefinitionPaletteItemWidgetTest {

    private static final String ITEM_DEFINITION_ID = "ITEM_DEFINITION_ID";
    private static final String ITEM_ID = "ITEM_ID";
    private static final int ITEM_ICON_SIZE = 1234;

    @Mock
    private CollapsedDefinitionPaletteItemWidgetView view;

    @Mock
    private CollapsedDefaultPaletteItem item;

    @Mock
    private ShapeFactory<?, ?> shapeFactory;

    @Mock
    private Glyph glyph;

    @Mock
    private Consumer<PaletteItemMouseEvent> itemMouseDownCallback;

    private ArgumentCaptor<PaletteItemMouseEvent> itemMouseEventCaptor;

    private CollapsedDefinitionPaletteItemWidget widget;

    @Before
    public void setUp() {
        when(item.getDefinitionId()).thenReturn(ITEM_DEFINITION_ID);
        when(item.getIconSize()).thenReturn(ITEM_ICON_SIZE);
        when(item.getId()).thenReturn(ITEM_ID);
        when(shapeFactory.getGlyph(ITEM_DEFINITION_ID, AbstractPalette.PaletteGlyphConsumer.class)).thenReturn(glyph);
        itemMouseEventCaptor = ArgumentCaptor.forClass(PaletteItemMouseEvent.class);

        widget = new CollapsedDefinitionPaletteItemWidget(view);
        widget.setUp();
        verify(view,
               times(1)).init(widget);
    }

    @Test
    public void testInitialize() {
        widget.initialize(item,
                          shapeFactory,
                          itemMouseDownCallback);
        verify(shapeFactory).getGlyph(ITEM_DEFINITION_ID,
                                      AbstractPalette.PaletteGlyphConsumer.class);
        verify(view,
               times(1)).render(glyph,
                                ITEM_ICON_SIZE,
                                ITEM_ICON_SIZE);
    }

    @Test
    public void testGetItem() {
        widget.initialize(item,
                          shapeFactory,
                          itemMouseDownCallback);
        assertEquals(item,
                     widget.getItem());
    }

    @Test
    public void testOnMouseDown() {
        final int clientX = 1;
        final int clientY = 2;
        final int x = 3;
        final int y = 4;
        widget.initialize(item,
                          shapeFactory,
                          itemMouseDownCallback);
        widget.onMouseDown(clientX,
                           clientY,
                           x,
                           y);
        verify(itemMouseDownCallback,
               times(1)).accept(itemMouseEventCaptor.capture());
        assertEquals(ITEM_ID,
                     itemMouseEventCaptor.getValue().getId());
        assertEquals(clientX,
                     itemMouseEventCaptor.getValue().getMouseX(),
                     0);
        assertEquals(clientY,
                     itemMouseEventCaptor.getValue().getMouseY(),
                     0);
        assertEquals(x,
                     itemMouseEventCaptor.getValue().getItemX(),
                     0);
        assertEquals(y,
                     itemMouseEventCaptor.getValue().getItemY(),
                     0);
    }

    @Test
    public void testGetElement() {
        final HTMLElement element = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(element);
        assertEquals(element,
                     widget.getElement());
    }
}
