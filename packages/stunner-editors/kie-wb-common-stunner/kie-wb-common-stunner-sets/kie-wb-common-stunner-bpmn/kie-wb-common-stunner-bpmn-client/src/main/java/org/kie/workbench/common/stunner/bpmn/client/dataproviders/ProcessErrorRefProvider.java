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

import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.commons.Pair;

public class ProcessErrorRefProvider
        extends AbstractProcessFilteredNodeProvider {

    private static final Predicate<Node> startErrorEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof StartErrorEvent;

    private static final Predicate<Node> intermediateErrorEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof IntermediateErrorEventCatching;

    private static final Predicate<Node> endErrorEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof EndErrorEvent;

    private static final Predicate<Node> allErrorEventsFilter = startErrorEventsFilter
            .or(intermediateErrorEventsFilter)
            .or(endErrorEventsFilter);

    @Inject
    public ProcessErrorRefProvider(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public Predicate<Node> getFilter() {
        return allErrorEventsFilter;
    }

    @Override
    public Function<Node, Pair<Object, String>> getMapper() {
        return node -> {
            ErrorRef errorRef = null;
            if (startErrorEventsFilter.test(node)) {
                errorRef = ((StartErrorEvent) ((View) node.getContent()).getDefinition()).getExecutionSet().getErrorRef();
            } else if (intermediateErrorEventsFilter.test(node)) {
                errorRef = ((IntermediateErrorEventCatching) ((View) node.getContent()).getDefinition()).getExecutionSet().getErrorRef();
            } else if (endErrorEventsFilter.test(node)) {
                errorRef = ((EndErrorEvent) ((View) node.getContent()).getDefinition()).getExecutionSet().getErrorRef();
            }

            if (errorRef != null && errorRef.getValue() != null && !errorRef.getValue().isEmpty()) {
                return new Pair<>(errorRef.getValue(),
                                  errorRef.getValue(),
                                  Pair.PairEqualsMode.K1);
            } else {
                return null;
            }
        };
    }
}