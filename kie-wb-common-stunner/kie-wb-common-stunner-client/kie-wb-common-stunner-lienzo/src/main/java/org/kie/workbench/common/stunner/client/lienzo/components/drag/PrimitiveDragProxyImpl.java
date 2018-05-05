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

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.drag.PrimitiveDragProxy;
import org.kie.workbench.common.stunner.lienzo.primitive.AbstractDragProxy;

@Dependent
public class PrimitiveDragProxyImpl implements PrimitiveDragProxy<Layer, IPrimitive<?>> {

    private Layer layer;
    private AbstractDragProxy<?> proxy;

    protected PrimitiveDragProxyImpl() {
    }

    @Override
    public DragProxy<Layer, IPrimitive<?>, DragProxyCallback> proxyFor(final Layer context) {
        this.layer = context;
        return this;
    }

    @Override
    public DragProxy<Layer, IPrimitive<?>, DragProxyCallback> show(final IPrimitive<?> item,
                                                                   final int x,
                                                                   final int y,
                                                                   final DragProxyCallback callback) {
        clear();
        this.proxy = new org.kie.workbench.common.stunner.lienzo.primitive.PrimitiveDragProxy(layer,
                                                                                              item,
                                                                                              x,
                                                                                              y,
                                                                                              200,
                                                                                              new org.kie.workbench.common.stunner.lienzo.primitive.PrimitiveDragProxy.Callback() {

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
                                                                                              });
        return this;
    }

    @Override
    public void clear() {
        if (null != this.proxy) {
            this.proxy.clear();
            this.layer.draw();
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
}
