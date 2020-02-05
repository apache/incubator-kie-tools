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
package org.kie.workbench.common.stunner.client.widgets.palette;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.collapsed.CollapsedDefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.CollapsedDefaultPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinition;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteItemMouseEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMinimizedEvent;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.preferences.StunnerDiagramEditorPreferences;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BS3PaletteWidgetImplTest {

    private static final int CATEGORY_ITEMS_COUNT = 6;
    private static final int SIMPLE_ITEMS_COUNT = 8;
    private static final int COLLAPSED_ITEMS_COUNT = 2;

    private static final String DEFINITION_SET_ID = "DEFINITION_SET_ID";
    private static final String DEFINITION_ID = "DEFINITION_ID";

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private ShapeSet shapeSet;

    @Mock
    private ShapeFactory shapeFactory;

    @Mock
    private ClientFactoryService clientFactoryServices;

    @Mock
    private BS3PaletteWidgetView view;

    @Mock
    private ShapeGlyphDragHandler shapeGlyphDragHandler;

    @Mock
    private Consumer<PaletteItemMouseEvent> itemMouseDownCallback;

    @Mock
    private ManagedInstance<DefinitionPaletteCategoryWidget> categoryWidgetInstance;

    private List<DefinitionPaletteCategoryWidget> createdCategoryWidgets = new ArrayList<>();

    @Mock
    private ManagedInstance<DefinitionPaletteItemWidget> definitionPaletteItemWidgets;

    private List<DefinitionPaletteItemWidget> createdItemWidgets = new ArrayList<>();

    @Mock
    private ManagedInstance<CollapsedDefinitionPaletteItemWidget> collapsedDefinitionPaletteItemWidgets;

    private List<CollapsedDefinitionPaletteItemWidget> createdCollapsedItemWidgets = new ArrayList<>();

    @Mock
    private DefaultPaletteDefinition paletteDefinition;

    @Mock
    private StunnerPreferencesRegistries preferencesRegistries;

    @Mock
    private StunnerPreferences stunnerPreferences;

    @Mock
    private StunnerDiagramEditorPreferences diagramEditorPreferences;

    private BS3PaletteWidgetImpl palette;

    @Before
    public void setup() {
        when(shapeManager.getDefaultShapeSet(DEFINITION_SET_ID)).thenReturn(shapeSet);
        when(shapeSet.getShapeFactory()).thenReturn(shapeFactory);
        when(stunnerPreferences.getDiagramEditorPreferences()).thenReturn(diagramEditorPreferences);

        when(preferencesRegistries.get(anyString(), eq(StunnerPreferences.class))).thenReturn(stunnerPreferences);
        createdCategoryWidgets.clear();
        createdItemWidgets.clear();
        this.palette = new BS3PaletteWidgetImpl(shapeManager,
                                                clientFactoryServices,
                                                view,
                                                shapeGlyphDragHandler,
                                                preferencesRegistries,
                                                categoryWidgetInstance,
                                                definitionPaletteItemWidgets,
                                                collapsedDefinitionPaletteItemWidgets) {
            @Override
            protected DefinitionPaletteCategoryWidget newDefinitionPaletteCategoryWidget() {
                DefinitionPaletteCategoryWidget categoryWidget = mock(DefinitionPaletteCategoryWidget.class);
                createdCategoryWidgets.add(categoryWidget);
                when(categoryWidgetInstance.get()).thenReturn(categoryWidget);
                return super.newDefinitionPaletteCategoryWidget();
            }

            @Override
            protected DefinitionPaletteItemWidget newDefinitionPaletteItemWidget() {
                DefinitionPaletteItemWidget itemWidget = mock(DefinitionPaletteItemWidget.class);
                createdItemWidgets.add(itemWidget);
                when(definitionPaletteItemWidgets.get()).thenReturn(itemWidget);
                return super.newDefinitionPaletteItemWidget();
            }

            @Override
            protected CollapsedDefinitionPaletteItemWidget newCollapsedDefinitionPaletteItemWidget() {
                CollapsedDefinitionPaletteItemWidget itemWidget = mock(CollapsedDefinitionPaletteItemWidget.class);
                createdCollapsedItemWidgets.add(itemWidget);
                when(collapsedDefinitionPaletteItemWidgets.get()).thenReturn(itemWidget);
                return super.newCollapsedDefinitionPaletteItemWidget();
            }
        };
        this.palette.init();
    }

    @Test
    public void checkDestructionReleasesResources() {
        palette.doDestroy();

        verify(categoryWidgetInstance).destroyAll();
        verify(definitionPaletteItemWidgets).destroyAll();
        verify(collapsedDefinitionPaletteItemWidgets).destroyAll();
        verify(view).destroy();
    }

    @Test
    public void checkOnScreenMaximisedDiagramEditor() {
        final ScreenMaximizedEvent event = new ScreenMaximizedEvent(true);
        palette.onScreenMaximized(event);

        verify(view).showEmptyView(false);
    }

    @Test
    public void checkOnScreenMaximisedNotDiagramEditor() {
        //showEmptyView(true) is called in the palette.init() method so reset for this test
        reset(view);

        final ScreenMaximizedEvent event = new ScreenMaximizedEvent(false);
        palette.onScreenMaximized(event);

        verify(view).showEmptyView(true);
    }

    @Test
    public void checkOnScreenMinimisedDiagramEditor() {
        final ScreenMinimizedEvent event = new ScreenMinimizedEvent(true);
        palette.onScreenMinimized(event);

        verify(view).showEmptyView(false);
    }

    @Test
    public void checkOnScreenMinimisedNotDiagramEditor() {
        final ScreenMinimizedEvent event = new ScreenMinimizedEvent(false);
        palette.onScreenMinimized(event);

        verify(view).showEmptyView(false);
    }

    @Test
    public void testBind() {
        when(paletteDefinition.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);

        boolean arbitraryHidePanelValue = true;
        when(diagramEditorPreferences.isAutoHidePalettePanel()).thenReturn(arbitraryHidePanelValue);

        List<DefaultPaletteItem> items = new ArrayList<>();
        items.addAll(mockCategoryItems(CATEGORY_ITEMS_COUNT));
        items.addAll(mockSimpleItems(SIMPLE_ITEMS_COUNT));
        items.addAll(mockCollapsedItems(COLLAPSED_ITEMS_COUNT));
        when(paletteDefinition.getItems()).thenReturn(items);

        palette.bind(paletteDefinition);

        verify(categoryWidgetInstance,
               times(CATEGORY_ITEMS_COUNT)).get();
        verify(definitionPaletteItemWidgets,
               times(SIMPLE_ITEMS_COUNT)).get();
        verify(collapsedDefinitionPaletteItemWidgets,
               times(COLLAPSED_ITEMS_COUNT)).get();

        assertEquals(CATEGORY_ITEMS_COUNT,
                     createdCategoryWidgets.size());
        assertEquals(SIMPLE_ITEMS_COUNT,
                     createdItemWidgets.size());
        assertEquals(COLLAPSED_ITEMS_COUNT,
                     createdCollapsedItemWidgets.size());

        List<DefaultPaletteCategory> categoryItems = items.stream()
                .filter(item -> (item instanceof DefaultPaletteCategory))
                .map(categoryItem -> (DefaultPaletteCategory) categoryItem)
                .collect(Collectors.toList());
        for (int i = 0; i < createdCategoryWidgets.size(); i++) {
            DefaultPaletteCategory category = categoryItems.get(i);
            DefinitionPaletteCategoryWidget widget = createdCategoryWidgets.get(i);
            widget.setOnOpenCallback(anyObject());
            widget.setOnCloseCallback(anyObject());
            verify(widget,
                   times(1)).initialize(eq(category),
                                        eq(shapeFactory),
                                        anyObject());
            verify(widget,
                   times(1)).setAutoHidePanel(arbitraryHidePanelValue);
        }

        List<DefaultPaletteItem> paletteItems = items.stream()
                .filter(item -> !(item instanceof DefaultPaletteCategory) && !(item instanceof CollapsedDefaultPaletteItem))
                .collect(Collectors.toList());
        for (int i = 0; i < createdItemWidgets.size(); i++) {
            DefaultPaletteItem item = paletteItems.get(i);
            DefinitionPaletteItemWidget widget = createdItemWidgets.get(i);
            verify(widget,
                   times(1)).initialize(eq(item),
                                        eq(shapeFactory),
                                        anyObject());
        }

        List<CollapsedDefaultPaletteItem> collapsedPaletteItems = items.stream()
                .filter(item -> (item instanceof CollapsedDefaultPaletteItem))
                .map(item -> (CollapsedDefaultPaletteItem) item)
                .collect(Collectors.toList());
        for (int i = 0; i < createdCollapsedItemWidgets.size(); i++) {
            CollapsedDefaultPaletteItem item = collapsedPaletteItems.get(i);
            CollapsedDefinitionPaletteItemWidget widget = createdCollapsedItemWidgets.get(i);
            verify(widget,
                   times(1)).initialize(eq(item),
                                        eq(shapeFactory),
                                        anyObject());
        }
    }

    @Test
    public void testSetPreferences() {
        when(paletteDefinition.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);
        List<DefaultPaletteItem> items = new ArrayList<>();
        items.addAll(mockCategoryItems(CATEGORY_ITEMS_COUNT));
        items.addAll(mockSimpleItems(SIMPLE_ITEMS_COUNT));
        items.addAll(mockCollapsedItems(COLLAPSED_ITEMS_COUNT));
        when(paletteDefinition.getItems()).thenReturn(items);
        when(diagramEditorPreferences.isAutoHidePalettePanel()).thenReturn(true);

        palette.bind(paletteDefinition);

        createdCategoryWidgets.forEach(categoryWidget -> verify(categoryWidget,
                                                                times(1)).setAutoHidePanel(eq(true)));
    }

    @Test
    public void testGetGlyph() {
        when(paletteDefinition.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);

        palette.bind(paletteDefinition);

        palette.getShapeGlyph(DEFINITION_ID);

        verify(shapeFactory).getGlyph(eq(DEFINITION_ID), eq(AbstractPalette.PaletteGlyphConsumer.class));
    }

    @Test
    public void testGetShapeDragProxyGlyph() {
        when(paletteDefinition.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);

        palette.bind(paletteDefinition);

        palette.getShapeDragProxyGlyph(DEFINITION_ID);

        verify(shapeFactory).getGlyph(eq(DEFINITION_ID), eq(AbstractPalette.PaletteDragProxyGlyphConsumer.class));
    }

    private List<DefaultPaletteCategory> mockCategoryItems(int size) {
        List<DefaultPaletteCategory> items = new ArrayList<>();
        String categoryIdPrefix = "CategoryID";
        String categoryId;
        DefaultPaletteCategory category;
        for (int i = 0; i < size; i++) {
            categoryId = categoryIdPrefix + i;
            category = mock(DefaultPaletteCategory.class);
            when(category.getId()).thenReturn(categoryId);
            items.add(category);
        }
        return items;
    }

    private List<DefaultPaletteItem> mockSimpleItems(int size) {
        List<DefaultPaletteItem> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            items.add(mock(DefaultPaletteItem.class));
        }
        return items;
    }

    private List<CollapsedDefaultPaletteItem> mockCollapsedItems(int size) {
        List<CollapsedDefaultPaletteItem> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            items.add(mock(CollapsedDefaultPaletteItem.class));
        }
        return items;
    }
}
