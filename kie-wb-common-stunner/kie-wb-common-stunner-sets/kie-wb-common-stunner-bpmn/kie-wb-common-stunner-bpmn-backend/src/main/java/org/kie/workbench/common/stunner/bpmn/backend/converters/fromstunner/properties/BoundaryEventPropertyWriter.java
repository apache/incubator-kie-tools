/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.EventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.dc;

public class BoundaryEventPropertyWriter extends CatchEventPropertyWriter {

    private final BoundaryEvent event;

    public BoundaryEventPropertyWriter(BoundaryEvent event, VariableScope variableScope) {
        super(event, variableScope);
        this.event = event;
    }

    @Override
    public void setCancelActivity(Boolean value) {
        CustomAttribute.boundarycaForEvent.of(flowElement).set(value);
        event.setCancelActivity(value);
    }

    public void setParentActivity(ActivityPropertyWriter parent) {
        org.eclipse.dd.dc.Bounds parentBounds =
                getParentActivityBounds(parent.getShape().getBounds());
        getShape().setBounds(parentBounds);
        event.setAttachedToRef(parent.getFlowElement());
    }

    @Override
    public void addEventDefinition(EventDefinition eventDefinition) {
        this.event.getEventDefinitions().add(eventDefinition);
    }

    /*
     *  absolute coordinates (absoluteX, absoluteY) of the boundary
     *  event are computed with the following formulae:
     *
     *    absoluteX := parentX + x - width/2
     *    absoluteY := parentY + x - height/2
     *
     *  where (x,y) is relative to (parentX,parentY)
     *
     *
     *   (parentX, parentY)
     *         +----------------------+
     *         |                      |
     *         |                      |
     *         |        parent        |
     *         |                      |
     *         |                      |
     *         |                      |
     *         |      (x,y)           |
     *         |       +-------+      |
     *         |       |       |      |
     *         +-------- event -------+
     *                 |       |
     *                 +-------+
     *
     *
     *
     */
    protected org.eclipse.dd.dc.Bounds getParentActivityBounds(org.eclipse.dd.dc.Bounds parentRect) {
        if (getShape().getBounds() == null) {
            throw new IllegalArgumentException(
                    "Cannot set parent bounds if the child " +
                            "has undefined bounds. Use setBounds() first.");
        }

        org.eclipse.dd.dc.Bounds relativeBounds = getShape().getBounds();
        float x = relativeBounds.getX();
        float y = relativeBounds.getY();
        float width = relativeBounds.getWidth();
        float height = relativeBounds.getHeight();

        float parentX = parentRect.getX();
        float parentY = parentRect.getY();

        org.eclipse.dd.dc.Bounds bounds = dc.createBounds();
        bounds.setX(parentX + x - width / 2);
        bounds.setY(parentY + y - height / 2);
        bounds.setWidth(width);
        bounds.setHeight(height);

        return bounds;
    }

    @Override
    public void setBounds(Bounds rect) {
        Bounds.Bound bound = rect.getUpperLeft();
        CustomAttribute.dockerInfo.of(flowElement).set(
                Point2D.create(bound.getX(), bound.getY()));
        super.setBounds(rect);
    }
}