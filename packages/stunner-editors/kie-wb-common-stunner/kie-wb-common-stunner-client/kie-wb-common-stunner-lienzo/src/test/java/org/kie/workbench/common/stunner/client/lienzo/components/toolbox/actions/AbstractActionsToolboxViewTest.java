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


package org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions;

import java.util.Collections;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.toolbox.items.decorator.BoxDecorator;
import com.ait.lienzo.client.core.shape.toolbox.items.decorator.DecoratorsFactory;
import com.ait.lienzo.client.core.shape.toolbox.items.impl.ButtonItemImpl;
import com.ait.lienzo.client.core.shape.toolbox.items.impl.ButtonsFactory;
import com.ait.lienzo.client.core.shape.toolbox.items.impl.ItemsToolboxHighlight;
import com.ait.lienzo.client.core.shape.toolbox.items.impl.ToolboxFactory;
import com.ait.lienzo.client.core.shape.toolbox.items.impl.WiresShapeToolbox;
import com.ait.lienzo.client.core.shape.toolbox.items.tooltip.ToolboxTextTooltip;
import com.ait.lienzo.client.core.shape.toolbox.items.tooltip.TooltipFactory;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.widget.panel.impl.LienzoPanelScrollEvent;
import org.junit.Test;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresLayer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.client.lienzo.util.ToolboxRefreshEvent;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolbox;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractActionsToolboxViewTest {

    protected ToolboxFactory toolboxFactory;
    protected ButtonsFactory buttonsFactory;
    protected TooltipFactory tooltipFactory;
    protected DecoratorsFactory decoratorsFactory;
    protected ActionsToolbox toolbox;
    protected WiresCanvas canvas;
    protected WiresCanvasView canvasView;
    protected WiresLayer wiresLayer;
    protected Layer layer;
    protected Layer topLayer;
    protected WiresShape shape;
    protected LienzoGlyphRenderers glyphRenderers;
    protected WiresShapeToolbox toolboxView;
    protected BoundingBox toolboxViewBoundingBox;
    protected ButtonItemImpl buttonItem;
    protected BoxDecorator boxDecorator;
    protected ToolboxTextTooltip toolboxTooltip;
    protected ItemsToolboxHighlight toolboxHighlight;
    protected Group glyphView;

    @SuppressWarnings("unchecked")
    protected void init() throws Exception {
        toolboxFactory = mock(ToolboxFactory.class);
        buttonsFactory = mock(ButtonsFactory.class);
        tooltipFactory = mock(TooltipFactory.class);
        decoratorsFactory = mock(DecoratorsFactory.class);
        toolbox = mock(ActionsToolbox.class);
        toolboxViewBoundingBox = mock(BoundingBox.class);
        canvas = mock(WiresCanvas.class);
        canvasView = mock(WiresCanvasView.class);
        layer = mock(Layer.class);
        topLayer = mock(Layer.class);
        wiresLayer = mock(WiresLayer.class);
        shape = mock(WiresShape.class);
        glyphRenderers = mock(LienzoGlyphRenderers.class);
        toolboxView = mock(WiresShapeToolbox.class);
        buttonItem = mock(ButtonItemImpl.class);
        boxDecorator = mock(BoxDecorator.class);
        toolboxTooltip = mock(ToolboxTextTooltip.class);
        toolboxHighlight = mock(ItemsToolboxHighlight.class);
        glyphView = mock(Group.class);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getLayer()).thenReturn(wiresLayer);
        when(wiresLayer.getTopLayer()).thenReturn(topLayer);
        when(wiresLayer.getLienzoLayer()).thenReturn(layer);
        when(toolboxFactory.buttons()).thenReturn(buttonsFactory);
        when(toolboxFactory.decorators()).thenReturn(decoratorsFactory);
        when(toolboxFactory.tooltips()).thenReturn(tooltipFactory);
        when(toolboxFactory.forWiresShape(any()))
                .thenReturn(toolboxView);
        when(buttonsFactory.button(Mockito.<Group>any())).thenReturn(buttonItem);
        when(decoratorsFactory.box()).thenReturn(boxDecorator);
        when(tooltipFactory.forToolbox(any())).thenReturn(toolboxTooltip);
        when(boxDecorator.configure(any())).thenReturn(boxDecorator);
        when(boxDecorator.copy()).thenReturn(boxDecorator);
        when(boxDecorator.setPadding(5)).thenReturn(boxDecorator);
        when(toolboxViewBoundingBox.getWidth()).thenReturn(300d);
        when(toolboxViewBoundingBox.getHeight()).thenReturn(600d);
        when(toolboxView.getBoundingBox()).thenReturn(toolboxViewBoundingBox);
        when(toolboxView.attachTo(any())).thenReturn(toolboxView);
        when(toolboxView.decorate(any())).thenReturn(toolboxView);
        when(toolboxView.offset(any())).thenReturn(toolboxView);
        when(toolboxView.at(any())).thenReturn(toolboxView);
        when(toolboxView.grid(any())).thenReturn(toolboxView);
        when(toolboxView.useShowExecutor(any())).thenReturn(toolboxView);
        when(toolboxView.useHideExecutor(any())).thenReturn(toolboxView);
        when(buttonItem.tooltip(any())).thenReturn(buttonItem);
        when(buttonItem.decorate(any())).thenReturn(buttonItem);
        when(buttonItem.onMouseEnter(any())).thenReturn(buttonItem);
        when(buttonItem.onMouseExit(any())).thenReturn(buttonItem);
        when(buttonItem.onClick(any())).thenReturn(buttonItem);
        when(toolboxTooltip.at(any())).thenReturn(toolboxTooltip);
        when(toolboxTooltip.towards(any())).thenReturn(toolboxTooltip);
        when(toolboxTooltip.setText(any())).thenReturn(toolboxTooltip);
        when(toolboxTooltip.forComputedBoundingBox(any())).thenReturn(toolboxTooltip);
        when(toolboxTooltip.withText(any())).thenReturn(toolboxTooltip);
        when(toolbox.size()).thenReturn(0);
        when(toolbox.iterator()).thenReturn(Collections.emptyIterator());
        when(glyphRenderers.render(any(),
                                   anyDouble(),
                                   anyDouble()))
                .thenReturn(glyphView);
    }

    @SuppressWarnings("unchecked")
    protected void testAddButton(final String title) {
        // Verify tootlip.
        verify(toolboxTooltip,
               times(1)).createItem(eq(title));
        verify(buttonItem,
               times(1)).tooltip(any());
        // Verify mouse enter.
        final ArgumentCaptor<NodeMouseEnterHandler> enterHandlerArgumentCaptor =
                ArgumentCaptor.forClass(NodeMouseEnterHandler.class);
        verify(buttonItem,
               times(1)).onMouseEnter(enterHandlerArgumentCaptor.capture());
        final NodeMouseEnterHandler enterHandler = enterHandlerArgumentCaptor.getValue();
        final NodeMouseEnterEvent mouseEnterEvent = mock(NodeMouseEnterEvent.class);
        enterHandler.onNodeMouseEnter(mouseEnterEvent);
        verify(canvasView,
               times(1)).setCursor(eq(AbstractCanvas.Cursors.POINTER));
        // Verify mouse exit.
        final ArgumentCaptor<NodeMouseExitHandler> exitHandlerArgumentCaptor =
                ArgumentCaptor.forClass(NodeMouseExitHandler.class);
        verify(buttonItem,
               times(1)).onMouseExit(exitHandlerArgumentCaptor.capture());
        final NodeMouseExitHandler exitHandler = exitHandlerArgumentCaptor.getValue();
        final NodeMouseExitEvent mouseExitEvent = mock(NodeMouseExitEvent.class);
        exitHandler.onNodeMouseExit(mouseExitEvent);
        verify(canvasView,
               times(1)).setCursor(eq(AbstractCanvas.Cursors.DEFAULT));
    }


    @Mock
    AbstractActionsToolboxView toolboxView2;

    @Test
    public void testDestroyTopLayerRepaint() {
        doCallRealMethod().when(toolboxView2).destroy();

        toolboxView2.destroy();
        verify(toolboxView2,
               times(1)).drawTopLayer();
    }

    @Test
    public void testOnScroll() {
        doCallRealMethod().when(toolboxView2).onScrollEvent(any());
        toolboxView2.onScrollEvent(new LienzoPanelScrollEvent());
        verify(toolboxView2,
               times(1)).drawTopLayer();
    }

    @Test
    public void testOnToolboxRefreshEvent() {
        doCallRealMethod().when(toolboxView2).onToolboxRefreshEvent(any());
        toolboxView2.onToolboxRefreshEvent(new ToolboxRefreshEvent());
        verify(toolboxView2,
                times(1)).drawTopLayer();
    }

}
