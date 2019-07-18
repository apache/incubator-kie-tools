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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.gateways;

import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.Gateway;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.GatewayPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.EventGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.DefaultRoute;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.GatewayExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class GatewayConverter {

    private final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;

    public GatewayConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory) {
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    public BpmnNode convert(org.eclipse.bpmn2.Gateway gateway) {
        return Match.of(org.eclipse.bpmn2.Gateway.class, BpmnNode.class)
                .when(org.eclipse.bpmn2.ParallelGateway.class, this::parallelGateway)
                .when(org.eclipse.bpmn2.ExclusiveGateway.class, this::exclusiveGateway)
                .when(org.eclipse.bpmn2.InclusiveGateway.class, this::inclusiveGateway)
                .when(org.eclipse.bpmn2.EventBasedGateway.class, this::eventGateway)
                .apply(gateway).value();
    }

    private BpmnNode inclusiveGateway(Gateway gateway) {
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

        return BpmnNode.of(node, p);
    }

    private BpmnNode exclusiveGateway(Gateway gateway) {
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

        return BpmnNode.of(node, p);
    }

    private BpmnNode parallelGateway(Gateway gateway) {
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

        return BpmnNode.of(node, p);
    }

    private BpmnNode eventGateway(EventBasedGateway eventGateway) {
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

        return BpmnNode.of(node, p);
    }
}
