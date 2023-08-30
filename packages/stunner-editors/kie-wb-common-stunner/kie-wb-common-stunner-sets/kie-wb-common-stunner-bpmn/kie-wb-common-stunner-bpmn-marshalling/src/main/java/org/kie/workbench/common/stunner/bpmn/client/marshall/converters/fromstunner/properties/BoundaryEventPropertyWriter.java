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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.Optional;
import java.util.Set;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.EventDefinition;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.PropertyWriterUtils.getDockSourceNode;

public class BoundaryEventPropertyWriter extends CatchEventPropertyWriter {

    private final BoundaryEvent event;

    public BoundaryEventPropertyWriter(BoundaryEvent event, VariableScope variableScope, Set<DataObject> dataObjects) {
        super(event, variableScope, dataObjects);
        this.event = event;
    }

    @Override
    public void setCancelActivity(Boolean value) {
        CustomAttribute.boundarycaForEvent.of(flowElement).set(value);
        event.setCancelActivity(value);
    }

    public void setParentActivity(ActivityPropertyWriter parent) {
        event.setAttachedToRef(parent.getFlowElement());
    }

    @Override
    public void addEventDefinition(EventDefinition eventDefinition) {
        this.event.getEventDefinitions().add(eventDefinition);
    }

    @Override
    public void setAbsoluteBounds(Node<? extends View, ?> node) {
        Bound ul = node.getContent().getBounds().getUpperLeft();
        //docker information is relative
        setDockerInfo(Point2D.create(ul.getX(), ul.getY()));

        Optional<Node<View, Edge>> dockSourceNode = getDockSourceNode(node);
        if (dockSourceNode.isPresent()) {
            //docked node bounds are relative to the dockSourceNode in Stunner, but not in bpmn2 standard so the node
            //absolute bounds must be calculated by using hte dockSourceNode absolute coordinates.
            Bounds dockSourceNodeBounds = absoluteBounds(dockSourceNode.get());
            Bounds nodeBounds = node.getContent().getBounds();
            double x = dockSourceNodeBounds.getX() + nodeBounds.getUpperLeft().getX();
            double y = dockSourceNodeBounds.getY() + nodeBounds.getUpperLeft().getY();
            super.setBounds(Bounds.create(x, y, x + nodeBounds.getWidth(), y + nodeBounds.getHeight()));
        } else {
            //uncommon case
            super.setAbsoluteBounds(node);
        }
    }

    private void setDockerInfo(Point2D docker) {
        CustomAttribute.dockerInfo.of(event).set(docker);
    }
}