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


package org.kie.workbench.common.stunner.client.lienzo.wires;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlPointHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresHandlerFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresHandlerFactoryImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;

@ApplicationScoped
@Default
public class StunnerWiresHandlerFactory implements WiresHandlerFactory {

    private final WiresHandlerFactory delegate;

    public StunnerWiresHandlerFactory() {
        this.delegate = new WiresHandlerFactoryImpl();
    }

    StunnerWiresHandlerFactory(WiresHandlerFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public WiresConnectorHandler newConnectorHandler(final WiresConnector wiresConnector,
                                                     final WiresManager wiresManager) {
        return delegate.newConnectorHandler(wiresConnector, wiresManager);
    }

    @Override
    public WiresControlPointHandler newControlPointHandler(final WiresConnector wiresConnector,
                                                           final WiresManager wiresManager) {
        return delegate.newControlPointHandler(wiresConnector, wiresManager);
    }

    @Override
    public WiresShapeHandler newShapeHandler(final WiresShape shape,
                                             final WiresShapeHighlight<PickerPart.ShapePart> highlight,
                                             final WiresManager wiresManager) {
        return delegate.newShapeHandler(shape, highlight, wiresManager);
    }
}
