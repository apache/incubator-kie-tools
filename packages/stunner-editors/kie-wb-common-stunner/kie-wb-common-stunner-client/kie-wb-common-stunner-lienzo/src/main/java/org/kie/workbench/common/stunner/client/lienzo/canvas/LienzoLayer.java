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


package org.kie.workbench.common.stunner.client.lienzo.canvas;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import org.kie.workbench.common.stunner.core.client.canvas.TransformImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.uberfire.mvp.Command;

@Dependent
public class LienzoLayer {

    private static Logger LOGGER = Logger.getLogger(LienzoLayer.class.getName());

    private final LienzoCustomLayer layer;

    static class LienzoCustomLayer extends Layer {

        private boolean skipDraw = false;

        @Override
        public Layer draw(final Context2D context) {
            if (skipDraw) {
                return this;
            }
            return super.draw(context);
        }
    }

    public void setSkipDraw(final boolean skipDraw) {
        layer.skipDraw = skipDraw;
    }

    public LienzoLayer() {
        this(new LienzoCustomLayer());
    }

    LienzoLayer(final LienzoCustomLayer layer) {
        this.layer = layer;
    }

    public LienzoLayer add(final IPrimitive<?> shape) {
        if (layer.getChildNodes().contains(shape)) {
            LOGGER.log(Level.WARNING, "Cannot add a primitive shape into the layer twice!");
        } else {
            layer.add(shape);
        }
        return this;
    }

    public LienzoLayer delete(final IPrimitive<?> shape) {
        layer.remove(shape);
        return this;
    }

    public void clear() {
        layer.clear();
    }

    public void onAfterDraw(final Command callback) {
        layer.setOnLayerAfterDraw(layer1 -> callback.execute());
    }

    public Layer getTopLayer() {
        return isReady() ?
                layer.getScene().getTopLayer() :
                null;
    }

    public void add(final Layer layer) {
        if (isReady()) {
            this.layer.getScene().add(layer);
        }
    }

    public boolean isReady() {
        return null != layer.getScene();
    }

    public void remove(final Layer layer) {
        if (isReady()) {
            this.layer.getScene().remove(layer);
        }
    }

    public void destroy() {
        layer.removeAll();
        layer.removeFromParent();
    }

    public Layer getLienzoLayer() {
        return this.layer;
    }

    protected Point2D getTranslate() {
        return new Point2D(
                layer.getAbsoluteTransform().getTranslateX(),
                layer.getAbsoluteTransform().getTranslateY()
        );
    }

    protected Point2D getScale() {
        return new Point2D(
                layer.getAbsoluteTransform().getScaleX(),
                layer.getAbsoluteTransform().getScaleY()
        );
    }

    public org.kie.workbench.common.stunner.core.client.canvas.Transform getTransform() {
        return new TransformImpl(getTranslate(),
                                 getScale());
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
        transform.scaleWithXY(sx,
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
