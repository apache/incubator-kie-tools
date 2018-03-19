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
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnEdge;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;

/**
 * A boundary event is also a sort-of-edge.
 * This converter generates the "edge" part of a boundary event.
 * The node part is converted by the {@link IntermediateCatchEventConverter}
 */
public class BoundaryEventConverter {

    public BpmnEdge convertEdge(BoundaryEvent e, Map<String, BpmnNode> nodes) {
        return BpmnEdge.docked(nodes.get(e.getAttachedToRef().getId()), nodes.get(e.getId()));
    }
}
