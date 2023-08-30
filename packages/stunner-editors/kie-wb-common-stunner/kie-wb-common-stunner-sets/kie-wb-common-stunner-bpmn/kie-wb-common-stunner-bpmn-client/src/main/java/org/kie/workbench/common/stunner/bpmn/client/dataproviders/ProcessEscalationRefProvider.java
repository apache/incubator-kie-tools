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


package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.function.Function;
import java.util.function.Predicate;

import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationRef;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.commons.Pair;

public class ProcessEscalationRefProvider extends AbstractProcessFilteredNodeProvider {

    private static final Predicate<Node> startEscalationEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof StartEscalationEvent;

    private static final Predicate<Node> intermediateCatchingEscalationEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof IntermediateEscalationEvent;

    private static final Predicate<Node> intermediateThrowingEscalationEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof IntermediateEscalationEventThrowing;

    private static final Predicate<Node> endEscalationEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof EndEscalationEvent;

    private static final Predicate<Node> allSignalEventsFilter = startEscalationEventsFilter
            .or(intermediateCatchingEscalationEventsFilter)
            .or(intermediateThrowingEscalationEventsFilter)
            .or(endEscalationEventsFilter);

    @Inject
    public ProcessEscalationRefProvider(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public Predicate<Node> getFilter() {
        return allSignalEventsFilter;
    }

    @Override
    public Function<Node, Pair<Object, String>> getMapper() {
        return node -> {
            EscalationRef escalationRef = null;
            if (startEscalationEventsFilter.test(node)) {
                escalationRef = ((StartEscalationEvent) ((View) node.getContent()).getDefinition()).getExecutionSet().getEscalationRef();
            } else if (intermediateCatchingEscalationEventsFilter.test(node)) {
                escalationRef = ((IntermediateEscalationEvent) ((View) node.getContent()).getDefinition()).getExecutionSet().getEscalationRef();
            } else if (intermediateThrowingEscalationEventsFilter.test(node)) {
                escalationRef = ((IntermediateEscalationEventThrowing) ((View) node.getContent()).getDefinition()).getExecutionSet().getEscalationRef();
            } else if (endEscalationEventsFilter.test(node)) {
                escalationRef = ((EndEscalationEvent) ((View) node.getContent()).getDefinition()).getExecutionSet().getEscalationRef();
            }

            if (escalationRef != null && escalationRef.getValue() != null && !escalationRef.getValue().isEmpty()) {
                return new Pair<>(escalationRef.getValue(),
                                  escalationRef.getValue(),
                                  Pair.PairEqualsMode.K1);
            } else {
                return null;
            }
        };
    }
}