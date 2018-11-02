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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.associations;

import java.util.Map;

import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnEdge;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.AssociationPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.Association;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class AssociationConverter {

    private final PropertyReaderFactory propertyReaderFactory;
    private TypedFactoryManager factoryManager;

    public AssociationConverter(TypedFactoryManager factoryManager,
                                PropertyReaderFactory propertyReaderFactory) {
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    public BpmnEdge convertEdge(org.eclipse.bpmn2.Association association,
                                Map<String, BpmnNode> nodes) {
        Edge<View<Association>, Node> edge = factoryManager.newEdge(association.getId(),
                                                                    Association.class);

        Association definition = edge.getContent().getDefinition();
        AssociationPropertyReader p = propertyReaderFactory.of(association);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(""),
                new Documentation(p.getDocumentation())
        ));

        return BpmnEdge.of(
                edge,
                nodes.get(p.getSourceId()),
                p.getSourceConnection(),
                p.getControlPoints(),
                nodes.get(p.getTargetId()),
                p.getTargetConnection());
    }
}