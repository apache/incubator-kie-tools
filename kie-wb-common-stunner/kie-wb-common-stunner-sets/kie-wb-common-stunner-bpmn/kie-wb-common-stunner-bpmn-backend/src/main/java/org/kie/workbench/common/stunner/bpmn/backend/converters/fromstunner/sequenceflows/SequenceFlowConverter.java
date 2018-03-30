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

import org.eclipse.bpmn2.FormalExpression;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.ElementContainer;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.SequenceFlowPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.SequenceFlowExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts.asCData;

public class SequenceFlowConverter {

    private static final Logger LOG = LoggerFactory.getLogger(SequenceFlowConverter.class);

    private final PropertyWriterFactory propertyWriterFactory;

    public SequenceFlowConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public Result<SequenceFlowPropertyWriter> toFlowElement(Edge<?, ?> edge, ElementContainer process) {
        ViewConnector<SequenceFlow> content = (ViewConnector<SequenceFlow>) edge.getContent();
        SequenceFlow definition = content.getDefinition();
        org.eclipse.bpmn2.SequenceFlow seq = bpmn2.createSequenceFlow();
        SequenceFlowPropertyWriter p = propertyWriterFactory.of(seq);

        seq.setId(edge.getUUID());

        BasePropertyWriter pSrc = process.getChildElement(edge.getSourceNode().getUUID());
        BasePropertyWriter pTgt = process.getChildElement(edge.getTargetNode().getUUID());

        if (pSrc == null || pTgt == null) {
            String msg = String.format("pSrc = %s, pTgt = %s", pSrc, pTgt);
            LOG.warn(msg);
            return Result.failure(msg);
        }

        p.setSource(pSrc);
        p.setTarget(pTgt);

        seq.setId(edge.getUUID());
        seq.setName(definition.getGeneral().getName().getValue());

        p.setConnection(content);

        SequenceFlowExecutionSet executionSet = definition.getExecutionSet();
        ScriptTypeValue scriptTypeValue = executionSet.getConditionExpression().getValue();
        String language = scriptTypeValue.getLanguage();
        String script = scriptTypeValue.getScript();

        if (script != null) {
            FormalExpression formalExpression = bpmn2.createFormalExpression();
            String uri = Scripts.scriptLanguageToUri(language);
            formalExpression.setLanguage(uri);
            formalExpression.setBody(asCData(script));
            seq.setConditionExpression(formalExpression);
        }

        process.addChildElement(p);
        process.addChildEdge(p.getEdge());
        return Result.of(p);
    }
}
