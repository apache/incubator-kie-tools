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

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class BoundaryEventPropertyReader extends CatchEventPropertyReader {

    private final BoundaryEvent event;

    public BoundaryEventPropertyReader(BoundaryEvent event, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(event, diagram, definitionResolver);
        this.event = event;
    }

    @Override
    public boolean isCancelActivity() {
        return event.isCancelActivity();
    }

    @Override
    protected Bounds computeBounds(final org.eclipse.dd.dc.Bounds bounds) {
        final Point2D docker = getDockerInfo();
        double x = 0;
        double y = 0;
        if (docker.getX() != 0 && docker.getY() != 0) {
            x = docker.getX() * resolutionFactor;
            y = docker.getY() * resolutionFactor;
        } else if (event.getAttachedToRef() != null) {
            //when the node was generated in other tool than Stunner/jBPM designer the dockerInfo attribute don't exists
            //and we need to use attachedToRef activity position to calculate the bounded event relative coordinates.
            String activityId = event.getAttachedToRef().getId();
            org.eclipse.dd.dc.Bounds activityBounds = definitionResolver.getShape(activityId).getBounds();
            x = bounds.getX() * resolutionFactor - activityBounds.getX() * resolutionFactor;
            y = bounds.getY() * resolutionFactor - activityBounds.getY() * resolutionFactor;
            //if required adjust the event relative position according with the positioning supported by Stunner.
            if (x < -WIDTH / 2) {
                x = -WIDTH / 2;
            } else if (x > (activityBounds.getWidth() * resolutionFactor) - WIDTH / 2) {
                x = activityBounds.getWidth() * resolutionFactor - WIDTH / 2;
            }
            if (y < -HEIGHT / 2) {
                y = -HEIGHT / 2;
            } else if (y > (activityBounds.getHeight() * resolutionFactor) - HEIGHT / 2) {
                y = activityBounds.getHeight() * resolutionFactor - HEIGHT / 2;
            }
        }
        return Bounds.create(x, y, x + WIDTH, y + HEIGHT);
    }

    Point2D getDockerInfo() {
        return CustomAttribute.dockerInfo.of(element).get();
    }
}
