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


package com.ait.lienzo.client.core.event;

import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.types.Point2DArray;
import elemental2.dom.Event;
import elemental2.dom.HTMLElement;

public class OrthogonalPolylinePointsChangedEvent extends AbstractNodeEvent<OrthogonalPolylinePointsChangedHandler, Node> {

    private static final Type<OrthogonalPolylinePointsChangedHandler> TYPE = new Type<>();
    private Point2DArray orthogonalPoints;

    public OrthogonalPolylinePointsChangedEvent(HTMLElement relativeElement) {
        super(relativeElement);
    }

    @Override
    public void dispatch(OrthogonalPolylinePointsChangedHandler handler) {
        handler.onOrthogonalPointsChanged(this);
    }

    @Override
    public Event getNativeEvent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Type<OrthogonalPolylinePointsChangedHandler> getAssociatedType() {
        return TYPE;
    }

    public static final Type<OrthogonalPolylinePointsChangedHandler> getType() {
        return TYPE;
    }

    public Point2DArray getOrthogonalPoints() {
        return orthogonalPoints;
    }

    public void override(final Point2DArray orthogonalPoints) {
        this.orthogonalPoints = orthogonalPoints;
    }
}
