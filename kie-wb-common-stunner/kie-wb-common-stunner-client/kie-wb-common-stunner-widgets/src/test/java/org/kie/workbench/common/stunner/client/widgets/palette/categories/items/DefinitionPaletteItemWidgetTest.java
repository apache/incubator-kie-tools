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

package org.kie.workbench.common.stunner.client.widgets.palette.categories.items;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.AbstractPaletteRenderingTest;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconRenderer;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionPaletteItemWidgetTest extends AbstractPaletteRenderingTest {

    protected static final String ITEM_ID = "testId";

    @Mock
    private DefinitionPaletteItem item;

    @Mock
    private DefinitionPaletteItemWidgetView view;

    private DefinitionPaletteItemWidget widget;

    @Before
    public void init() {
        super.init();
        when(item.getId()).thenReturn(ITEM_ID);

        widget = new DefinitionPaletteItemWidget(view);
        widget.setUp();
        verify(view).init(widget);
    }

    @Test
    public void testFunctionallity() {
        widget.initialize(item,
                          iconRendererProvider,
                          itemMouseDownCallback);

        verify(iconRendererProvider).getDefinitionIconRenderer(item);
        verify(iconRenderer).resize(IconRenderer.Size.SMALL);
        verify(view).render(iconRenderer);

        widget.getItem();

        widget.onMouseDown(0,
                           0,
                           0,
                           0);
        verify(item).getId();
        verify(itemMouseDownCallback).onItemMouseDown(ITEM_ID,
                                                      0,
                                                      0,
                                                      0,
                                                      0);

        widget.getElement();
        verify(view).getElement();
    }
}
