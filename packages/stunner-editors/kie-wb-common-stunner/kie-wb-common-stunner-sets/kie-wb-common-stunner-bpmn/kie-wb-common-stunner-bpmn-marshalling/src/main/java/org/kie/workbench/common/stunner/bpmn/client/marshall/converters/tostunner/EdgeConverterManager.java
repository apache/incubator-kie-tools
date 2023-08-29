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

import java.util.Map;

import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingRequest.Mode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Match;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.associations.AssociationConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.events.BoundaryEventConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.sequenceflows.SequenceFlowConverter;

public class EdgeConverterManager extends AbstractConverter {

    private final SequenceFlowConverter sequenceFlowConverter;
    private final BoundaryEventConverter boundaryEventConverter;
    private final AssociationConverter associationConverter;

    public EdgeConverterManager(
            TypedFactoryManager factoryManager,
            PropertyReaderFactory propertyReaderFactory,
            Mode mode) {
        super(mode);
        this.sequenceFlowConverter = new SequenceFlowConverter(factoryManager, propertyReaderFactory);
        this.boundaryEventConverter = new BoundaryEventConverter();
        this.associationConverter = new AssociationConverter(factoryManager, propertyReaderFactory);
    }

    public Result<BpmnEdge> convertEdge(BaseElement baseElement, Map<String, BpmnNode> nodes) {
        return Match.<BaseElement, Result<BpmnEdge>>of()
                .<SequenceFlow>when(e -> e instanceof SequenceFlow, e -> sequenceFlowConverter.convertEdge(e, nodes))
                .<BoundaryEvent>when(e -> e instanceof BoundaryEvent, e -> boundaryEventConverter.convertEdge(e, nodes))
                .<Association>when(e -> e instanceof Association, e -> associationConverter.convertEdge(e, nodes))
                .defaultValue(Result.ignored("Not an Edge element", getNotFoundMessage(baseElement)))
                .apply(baseElement)
                .value();
    }
}
