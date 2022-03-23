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

package org.kie.workbench.common.stunner.client.lienzo.components.proxies;

import java.util.function.Function;

import com.ait.lienzo.client.core.shape.wires.proxy.AbstractWiresProxy;
import com.ait.lienzo.client.core.shape.wires.proxy.WiresDragProxy;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.core.client.components.proxies.AbstractShapeProxyView;
import org.kie.workbench.common.stunner.core.client.shape.ElementShape;

public abstract class LienzoShapeProxyView<S extends ElementShape>
        extends AbstractShapeProxyView<S> {

    private WiresDragProxy dragProxy;

    static Function<AbstractWiresProxy, WiresDragProxy> DRAG_PROXY_BUILDER =
            p -> new WiresDragProxy(() -> p);

    @Override
    public void start(final double x,
                      final double y) {
        build();
        dragProxy.enable(x, y);
    }

    @Override
    protected void doDestroy() {
        if (null != dragProxy) {
            dragProxy.destroy();
            dragProxy = null;
        }
    }

    protected abstract AbstractWiresProxy create();

    private void build() {
        final AbstractWiresProxy proxy = create();
        this.dragProxy = DRAG_PROXY_BUILDER.apply(proxy);
    }

    protected WiresCanvas getWiresCanvas() {
        return (WiresCanvas) getCanvas();
    }
}
