/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.components.drag;

import javax.enterprise.context.Dependent;

import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.drag.ShapeViewDragProxy;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.lienzo.primitive.AbstractDragProxy;
import org.kie.workbench.common.stunner.lienzo.primitive.WiresConnectorDragProxy;
import org.kie.workbench.common.stunner.lienzo.primitive.WiresShapeDragProxy;

@Dependent
public class ShapeViewDragProxyImpl implements ShapeViewDragProxy<AbstractCanvas> {

    private LienzoLayer layer;
    private AbstractDragProxy<?> proxy;

    @Override
    public DragProxy<AbstractCanvas, ShapeView<?>, DragProxyCallback> proxyFor(final AbstractCanvas context) {
        this.layer = (LienzoLayer) context.getLayer();
        return this;
    }

    @Override
    public DragProxy<AbstractCanvas, ShapeView<?>, DragProxyCallback> show(final ShapeView<?> item,
                                                                           final int x,
                                                                           final int y,
                                                                           final DragProxyCallback callback) {
        clear();
        final AbstractDragProxy.Callback c = new AbstractDragProxy.Callback() {

            @Override
            public void onStart(final int x,
                                final int y) {
                callback.onStart(x,
                                 y);
            }

            @Override
            public void onMove(final int x,
                               final int y) {
                callback.onMove(x,
                                y);
            }

            @Override
            public void onComplete(final int x,
                                   final int y) {
                callback.onComplete(x,
                                    y);
            }
        };
        if (item instanceof WiresShape) {
            final WiresShape wiresShape = (WiresShape) item;
            this.proxy = new WiresShapeDragProxy(getLayer().getLienzoLayer(),
                                                 wiresShape,
                                                 x,
                                                 y,
                                                 100,
                                                 c);
        } else if (item instanceof WiresConnector) {
            final WiresConnector wiresConnector = (WiresConnector) item;
            this.proxy = new WiresConnectorDragProxy(getLayer().getLienzoLayer(),
                                                     wiresConnector,
                                                     x,
                                                     y,
                                                     100,
                                                     c);
        }
        return this;
    }

    @Override
    public void clear() {
        if (null != this.proxy) {
            this.proxy.clear();
        }
    }

    @Override
    public void destroy() {
        if (null != this.proxy) {
            this.proxy.destroy();
            this.proxy = null;
        }
        this.layer = null;
    }

    private LienzoLayer getLayer() {
        return layer;
    }
}
