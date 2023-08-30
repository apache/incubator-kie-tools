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


package org.kie.workbench.common.stunner.bpmn.client.forms.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.forms.client.formFilters.StunnerFormElementFilterProvider;

public class CatchingIntermediateEventFilterProvider implements StunnerFormElementFilterProvider {

    private static final ArrayList cancelActivityEnabledClasses = new ArrayList(
            Arrays.asList(IntermediateSignalEventCatching.class,
                          IntermediateTimerEvent.class,
                          IntermediateConditionalEvent.class,
                          IntermediateMessageEventCatching.class,
                          IntermediateEscalationEvent.class));

    private final SessionManager sessionManager;
    private final Supplier<Class<?>> definitionSupplier;

    public CatchingIntermediateEventFilterProvider(final SessionManager sessionManager,
                                                   final Class<?> intermediateEventClass) {
        this.sessionManager = sessionManager;
        this.definitionSupplier = () -> intermediateEventClass;
    }

    @Override
    public Class<?> getDefinitionType() {
        return definitionSupplier.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<FormElementFilter> provideFilters(final String uuid,
                                                        final Object definition) {
        Predicate predicate = o -> isBoundaryEvent(uuid);
        predicate = predicate.and(o -> isCancelActivityEnabled(uuid));

        final FormElementFilter isInterruptingFilter = new FormElementFilter("executionSet.cancelActivity",
                                                                             predicate);
        return Collections.singletonList(isInterruptingFilter);
    }

    @SuppressWarnings("unchecked")
    private boolean isBoundaryEvent(final String uuid) {
        final AbstractCanvasHandler canvasHandler = (AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler();
        final Node node = canvasHandler.getGraphIndex().getNode(uuid);
        return GraphUtils.isDockedNode(node);
    }

    @SuppressWarnings("unchecked")
    private boolean isCancelActivityEnabled(final String uuid) {
        AbstractCanvasHandler canvasHandler = (AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler();
        Node<View<?>, Edge> node = canvasHandler.getGraphIndex().getNode(uuid);

        if (null != node &&
                null != node.getContent() &&
                node.getContent() instanceof View) {
            Class intermediateEventClass = ((Node<View<?>, Edge>) node)
                    .getContent()
                    .getDefinition()
                    .getClass();

            return cancelActivityEnabledClasses.contains(intermediateEventClass);
        }

        return false;
    }
}
