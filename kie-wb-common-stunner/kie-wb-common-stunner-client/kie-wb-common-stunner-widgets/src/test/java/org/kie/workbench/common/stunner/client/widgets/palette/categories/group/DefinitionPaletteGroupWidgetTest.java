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

package org.kie.workbench.common.stunner.client.widgets.palette.categories.group;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.AbstractPaletteRenderingTest;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionPaletteGroupWidgetTest extends AbstractPaletteRenderingTest {

    protected static final String ITEM_ID = "testId";

    @Mock
    private DefinitionPaletteGroup group;

    @Mock
    private DefinitionPaletteGroupWidgetView view;

    @Mock
    private ManagedInstance<DefinitionPaletteItemWidget> definitionPaletteItemWidgets;

    protected List<DefinitionPaletteItemWidget> itemWidgets = new ArrayList<>();

    private DefinitionPaletteGroupWidget widget;

    @Before
    public void init() {
        super.init();
        when(definitionPaletteItemWidgets.get()).then(invocationOnMock -> {
                                                          DefinitionPaletteItemWidget widget = getDefinitionPaletteItemWidget();
                                                          itemWidgets.add(widget);
                                                          return widget;
                                                      }
        );
        itemWidgets.clear();
        widget = new DefinitionPaletteGroupWidget(view,
                                                  definitionPaletteItemWidgets);
        widget.setUp();
        verify(view).init(widget);
    }

    @Test
    public void testFunctionallityWithActionLinks() {
        List<DefinitionPaletteItem> paletteItems = new ArrayList<>();
        paletteItems.add(getDefintionPaletteItem());
        paletteItems.add(getDefintionPaletteItem());
        paletteItems.add(getDefintionPaletteItem());
        paletteItems.add(getDefintionPaletteItem());

        when(group.getItems()).thenReturn(paletteItems);

        widget.initialize(group,
                          iconRendererProvider,
                          itemMouseDownCallback);

        verify(view).initView();
        verify(definitionPaletteItemWidgets).destroyAll();
        verify(group).getItems();

        verify(definitionPaletteItemWidgets,
               times(4)).get();
        verify(view,
               times(4)).addItem(any());
        verify(view).addAnchors();
        verify(view).showMoreAnchor();
        assertEquals(4,
                     itemWidgets.size());
        verify(itemWidgets.get(3)).getElement();

        widget.getItem();
        widget.getElement();
        verify(view).getElement();

        widget.showMore();
        view.showLessAnchor();
        verify(itemWidgets.get(3),
               times(2)).getElement();

        widget.showLess();
        view.showMoreAnchor();
        verify(itemWidgets.get(3),
               times(3)).getElement();
    }

    @Test
    public void testFunctionallityWithoutActionLinks() {

        List<DefinitionPaletteItem> paletteItems = new ArrayList<>();
        paletteItems.add(getDefintionPaletteItem());
        paletteItems.add(getDefintionPaletteItem());

        when(group.getItems()).thenReturn(paletteItems);

        widget.initialize(group,
                          iconRendererProvider,
                          itemMouseDownCallback);

        verify(view).initView();
        verify(definitionPaletteItemWidgets).destroyAll();
        verify(group).getItems();

        verify(definitionPaletteItemWidgets,
               times(2)).get();
        verify(view,
               times(2)).addItem(any());
        verify(view).addAnchors();
        verify(view,
               never()).showMoreAnchor();
        assertEquals(2,
                     itemWidgets.size());

        widget.getItem();
        widget.getElement();
        verify(view).getElement();
    }

    protected DefinitionPaletteItem getDefintionPaletteItem() {
        DefinitionPaletteItem item = mock(DefinitionPaletteItem.class);
        when(item.getId()).thenReturn(ITEM_ID);
        return item;
    }

    @After
    public void finish() {
        widget.destroy();
        verify(definitionPaletteItemWidgets,
               times(2)).destroyAll();
    }
}
