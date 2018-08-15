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

package org.kie.workbench.common.stunner.client.lienzo.canvas;

import javax.enterprise.context.Dependent;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.DataURLType;
import org.kie.workbench.common.stunner.client.lienzo.Lienzo;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractLayer;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.uberfire.mvp.Command;

/**
 * An Stunner's Layer type implementation that wraps a Lienzo layer and provides
 * support for primitives, shapes and wires.
 */
@Dependent
@Lienzo
public class LienzoLayer extends AbstractLayer<LienzoLayer, ShapeView<?>, Shape<?>> {

    private static final ViewEventType[] SUPPORTED_EVENT_TYPES = new ViewEventType[]{
            ViewEventType.MOUSE_CLICK, ViewEventType.MOUSE_DBL_CLICK, ViewEventType.MOUSE_MOVE
    };

    protected ViewEventHandlerManager eventHandlerManager;
    protected com.ait.lienzo.client.core.shape.Layer layer;

    public LienzoLayer() {
    }

    @Override
    public LienzoLayer initialize(final Object view) {
        this.layer = (com.ait.lienzo.client.core.shape.Layer) view;
        this.eventHandlerManager = new ViewEventHandlerManager(layer,
                                                               SUPPORTED_EVENT_TYPES);
        return this;
    }

    @Override
    public LienzoLayer addShape(final ShapeView<?> shape) {
        if (WiresUtils.isWiresContainer(shape)) {
            layer.add(((WiresContainer) shape).getGroup());
        } else if (WiresUtils.isWiresConnector(shape)) {
            layer.add(((WiresConnector) shape).getLine());
        } else {
            layer.add((IPrimitive<?>) shape);
        }
        return this;
    }

    @Override
    public LienzoLayer removeShape(final ShapeView<?> shape) {
        if (WiresUtils.isWiresContainer(shape)) {
            layer.remove(((WiresContainer) shape).getGroup());
        } else if (WiresUtils.isWiresConnector(shape)) {
            layer.remove(((WiresConnector) shape).getLine());
        } else {
            layer.remove((IPrimitive<?>) shape);
        }
        return this;
    }

    @Override
    public void clear() {
        layer.clear();
    }

    @Override
    public String toDataURL() {
        return layer.toDataURL(DataURLType.PNG);
    }

    @Override
    public String toDataURL(final URLDataType type) {
        switch (type) {
            case JPG:
                return layer.toDataURL(DataURLType.JPG);
        }
        return layer.toDataURL(DataURLType.PNG);
    }

    @Override
    public void onAfterDraw(final Command callback) {
        layer.setOnLayerAfterDraw(layer1 -> callback.execute());
    }

    @Override
    public void destroy() {
        // Clear registered event handers.
        if (null != eventHandlerManager) {
            eventHandlerManager.destroy();
            eventHandlerManager = null;
        }
        // Remove the layer stuff.
        if (null != layer) {
            layer.removeAll();
            layer.removeFromParent();
            layer = null;
        }
    }

    @Override
    public boolean supports(final ViewEventType type) {
        return eventHandlerManager.supports(type);
    }

    @Override
    public LienzoLayer addHandler(final ViewEventType type,
                                  final ViewHandler<? extends ViewEvent> eventHandler) {
        eventHandlerManager.addHandler(type,
                                       eventHandler);
        return this;
    }

    @Override
    public LienzoLayer removeHandler(final ViewHandler<? extends ViewEvent> eventHandler) {
        eventHandlerManager.removeHandler(eventHandler);
        return this;
    }

    @Override
    public LienzoLayer enableHandlers() {
        eventHandlerManager.enable();
        return this;
    }

    @Override
    public LienzoLayer disableHandlers() {
        eventHandlerManager.disable();
        return this;
    }

    @Override
    public Shape<?> getAttachableShape() {
        return null;
    }

    public com.ait.lienzo.client.core.shape.Layer getLienzoLayer() {
        return this.layer;
    }

    @Override
    protected Point2D getTranslate() {
        return new Point2D(
                layer.getAbsoluteTransform().getTranslateX(),
                layer.getAbsoluteTransform().getTranslateY()
        );
    }

    @Override
    protected Point2D getScale() {
        return new Point2D(
                layer.getAbsoluteTransform().getScaleX(),
                layer.getAbsoluteTransform().getScaleY()
        );
    }

    public void translate(final double tx,
                          final double ty) {
        setTransform(t -> translate(t,
                                    tx,
                                    ty));
    }

    public void scale(final double sx,
                      final double sy) {
        setTransform(t -> scale(t,
                                sx,
                                sy));
    }

    public void scale(final double delta) {
        setTransform(t -> scale(t,
                                delta));
    }

    private interface TransformCallback {

        void apply(Transform transform);
    }

    private void setTransform(final TransformCallback callback) {

        Transform transform = getViewPort().getTransform();

        if (transform == null) {
            getViewPort().setTransform(transform = new Transform());
        }

        callback.apply(transform);

        getViewPort().setTransform(transform);
    }

    private void scale(final Transform transform,
                       final double sx,
                       final double sy) {
        transform.scale(sx,
                        sy);
        this.getViewPort().batch();
    }

    private void scale(final Transform transform,
                       final double delta) {
        transform.scale(delta);
    }

    private void translate(final Transform transform,
                           final double tx,
                           final double ty) {
        transform.translate(tx,
                            ty);
    }

    private Viewport getViewPort() {
        return layer.getViewport();
    }
}
