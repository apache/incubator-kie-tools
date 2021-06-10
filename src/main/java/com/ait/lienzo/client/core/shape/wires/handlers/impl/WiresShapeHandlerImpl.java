/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.MouseEvent;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragContext;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeControlUtils.excludeFromIndex;

/**
 * This handler's goals are:
 * - Delegate some mouse interactions for a single wires shape to its wires shape control instance
 * - Displays some highlights to provide feedback for containment and docking operations.
 */
public class WiresShapeHandlerImpl extends WiresManager.WiresDragHandler implements WiresShapeHandler {

    private final Consumer<NodeMouseClickEvent> clickEventConsumer;
    private final WiresShapeHighlightControl highlightControl;

    public WiresShapeHandlerImpl(final Supplier<WiresLayerIndex> indexBuilder,
                                 final WiresShape shape,
                             final WiresShapeHighlight<PickerPart.ShapePart> highlight,
                             final WiresManager manager) {
        super(manager);
        this.clickEventConsumer = new Consumer<NodeMouseClickEvent>() {
            @Override
            public void accept(NodeMouseClickEvent event) {
                if (getWiresManager().getSelectionManager() != null) {
                    getWiresManager().getSelectionManager().selected(getShape(),
                                                                     event.isShiftKeyDown());
                }
            }
        };
        this.highlightControl = new WiresShapeHighlightControl(getWiresManager(),
                                                               indexBuilder,
                                                               highlight,
                                                               new Supplier<WiresShapeControl>() {
                                                                   @Override
                                                                   public WiresShapeControl get() {
                                                                       return shape.getControl();
                                                                   }
                                                               });
    }

    public WiresShapeHandlerImpl(final Supplier<WiresLayerIndex> indexBuilder,
                                 final WiresShape shape,
                                 final WiresShapeHighlight<PickerPart.ShapePart> highlight,
                                 final WiresManager manager,
                                 final Consumer<NodeMouseClickEvent> clickEventConsumer) {
        super(manager);
        this.clickEventConsumer = clickEventConsumer;
        this.highlightControl = new WiresShapeHighlightControl(getWiresManager(),
                                                               indexBuilder,
                                                               highlight,
                                                               new Supplier<WiresShapeControl>() {
                                                                   @Override
                                                                   public WiresShapeControl get() {
                                                                       return shape.getControl();
                                                                   }
                                                               });
    }

    @Override
    public WiresShapeControl getControl() {
        return highlightControl.getDelegate();
    }

    @Override
    public void startDrag(DragContext dragContext) {
        super.startDrag(dragContext);

        final Point2D startAdjusted = dragContext.getStartAdjusted();
        getHighlightControl().onMoveStart(startAdjusted.getX(), startAdjusted.getY());

    }

    @Override
    protected boolean doAdjust(Point2D dxy) {

        if (getHighlightControl().onMove(dxy.getX(),
                           dxy.getY())) {
            dxy.set(getHighlightControl().getAdjust());
            return true;
        }

        return false;
    }

    @Override
    protected void doOnNodeDragEnd(NodeDragEndEvent event) {

        final Point2D distanceAdjusted = event.getDragContext().getDistanceAdjusted();
        final double adjustedX = distanceAdjusted.getX();
        final double adjustedY = distanceAdjusted.getY();
        final int dx = (int) adjustedX;
        final int dy = (int) adjustedY;

        getHighlightControl().onMove(dx, dy);
        getHighlightControl().onMoveComplete();

    }

    @Override
    public void onNodeMouseClick(NodeMouseClickEvent event) {
        getHighlightControl().onMouseClick(new MouseEvent(event.getX(),
                                                 event.getY(),
                                                 event.isShiftKeyDown(),
                                                 event.isAltKeyDown(),
                                                 event.isCtrlKeyDown()));
        clickEventConsumer.accept(event);
    }

    @Override
    public void onNodeMouseDown(NodeMouseDownEvent event) {
        getHighlightControl().onMouseDown(new MouseEvent(event.getX(),
                                           event.getY(),
                                           event.isShiftKeyDown(),
                                           event.isAltKeyDown(),
                                           event.isCtrlKeyDown()));
    }

    @Override
    public void onNodeMouseUp(NodeMouseUpEvent event) {
        getHighlightControl().onMouseUp(new MouseEvent(event.getX(),
                                         event.getY(),
                                         event.isShiftKeyDown(),
                                         event.isAltKeyDown(),
                                         event.isCtrlKeyDown()));
    }

    private WiresShape getShape() {
        return getControl().getParentPickerControl().getShape();
    }

    private WiresShapeHighlightControl getHighlightControl() {
        return highlightControl;
    }

}
