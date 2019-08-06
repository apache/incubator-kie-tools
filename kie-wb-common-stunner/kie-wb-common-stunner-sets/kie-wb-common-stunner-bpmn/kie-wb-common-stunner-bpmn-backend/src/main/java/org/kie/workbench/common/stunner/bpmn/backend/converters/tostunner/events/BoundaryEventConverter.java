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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.events;

import java.util.Map;

import org.eclipse.bpmn2.BoundaryEvent;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnEdge;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.EdgeConverter;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessage;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessageKeys;
import org.kie.workbench.common.stunner.core.validation.Violation;

/**
 * A boundary event is also a sort-of-edge.
 * This converter generates the "edge" part of a boundary event.
 * The node part is converted by the {@link IntermediateCatchEventConverter}
 */
public class BoundaryEventConverter implements EdgeConverter<BoundaryEvent> {

    @Override
    public Result<BpmnEdge> convertEdge(BoundaryEvent event, Map<String, BpmnNode> nodes) {
        String parentId = event.getAttachedToRef().getId();
        String childId = event.getId();
        return valid(nodes, parentId, childId)
                ? Result.success(BpmnEdge.docked(nodes.get(parentId), nodes.get(childId)))
                : Result.ignored("Boundary ignored",
                                 MarshallingMessage.builder()
                                         .message("Boundary ignored")
                                         .messageKey(MarshallingMessageKeys.boundaryIgnored)
                                         .messageArguments(childId, parentId)
                                         .type(Violation.Type.WARNING)
                                         .build());
    }
}
