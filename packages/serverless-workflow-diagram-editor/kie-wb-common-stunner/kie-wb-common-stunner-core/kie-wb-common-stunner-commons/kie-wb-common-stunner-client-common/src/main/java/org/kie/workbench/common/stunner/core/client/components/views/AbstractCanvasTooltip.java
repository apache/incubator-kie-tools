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


package org.kie.workbench.common.stunner.core.client.components.views;

import org.kie.workbench.common.stunner.core.client.canvas.Transform;
import org.kie.workbench.common.stunner.core.client.canvas.TransformImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public abstract class AbstractCanvasTooltip<T> implements CanvasTooltip<T> {

    private Point2D canvasLocation;
    private Transform transform;

    protected AbstractCanvasTooltip() {
        this.canvasLocation = new Point2D(0,
                                          0);
        this.transform = TransformImpl.NO_TRANSFORM;
    }

    protected abstract void showAt(T content,
                                   Point2D location);

    @Override
    public void setCanvasLocation(final Point2D location) {
        this.canvasLocation = location;
    }

    @Override
    public void setTransform(final Transform transform) {
        this.transform = transform;
    }

    @Override
    public void show(final T content,
                     final Point2D location) {
        final Point2D computedLocation = getComputedLocation(location);
        showAt(content,
               computedLocation);
    }

    private Point2D getComputedLocation(final Point2D location) {
        final Point2D transformed = this.transform.transform(location.getX(),
                                                             location.getY());
        return new Point2D(transformed.getX() + canvasLocation.getX(),
                           transformed.getY() + canvasLocation.getY());
    }
}
