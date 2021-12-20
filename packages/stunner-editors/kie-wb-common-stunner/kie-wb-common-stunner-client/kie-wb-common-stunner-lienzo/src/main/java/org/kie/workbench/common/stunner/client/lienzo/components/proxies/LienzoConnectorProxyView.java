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

import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.proxy.AbstractWiresProxy;
import com.ait.lienzo.client.core.shape.wires.proxy.WiresConnectorProxy;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.Shape;

@Dependent
public class LienzoConnectorProxyView
        extends LienzoShapeProxyView<EdgeShape> {

    private EdgeShape connector;

    @Override
    protected void doDestroy() {
        super.doDestroy();
        connector = null;
    }

    @Override
    protected AbstractWiresProxy create() {
        connector = getShapeBuilder().get();
        return new WiresConnectorProxy(getWiresCanvas().getWiresManager(),
                                       () -> getWiresConnector(connector),
                                       wiresConnector -> getShapeAcceptor().accept(connector),
                                       wiresConnector -> getShapeDestroyer().accept(connector));
    }

    private static WiresConnector getWiresConnector(final Shape<?> connectorShape) {
        return (WiresConnector) connectorShape.getShapeView();
    }
}
