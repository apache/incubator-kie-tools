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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.gateways;

import org.eclipse.bpmn2.GatewayDirection;
import org.kie.workbench.common.stunner.bpmn.backend.converters.NodeMatch;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.GatewayPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.EventGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.GatewayExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class GatewayConverter {

    private final PropertyWriterFactory propertyWriterFactory;

    public GatewayConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public PropertyWriter toFlowElement(Node<View<BaseGateway>, ?> node) {
        return NodeMatch.fromNode(BaseGateway.class, PropertyWriter.class)
                .when(ParallelGateway.class, this::parallel)
                .when(ExclusiveGateway.class, this::exclusive)
                .when(InclusiveGateway.class, this::inclusive)
                .when(EventGateway.class, this::event)
                .apply(node).value();
    }

    private PropertyWriter inclusive(Node<View<InclusiveGateway>, ?> n) {
        GatewayPropertyWriter p = propertyWriterFactory.of(bpmn2.createInclusiveGateway());
        p.setId(n.getUUID());

        InclusiveGateway definition = n.getContent().getDefinition();

        p.setGatewayDirection(n);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        GatewayExecutionSet executionSet = definition.getExecutionSet();
        p.setDefaultRoute(executionSet.getDefaultRoute().getValue());

        p.setAbsoluteBounds(n);

        return p;
    }

    private PropertyWriter exclusive(Node<View<ExclusiveGateway>, ?> n) {
        GatewayPropertyWriter p = propertyWriterFactory.of(bpmn2.createExclusiveGateway());
        p.setId(n.getUUID());

        ExclusiveGateway definition = n.getContent().getDefinition();

        p.setGatewayDirection(n);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        GatewayExecutionSet executionSet = definition.getExecutionSet();
        p.setDefaultRoute(executionSet.getDefaultRoute().getValue());

        p.setAbsoluteBounds(n);

        return p;
    }

    private PropertyWriter parallel(Node<View<ParallelGateway>, ?> n) {
        GatewayPropertyWriter p = propertyWriterFactory.of(bpmn2.createParallelGateway());
        p.setId(n.getUUID());

        ParallelGateway definition = n.getContent().getDefinition();

        p.setGatewayDirection(n);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        p.setAbsoluteBounds(n);

        return p;
    }

    private PropertyWriter event(Node<View<EventGateway>, ?> n) {
        GatewayPropertyWriter p = propertyWriterFactory.of(bpmn2.createEventBasedGateway());
        p.setId(n.getUUID());
        p.setGatewayDirection(GatewayDirection.DIVERGING);

        EventGateway definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        p.setAbsoluteBounds(n);

        return p;
    }
}
