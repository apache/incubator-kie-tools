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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.List;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.util.PropertyWriterUtils;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.DiscreteConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts.asCData;

public class SequenceFlowPropertyWriter extends PropertyWriter {

    private final SequenceFlow sequenceFlow;
    private BasePropertyWriter source;
    private BasePropertyWriter target;
    private BPMNEdge bpmnEdge;

    public SequenceFlowPropertyWriter(SequenceFlow sequenceFlow, VariableScope variableScope) {
        super(sequenceFlow, variableScope);
        this.sequenceFlow = sequenceFlow;
    }

    public void setAutoConnectionSource(Connection connection) {
        DiscreteConnection c = (DiscreteConnection) connection;
        CustomElement.autoConnectionSource.of(sequenceFlow).set(c.isAuto());
    }

    public void setAutoConnectionTarget(Connection connection) {
        DiscreteConnection c = (DiscreteConnection) connection;
        CustomElement.autoConnectionTarget.of(sequenceFlow).set(c.isAuto());
    }

    public void setConnection(ViewConnector<? extends BPMNViewDefinition> connector) {
        Connection sourceConnection = connector.getSourceConnection().get();
        Connection targetConnection = connector.getTargetConnection().get();

        setAutoConnectionSource(sourceConnection);
        setAutoConnectionTarget(targetConnection);

        List<ControlPoint> controlPoints = connector.getControlPoints();
        bpmnEdge = PropertyWriterUtils.createBPMNEdge(source, target, sourceConnection, controlPoints, targetConnection);
        bpmnEdge.setBpmnElement(sequenceFlow);
    }

    public void setSource(BasePropertyWriter pSrc) {
        this.source = pSrc;
        sequenceFlow.setSourceRef((FlowNode) pSrc.getElement());
        pSrc.setTarget(this);
    }

    public void setTarget(BasePropertyWriter pTgt) {
        this.target = pTgt;
        sequenceFlow.setTargetRef((FlowNode) pTgt.getElement());
        pTgt.setSource(this);
    }

    public BPMNEdge getEdge() {
        return bpmnEdge;
    }

    public void setPriority(String value) {
        if (value != null && !value.isEmpty()) {
            CustomAttribute.priority.of(sequenceFlow).set(value);
        }
    }

    public void setConditionExpression(ScriptTypeValue scriptTypeValue) {
        String language = scriptTypeValue.getLanguage();
        String script = scriptTypeValue.getScript();

        if (script != null && !script.isEmpty()) {
            FormalExpression formalExpression = bpmn2.createFormalExpression();
            String uri = Scripts.scriptLanguageToUri(language);
            formalExpression.setLanguage(uri);
            formalExpression.setBody(asCData(script));
            sequenceFlow.setConditionExpression(formalExpression);
        }
    }
}
