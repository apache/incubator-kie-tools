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

package org.kie.workbench.common.dmn.client.editors.search;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.widgets.client.search.common.BaseEditorSearchIndex;
import org.kie.workbench.common.widgets.client.search.common.SearchPerformedEvent;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex.SearchContext.BOXED_EXPRESSION;
import static org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex.SearchContext.DATA_TYPES;
import static org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex.SearchContext.GRAPH;

@Dependent
public class DMNEditorSearchIndex extends BaseEditorSearchIndex<DMNSearchableElement> {

    private final DMNGraphSubIndex graphSubIndex;

    private final DMNGridSubIndex gridSubIndex;

    private final DMNDataTypesSubIndex dataTypesSubIndex;

    private final SessionManager sessionManager;

    private Supplier<Boolean> isDataTypesTabActiveSupplier = () -> false;

    private final Event<SearchPerformedEvent> searchPerformedEvent;

    @Inject
    public DMNEditorSearchIndex(final DMNGraphSubIndex graphSubIndex,
                                final DMNGridSubIndex gridSubIndex,
                                final DMNDataTypesSubIndex dataTypesSubIndex,
                                final SessionManager sessionManager,
                                final Event<SearchPerformedEvent> searchPerformedEvent) {
        this.graphSubIndex = graphSubIndex;
        this.gridSubIndex = gridSubIndex;
        this.dataTypesSubIndex = dataTypesSubIndex;
        this.sessionManager = sessionManager;
        this.searchPerformedEvent = searchPerformedEvent;
    }

    @PostConstruct
    public void init() {
        registerSubIndex(graphSubIndex);
        registerSubIndex(gridSubIndex);
        setNoResultsFoundCallback(getNoResultsFoundCallback());
        setSearchClosedCallback(getSearchClosedCallback());
        setSearchPerformedCallback(getSearchPerformedCallback());
    }

    Command getSearchPerformedCallback() {
        return () -> {
            final Optional<DMNSearchableElement> current = getCurrentResult();

            DMNSearchableElement value = null;
            if (current.isPresent()) {
                value = current.get();
            }

            final SearchPerformedEvent event = new SearchPerformedEvent(value);
            searchPerformedEvent.fire(event);
        };
    }

    @Override
    protected List<DMNSearchableElement> getSearchableElements() {
        return getSubIndex().getSearchableElements();
    }

    Command getNoResultsFoundCallback() {
        return () -> getSubIndex().onNoResultsFound();
    }

    public void setIsDataTypesTabActiveSupplier(final Supplier<Boolean> isDataTypesTabActiveSupplier) {
        this.isDataTypesTabActiveSupplier = isDataTypesTabActiveSupplier;
    }

    Command getSearchClosedCallback() {
        return () -> getSubIndex().onSearchClosed();
    }

    private DMNSubIndex getSubIndex() {
        final SearchContext searchContext = currentSearchContext();
        switch (searchContext) {
            case BOXED_EXPRESSION:
                return gridSubIndex;
            case DATA_TYPES:
                return dataTypesSubIndex;
            case GRAPH:
                return graphSubIndex;
            default:
                throw new UnsupportedOperationException("Unsupported search context: " + searchContext);
        }
    }

    public Supplier<Boolean> getIsDataTypesTabActiveSupplier() {
        return isDataTypesTabActiveSupplier;
    }

    private SearchContext currentSearchContext() {
        if (getIsDataTypesTabActiveSupplier().get()) {
            return DATA_TYPES;
        }
        if (isExpressionEditorActive()) {
            return BOXED_EXPRESSION;
        }
        return GRAPH;
    }

    private boolean isExpressionEditorActive() {
        final DMNSession session = getCurrentDMNSession();
        if (Objects.isNull(session)) {
            return false;
        }
        return session.getExpressionEditor().isActive();
    }

    @SuppressWarnings("all")
    private DMNSession getCurrentDMNSession() {
        ClientSession s = sessionManager.getCurrentSession();
        return s instanceof DMNSession ? (DMNSession) s : null;
    }

    enum SearchContext {
        BOXED_EXPRESSION,
        DATA_TYPES,
        GRAPH
    }
}
