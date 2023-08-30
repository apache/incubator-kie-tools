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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TextAnnotation;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.BPMNElementDecorators;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Match;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;

public class FlowElementConverter extends AbstractConverter {

    private final BaseConverterFactory converterFactory;

    public FlowElementConverter(BaseConverterFactory converterFactory) {
        super(converterFactory.getDefinitionResolver().getMode());
        this.converterFactory = converterFactory;
    }

    public Result<BpmnNode> convertNode(FlowElement flowElement) {
        return (Result<BpmnNode>) Match.<FlowElement, Result<BpmnNode>>of()
                .<StartEvent>when(e -> e instanceof StartEvent,
                                  converterFactory.startEventConverter()::convert)
                .<EndEvent>when(e -> e instanceof EndEvent,
                                converterFactory.endEventConverter()::convert)
                .<BoundaryEvent>when(e -> e instanceof BoundaryEvent,
                                     converterFactory.intermediateCatchEventConverter()::convertBoundaryEvent)
                .<IntermediateCatchEvent>when(e -> e instanceof IntermediateCatchEvent,
                                              converterFactory.intermediateCatchEventConverter()::convert)
                .<IntermediateThrowEvent>when(e -> e instanceof IntermediateThrowEvent,
                                              converterFactory.intermediateThrowEventConverter()::convert)
                .<Task>when(e -> e instanceof Task,
                            converterFactory.taskConverter()::convert)
                .<Gateway>when(e -> e instanceof Gateway,
                               element -> converterFactory.gatewayConverter().convert((Gateway) element))
                .<SubProcess>when(e -> e instanceof SubProcess,
                                  element -> converterFactory.subProcessConverter().convertSubProcess((SubProcess) element))
                .<CallActivity>when(e -> e instanceof CallActivity,
                                    element -> converterFactory.callActivityConverter().convert((CallActivity) element))
                .<TextAnnotation>when(e -> e instanceof TextAnnotation,
                                      element -> converterFactory.textAnnotationConverter().convert((TextAnnotation) element))
                .<DataObjectReference>when(e -> e instanceof DataObjectReference,
                                           element -> converterFactory.dataObjectConverter().convert((DataObjectReference) element))
                .ignore(e -> e instanceof DataObject, DataObject.class)
                .defaultValue(Result.ignored("FlowElement not found", getNotFoundMessage(flowElement)))
                .inputDecorator(BPMNElementDecorators.flowElementDecorator())
                .outputDecorator(BPMNElementDecorators.resultBpmnDecorator())
                .mode(getMode())
                .apply(flowElement)
                .value();
    }
}
