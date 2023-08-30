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

import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.commons.Pair;

public class ProcessSignalRefProvider
        extends AbstractProcessFilteredNodeProvider {

    private static final Predicate<Node> startSignalEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof StartSignalEvent;

    private static final Predicate<Node> intermediateCatchingSignalEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof IntermediateSignalEventCatching;

    private static final Predicate<Node> intermediateThrowingSignalEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof IntermediateSignalEventThrowing;

    private static final Predicate<Node> endSignalEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof EndSignalEvent;

    private static final Predicate<Node> allSignalEventsFilter = startSignalEventsFilter
            .or(intermediateCatchingSignalEventsFilter)
            .or(intermediateThrowingSignalEventsFilter)
            .or(endSignalEventsFilter);

    @Inject
    public ProcessSignalRefProvider(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public Predicate<Node> getFilter() {
        return allSignalEventsFilter;
    }

    @Override
    public Function<Node, Pair<Object, String>> getMapper() {
        return node -> {
            SignalRef signalRef = null;
            if (startSignalEventsFilter.test(node)) {
                signalRef = ((StartSignalEvent) ((View) node.getContent()).getDefinition()).getExecutionSet().getSignalRef();
            } else if (intermediateCatchingSignalEventsFilter.test(node)) {
                signalRef = ((IntermediateSignalEventCatching) ((View) node.getContent()).getDefinition()).getExecutionSet().getSignalRef();
            } else if (intermediateThrowingSignalEventsFilter.test(node)) {
                signalRef = ((IntermediateSignalEventThrowing) ((View) node.getContent()).getDefinition()).getExecutionSet().getSignalRef();
            } else if (endSignalEventsFilter.test(node)) {
                signalRef = ((EndSignalEvent) ((View) node.getContent()).getDefinition()).getExecutionSet().getSignalRef();
            }

            if (signalRef != null && signalRef.getValue() != null && !signalRef.getValue().isEmpty()) {
                return new Pair<>(signalRef.getValue(),
                                  signalRef.getValue(),
                                  Pair.PairEqualsMode.K1);
            } else {
                return null;
            }
        };
    }
}