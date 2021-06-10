/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.shape.toolbox.items.impl;

import java.util.Iterator;
import java.util.function.BiConsumer;

import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.toolbox.grid.Point2DGrid;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratedItem;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratorItem;
import com.ait.lienzo.client.core.shape.toolbox.items.LayerToolbox;
import com.ait.lienzo.client.core.shape.toolbox.items.TooltipItem;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.event.AbstractWiresDragEvent;
import com.ait.lienzo.client.core.shape.wires.event.AbstractWiresResizeEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresMoveEvent;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.tools.client.event.HandlerRegistration;

/**
 * A LayerToolbox implementation for WiresShape's.
 * It basically wraps a toolbox instance, adds it into the specified
 * Layer, and observes for some wires shape's events in order to
 * use the update the toolbox with the right locations and size,
 * given by the shape's bounding box.
 */
public class WiresShapeToolbox
        implements LayerToolbox {

    private final ToolboxImpl toolbox;
    private final Point2D toolboxOffset;
    private final Point2D gridOffset;
    private HandlerRegistration wiresMoveHandlerReg;
    private HandlerRegistration wiresDragStartHandlerReg;
    private HandlerRegistration wiresDragMoveHandlerReg;
    private HandlerRegistration wiresDragEndHandlerReg;
    private HandlerRegistration wiresResizeStartHandlerReg;
    private HandlerRegistration wiresResizeStepHandlerReg;
    private HandlerRegistration wiresResizeEndHandlerReg;

    public WiresShapeToolbox(final WiresShape shape) {
        this(shape,
             new ToolboxImpl(() -> shape.getPath().getBoundingBox()));
    }

    WiresShapeToolbox(final WiresShape shape,
                      final ToolboxImpl toolbox) {
        this.gridOffset = new Point2D(0,
                                      0);
        this.toolboxOffset = new Point2D(0,
                                         0);
        this.toolbox = toolbox;
        initHandlers(shape);
        shapeOffset(shape);
        hide();
    }

    @Override
    public WiresShapeToolbox attachTo(final Layer layer) {
        layer.add(toolbox.asPrimitive());
        return this;
    }

    @Override
    public WiresShapeToolbox at(final Direction at) {
        toolbox.at(at);
        return this;
    }

    @Override
    public WiresShapeToolbox offset(final Point2D value) {
        this.toolboxOffset.setX(value.getX());
        this.toolboxOffset.setY(value.getY());
        return reposition();
    }

    @Override
    public WiresShapeToolbox grid(final Point2DGrid grid) {
        toolbox.grid(grid);
        // Add some padding to fit the grid with a shape..
        final Point2D go = getGridOffset(grid,
                                         toolbox.getAt());
        gridOffset.setX(go.getX());
        gridOffset.setY(go.getY());
        return reposition();
    }

    @Override
    public WiresShapeToolbox add(final DecoratedItem... items) {
        toolbox.add(items);
        return this;
    }

    @Override
    public Iterator<DecoratedItem> iterator() {
        return toolbox.iterator();
    }

    @Override
    public WiresShapeToolbox show() {
        toolbox.show();
        return this;
    }

    @Override
    public WiresShapeToolbox hide() {
        toolbox.hide();
        return this;
    }

    public WiresShapeToolbox useShowExecutor(final BiConsumer<Group, Runnable> executor) {
        toolbox.useShowExecutor(executor);
        return this;
    }

    public WiresShapeToolbox useHideExecutor(final BiConsumer<Group, Runnable> executor) {
        toolbox.useHideExecutor(executor);
        return this;
    }

    @Override
    public boolean isVisible() {
        return toolbox.isVisible();
    }

    @Override
    public WiresShapeToolbox decorate(final DecoratorItem<?> decorator) {
        toolbox.decorate(decorator);
        return this;
    }

    @Override
    public LayerToolbox tooltip(final TooltipItem tooltip) {
        toolbox.tooltip(tooltip);
        return this;
    }

    @Override
    public WiresShapeToolbox onMouseEnter(final NodeMouseEnterHandler handler) {
        toolbox.onMouseEnter(handler);
        return this;
    }

    @Override
    public WiresShapeToolbox onMouseExit(final NodeMouseExitHandler handler) {
        toolbox.onMouseExit(handler);
        return this;
    }

    @Override
    public Layer getLayer() {
        return toolbox.asPrimitive().getLayer();
    }

    public BoundingBox getBoundingBox() {
        return toolbox.getBoundingBox().get();
    }

    @Override
    public void destroy() {
        doDestroy();
    }

    public void hideAndDestroy() {
        toolbox.hide(() -> {},
                     WiresShapeToolbox.this::doDestroy);
    }

    private void initHandlers(final WiresShape shape) {
        wiresMoveHandlerReg = shape.addWiresMoveHandler(this::onMove);
        wiresDragStartHandlerReg = shape.addWiresDragStartHandler(this::onMove);
        wiresDragMoveHandlerReg = shape.addWiresDragMoveHandler(this::onMove);
        wiresDragEndHandlerReg = shape.addWiresDragEndHandler(this::onMove);
        wiresResizeStartHandlerReg = shape.addWiresResizeStartHandler(this::onResize);
        wiresResizeStepHandlerReg = shape.addWiresResizeStepHandler(this::onResize);
        wiresResizeEndHandlerReg = shape.addWiresResizeEndHandler(this::onResize);
    }

    WiresShapeToolbox reposition() {
        toolbox.offset(toolboxOffset.offset(gridOffset.getX(), gridOffset.getY()));
        return this;
    }

    void resize(final double width,
                final double height) {
        toolbox
                .setGridSize(width,
                             height)
                .refresh();
    }

    private void doDestroy() {
        toolbox.destroy();
        wiresMoveHandlerReg.removeHandler();
        wiresDragStartHandlerReg.removeHandler();
        wiresDragMoveHandlerReg.removeHandler();
        wiresDragEndHandlerReg.removeHandler();
        wiresResizeStartHandlerReg.removeHandler();
        wiresResizeStepHandlerReg.removeHandler();
        wiresResizeEndHandlerReg.removeHandler();
        wiresMoveHandlerReg = null;
        wiresDragStartHandlerReg = null;
        wiresDragMoveHandlerReg = null;
        wiresDragEndHandlerReg = null;
        wiresResizeStartHandlerReg = null;
        wiresResizeStepHandlerReg = null;
        wiresResizeEndHandlerReg = null;
    }

    private void onResize(final AbstractWiresResizeEvent event) {
        shapeOffset((WiresContainer) event.getSource());
        resize(event.getWidth(),
               event.getHeight());
    }

    private void onMove(final WiresMoveEvent event) {
        shapeOffset(event.getSource());
    }

    private void onMove(final AbstractWiresDragEvent event) {
        shapeOffset((WiresContainer) event.getSource());
    }

    private void shapeOffset(final WiresContainer shape) {
        offset(shape.getGroup().getComputedLocation());
    }

    private static Point2D getGridOffset(final Point2DGrid grid,
                                         final Direction at) {
        final double margin = null != grid ? grid.getMargin() : 0;
        final Point2D pad = new Point2D(0,
                                        0);
        switch (at) {
            case NORTH:
                pad.setX(-margin);
                pad.setY(-margin);
            case SOUTH:
                pad.setX(-margin);
                pad.setY(margin);
            case EAST:
                pad.setX(margin);
                break;
            case NORTH_EAST:
                pad.setY(-margin);
                pad.setX(margin);
                break;
            case SOUTH_EAST:
                pad.setY(-margin);
                pad.setX(margin);
                break;
            case SOUTH_WEST:
                pad.setX(-margin);
                pad.setY(margin);
                break;
            case NORTH_WEST:
                pad.setY(-margin);
                break;
        }
        return pad;
    }
}
