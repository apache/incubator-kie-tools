/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.search;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditor;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.widgets.client.search.common.BaseEditorSearchIndex;
import org.uberfire.mvp.Command;

@Dependent
public class DMNEditorSearchIndex extends BaseEditorSearchIndex<DMNSearchableElement> {

    private final DMNGraphSubIndex graphSubIndex;

    private final DMNGridSubIndex gridSubIndex;

    private final SessionManager sessionManager;

    private final DMNGraphUtils graphUtils;

    private final DMNGridHelper dmnGridHelper;

    private final Event<CanvasClearSelectionEvent> canvasClearSelectionEventEvent;

    private final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Inject
    public DMNEditorSearchIndex(final DMNGraphSubIndex graphSubIndex,
                                final DMNGridSubIndex gridSubIndex,
                                final SessionManager sessionManager,
                                final DMNGraphUtils graphUtils,
                                final DMNGridHelper dmnGridHelper,
                                final Event<CanvasClearSelectionEvent> canvasClearSelectionEventEvent,
                                final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent) {
        this.graphSubIndex = graphSubIndex;
        this.gridSubIndex = gridSubIndex;
        this.sessionManager = sessionManager;
        this.graphUtils = graphUtils;
        this.dmnGridHelper = dmnGridHelper;
        this.canvasClearSelectionEventEvent = canvasClearSelectionEventEvent;
        this.domainObjectSelectionEvent = domainObjectSelectionEvent;
    }

    @PostConstruct
    public void init() {
        registerSubIndex(graphSubIndex);
        registerSubIndex(gridSubIndex);
        setNoResultsFoundCallback(getNoResultsFoundCallback());
    }

    Command getNoResultsFoundCallback() {
        return () -> {
            if (getExpressionEditor().isActive()) {
                clearGridSelection();
            } else {
                clearGraphSelection();
            }
        };
    }

    private void clearGraphSelection() {
        canvasClearSelectionEventEvent.fire(new CanvasClearSelectionEvent(getCanvasHandler()));
        domainObjectSelectionEvent.fire(new DomainObjectSelectionEvent(getCanvasHandler(), new NOPDomainObject()));
    }

    private CanvasHandler getCanvasHandler() {
        return graphUtils.getCanvasHandler();
    }

    private void clearGridSelection() {
        dmnGridHelper.clearSelections();
    }

    @Override
    protected List<DMNSearchableElement> getSearchableElements() {
        if (getExpressionEditor().isActive()) {
            return gridSubIndex.getSearchableElements();
        } else {
            return graphSubIndex.getSearchableElements();
        }
    }

    private ExpressionEditor getExpressionEditor() {
        return (ExpressionEditor) getCurrentSession().getExpressionEditor();
    }

    private DMNSession getCurrentSession() {
        return sessionManager.getCurrentSession();
    }
}
