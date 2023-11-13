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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.artifacts;

import org.eclipse.bpmn2.DataObjectReference;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.NodeConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.DataObjectPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectType;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class DataObjectConverter implements NodeConverter<org.eclipse.bpmn2.DataObjectReference> {

    private final TypedFactoryManager typedFactoryManager;
    private PropertyReaderFactory propertyReaderFactory;

    public DataObjectConverter(TypedFactoryManager typedFactoryManager, PropertyReaderFactory propertyReaderFactory) {
        this.typedFactoryManager = typedFactoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    @Override
    public Result<BpmnNode> convert(org.eclipse.bpmn2.DataObjectReference element) {
        return convert(element, propertyReaderFactory.of(element));
    }

    private Result<BpmnNode> convert(DataObjectReference element, DataObjectPropertyReader p) {
        Node<View<DataObject>, Edge> node = typedFactoryManager.newNode(element.getId(),
                                                                        DataObject.class);
        DataObject definition = node.getContent().getDefinition();
        definition.setName(new Name(p.getName()));
        definition.setType(new DataObjectType(new DataObjectTypeValue(p.getType())));
        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));
        return Result.success(BpmnNode.of(node, p));
    }
}

