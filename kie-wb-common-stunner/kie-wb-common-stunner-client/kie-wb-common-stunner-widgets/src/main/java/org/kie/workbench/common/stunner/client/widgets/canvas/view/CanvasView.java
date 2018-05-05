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
package org.kie.workbench.common.stunner.client.widgets.canvas.view;

import java.util.Objects;

import javax.annotation.PostConstruct;

import com.ait.lienzo.client.core.shape.GridLayer;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoGridLayerBuilder;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasGrid;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public class CanvasView extends Composite implements AbstractCanvas.View<com.ait.lienzo.client.widget.LienzoPanel> {

    public static final String CURSOR = "cursor";
    public static final String CURSOR_NOT_ALLOWED = "not-allowed";
    private static final String Bg_COLOR = "#FFFFFF";

    private final FlowPanel mainPanel = new FlowPanel();
    private final FlowPanel toolsPanel = new FlowPanel();
    private final Layer canvasLayer = new Layer();
    private com.ait.lienzo.client.widget.LienzoPanel panel;
    private org.kie.workbench.common.stunner.core.client.canvas.Layer layer;

    /**
     * Defines the usable area in the layer by stroking a rectangle for the
     * defined canvas size. It provides visual feedback about the diagram's
     * boundaries to the user once using mediators (zooming, panning, etc).
     * It's added into the scene's top layer, so same transforms as the
     * layer used by stunner are applied as well on this shape.
     */
    protected final Rectangle decorator = new Rectangle(1,
                                                        1)
            .setFillAlpha(0) // By default no decorator's fill.
            .setStrokeAlpha(0) // By default no decorator's stroke.
            .setStrokeWidth(1)
            .setStrokeColor(ColorName.LIGHTGREY);

    @PostConstruct
    public void init() {
        initWidget(mainPanel);
    }

    @Override
    public AbstractCanvas.View show(final com.ait.lienzo.client.widget.LienzoPanel panel,
                                    final int width,
                                    final int height,
                                    final org.kie.workbench.common.stunner.core.client.canvas.Layer layer) {
        this.panel = panel;
        this.layer = layer;
        layer.initialize(canvasLayer);
        panel.getElement().getStyle().setBackgroundColor(Bg_COLOR);
        mainPanel.add(toolsPanel);
        mainPanel.add(panel);
        panel.getScene().add(canvasLayer);
        decorator.setWidth(width);
        decorator.setHeight(height);
        if (null == decorator.getLayer() && null != canvasLayer.getScene()) {
            canvasLayer.getScene().getTopLayer().add(decorator);
        }
        return this;
    }

    @Override
    public AbstractCanvas.View add(final IsWidget widget) {
        toolsPanel.add(widget);
        return this;
    }

    @Override
    public AbstractCanvas.View remove(final IsWidget widget) {
        toolsPanel.remove(widget);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractCanvas.View addShape(final ShapeView<?> shapeView) {
        layer.addShape(shapeView);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractCanvas.View removeShape(final ShapeView<?> shapeView) {
        layer.removeShape(shapeView);
        return this;
    }

    @Override
    public AbstractCanvas.View addChildShape(final ShapeView<?> parent,
                                             final ShapeView<?> child) {
        final WiresContainer parentShape = (WiresContainer) parent;
        final WiresShape childShape = (WiresShape) child;
        return this.addChildShape(parentShape,
                                  childShape);
    }

    AbstractCanvas.View addChildShape(final WiresContainer parentShape,
                                      final WiresShape childShape) {
        parentShape.add(childShape);
        return this;
    }

    @Override
    public AbstractCanvas.View removeChildShape(final ShapeView<?> parent,
                                                final ShapeView<?> child) {
        final WiresContainer parentShape = (WiresContainer) parent;
        final WiresShape childShape = (WiresShape) child;
        return this.removeChildShape(parentShape,
                                     childShape);
    }

    AbstractCanvas.View removeChildShape(final WiresContainer parentShape,
                                         final WiresShape childShape) {
        parentShape.remove(childShape);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractCanvas.View dock(final ShapeView<?> parent,
                                    final ShapeView<?> child) {
        final WiresShape parentShape = (WiresShape) parent;
        final WiresShape childShape = (WiresShape) child;
        dockShape(parentShape,
                  childShape);
        return this;
    }

    AbstractCanvas.View dockShape(final WiresContainer parentShape,
                                  final WiresShape childShape) {
        WiresDockingControl dockingControl = childShape.getControl().getDockingControl();
        Point2D dockLocation = (Objects.isNull(dockingControl.getCandidateLocation()) ?
                childShape.getLocation() :
                dockingControl.getCandidateLocation());
        dockingControl.dock(childShape, parentShape, dockLocation);
        return this;
    }

    @Override
    public AbstractCanvas.View undock(final ShapeView<?> target,
                                      final ShapeView<?> child) {
        final WiresShape targetShape = (WiresShape) target;
        final WiresShape childShape = (WiresShape) child;
        return undock(targetShape,
                      childShape);
    }

    AbstractCanvas.View undock(final WiresShape targetShape,
                               final WiresShape childShape) {

        childShape.getControl()
                .getDockingControl()
                .undock(childShape, targetShape);
        return this;
    }

    @Override
    public double getAbsoluteX() {
        return panel.getAbsoluteLeft();
    }

    @Override
    public double getAbsoluteY() {
        return panel.getAbsoluteTop();
    }

    @Override
    public int getWidth() {
        return panel.getWidth();
    }

    @Override
    public int getHeight() {
        return panel.getHeight();
    }

    @Override
    public AbstractCanvas.View setGrid(final CanvasGrid grid) {
        if (null != grid) {
            GridLayer gridLayer = LienzoGridLayerBuilder.getLienzoGridFor(grid);
            panel.setBackgroundLayer(gridLayer);
        } else {
            panel.setBackgroundLayer(null);
        }
        return this;
    }

    @Override
    public AbstractCanvas.View setCursor(final AbstractCanvas.Cursors cursor) {
        Style style = panel.getElement().getStyle();
        switch (cursor) {
            case AUTO:
                style.setCursor(Style.Cursor.AUTO);
                break;
            case MOVE:
                style.setCursor(Style.Cursor.MOVE);
                break;
            case TEXT:
                style.setCursor(Style.Cursor.TEXT);
                break;
            case POINTER:
                style.setCursor(Style.Cursor.POINTER);
                break;
            case NOT_ALLOWED:
                style.setProperty(CURSOR, CURSOR_NOT_ALLOWED);
                break;
            case WAIT:
                style.setCursor(Style.Cursor.WAIT);
                break;
            case CROSSHAIR:
                style.setCursor(Style.Cursor.CROSSHAIR);
                break;
        }
        return this;
    }

    @Override
    public AbstractCanvas.View setDecoratorStrokeWidth(final double width) {
        decorator.setStrokeWidth(width);
        return this;
    }

    @Override
    public AbstractCanvas.View setDecoratorStrokeAlpha(final double alpha) {
        decorator.setStrokeAlpha(alpha);
        return this;
    }

    @Override
    public AbstractCanvas.View setDecoratorStrokeColor(final String color) {
        decorator.setStrokeColor(color);
        return this;
    }

    @Override
    public org.kie.workbench.common.stunner.core.client.canvas.Layer getLayer() {
        return layer;
    }

    protected Layer getCanvasLayer() {
        return canvasLayer;
    }

    @Override
    public AbstractCanvas.View clear() {
        layer.clear();
        return this;
    }

    @Override
    public void destroy() {
        decorator.removeFromParent();
        this.mainPanel.clear();
        this.toolsPanel.clear();
        this.layer.destroy();
        this.canvasLayer.removeFromParent();
        this.layer = null;
        this.panel.removeAll();
        this.panel.removeFromParent();
        this.panel = null;
    }
}

