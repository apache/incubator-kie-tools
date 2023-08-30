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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.sequenceflows;

import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.ElementContainer;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.SequenceFlowPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.SequenceFlowExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class SequenceFlowConverter {

    private final PropertyWriterFactory propertyWriterFactory;

    public SequenceFlowConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public Result<BasePropertyWriter> toFlowElement(Edge<?, ?> edge, ElementContainer process) {
        ViewConnector<SequenceFlow> connector = (ViewConnector<SequenceFlow>) edge.getContent();
        SequenceFlow definition = connector.getDefinition();
        org.eclipse.bpmn2.SequenceFlow seq = bpmn2.createSequenceFlow();
        SequenceFlowPropertyWriter p = propertyWriterFactory.of(seq);

        seq.setId(edge.getUUID());

        BasePropertyWriter pSrc = edge.getSourceNode() != null ? process.getChildElement(edge.getSourceNode().getUUID()) : null;
        BasePropertyWriter pTgt = edge.getTargetNode() != null ? process.getChildElement(edge.getTargetNode().getUUID()) : null;

        if (pSrc == null) {
            return Result.failure("Source connection is not set for sequence flow (edge) id = " + edge.getUUID());
        }

        if (pTgt == null) {
            return Result.failure("Target connection is not set for sequence flow (edge) id = " + edge.getUUID());
        }

        p.setSource(pSrc);
        p.setTarget(pTgt);

        p.setConnection(connector);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        SequenceFlowExecutionSet executionSet = definition.getExecutionSet();
        p.setPriority(executionSet.getPriority().getValue());
        p.setConditionExpression(executionSet.getConditionExpression().getValue());

        return Result.of(p);
    }
}
