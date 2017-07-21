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

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.palette.PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.BS3PaletteViewFactory;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.Palette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionsPalette;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DMNPaletteWidgetTest {

    private static final String DEFINITION_SET_ID = "definitionSetId";

    private static final String DEFINITION_ID = "definitionId";

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private ClientFactoryService clientFactoryServices;

    @Mock
    private DMNPaletteWidgetView view;

    @Mock
    private ManagedInstance<DMNPaletteItemWidget> paletteItemWidgets;

    @Mock
    private ClientFactoryManager clientFactoryManager;

    @Mock
    private DefinitionsPalette palette;

    @Mock
    private ShapeSet shapeSet;

    @Mock
    private ShapeFactory shapeFactory;

    @Mock
    private Glyph glyph;

    @Mock
    private PaletteWidget.ItemDragStartCallback itemDragStartCallback;

    @Mock
    private PaletteWidget.ItemDragUpdateCallback itemDragUpdateCallback;

    @Mock
    private PaletteWidget.ItemDropCallback itemDropCallback;

    private Object definition = new Object();

    private DMNPaletteWidget widget;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.widget = new DMNPaletteWidget(shapeManager,
                                           clientFactoryServices,
                                           view,
                                           paletteItemWidgets);
        when(clientFactoryServices.getClientFactoryManager()).thenReturn(clientFactoryManager);
        when(clientFactoryManager.newDefinition(eq(DEFINITION_ID))).thenReturn(definition);
        when(palette.getDefinitionSetId()).thenReturn(DEFINITION_SET_ID);
        when(shapeManager.getDefaultShapeSet(eq(DEFINITION_SET_ID))).thenReturn(shapeSet);
        when(shapeSet.getShapeFactory()).thenReturn(shapeFactory);
        when(shapeFactory.getGlyph(eq(DEFINITION_ID))).thenReturn(glyph);
    }

    @Test
    public void checkViewHasPresenterInjected() {
        widget.init();
        verify(view).init(eq(widget));
        verify(view).setBackgroundColor(eq(DMNPaletteWidget.BG_COLOR));
        verify(view).showEmptyView(eq(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkOnDragStartWhenCallbackIsSet() {
        widget.onItemDragStart(itemDragStartCallback);

        widget.bind(palette);

        widget.onDragStart(DEFINITION_ID,
                           10.0,
                           20.0);

        verify(clientFactoryServices).getClientFactoryManager();
        verify(clientFactoryManager).newDefinition(eq(DEFINITION_ID));

        verify(itemDragStartCallback).onDragStartItem(eq(definition),
                                                      eq(shapeFactory),
                                                      eq(10.0),
                                                      eq(20.0));
    }

    @Test
    public void checkOnDragStartWhenCallbackIsNotSet() {
        widget.onDragStart(DEFINITION_ID,
                           10.0,
                           20.0);

        verify(clientFactoryServices,
               never()).getClientFactoryManager();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkOnDragProxyMoveWhenCallbackIsSet() {
        widget.onItemDragUpdate(itemDragUpdateCallback);

        widget.bind(palette);

        widget.onDragProxyMove(DEFINITION_ID,
                               10.0,
                               20.0);

        verify(clientFactoryServices).getClientFactoryManager();
        verify(clientFactoryManager).newDefinition(eq(DEFINITION_ID));

        verify(itemDragUpdateCallback).onDragUpdateItem(eq(definition),
                                                        eq(shapeFactory),
                                                        eq(10.0),
                                                        eq(20.0));
    }

    @Test
    public void checkOnDragProxyMoveWhenCallbackIsNotSet() {
        widget.onDragProxyMove(DEFINITION_ID,
                               10.0,
                               20.0);

        verify(clientFactoryServices,
               never()).getClientFactoryManager();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkOnDragProxyCompleteWhenCallbackIsSet() {
        widget.onItemDrop(itemDropCallback);

        widget.bind(palette);

        widget.onDragProxyComplete(DEFINITION_ID,
                                   10.0,
                                   20.0);

        verify(clientFactoryServices).getClientFactoryManager();
        verify(clientFactoryManager).newDefinition(eq(DEFINITION_ID));

        verify(itemDropCallback).onDropItem(eq(definition),
                                            eq(shapeFactory),
                                            eq(10.0),
                                            eq(20.0));
    }

    @Test
    public void checkOnDragProxyCompleteWhenCallbackIsNotSet() {
        widget.onDragProxyComplete(DEFINITION_ID,
                                   10.0,
                                   20.0);

        verify(clientFactoryServices,
               never()).getClientFactoryManager();
    }

    @Test
    public void checkGetShapeGlyphForKnownDefinition() {
        widget.bind(palette);

        assertEquals(glyph,
                     widget.getShapeGlyph(DEFINITION_ID));
    }

    @Test
    public void checkGetShapeGlyphForUnknownDefinition() {
        widget.bind(palette);

        assertNull(widget.getShapeGlyph(""));
    }

    @Test
    public void checkBeforeBindClearsView() {
        widget.beforeBind();

        verify(view).clear();
        verify(view).showEmptyView(eq(false));
    }

    @Test
    public void checkBindInitialisesView() {
        final DefinitionPaletteItem item1 = mock(DefinitionPaletteItem.class);
        final DefinitionPaletteItem item2 = mock(DefinitionPaletteItem.class);
        final List<DefinitionPaletteItem> items = new ArrayList<DefinitionPaletteItem>() {{
            add(item1);
            add(item2);
        }};
        final DMNPaletteItemWidget paletteItemWidget = mock(DMNPaletteItemWidget.class);

        when(palette.getItems()).thenReturn(items);
        when(paletteItemWidgets.get()).thenReturn(paletteItemWidget);

        widget.bind(palette);

        verify(paletteItemWidget,
               times(1)).initialize(eq(item1),
                                    eq(shapeFactory),
                                    any(Palette.ItemMouseDownCallback.class));
        verify(paletteItemWidget,
               times(1)).initialize(eq(item2),
                                    eq(shapeFactory),
                                    any(Palette.ItemMouseDownCallback.class));
        verify(view,
               times(2)).add(paletteItemWidget);
    }

    @Test
    public void checkUnbindWhenPaletteIsBound() {
        widget.bind(palette);

        reset(view);

        widget.unbind();

        verify(view).clear();
        verify(view).showEmptyView(eq(true));
    }

    @Test
    public void checkUnbindWhenPaletteIsNotBound() {
        widget.unbind();

        verify(view,
               never()).clear();
        verify(view,
               never()).showEmptyView(anyBoolean());
    }

    @Test
    public void checkDoDestroy() {
        final BS3PaletteViewFactory viewFactory = mock(BS3PaletteViewFactory.class);

        widget.setViewFactory(viewFactory);

        widget.doDestroy();

        verify(paletteItemWidgets).destroyAll();
        verify(viewFactory).destroy();
        verify(view).destroy();
    }
}
