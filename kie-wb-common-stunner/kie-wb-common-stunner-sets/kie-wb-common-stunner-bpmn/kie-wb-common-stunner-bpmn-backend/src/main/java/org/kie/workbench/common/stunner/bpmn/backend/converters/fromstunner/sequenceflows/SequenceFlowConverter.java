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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.sequenceflows;

import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.ElementContainer;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.SequenceFlowPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.SequenceFlowExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class SequenceFlowConverter {

    private static final Logger LOG = LoggerFactory.getLogger(SequenceFlowConverter.class);

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

        BasePropertyWriter pSrc = process.getChildElement(edge.getSourceNode().getUUID());
        BasePropertyWriter pTgt = process.getChildElement(edge.getTargetNode().getUUID());

        if (pSrc == null || pTgt == null) {
            String msg = String.format("pSrc = %s, pTgt = %s", pSrc, pTgt);
            LOG.debug(msg);
            return Result.failure(msg);
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
