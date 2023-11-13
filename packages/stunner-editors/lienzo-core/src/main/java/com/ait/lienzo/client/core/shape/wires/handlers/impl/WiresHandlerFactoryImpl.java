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


package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlPointHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresHandlerFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;

/**
 * Default factory implementation for Wires Handlers.
 */
public class WiresHandlerFactoryImpl implements WiresHandlerFactory {

    @Override
    public WiresConnectorHandler newConnectorHandler(final WiresConnector connector,
                                                     final WiresManager wiresManager) {
        return new WiresConnectorHandlerImpl(connector, wiresManager);
    }

    @Override
    public WiresControlPointHandler newControlPointHandler(final WiresConnector connector,
                                                           final WiresManager wiresManager) {
        return new WiresControlPointHandlerImpl(connector, wiresManager);
    }

    @Override
    public WiresShapeHandler newShapeHandler(final WiresShape shape,
                                             final WiresShapeHighlight<PickerPart.ShapePart> highlight,
                                             final WiresManager manager) {
        final WiresControlFactory controlFactory = manager.getControlFactory();
        final Supplier<WiresLayerIndex> indexBuilder = () -> controlFactory.newIndex(manager);
        return new WiresShapeHandlerImpl(indexBuilder,
                                         shape,
                                         highlight,
                                         manager);
    }
}
