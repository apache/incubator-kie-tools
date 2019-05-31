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
package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner;

import org.kie.workbench.common.stunner.bpmn.backend.converters.NodeMatch;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.BaseReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class FlowElementConverter {

    private final ConverterFactory converterFactory;

    public FlowElementConverter(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    public Result<PropertyWriter> toFlowElement(Node<View<? extends BPMNViewDefinition>, ?> node) {
        return NodeMatch.fromNode(BPMNViewDefinition.class, PropertyWriter.class)
                .when(BaseStartEvent.class, converterFactory.startEventConverter()::toFlowElement)
                .when(BaseCatchingIntermediateEvent.class, converterFactory.intermediateCatchEventConverter()::toFlowElement)
                .when(BaseThrowingIntermediateEvent.class, converterFactory.intermediateThrowEventConverter()::toFlowElement)
                .when(BaseEndEvent.class, converterFactory.endEventConverter()::toFlowElement)
                .when(BaseTask.class, converterFactory.taskConverter()::toFlowElement)
                .when(BaseGateway.class, converterFactory.gatewayConverter()::toFlowElement)
                .when(BaseReusableSubprocess.class, converterFactory.reusableSubprocessConverter()::toFlowElement)
                .ignore(BaseSubprocess.class)
                .ignore(Lane.class)
                .apply(node);
    }
}
