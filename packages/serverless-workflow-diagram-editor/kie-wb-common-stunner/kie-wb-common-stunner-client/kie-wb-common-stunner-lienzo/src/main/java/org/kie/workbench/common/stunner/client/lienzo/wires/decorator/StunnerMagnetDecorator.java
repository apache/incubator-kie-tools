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


package org.kie.workbench.common.stunner.client.lienzo.wires.decorator;

import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.decorator.MagnetDecorator;
import com.ait.lienzo.client.core.shape.wires.decorator.PointHandleDecorator;

public class StunnerMagnetDecorator extends MagnetDecorator {

    @Override
    public Shape decorate(Shape shape, ShapeState state) {
        switch (state) {
            case VALID:
            case INVALID:
            case NONE:
                shape.setFillColor(PointHandleDecorator.MAIN_COLOR)
                        .setStrokeWidth(0)
                        .setFillAlpha(0.8)
                        .setStrokeAlpha(1)
                        .moveToTop();
        }
        return shape;
    }
}
