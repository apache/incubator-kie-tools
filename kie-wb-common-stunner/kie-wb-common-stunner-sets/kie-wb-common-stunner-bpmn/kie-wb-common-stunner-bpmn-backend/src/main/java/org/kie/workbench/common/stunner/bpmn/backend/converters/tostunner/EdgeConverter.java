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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner;

import java.util.Map;

import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.associations.AssociationConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.events.BoundaryEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.sequenceflows.SequenceFlowConverter;

public class EdgeConverter {

    private final SequenceFlowConverter sequenceFlowConverter;
    private final BoundaryEventConverter boundaryEventConverter;
    private final AssociationConverter associationConverter;

    public EdgeConverter(
            TypedFactoryManager factoryManager,
            PropertyReaderFactory propertyReaderFactory) {
        this.sequenceFlowConverter = new SequenceFlowConverter(factoryManager, propertyReaderFactory);
        this.boundaryEventConverter = new BoundaryEventConverter();
        this.associationConverter = new AssociationConverter(factoryManager, propertyReaderFactory);
    }

    public Result<BpmnEdge> convertEdge(BaseElement baseElement, Map<String, BpmnNode> nodes) {
        return Match.of(BaseElement.class, BpmnEdge.class)
                .when(SequenceFlow.class, e -> sequenceFlowConverter.convertEdge(e, nodes))
                .when(BoundaryEvent.class, e -> boundaryEventConverter.convertEdge(e, nodes))
                .when(Association.class, e -> associationConverter.convertEdge(e, nodes))
                .apply(baseElement);
    }
}
