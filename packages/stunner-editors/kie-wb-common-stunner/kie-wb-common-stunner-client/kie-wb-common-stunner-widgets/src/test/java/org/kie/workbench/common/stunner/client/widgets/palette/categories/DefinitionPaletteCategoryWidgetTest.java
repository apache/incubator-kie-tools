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


package org.kie.workbench.common.stunner.client.widgets.palette.categories;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteItemMouseEvent;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionPaletteCategoryWidgetTest {

    private static final int ICON_SIZE = 1234;

    private static final int GROUPS_COUNT = 5;

    private static final int SIMPLE_ITEMS_COUNT = 6;

    @Mock
    private DefinitionPaletteCategoryWidgetView view;

    @Mock
    private ManagedInstance<DefinitionPaletteItemWidget> definitionPaletteItemWidgetInstance;

    @Mock
    private ManagedInstance<DefinitionPaletteGroupWidget> definitionPaletteGroupWidgetInstance;

    @Mock
    private ShapeFactory<?, ?> shapeFactory;

    @Mock
    private Consumer<PaletteItemMouseEvent> itemMouseDownCallback;

    @Mock
    private Glyph categoryGlyph;

    @Mock
    private DefinitionPaletteItemWidget itemWidget;

    @Mock
    private DefinitionPaletteGroupWidget groupWidget;

    private ArgumentCaptor<PaletteItemMouseEvent> itemMouseEventCaptor;

    private DefinitionPaletteCategoryWidget widget;

    @Before
    public void setUp() {
        itemMouseEventCaptor = ArgumentCaptor.forClass(PaletteItemMouseEvent.class);
        widget = new DefinitionPaletteCategoryWidget(view,
                                                     definitionPaletteItemWidgetInstance,
                                                     definitionPaletteGroupWidgetInstance);
        widget.setUp();
        verify(view,
               times(1)).init(widget);
    }

    @Test
    public void testGetElement() {
        HTMLElement element = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(element);
        assertEquals(element,
                     widget.getElement());
    }

    @Test
    public void testInitialize() {
        when(definitionPaletteItemWidgetInstance.get()).thenReturn(itemWidget);
        when(definitionPaletteGroupWidgetInstance.get()).thenReturn(groupWidget);

        DefaultPaletteCategory category = mockPaletteCategory();
        widget.initialize(category,
                          shapeFactory,
                          itemMouseDownCallback);
        verify(view,
               times(1)).render(categoryGlyph,
                                ICON_SIZE,
                                ICON_SIZE);

        verify(definitionPaletteItemWidgetInstance,
               times(SIMPLE_ITEMS_COUNT)).get();
        category.getItems()
                .stream()
                .filter(item -> !(item instanceof PaletteGroup))
                .forEach(simpleItem -> verify(itemWidget,
                                              times(1)).initialize(simpleItem,
                                                                   shapeFactory,
                                                                   itemMouseDownCallback));

        verify(definitionPaletteGroupWidgetInstance,
               times(GROUPS_COUNT)).get();
        category.getItems()
                .stream()
                .filter(item -> (item instanceof PaletteGroup))
                .map(item -> (DefaultPaletteGroup) item)
                .forEach(groupItem -> verify(groupWidget,
                                             times(1)).initialize(groupItem,
                                                                  shapeFactory,
                                                                  itemMouseDownCallback));
    }

    @Test
    public void testSetVisible() {
        widget.setVisible(true);
        verify(view,
               times(1)).setVisible(true);
        widget.setVisible(false);
        verify(view,
               times(1)).setVisible(false);
    }

    @Test
    public void testSetAutoHidePanel() {
        widget.setAutoHidePanel(true);
        verify(view,
               times(1)).setAutoHidePanel(true);
        widget.setAutoHidePanel(false);
        verify(view,
               times(1)).setAutoHidePanel(false);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOnOpenCallback() {
        Consumer<DefaultPaletteCategory> onOpenCallback = mock(Consumer.class);
        widget.setOnOpenCallback(onOpenCallback);
        widget.onOpen();
        verify(onOpenCallback,
               times(1)).accept(anyObject());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOnCloseCallback() {
        Consumer<DefaultPaletteCategory> onCloseCallback = mock(Consumer.class);
        widget.setOnCloseCallback(onCloseCallback);
        widget.onClose();
        verify(onCloseCallback,
               times(1)).accept(anyObject());
    }

    @Test
    public void testOnMouseDown() {
        int clientX = 1;
        int clientY = 2;
        int x = 3;
        int y = 4;
        when(definitionPaletteItemWidgetInstance.get()).thenReturn(itemWidget);
        when(definitionPaletteGroupWidgetInstance.get()).thenReturn(groupWidget);
        DefaultPaletteCategory category = mockPaletteCategory();
        widget.initialize(category,
                          shapeFactory,
                          itemMouseDownCallback);
        widget.onMouseDown(clientX,
                           clientY,
                           x,
                           y);
        verify(itemMouseDownCallback,
               times(1)).accept(itemMouseEventCaptor.capture());
        assertEquals((float) clientX,
                     itemMouseEventCaptor.getValue().getMouseX(),
                     0);
        assertEquals((float) clientY,
                     itemMouseEventCaptor.getValue().getMouseY(),
                     0);
        assertEquals((float) x,
                     itemMouseEventCaptor.getValue().getItemX(),
                     0);
        assertEquals((float) y,
                     itemMouseEventCaptor.getValue().getItemY(),
                     0);
    }

    @Test
    public void testDestroy() {
        widget.destroy();
        verify(definitionPaletteItemWidgetInstance,
               times(1)).destroyAll();
        verify(definitionPaletteGroupWidgetInstance,
               times(1)).destroyAll();
    }

    private DefaultPaletteCategory mockPaletteCategory() {
        DefaultPaletteCategory paletteCategory = mock(DefaultPaletteCategory.class);
        when(paletteCategory.getGlyph()).thenReturn(categoryGlyph);
        when(paletteCategory.getIconSize()).thenReturn(ICON_SIZE);

        List<DefaultPaletteItem> items = new ArrayList<>();
        //add an arbitrary number of groups
        items.addAll(mockGroupItems(GROUPS_COUNT));
        //add an arbitrary number of simple item.
        items.addAll(mockSimpleItems(SIMPLE_ITEMS_COUNT));
        when(paletteCategory.getItems()).thenReturn(items);
        return paletteCategory;
    }

    private List<DefaultPaletteItem> mockSimpleItems(int size) {
        List<DefaultPaletteItem> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            items.add(mock(DefaultPaletteItem.class));
        }
        return items;
    }

    private List<DefaultPaletteGroup> mockGroupItems(int size) {
        List<DefaultPaletteGroup> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            items.add(mock(DefaultPaletteGroup.class));
        }
        return items;
    }
}
