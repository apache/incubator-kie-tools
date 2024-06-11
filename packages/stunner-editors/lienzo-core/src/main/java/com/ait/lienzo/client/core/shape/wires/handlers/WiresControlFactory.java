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

package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;

/**
 * In general control handlers provide user interaction common functions/logic in a way that they're decoupled
 * from the concrete event types fired, and these calls be reused programatically as well.
 * <p/>
 * By default, <code>com.ait.lienzo.client.core.shape.wires.WiresManager</code> register some event types when a shape or connector is registered. Those event handlers
 * just delegate the operations to the different control interfaces.
 * <p/>
 * Developers can provide custom control instances to override default event handlers behaviors by providing
 * a concrete <code>WiresControlFactory</code> instance for <code>com.ait.lienzo.client.core.shape.wires.WiresManager</code>.
 */
public interface WiresControlFactory {

    /**
     * Creates a new control instance for a shape.
     */
    WiresShapeControl newShapeControl(WiresShape shape,
                                      WiresManager wiresManager);

    /**
     * Creates a new control instance for a connector.
     */
    WiresConnectorControl newConnectorControl(WiresConnector connector,
                                              WiresManager wiresManager);

    /**
     * Creates a new control instance for a connection.
     */
    WiresConnectionControl newConnectionControl(WiresConnector connector,
                                                boolean headNotTail,
                                                WiresManager wiresManager);

    /**
     * Creates a new control instance that composite other controls,
     * for example when handling with multiple shapes and connectors
     */
    WiresCompositeControl newCompositeControl(WiresCompositeControl.Context context,
                                              WiresManager wiresManager);

    /**
     * Creates a new shape highlight control.
     */
    WiresShapeHighlight<PickerPart.ShapePart> newShapeHighlight(WiresManager wiresManager);

    /**
     * Creates a new wires index..
     */
    WiresLayerIndex newIndex(WiresManager manager);
}
