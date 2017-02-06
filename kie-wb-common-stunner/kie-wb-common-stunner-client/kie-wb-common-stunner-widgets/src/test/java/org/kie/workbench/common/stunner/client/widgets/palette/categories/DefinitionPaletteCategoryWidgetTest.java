/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.palette.categories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconRenderer;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionPaletteCategoryWidgetTest extends AbstractPaletteRenderingTest {

    public static final String CATEGORY_ID = "category";

    public static int MAX_ELEMENTS_SIZE = 4;

    @Mock
    private DefinitionPaletteCategory item;
    @Mock
    private DefinitionPaletteCategoryWidgetView view;

    @Mock
    private ManagedInstance<DefinitionPaletteItemWidget> definitionPaletteItemWidgetInstance;

    @Mock
    private ManagedInstance<DefinitionPaletteGroupWidget> definitionPaletteGroupWidgetInstance;

    private List<DefinitionPaletteGroupWidget> groupWidgets = new ArrayList<>();

    private List<DefinitionPaletteItem> groups = new ArrayList<>();

    private Map<String, List<DefinitionPaletteItem>> groupItems = new HashMap<>();

    private DefinitionPaletteCategoryWidget widget;

    @Before
    public void init() {
        super.init();

        when(definitionPaletteGroupWidgetInstance.get()).thenAnswer(invocationOnMock -> {
            DefinitionPaletteGroupWidget widget = getDefinitionPaletteGroupWidget();
            groupWidgets.add(widget);
            return widget;
        });

        widget = new DefinitionPaletteCategoryWidget(view,
                                                     definitionPaletteItemWidgetInstance,
                                                     definitionPaletteGroupWidgetInstance);
        widget.setUp();
        verify(view).init(widget);

        groups.clear();

        groupItems.clear();

        for (int i = 0; i < MAX_ELEMENTS_SIZE; i++) {
            String groupId = "group_" + i;

            DefinitionPaletteGroup group = mock(DefinitionPaletteGroup.class);
            groups.add(group);
            when(group.getId()).thenReturn(groupId);
            List<DefinitionPaletteItem> items = new ArrayList<>();
            groupItems.put(groupId,
                           items);
            for (int j = 0; j < MAX_ELEMENTS_SIZE; j++) {
                DefinitionPaletteItem item = mock(DefinitionPaletteItem.class);
                String itemId = groupId + "_item_" + j;
                when(item.getId()).thenReturn(itemId);
                items.add(item);
            }
            when(group.getItems()).thenReturn(items);
        }

        widget.getCategory();
        widget.getElement();
        verify(view).getElement();

        when(item.getId()).thenReturn(CATEGORY_ID);
        when(item.getItems()).thenReturn(groups);
    }

    @Test
    public void testFunctionallity() {
        widget.initialize(
                item,
                iconRendererProvider,
                itemMouseDownCallback
        );

        verify(iconRendererProvider).getCategoryIconRenderer(item);
        verify(iconRenderer).resize(IconRenderer.Size.LARGE);
        verify(view).render(iconRenderer);
        verify(definitionPaletteGroupWidgetInstance,
               times(MAX_ELEMENTS_SIZE)).get();

        assertEquals(MAX_ELEMENTS_SIZE,
                     groupWidgets.size());

        for (int i = 0; i < MAX_ELEMENTS_SIZE; i++) {
            DefinitionPaletteGroupWidget groupWidget = groupWidgets.get(i);

            verify(groupWidget).initialize((DefinitionPaletteGroup) groups.get(i),
                                           iconRendererProvider,
                                           itemMouseDownCallback);

            verify(view).addGroup(groupWidget);
        }

        widget.onMouseDown(0,
                           0,
                           0,
                           0);
        verify(itemMouseDownCallback).onItemMouseDown(CATEGORY_ID,
                                                      0,
                                                      0,
                                                      0,
                                                      0);
    }

    @After
    public void destroy() {
        widget.destroy();
        verify(definitionPaletteItemWidgetInstance).destroyAll();
        verify(definitionPaletteGroupWidgetInstance).destroyAll();
    }
}
