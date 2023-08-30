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

import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.commons.Pair;

public class ProcessMessageRefProvider
        extends AbstractProcessFilteredNodeProvider {

    private static final Predicate<Node> startMessageEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof StartMessageEvent;

    private static final Predicate<Node> intermediateMessageCatchingEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof IntermediateMessageEventCatching;
    private static final Predicate<Node> intermediateMessageThrowingEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof IntermediateMessageEventThrowing;

    private static final Predicate<Node> endMessageEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof EndMessageEvent;

    private static final Predicate<Node> allMessageEventsFilter = startMessageEventsFilter
            .or(intermediateMessageCatchingEventsFilter)
            .or(intermediateMessageThrowingEventsFilter)
            .or(endMessageEventsFilter);

    @Inject
    public ProcessMessageRefProvider(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public Predicate<Node> getFilter() {
        return allMessageEventsFilter;
    }

    @Override
    public Function<Node, Pair<Object, String>> getMapper() {
        return node -> {
            MessageRef messageRef = null;
            if (startMessageEventsFilter.test(node)) {
                messageRef = ((StartMessageEvent) ((View) node.getContent()).getDefinition()).getExecutionSet().getMessageRef();
            } else if (intermediateMessageCatchingEventsFilter.test(node)) {
                messageRef = ((IntermediateMessageEventCatching) ((View) node.getContent()).getDefinition()).getExecutionSet().getMessageRef();
            } else if (intermediateMessageThrowingEventsFilter.test(node)) {
                messageRef = ((IntermediateMessageEventThrowing) ((View) node.getContent()).getDefinition()).getExecutionSet().getMessageRef();
            } else if (endMessageEventsFilter.test(node)) {
                messageRef = ((EndMessageEvent) ((View) node.getContent()).getDefinition()).getExecutionSet().getMessageRef();
            }

            if (messageRef != null && messageRef.getValue() != null && !messageRef.getValue().isEmpty()) {
                return new Pair<>(messageRef.getValue(),
                                  messageRef.getValue(),
                                  Pair.PairEqualsMode.K1);
            } else {
                return null;
            }
        };
    }
}