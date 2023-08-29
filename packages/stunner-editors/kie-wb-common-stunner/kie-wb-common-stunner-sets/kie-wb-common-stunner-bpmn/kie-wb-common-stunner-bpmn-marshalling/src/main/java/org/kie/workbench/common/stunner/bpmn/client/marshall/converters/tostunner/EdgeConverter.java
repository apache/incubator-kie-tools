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

import org.eclipse.bpmn2.BaseElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessage;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.EdgePropertyReader;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.validation.Violation;

public interface EdgeConverter<T extends BaseElement> {

    Result<BpmnEdge> convertEdge(T element, Map<String, BpmnNode> nodes);

    default Result<BpmnEdge> result(Map<String, BpmnNode> nodes, Edge<? extends View<?>, Node> edge,
                                    EdgePropertyReader p, String errorMessage, String messageKey) {
        return valid(nodes, p.getSourceId(), p.getTargetId()) ?
                Result.success(BpmnEdge.of(
                        edge,
                        nodes.get(p.getSourceId()),
                        p.getSourceConnection(),
                        p.getControlPoints(),
                        nodes.get(p.getTargetId()),
                        p.getTargetConnection(),
                        p))
                : Result.ignored(errorMessage,
                                 MarshallingMessage
                                         .builder()
                                         .message(errorMessage)
                                         .messageKey(messageKey)
                                         .messageArguments(p.getSourceId(),
                                                           p.getTargetId())
                                         .type(Violation.Type.WARNING)
                                         .build());
    }

    default boolean valid(Map<String, BpmnNode> nodes, String parentId, String childId) {
        return nodes.containsKey(parentId) && nodes.containsKey(childId);
    }
}