/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class ProcessMessageRefProvider
        implements SelectorDataProvider {

    private SessionManager sessionManager;

    @Inject
    public ProcessMessageRefProvider(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SelectorData getSelectorData(final FormRenderingContext context) {
        Map<Object, String> values = new TreeMap<>(this::safeCompare);
        findCurrentMessageRefs().forEach(message -> {
            values.put(message,
                       message);
        });
        return new SelectorData(values,
                                null);
    }

    private int safeCompare(Object obj1,
                            Object obj2) {
        if (obj1 == null) {
            return obj2 != null ? -1 : 0;
        } else if (obj2 == null) {
            return 1;
        } else {
            return obj1.toString().compareTo(obj2.toString());
        }
    }

    private Collection<String> findCurrentMessageRefs() {
        Diagram diagram = sessionManager.getCurrentSession().getCanvasHandler().getDiagram();

        Predicate<Node> startMessageEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof StartMessageEvent;
        Predicate<Node> endMessageEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof EndMessageEvent;
        Predicate<Node> intermediateMessageEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof IntermediateMessageEventCatching;
        Predicate<Node> messageNodesFilter = endMessageEventsFilter.or(intermediateMessageEventsFilter).or(startMessageEventsFilter);

        Function<Node, MessageRef> messageRefMapper = node -> {
            if (((View) node.getContent()).getDefinition() instanceof StartMessageEvent) {
                return ((StartMessageEvent) ((View) node.getContent()).getDefinition()).getExecutionSet().getMessageRef();
            } else if (((View) node.getContent()).getDefinition() instanceof EndMessageEvent) {
                return ((EndMessageEvent) ((View) node.getContent()).getDefinition()).getExecutionSet().getMessageRef();
            } else if (((View) node.getContent()).getDefinition() instanceof IntermediateMessageEventCatching) {
                return ((IntermediateMessageEventCatching) ((View) node.getContent()).getDefinition()).getExecutionSet().getMessageRef();
            }
            return null;
        };

        @SuppressWarnings("unchecked")
        Iterable<Node> it = diagram.getGraph().nodes();
        return StreamSupport.stream(it.spliterator(),
                                    false)
                .filter(messageNodesFilter)
                .map(messageRefMapper)
                .filter(messageRef -> messageRef != null && messageRef.getValue() != null && !messageRef.getValue().isEmpty())
                .map(MessageRef::getValue)
                .collect(Collectors.toSet());
    }
}