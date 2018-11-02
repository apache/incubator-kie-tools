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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.dc;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts.asCData;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public abstract class BasePropertyWriter {

    protected final BaseElement baseElement;
    protected final VariableScope variableScope;
    protected final List<ItemDefinition> itemDefinitions = new ArrayList<>();
    protected final List<RootElement> rootElements = new ArrayList<>();
    protected BPMNShape shape;

    public BasePropertyWriter(BaseElement baseElement, VariableScope variableScope) {
        this.baseElement = baseElement;
        this.variableScope = variableScope;
    }

    public String getId() {
        return this.baseElement.getId();
    }

    public void setBounds(Bounds rect) {
        this.shape = di.createBPMNShape();
        shape.setId(Ids.bpmnShape(getId()));
        shape.setBpmnElement(baseElement);

        org.eclipse.dd.dc.Bounds bounds = dc.createBounds();

        Bounds.Bound upperLeft = rect.getUpperLeft();
        Bounds.Bound lowerRight = rect.getLowerRight();

        bounds.setX(upperLeft.getX().floatValue());
        bounds.setY(upperLeft.getY().floatValue());
        bounds.setWidth(lowerRight.getX().floatValue() - upperLeft.getX().floatValue());
        bounds.setHeight(lowerRight.getY().floatValue() - upperLeft.getY().floatValue());

        shape.setBounds(bounds);
    }

    public BaseElement getElement() {
        return baseElement;
    }

    public void setDocumentation(String value) {
        if (!isEmpty(value)) {
            Documentation documentation = bpmn2.createDocumentation();
            documentation.setText(asCData(value));
            baseElement.getDocumentation().add(documentation);
        }
    }

    /**
     * @return the shape associated to this element, shape if it's not an edge
     */
    public BPMNShape getShape() {
        return shape;
    }

    /**
     * @return the edge associated to this element, null if it's not an edge
     */
    public BPMNEdge getEdge() {
        return null;
    }

    public void setSource(BasePropertyWriter source) {

    }

    public void setTarget(BasePropertyWriter target) {

    }

    public void addChild(BasePropertyWriter child) {

    }

    public void setParent(BasePropertyWriter parent) {
        parent.addChild(this);
        if (this.getShape() == null) {
            return;
        }
        if (parent.getShape() == null) {
            throw new IllegalArgumentException(
                    "Cannot set parent with undefined shape: " +
                            parent.getElement().getId());
        }
        org.eclipse.dd.dc.Bounds parentBounds =
                getParentBounds(parent.getShape().getBounds());
        getShape().setBounds(parentBounds);
    }

    protected org.eclipse.dd.dc.Bounds getParentBounds(org.eclipse.dd.dc.Bounds parentRect) {
        if (getShape() == null) {
            throw new NullPointerException(
                    "Shape is null:" + getElement().getId());
        }
        if (getShape().getBounds() == null) {
            throw new IllegalArgumentException(
                    "Cannot set parent bounds if the child " +
                            "has undefined bounds. Use setBounds() first." + getElement().getId());
        }

        org.eclipse.dd.dc.Bounds relativeBounds = getShape().getBounds();
        float x = relativeBounds.getX();
        float y = relativeBounds.getY();
        float width = relativeBounds.getWidth();
        float height = relativeBounds.getHeight();

        float parentX = parentRect.getX();
        float parentY = parentRect.getY();

        org.eclipse.dd.dc.Bounds bounds = dc.createBounds();
        bounds.setX(parentX + x);
        bounds.setY(parentY + y);
        bounds.setWidth(width);
        bounds.setHeight(height);

        return bounds;
    }

    protected void addItemDefinition(ItemDefinition itemDefinition) {
        this.itemDefinitions.add(itemDefinition);
    }

    protected void addRootElement(RootElement rootElement) {
        this.rootElements.add(rootElement);
    }

    public List<ItemDefinition> getItemDefinitions() {
        return itemDefinitions;
    }

    public List<RootElement> getRootElements() {
        return rootElements;
    }
}
