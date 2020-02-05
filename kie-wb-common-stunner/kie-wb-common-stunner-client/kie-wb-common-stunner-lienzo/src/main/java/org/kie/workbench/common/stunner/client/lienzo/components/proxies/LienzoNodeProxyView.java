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

import javax.enterprise.context.Dependent;

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.proxy.AbstractWiresProxy;
import com.ait.lienzo.client.core.shape.wires.proxy.WiresShapeProxy;
import org.kie.workbench.common.stunner.core.client.shape.NodeShape;
import org.kie.workbench.common.stunner.core.client.shape.Shape;

@Dependent
public class LienzoNodeProxyView
        extends LienzoShapeProxyView<NodeShape> {

    private NodeShape shape;

    @Override
    protected void doDestroy() {
        super.doDestroy();
        shape = null;
    }

    @Override
    protected AbstractWiresProxy create() {
        shape = getShapeBuilder().get();
        return new WiresShapeProxy(getWiresCanvas().getWiresManager(),
                                   () -> getWiresShape(shape),
                                   wiresShape -> getShapeAcceptor().accept(shape),
                                   wiresShape -> getShapeDestroyer().accept(shape));
    }

    private static WiresShape getWiresShape(final Shape<?> connectorShape) {
        return (WiresShape) connectorShape.getShapeView();
    }
}
