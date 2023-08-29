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


package com.ait.lienzo.client.core.shape.wires.types;

import com.ait.lienzo.client.core.shape.IDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import jsinterop.annotations.JsType;

@JsType
public class JsWiresConnector {

    WiresConnector connector;

    public JsWiresConnector(WiresConnector connector) {
        this.connector = connector;
    }

    public JsWiresConnection getHeadConnection() {
        return new JsWiresConnection(connector.getHeadConnection());
    }

    public JsWiresConnection getTailConnection() {
        return new JsWiresConnection(connector.getTailConnection());
    }

    public NFastArrayList<Point2D> getConnectorPoints() {
        final IDirectionalMultiPointShape<?> line = connector.getLine();
        if (line instanceof OrthogonalPolyLine) {
            return ((OrthogonalPolyLine) line).getComputedPoint2DArray().toNFastArrayList();
        }
        return connector.getLine().getPoint2DArray().toNFastArrayList();
    }

    public Double[] getConnectorPointsAsArray() {
        NFastArrayList<Point2D> points = getConnectorPoints();
        Double[] pointsArray = new Double[points.size() * 2];
        int i = 0;
        for (Point2D point : points.toList()) {
            pointsArray[i++] = point.getX();
            pointsArray[i++] = point.getY();
        }
        return pointsArray;
    }
}
