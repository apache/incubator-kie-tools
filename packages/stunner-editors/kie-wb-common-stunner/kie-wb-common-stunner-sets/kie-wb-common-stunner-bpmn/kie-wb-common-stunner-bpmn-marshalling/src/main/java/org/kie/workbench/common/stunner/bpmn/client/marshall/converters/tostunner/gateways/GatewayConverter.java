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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.gateways;

import org.eclipse.bpmn2.Gateway;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingRequest.Mode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.BPMNElementDecorators;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Match;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.AbstractConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.NodeConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.GatewayPropertyReader;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.EventGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.DefaultRoute;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.GatewayExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class GatewayConverter extends AbstractConverter implements NodeConverter<Gateway> {

    private final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;

    public GatewayConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory,
                            Mode mode) {
        super(mode);
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    public Result<BpmnNode> convert(Gateway gateway) {
        return Match.<Gateway, Result<BpmnNode>>of()
                .<org.eclipse.bpmn2.ParallelGateway>when(e -> e instanceof org.eclipse.bpmn2.ParallelGateway,
                                                         this::parallelGateway)
                .<org.eclipse.bpmn2.ExclusiveGateway>when(e -> e instanceof org.eclipse.bpmn2.ExclusiveGateway,
                                                          this::exclusiveGateway)
                .<org.eclipse.bpmn2.InclusiveGateway>when(e -> e instanceof org.eclipse.bpmn2.InclusiveGateway,
                                                          this::inclusiveGateway)
                .<org.eclipse.bpmn2.EventBasedGateway>when(e -> e instanceof org.eclipse.bpmn2.EventBasedGateway,
                                                           this::eventGateway)
                .defaultValue(Result.ignored("Gateway not found"))
                .inputDecorator(BPMNElementDecorators.flowElementDecorator())
                .outputDecorator(BPMNElementDecorators.resultBpmnDecorator())
                .mode(getMode())
                .apply(gateway)
                .value();
    }

    private Result<BpmnNode> inclusiveGateway(Gateway gateway) {
        Node<View<InclusiveGateway>, Edge> node = factoryManager.newNode(gateway.getId(), InclusiveGateway.class);

        InclusiveGateway definition = node.getContent().getDefinition();
        GatewayPropertyReader p = propertyReaderFactory.of(gateway);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new GatewayExecutionSet(
                new DefaultRoute(p.getDefaultRoute())
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        return Result.success(BpmnNode.of(node, p));
    }

    private Result<BpmnNode> exclusiveGateway(Gateway gateway) {
        Node<View<ExclusiveGateway>, Edge> node = factoryManager.newNode(gateway.getId(), ExclusiveGateway.class);

        ExclusiveGateway definition = node.getContent().getDefinition();
        GatewayPropertyReader p = propertyReaderFactory.of(gateway);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new GatewayExecutionSet(
                new DefaultRoute(p.getDefaultRoute())
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        return Result.success(BpmnNode.of(node, p));
    }

    private Result<BpmnNode> parallelGateway(Gateway gateway) {
        Node<View<ParallelGateway>, Edge> node = factoryManager.newNode(gateway.getId(), ParallelGateway.class);
        GatewayPropertyReader p = propertyReaderFactory.of(gateway);

        node.getContent().setBounds(p.getBounds());
        ParallelGateway definition = node.getContent().getDefinition();

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        return Result.success(BpmnNode.of(node, p));
    }

    private Result<BpmnNode> eventGateway(Gateway eventGateway) {
        Node<View<EventGateway>, Edge> node = factoryManager.newNode(eventGateway.getId(), EventGateway.class);
        GatewayPropertyReader p = propertyReaderFactory.of(eventGateway);

        node.getContent().setBounds(p.getBounds());
        EventGateway definition = node.getContent().getDefinition();

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setDimensionsSet(p.getCircleDimensionSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());
        definition.setAdvancedData(new AdvancedData(p.getMetaDataAttributes()));

        return Result.success(BpmnNode.of(node, p));
    }
}
