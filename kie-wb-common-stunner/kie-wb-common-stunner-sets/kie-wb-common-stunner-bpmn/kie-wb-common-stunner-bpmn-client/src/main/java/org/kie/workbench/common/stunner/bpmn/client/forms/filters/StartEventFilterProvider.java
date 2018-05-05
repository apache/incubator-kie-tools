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
package org.kie.workbench.common.stunner.bpmn.client.forms.filters;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
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
    public Collection<FormElementFilter> provideFilters(String elementUUID, Element<? extends Definition<?>> element, Object definition) {
        Predicate predicate = o -> isParentAnEventSubProcess(elementUUID);
        FormElementFilter isInterruptingFilter = new FormElementFilter("executionSet.isInterrupting",
                                                                       predicate);
        return Arrays.asList(isInterruptingFilter);
    }

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
}
