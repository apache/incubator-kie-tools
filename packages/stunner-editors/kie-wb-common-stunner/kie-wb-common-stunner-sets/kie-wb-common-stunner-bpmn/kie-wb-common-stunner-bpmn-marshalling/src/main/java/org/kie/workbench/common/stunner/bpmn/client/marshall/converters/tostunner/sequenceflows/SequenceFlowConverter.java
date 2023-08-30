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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.sequenceflows;

import java.util.Map;

import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessageKeys;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnEdge;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.EdgeConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.SequenceFlowPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.property.common.ConditionExpression;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.SequenceFlowExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class SequenceFlowConverter implements EdgeConverter<org.eclipse.bpmn2.SequenceFlow> {

    private final PropertyReaderFactory propertyReaderFactory;
    private TypedFactoryManager factoryManager;

    public SequenceFlowConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory) {
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    @Override
    public Result<BpmnEdge> convertEdge(org.eclipse.bpmn2.SequenceFlow seq, Map<String, BpmnNode> nodes) {
        Edge<View<SequenceFlow>, Node> edge = factoryManager.newEdge(seq.getId(), SequenceFlow.class);

        SequenceFlow definition = edge.getContent().getDefinition();
        SequenceFlowPropertyReader p = propertyReaderFactory.of(seq);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(new SequenceFlowExecutionSet(
                new Priority(p.getPriority()),
                new ConditionExpression(p.getConditionExpression())
        ));

        return result(nodes, edge, p, "Sequence Flow ignored from " + p.getSourceId() + " to " + p.getTargetId(),
                      MarshallingMessageKeys.sequenceFlowIgnored);
    }
}
