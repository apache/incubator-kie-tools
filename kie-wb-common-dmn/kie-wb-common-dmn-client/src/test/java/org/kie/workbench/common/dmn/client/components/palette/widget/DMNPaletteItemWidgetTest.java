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
package org.kie.workbench.common.dmn.client.components.palette.widget;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DMNPaletteItemWidgetTest {

    private static final String ITEM_ID = "itemId";

    private static final String DEFINITION_ID = "definitionId";

    @Mock
    private DMNPaletteItemWidgetView view;

    @Mock
    private DefinitionPaletteItem item;

    @Mock
    private ShapeFactory shapeFactory;

    @Mock
    private Palette.ItemMouseDownCallback itemMouseDownCallback;

    @Mock
    private Glyph glyph;

    private DMNPaletteItemWidget widget;

    @Before
    public void setup() {
        this.widget = new DMNPaletteItemWidget(view);
        when(item.getId()).thenReturn(ITEM_ID);
        when(item.getDefinitionId()).thenReturn(DEFINITION_ID);
        when(shapeFactory.getGlyph(eq(DEFINITION_ID))).thenReturn(glyph);
    }

    @Test
    public void checkViewHasPresenterInjected() {
        widget.init();
        verify(view).init(eq(widget));
    }

    @Test
    public void checkInitialiseRendersGlyph() {
        widget.initialize(item,
                          shapeFactory,
                          itemMouseDownCallback);
        verify(view).render(eq(glyph),
                            eq(DMNPaletteItemWidget.ICON_WIDTH),
                            eq(DMNPaletteItemWidget.ICON_HEIGHT));
    }

    @Test
    public void checkOnMouseDownNotInvokedIfCallbackIsSet() {
        widget.initialize(item,
                          shapeFactory,
                          itemMouseDownCallback);
        widget.onMouseDown(5,
                           10,
                           15,
                           20);
        verify(itemMouseDownCallback).onItemMouseDown(eq(ITEM_ID),
                                                      eq(5.0),
                                                      eq(10.0),
                                                      eq(15.0),
                                                      eq(20.0));
    }

    @Test
    public void checkOnMouseDownNotInvokedIfCallbackIsNotSet() {
        widget.onMouseDown(5,
                           10,
                           15,
                           20);
        verify(itemMouseDownCallback,
               never()).onItemMouseDown(anyString(),
                                        anyInt(),
                                        anyInt(),
                                        anyInt(),
                                        anyInt());
    }
}
