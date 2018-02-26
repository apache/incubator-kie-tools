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

package org.kie.workbench.common.stunner.bpmn.backend.converters.sequenceflows;

import org.kie.workbench.common.stunner.bpmn.backend.converters.GraphBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.SequenceFlowPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.SequenceFlowExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class SequenceFlowConverter {

    private TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;
    private final GraphBuildingContext context;

    public SequenceFlowConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory, GraphBuildingContext context) {
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
        this.context = context;
    }

    public Edge<? extends View<SequenceFlow>, ?> convert(org.eclipse.bpmn2.SequenceFlow seq) {
        Edge<View<SequenceFlow>, Node> edge = factoryManager.newEdge(seq.getId(), SequenceFlow.class);

        SequenceFlow definition = edge.getContent().getDefinition();
        SequenceFlowPropertyReader p = propertyReaderFactory.of(seq);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(seq.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new SequenceFlowExecutionSet(
                new Priority(p.getPriority()),
                new ConditionExpression(p.getConditionExpression())
        ));

        context.addEdge(
                edge,
                p.getSourceId(),
                p.getSourceConnection(),
                p.getTargetId(),
                p.getTargetConnection());

        return edge;
    }
}
