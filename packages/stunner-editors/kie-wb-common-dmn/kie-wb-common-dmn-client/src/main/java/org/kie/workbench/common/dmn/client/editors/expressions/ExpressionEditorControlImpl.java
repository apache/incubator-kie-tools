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

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasDomainObjectListener;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

@Dependent
public class ExpressionEditorControlImpl extends AbstractCanvasControl<AbstractCanvas> implements ExpressionEditorControl {

    private ExpressionEditorView view;
    private DecisionNavigatorPresenter decisionNavigator;
    private DMNGraphUtils dmnGraphUtils;
    private DMNDiagramsSession dmnDiagramsSession;
    private Event<CanvasElementUpdatedEvent> canvasElementUpdatedEvent;
    private DRDNameChanger drdNameChanger;

    private Optional<DMNSession> session = Optional.empty();
    private Optional<ExpressionEditorView.Presenter> expressionEditor = Optional.empty();

    private class RefreshEditorDomainObjectListener implements CanvasDomainObjectListener {

        private DMNSession session;

        public RefreshEditorDomainObjectListener(final DMNSession session) {
            this.session = session;
        }

        @Override
        public void update(final DomainObject domainObject) {
            final CanvasHandler canvasHandler = session.getCanvasHandler();
            final Diagram diagram = canvasHandler.getDiagram();
            final Graph<?, Node> graph = diagram.getGraph();

            for (final Node node : graph.nodes()) {
                if (node.getContent() instanceof Definition) {
                    final Definition definition = (Definition) node.getContent();
                    if (definition.getDefinition() instanceof DomainObject) {
                        final DomainObject d = (DomainObject) definition.getDefinition();
                        if (Objects.equals(d.getDomainObjectUUID(), domainObject.getDomainObjectUUID())) {
                            canvasElementUpdatedEvent.fire(new CanvasElementUpdatedEvent(canvasHandler, node));
                        }
                    }
                }
            }

            view.reloadEditor();
        }
    }

    private RefreshEditorDomainObjectListener refreshEditorDomainObjectListener;

    @Inject
    public ExpressionEditorControlImpl(final ExpressionEditorView view,
                                       final DecisionNavigatorPresenter decisionNavigator,
                                       final DMNGraphUtils dmnGraphUtils,
                                       final DMNDiagramsSession dmnDiagramsSession,
                                       final Event<CanvasElementUpdatedEvent> canvasElementUpdatedEvent,
                                       final DRDNameChanger drdNameChanger) {
        this.view = view;
        this.decisionNavigator = decisionNavigator;
        this.dmnGraphUtils = dmnGraphUtils;
        this.dmnDiagramsSession = dmnDiagramsSession;
        this.canvasElementUpdatedEvent = canvasElementUpdatedEvent;
        this.drdNameChanger = drdNameChanger;
    }

    @Override
    public void bind(final DMNSession session) {
        final ExpressionEditorView.Presenter editor = makeExpressionEditor(view,
                                                                           decisionNavigator,
                                                                           dmnGraphUtils,
                                                                           dmnDiagramsSession);
        editor.bind(session);
        this.session = Optional.of(session);
        this.expressionEditor = Optional.of(editor);
        this.refreshEditorDomainObjectListener = new RefreshEditorDomainObjectListener(session);
        session.getCanvasHandler().addDomainObjectListener(refreshEditorDomainObjectListener);
    }

    ExpressionEditorView.Presenter makeExpressionEditor(final ExpressionEditorView view,
                                                        final DecisionNavigatorPresenter decisionNavigator,
                                                        final DMNGraphUtils dmnGraphUtils,
                                                        final DMNDiagramsSession dmnDiagramsSession) {
        return new ExpressionEditor(view,
                                    decisionNavigator,
                                    dmnGraphUtils,
                                    dmnDiagramsSession,
                                    drdNameChanger);
    }

    @Override
    protected void doInit() {
    }

    @Override
    protected void doDestroy() {
        view = null;
        decisionNavigator = null;
        session.ifPresent(s -> s.getCanvasHandler().removeDomainObjectListener(refreshEditorDomainObjectListener));
        session = Optional.empty();
        expressionEditor = Optional.empty();
    }

    @Override
    public ExpressionEditorView.Presenter getExpressionEditor() {
        return expressionEditor.orElse(null);
    }

    @SuppressWarnings("unused")
    public void onCanvasFocusedSelectionEvent(final @Observes CanvasSelectionEvent event) {
        session.ifPresent(s -> {
            if (Objects.equals(s.getCanvasHandler(), event.getCanvasHandler())) {
                expressionEditor.ifPresent(ExpressionEditorView.Presenter::exit);
            }
        });
    }

    public void onCanvasElementUpdated(final @Observes CanvasElementUpdatedEvent event) {
        expressionEditor.ifPresent(editor -> editor.handleCanvasElementUpdated(event));
    }
}
