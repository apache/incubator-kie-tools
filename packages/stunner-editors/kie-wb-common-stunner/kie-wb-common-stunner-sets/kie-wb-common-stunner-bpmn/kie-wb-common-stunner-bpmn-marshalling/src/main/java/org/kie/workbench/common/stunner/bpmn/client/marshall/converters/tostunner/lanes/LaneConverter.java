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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.lanes;

import java.util.Optional;

import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessage;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessageKeys;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.NodeConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.LanePropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.validation.Violation;

public class LaneConverter implements NodeConverter<org.eclipse.bpmn2.Lane> {

    private final TypedFactoryManager typedFactoryManager;
    private PropertyReaderFactory propertyReaderFactory;

    public LaneConverter(TypedFactoryManager typedFactoryManager, PropertyReaderFactory propertyReaderFactory) {
        this.typedFactoryManager = typedFactoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    public Result<BpmnNode> convert(org.eclipse.bpmn2.Lane lane) {
        return convert(lane, propertyReaderFactory.of(lane));
    }

    public Result<BpmnNode> convert(org.eclipse.bpmn2.Lane lane, org.eclipse.bpmn2.Lane parent) {
        final Result<BpmnNode> result = convert(lane, propertyReaderFactory.of(lane, parent));
        return Optional.ofNullable(result.value())
                .map(value -> Result.success(value, MarshallingMessage.builder()
                        .message("Child Lane Set " + lane.getName() + " Converted to Lane " + parent.getName())
                        .messageKey(MarshallingMessageKeys.childLaneSetConverted)
                        .messageArguments(lane.getName(), parent.getName())
                        .type(Violation.Type.WARNING)
                        .build()))
                .get();
    }

    private Result<BpmnNode> convert(org.eclipse.bpmn2.Lane lane, LanePropertyReader p) {
        Node<View<Lane>, Edge> node = typedFactoryManager.newNode(lane.getId(), Lane.class);
        Lane definition = node.getContent().getDefinition();

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        return Result.success(BpmnNode.of(node, p));
    }
}
