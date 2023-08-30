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
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.StartConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.forms.client.formFilters.StunnerFormElementFilterProvider;

public class StartEventFilterProvider implements StunnerFormElementFilterProvider {

    private final SessionManager sessionManager;
    private final Supplier<Class<?>> definitionTypeSupplier;

    public StartEventFilterProvider(final SessionManager sessionManager,
                                    final Class<?> startEventClass) {
        this.sessionManager = sessionManager;
        this.definitionTypeSupplier = () -> startEventClass;
    }

    @Override
    public Class<?> getDefinitionType() {
        return definitionTypeSupplier.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<FormElementFilter> provideFilters(String elementUUID, Object definition) {
        Predicate parentPredicate = o -> isParentAnEventSubProcess(elementUUID);
        Predicate isInterruptingPredicate = o -> isInterruptingEnabled(elementUUID);
        Predicate predicate = parentPredicate.and(isInterruptingPredicate);

        FormElementFilter isInterruptingFilter = new FormElementFilter("executionSet.isInterrupting",
                                                                       parentPredicate.and(predicate));
        return Collections.singletonList(isInterruptingFilter);
    }

    @SuppressWarnings("unchecked")
    private boolean isParentAnEventSubProcess(final String uuid) {
        AbstractCanvasHandler canvasHandler = (AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler();
        Node node = canvasHandler.getGraphIndex().getNode(uuid);
        Element<?> parent = GraphUtils.getParent(node);
        if (null != parent &&
                null != parent.asNode() &&
                parent.asNode().getContent() instanceof View) {

            return ((Node<View<?>, Edge>) parent.asNode())
                    .getContent()
                    .getDefinition()
                    .getClass()
                    .equals(EventSubprocess.class);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private boolean isInterruptingEnabled(final String uuid) {
        ArrayList<Class> isInterruptingEnabledClasses = new ArrayList();
        isInterruptingEnabledClasses.add(StartSignalEvent.class);
        isInterruptingEnabledClasses.add(StartTimerEvent.class);
        isInterruptingEnabledClasses.add(StartConditionalEvent.class);
        isInterruptingEnabledClasses.add(StartEscalationEvent.class);
        isInterruptingEnabledClasses.add(StartMessageEvent.class);

        AbstractCanvasHandler canvasHandler = (AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler();
        Node<View<?>, Edge> node = canvasHandler.getGraphIndex().getNode(uuid);

        Class startEventClass = null;
        if (null != node &&
            null != node.getContent() &&
            node.getContent() instanceof View) {
            startEventClass = ((Node<View<?>, Edge>) node)
                              .getContent()
                              .getDefinition()
                              .getClass();
        }

        return isInterruptingEnabledClasses.contains(startEventClass);
    }
}
