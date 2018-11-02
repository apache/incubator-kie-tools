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
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.util.SafeComparator;
import org.uberfire.commons.data.Pair;

public abstract class AbstractProcessFilteredNodeProvider
        implements SelectorDataProvider {

    protected SessionManager sessionManager;

    public AbstractProcessFilteredNodeProvider(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    public abstract Predicate<Node> getFilter();

    public abstract Function<Node, Pair<Object, String>> getMapper();

    public Comparator<Object> getComparator() {
        return SafeComparator.TO_STRING_COMPARATOR;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SelectorData getSelectorData(final FormRenderingContext context) {
        Map<Object, String> values = new TreeMap<>(SafeComparator.of(this::getComparator));
        findElements(getFilter(),
                     getMapper()).forEach(pair -> values.put(pair.getK1(),
                                                             pair.getK2()));
        return new SelectorData(values,
                                null);
    }

    protected Collection<Pair<Object, String>> findElements(final Predicate<Node> filter,
                                                            final Function<Node, Pair<Object, String>> mapper) {
        final Diagram diagram = sessionManager.getCurrentSession().getCanvasHandler().getDiagram();
        @SuppressWarnings("unchecked")
        Iterable<Node> it = diagram.getGraph().nodes();
        return StreamSupport.stream(it.spliterator(),
                                    false)
                .filter(filter)
                .map(mapper)
                .filter(pair -> pair != null)
                .collect(Collectors.toSet());
    }
}