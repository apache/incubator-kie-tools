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

package org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.google.gwt.event.dom.client.MouseEvent;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresLayer;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolbox;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.lienzo.toolbox.grid.Point2DGrid;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.DecoratorItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.LayerToolbox;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.TooltipItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.decorator.BoxDecorator;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.decorator.DecoratorsFactory;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.impl.ButtonItemImpl;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.impl.ButtonsFactory;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.impl.ToolboxFactory;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.impl.WiresShapeToolbox;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.tooltip.ToolboxTextTooltip;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.tooltip.TooltipFactory;
import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
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
        glyphView = mock(Group.class);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getLayer()).thenReturn(wiresLayer);
        when(wiresLayer.getTopLayer()).thenReturn(topLayer);
        when(wiresLayer.getLienzoLayer()).thenReturn(layer);
        when(toolboxFactory.buttons()).thenReturn(buttonsFactory);
        when(toolboxFactory.decorators()).thenReturn(decoratorsFactory);
        when(toolboxFactory.tooltips()).thenReturn(tooltipFactory);
        when(toolboxFactory.forWiresShape(any(WiresShape.class)))
                .thenReturn(toolboxView);
        when(buttonsFactory.button(any(Group.class))).thenReturn(buttonItem);
        when(decoratorsFactory.box()).thenReturn(boxDecorator);
        when(tooltipFactory.forToolbox(any(LayerToolbox.class))).thenReturn(toolboxTooltip);
        when(boxDecorator.configure(any(Consumer.class))).thenReturn(boxDecorator);
        when(boxDecorator.copy()).thenReturn(boxDecorator);
        when(toolboxViewBoundingBox.getWidth()).thenReturn(300d);
        when(toolboxViewBoundingBox.getHeight()).thenReturn(600d);
        when(toolboxView.getBoundingBox()).thenReturn(toolboxViewBoundingBox);
        when(toolboxView.attachTo(any(Layer.class))).thenReturn(toolboxView);
        when(toolboxView.decorate(any(DecoratorItem.class))).thenReturn(toolboxView);
        when(toolboxView.offset(any(Point2D.class))).thenReturn(toolboxView);
        when(toolboxView.at(any(Direction.class))).thenReturn(toolboxView);
        when(toolboxView.grid(any(Point2DGrid.class))).thenReturn(toolboxView);
        when(toolboxView.useShowExecutor(any(BiConsumer.class))).thenReturn(toolboxView);
        when(toolboxView.useHideExecutor(any(BiConsumer.class))).thenReturn(toolboxView);
        when(buttonItem.tooltip(any(TooltipItem.class))).thenReturn(buttonItem);
        when(buttonItem.decorate(any(DecoratorItem.class))).thenReturn(buttonItem);
        when(buttonItem.onMouseEnter(any(NodeMouseEnterHandler.class))).thenReturn(buttonItem);
        when(buttonItem.onMouseExit(any(NodeMouseExitHandler.class))).thenReturn(buttonItem);
        when(buttonItem.onClick(any(NodeMouseClickHandler.class))).thenReturn(buttonItem);
        when(toolboxTooltip.at(any(Direction.class))).thenReturn(toolboxTooltip);
        when(toolboxTooltip.towards(any(Direction.class))).thenReturn(toolboxTooltip);
        when(toolboxTooltip.setText(anyString())).thenReturn(toolboxTooltip);
        when(toolboxTooltip.forComputedBoundingBox(any(Supplier.class))).thenReturn(toolboxTooltip);
        when(toolboxTooltip.withText(any(Consumer.class))).thenReturn(toolboxTooltip);
        when(toolbox.size()).thenReturn(2);
        when(glyphRenderers.render(any(Glyph.class),
                                   anyDouble(),
                                   anyDouble()))
                .thenReturn(glyphView);
    }

    @SuppressWarnings("unchecked")
    protected void testAddButton(final Consumer<MouseClickEvent> clickEventConsumer) {
        // Verify tootlip.
        verify(toolboxTooltip,
               times(1)).createItem(eq("title1"));
        verify(buttonItem,
               times(1)).tooltip(any(TooltipItem.class));
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
               times(1)).setCursor(eq(AbstractCanvas.Cursors.AUTO));
        // Verify mouse click.
        final ArgumentCaptor<NodeMouseClickHandler> clickHandlerArgumentCaptor =
                ArgumentCaptor.forClass(NodeMouseClickHandler.class);
        verify(buttonItem,
               times(1)).onClick(clickHandlerArgumentCaptor.capture());
        final NodeMouseClickHandler clickHandler = clickHandlerArgumentCaptor.getValue();
        final NodeMouseClickEvent mouseClickEvent = mock(NodeMouseClickEvent.class);
        when(mouseClickEvent.getMouseEvent()).thenReturn(mock(MouseEvent.class));
        clickHandler.onNodeMouseClick(mouseClickEvent);
        verify(clickEventConsumer,
               times(1)).accept(any(MouseClickEvent.class));
    }
}
