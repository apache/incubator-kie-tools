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

import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.types.Point2D;

/**
 * Connector control handler provides user interaction common functions/logic in a way that they're decoupled
 * from the concrete event types fired, and these calls be reused programatically as well.
 * <p>
 * The default event handlers used on wires connectors registrations delegate to this control, so developers
 * can create custom connector controls and provide the instances by using the
 * <code>com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory</code> and provide custom
 * user interaction behaviours rather than defaults.
 */
public interface WiresConnectorControl extends WiresMoveControl,
                                               WiresControl {

    /**
     * Add a control point on the existing {@link WiresConnector#getLine()} returning the selected index to the control point
     *
     * @param x position
     * @param y position
     * @return index to the added Control Point
     */
    int addControlPoint(double x, double y);

    boolean moveControlPoint(int index, Point2D location);

    void destroyControlPoint(int index);

    void showControlPoints();

    void hideControlPoints();

    boolean areControlPointsVisible();

    boolean accept();
}