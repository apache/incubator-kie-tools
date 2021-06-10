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

import java.util.Collection;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import java.util.function.Supplier;

/**
 * This handler's goals are:
 * - Delegate some mouse interactions for multiple wires shapes or connectors to their respective control instances
 * - Displays some highlights to provide feedback for containment operations.
 */
public class WiresCompositeShapeHandler
        extends WiresManager.WiresDragHandler
        implements DragConstraintEnforcer,
                   NodeDragEndHandler {

    private final Supplier<WiresLayerIndex>                 indexBuilder;
    private final WiresCompositeControl                     shapeControl;
    private final WiresShapeHighlight<PickerPart.ShapePart> highlight;

    public WiresCompositeShapeHandler(final Supplier<WiresLayerIndex> indexBuilder,
                                      final WiresCompositeControl shapeControl,
                                      final WiresShapeHighlight<PickerPart.ShapePart> highlight,
                                      final WiresManager manager) {
        super(manager);
        this.indexBuilder = indexBuilder;
        this.shapeControl = shapeControl;
        this.highlight = highlight;
    }

    @Override
    public void startDrag(final DragContext dragContext) {
        super.startDrag(dragContext);

        final WiresLayerIndex index = buildIndex();
        shapeControl.useIndex(() -> index);

        shapeControl.onMoveStart(dragContext.getDragStartX(),
                                 dragContext.getDragStartY());
    }

    @Override
    protected boolean doAdjust(final Point2D dxy) {

        final boolean adjusted = shapeControl.onMove(dxy.getX(),
                                                     dxy.getY());

        if (adjusted) {
            dxy.set(shapeControl.getAdjust());
            return true;
        }

        boolean shouldRestore = true;

        final WiresContainer parent = shapeControl.getSharedParent();

        if (null != parent && parent instanceof WiresShape) {
            if (shapeControl.isAllowed()) {
                highlight.highlight((WiresShape) parent,
                                    PickerPart.ShapePart.BODY);
                shouldRestore = false;
            } else {
                highlight.error((WiresShape) parent,
                                PickerPart.ShapePart.BODY);
                shouldRestore = false;
            }
        }
        if (shouldRestore) {
            highlight.restore();
        }

        return false;
    }

    @Override
    protected void doOnNodeDragEnd(NodeDragEndEvent event) {

        final Point2D distanceAdjusted = event.getDragContext().getDistanceAdjusted();
        final Double adjustedX = distanceAdjusted.getX();
        final Double adjustedY = distanceAdjusted.getY();
        final int dx = adjustedX.intValue();
        final int dy = adjustedY.intValue();

        shapeControl.onMove(dx, dy);
        shapeControl.onMoveComplete();

        if (shapeControl.accept()) {
            shapeControl.execute();
        } else {
            reset();
        }

        // Highlights.
        highlight.restore();

        // Clear the index once operations are complete.
        clearIndex();
    }

    @Override
    protected void doReset() {
        super.doReset();
        highlight.restore();
        clearIndex();
    }

    @Override
    public WiresControl getControl() {
        return shapeControl;
    }


    private WiresLayerIndex buildIndex() {
        final Collection<WiresShape> shapes = shapeControl.getContext().getShapes();
        final WiresLayerIndex index = indexBuilder.get();
        if (!shapes.isEmpty()) {
            for (WiresShape shape : shapes) {
                WiresShapeControlUtils.excludeFromIndex(index, shape);
            }
        }
        index.build(getWiresManager().getLayer());
        return index;

    }

    private void clearIndex() {
        WiresLayerIndex index = getIndex();
        if (null != index) {
            index.clear();
        }
    }

    WiresLayerIndex getIndex() {
        final Collection<WiresShape> shapes = shapeControl.getContext().getShapes();
        if (!shapes.isEmpty()) {
            return shapes.iterator().next()
                    .getControl()
                    .getParentPickerControl()
                    .getIndex();
        }
        return null;
    }
}
