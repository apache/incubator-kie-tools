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

package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.kie.workbench.common.stunner.bpmn.backend.converters.activities.CallActivityConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.events.BoundaryEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.events.EndEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.events.IntermediateCatchEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.events.IntermediateThrowEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.events.StartEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.gateways.GatewayConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.processes.SubProcessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.sequenceflows.SequenceFlowConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.TaskConverter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class FlowElementConverter {

    private final StartEventConverter startEventConverter;
    private final TaskConverter taskConverter;
    private final SequenceFlowConverter sequenceFlowConverter;
    private final GatewayConverter gatewayConverter;
    private final BoundaryEventConverter boundaryEventConverter;
    private final EndEventConverter endEventConverter;
    private final IntermediateThrowEventConverter intermediateThrowEventConverter;
    private final IntermediateCatchEventConverter intermediateCatchEventConverter;
    private final CallActivityConverter callActivityConverter;
    private final SubProcessConverter subProcessConverter;

    public FlowElementConverter(TypedFactoryManager factoryManager, PropertyReaderFactory propertyReaderFactory, GraphBuildingContext context) {
        this.startEventConverter = new StartEventConverter(factoryManager, propertyReaderFactory);
        this.endEventConverter = new EndEventConverter(factoryManager, propertyReaderFactory);
        this.intermediateThrowEventConverter = new IntermediateThrowEventConverter(factoryManager, propertyReaderFactory);
        this.intermediateCatchEventConverter = new IntermediateCatchEventConverter(factoryManager, propertyReaderFactory);
        this.taskConverter = new TaskConverter(factoryManager, propertyReaderFactory);
        this.sequenceFlowConverter = new SequenceFlowConverter(factoryManager, propertyReaderFactory, context);
        this.gatewayConverter = new GatewayConverter(factoryManager, propertyReaderFactory);
        this.boundaryEventConverter = new BoundaryEventConverter(factoryManager, propertyReaderFactory, context);
        this.callActivityConverter = new CallActivityConverter(factoryManager, propertyReaderFactory);
        this.subProcessConverter = new SubProcessConverter(factoryManager, propertyReaderFactory, this, context);
    }

    public Result<Node<? extends View<? extends BPMNViewDefinition>, ?>> convertNode(FlowElement flowElement) {
        return Match.ofNode(FlowElement.class, BPMNViewDefinition.class)
                .when(StartEvent.class, startEventConverter::convert)
                .when(EndEvent.class, endEventConverter::convert)
                .when(BoundaryEvent.class, boundaryEventConverter::convert)
                .when(IntermediateCatchEvent.class, intermediateCatchEventConverter::convert)
                .when(IntermediateThrowEvent.class, intermediateThrowEventConverter::convert)
                .when(Task.class, taskConverter::convert)
                .when(Gateway.class, gatewayConverter::convert)
                .when(SubProcess.class, subProcessConverter::convert)
                .when(CallActivity.class, callActivityConverter::convert)
                .ignore(SequenceFlow.class)
                .apply(flowElement);
    }

    public Result<Edge<? extends View<? extends BPMNViewDefinition>, ?>> convertEdge(FlowElement flowElement) {
        return Match.ofEdge(FlowElement.class, BPMNViewDefinition.class)
                .when(SequenceFlow.class, sequenceFlowConverter::convert)
                .apply(flowElement);
    }

    public void convertDockedNodes(FlowElement flowElement) {
        VoidMatch.ofEdge(FlowElement.class)
                .when(BoundaryEvent.class, boundaryEventConverter::convertEdge)
                .apply(flowElement);
    }
}
