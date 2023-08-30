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


package org.kie.workbench.common.stunner.core.client.canvas.controls.event;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;

/**
 * Event for requesting the canvas builder control to add a new shape.
 */
public final class BuildCanvasShapeEvent extends AbstractCanvasHandlerEvent<AbstractCanvasHandler> {

    private Object definition;
    private ShapeFactory<?, ? extends Shape> shapeFactory;
    private double clientX;
    private double clientY;

    public BuildCanvasShapeEvent(final AbstractCanvasHandler abstractCanvasHandler,
                                 final Object definition,
                                 final ShapeFactory<?, ? extends Shape> shapeFactory,
                                 final double clientX,
                                 final double clientY) {
        super(abstractCanvasHandler);
        this.definition = definition;
        this.shapeFactory = shapeFactory;
        this.clientX = clientX;
        this.clientY = clientY;
    }

    public Object getDefinition() {
        return definition;
    }

    public ShapeFactory<?, ? extends Shape> getShapeFactory() {
        return shapeFactory;
    }

    public double getClientX() {
        return clientX;
    }

    public double getClientY() {
        return clientY;
    }

    @Override
    public String toString() {
        return "BuildCanvasShapeEvent " +
                "[definition=" + definition + ", " +
                "factory=" + shapeFactory.toString() +
                ", clientX=" + clientX +
                ", clientY=" + clientY + "]";
    }
}
