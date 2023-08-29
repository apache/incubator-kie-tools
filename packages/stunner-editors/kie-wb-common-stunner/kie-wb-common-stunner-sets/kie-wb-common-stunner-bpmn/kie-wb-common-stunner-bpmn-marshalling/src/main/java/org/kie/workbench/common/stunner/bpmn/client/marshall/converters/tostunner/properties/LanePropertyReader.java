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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.Optional;

import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNShape;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

public class LanePropertyReader extends BasePropertyReader {

    private final Lane lane;
    private final BPMNShape parentLaneShape;

    public LanePropertyReader(Lane el, BPMNDiagram diagram, BPMNShape shape, BPMNShape parentLaneShape, double resolutionFactor) {
        super(el, diagram, shape, resolutionFactor);
        this.lane = el;
        this.parentLaneShape = parentLaneShape;
    }

    public LanePropertyReader(Lane el, BPMNDiagram diagram, BPMNShape shape, double resolutionFactor) {
        this(el, diagram, shape, null, resolutionFactor);
    }

    public String getName() {
        String extendedName = CustomElement.name.of(element).get();
        return extendedName.isEmpty() ?
                Optional.ofNullable(lane.getName()).orElse("")
                : extendedName;
    }

    @Override
    protected Bounds computeBounds(org.eclipse.dd.dc.Bounds bounds) {
        if (shape == null || parentLaneShape == null) {
            return super.computeBounds(bounds);
        } else {
            org.eclipse.dd.dc.Bounds parentLaneBounds = parentLaneShape.getBounds();
            final double x = parentLaneBounds.getX() * resolutionFactor;
            final double y = bounds.getY() * resolutionFactor;
            final double width = parentLaneBounds.getWidth() * resolutionFactor;
            final double height = bounds.getHeight() * resolutionFactor;
            return Bounds.create(x, y, x + width, y + height);
        }
    }

    @Override
    public RectangleDimensionsSet getRectangleDimensionsSet() {
        if (shape == null || parentLaneShape == null) {
            return super.getRectangleDimensionsSet();
        }
        org.eclipse.dd.dc.Bounds bounds = shape.getBounds();
        return new RectangleDimensionsSet(parentLaneShape.getBounds().getWidth() * resolutionFactor,
                                          bounds.getHeight() * resolutionFactor);
    }
}
