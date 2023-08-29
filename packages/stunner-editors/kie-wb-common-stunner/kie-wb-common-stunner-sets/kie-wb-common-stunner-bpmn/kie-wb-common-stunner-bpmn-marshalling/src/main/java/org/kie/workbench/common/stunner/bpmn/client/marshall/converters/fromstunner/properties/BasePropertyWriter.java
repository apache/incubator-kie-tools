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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.DocumentationTextHandler;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.MetaDataAttributes;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.dc;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.di;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts.asCData;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils.isEmpty;

public abstract class BasePropertyWriter {

    protected final BaseElement baseElement;
    protected final VariableScope variableScope;
    protected final List<ItemDefinition> itemDefinitions = new ArrayList<>();
    protected final List<RootElement> rootElements = new ArrayList<>();
    protected final List<Interface> interfaces = new ArrayList<>();
    protected BPMNShape shape;

    public BasePropertyWriter(BaseElement baseElement, VariableScope variableScope) {
        this.baseElement = baseElement;
        this.variableScope = variableScope;
    }

    public String getId() {
        return this.baseElement.getId();
    }

    public void setId(String id) {
        baseElement.setId(id);
    }

    protected void setBounds(Bounds rect) {
        this.shape = di.createBPMNShape();
        shape.setId(Ids.bpmnShape(getId()));
        shape.setBpmnElement(baseElement);

        org.eclipse.dd.dc.Bounds bounds = dc.createBounds();

        Bound upperLeft = rect.getUpperLeft();
        Bound lowerRight = rect.getLowerRight();

        bounds.setX(upperLeft.getX().floatValue());
        bounds.setY(upperLeft.getY().floatValue());
        bounds.setWidth(lowerRight.getX().floatValue() - upperLeft.getX().floatValue());
        bounds.setHeight(lowerRight.getY().floatValue() - upperLeft.getY().floatValue());

        shape.setBounds(bounds);
    }

    public void setAbsoluteBounds(Node<? extends View, ?> node) {
        setBounds(absoluteBounds(node));
    }

    public BaseElement getElement() {
        return baseElement;
    }

    public void setDocumentation(String value) {
        if (!isEmpty(value)) {
            Documentation documentation = bpmn2.createDocumentation();
            DocumentationTextHandler.of(documentation).setText(asCData(value));
            baseElement.getDocumentation().add(documentation);
        }
    }

    public void setMetaData(final MetaDataAttributes metaDataAttributes) {
        if (null != metaDataAttributes.getValue() && !metaDataAttributes.getValue().isEmpty()) {
            CustomElement.metaDataAttributes.of(baseElement).set(metaDataAttributes.getValue());
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
    }

    protected void addItemDefinition(ItemDefinition itemDefinition) {
        this.itemDefinitions.add(itemDefinition);
    }

    protected void addInterfaceDefinition(Interface iface) {
        this.interfaces.add(iface);
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

    public List<Interface> getInterfaces() {
        return interfaces;
    }

    public static org.kie.workbench.common.stunner.core.graph.content.Bounds absoluteBounds(final Node<? extends View, ?> node) {
        final Point2D point2D = GraphUtils.getComputedPosition(node);
        final org.kie.workbench.common.stunner.core.graph.content.Bounds bounds = node.getContent().getBounds();
        return org.kie.workbench.common.stunner.core.graph.content.Bounds.create(point2D.getX(),
                                                                                 point2D.getY(),
                                                                                 point2D.getX() + bounds.getWidth(),
                                                                                 point2D.getY() + bounds.getHeight());
    }
}
