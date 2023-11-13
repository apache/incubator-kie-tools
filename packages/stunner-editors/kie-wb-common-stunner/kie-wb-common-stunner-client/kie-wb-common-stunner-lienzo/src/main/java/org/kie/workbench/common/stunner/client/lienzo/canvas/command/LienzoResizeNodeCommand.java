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


package org.kie.workbench.common.stunner.client.lienzo.canvas.command;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.core.client.canvas.command.ResizeNodeCommand;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class LienzoResizeNodeCommand extends ResizeNodeCommand {

    public LienzoResizeNodeCommand(final Element<? extends View> candidate,
                                   final BoundingBox boundingBox) {
        super(candidate, boundingBox, LOCATION_PROVIDER, ON_RESIZE);
    }

    private static final BiFunction<Shape, Integer, Point2D> LOCATION_PROVIDER =
            (shape, index) -> {
                final WiresShape wiresShape = (WiresShape) shape.getShapeView();
                final WiresMagnet magnet = wiresShape.getMagnets().getMagnet(index);
                return new Point2D(magnet.getX(), magnet.getY());
            };

    private static final Consumer<Shape> ON_RESIZE = shape -> {
        final WiresShape wiresShape = (WiresShape) shape.getShapeView();
        wiresShape.refresh();
    };
}
