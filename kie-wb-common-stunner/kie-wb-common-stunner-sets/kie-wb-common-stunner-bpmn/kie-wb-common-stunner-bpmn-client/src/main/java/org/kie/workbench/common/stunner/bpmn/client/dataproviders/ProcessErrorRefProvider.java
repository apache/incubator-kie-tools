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
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class ProcessErrorRefProvider
        implements SelectorDataProvider {

    private SessionManager sessionManager;

    @Inject
    public ProcessErrorRefProvider(final SessionManager sessionManager) {
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
        findCurrentErrorRefs().forEach(error -> {
            values.put(error,
                       error);
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

    private Collection<String> findCurrentErrorRefs() {
        Diagram diagram = sessionManager.getCurrentSession().getCanvasHandler().getDiagram();
        Predicate<Node> endErrorEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof EndErrorEvent;
        Predicate<Node> intermediateErrorEventsFilter = node -> ((View) node.getContent()).getDefinition() instanceof IntermediateErrorEventCatching;
        Predicate<Node> errorNodesFilter = endErrorEventsFilter.or(intermediateErrorEventsFilter);

        Function<Node, ErrorRef> errorRefMapper = node -> {
            if (((View) node.getContent()).getDefinition() instanceof EndErrorEvent) {
                return ((EndErrorEvent) ((View) node.getContent()).getDefinition()).getExecutionSet().getErrorRef();
            } else if (((View) node.getContent()).getDefinition() instanceof IntermediateErrorEventCatching) {
                return ((IntermediateErrorEventCatching) ((View) node.getContent()).getDefinition()).getExecutionSet().getErrorRef();
            }
            return null;
        };

        @SuppressWarnings("unchecked")
        Iterable<Node> it = diagram.getGraph().nodes();
        return StreamSupport.stream(it.spliterator(),
                                    false)
                .filter(errorNodesFilter)
                .map(errorRefMapper)
                .filter(errorRef -> errorRef != null && errorRef.getValue() != null && !errorRef.getValue().isEmpty())
                .map(ErrorRef::getValue)
                .collect(Collectors.toSet());
    }
}