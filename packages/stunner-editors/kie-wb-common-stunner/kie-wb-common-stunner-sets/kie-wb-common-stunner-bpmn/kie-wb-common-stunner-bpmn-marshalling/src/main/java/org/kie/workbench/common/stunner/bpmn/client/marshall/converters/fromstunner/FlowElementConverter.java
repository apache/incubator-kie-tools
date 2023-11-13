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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner;

import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.BaseReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils.cast;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils.resultIgnored;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils.resultNotSupported;

public class FlowElementConverter {

    private final ConverterFactory converterFactory;

    public FlowElementConverter(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    public Result<PropertyWriter> toFlowElement(Node<View<? extends BPMNViewDefinition>, ?> node) {
        BPMNViewDefinition def = node.getContent().getDefinition();
        if (def instanceof BaseStartEvent) {
            return Result.success(converterFactory.startEventConverter().toFlowElement(cast(node)));
        }
        if (def instanceof BaseCatchingIntermediateEvent) {
            return Result.success(converterFactory.intermediateCatchEventConverter().toFlowElement(cast(node)));
        }
        if (def instanceof BaseThrowingIntermediateEvent) {
            return Result.success(converterFactory.intermediateThrowEventConverter().toFlowElement(cast(node)));
        }
        if (def instanceof BaseEndEvent) {
            return Result.success(converterFactory.endEventConverter().toFlowElement(cast(node)));
        }
        if (def instanceof BaseTask) {
            return Result.success(converterFactory.taskConverter().toFlowElement(cast(node)));
        }
        if (def instanceof BaseGateway) {
            return Result.success(converterFactory.gatewayConverter().toFlowElement(cast(node)));
        }
        if (def instanceof BaseReusableSubprocess) {
            return Result.success(converterFactory.reusableSubprocessConverter().toFlowElement(cast(node)));
        }
        if (def instanceof TextAnnotation) {
            return Result.success(converterFactory.textAnnotationConverter().toElement(cast(node)));
        }
        if (def instanceof DataObject) {
            return Result.success(converterFactory.dataObjectConverter().toElement(cast(node)));
        }
        if (def instanceof BaseSubprocess || def instanceof Lane) {
            return resultIgnored(def);
        }
        return resultNotSupported(def);
    }
}
