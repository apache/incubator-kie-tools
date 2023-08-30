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
import java.util.function.Supplier;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.editors.toolbar.ToolbarStateHandler;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.uberfire.mvp.Command;

public class ExpressionEditor implements ExpressionEditorView.Presenter {

    private DMNDiagramsSession dmnDiagramsSession;

    private DRDNameChanger drdNameChanger;

    private ExpressionEditorView view;

    private Optional<Command> exitCommand = Optional.empty();

    private Optional<ToolbarStateHandler> toolbarStateHandler = Optional.empty();

    private Optional<HasExpression> hasExpression = Optional.empty();

    private DecisionNavigatorPresenter decisionNavigator;

    private DMNGraphUtils dmnGraphUtils;

    public ExpressionEditor(final ExpressionEditorView view,
                            final DecisionNavigatorPresenter decisionNavigator,
                            final DMNGraphUtils dmnGraphUtils,
                            final DMNDiagramsSession dmnDiagramsSession,
                            final DRDNameChanger drdNameChanger) {
        this.view = view;
        this.decisionNavigator = decisionNavigator;
        this.dmnGraphUtils = dmnGraphUtils;
        this.dmnDiagramsSession = dmnDiagramsSession;
        this.drdNameChanger = drdNameChanger;

        this.view.init(this);
    }

    // When the current selection is the DRG, we return its name, otherwise the name of the selected DRD
    private final Supplier<String> returnToLinkTextSupplier = new Supplier<String>() {
        @Override
        public String get() {
            if (dmnDiagramsSession.isGlobalGraphSelected()) {
                return extractReturnToLinkFromDefinitions();
            }
            drdNameChanger.hideDRDNameChanger();
            return dmnDiagramsSession
                    .getCurrentDMNDiagramElement()
                    .map(DMNDiagramElement::getName)
                    .orElse(new Name())
                    .getValue();
        }

        private String extractReturnToLinkFromDefinitions() {
            if (Objects.isNull(dmnGraphUtils)) {
                return "";
            }
            final Definitions definitions = dmnGraphUtils.getModelDefinitions();
            if (Objects.isNull(definitions)) {
                return "";
            }
            final Name name = definitions.getName();
            return Objects.nonNull(name) ? name.getValue() : "";
        }
    };

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void bind(final DMNSession session) {
        view.bind(session);
    }

    @Override
    public ExpressionEditorView getView() {
        return view;
    }

    @Override
    public void setToolbarStateHandler(final ToolbarStateHandler toolbarStateHandler) {
        this.toolbarStateHandler = Optional.ofNullable(toolbarStateHandler);
    }

    @Override
    public void setExpression(final String nodeUUID,
                              final HasExpression hasExpression,
                              final Optional<HasName> hasName,
                              final boolean isOnlyVisualChangeAllowed) {
        this.hasExpression = Optional.ofNullable(hasExpression);

        view.setExpression(nodeUUID, hasExpression, hasName, isOnlyVisualChangeAllowed);
        view.setReturnToLinkText(returnToLinkTextSupplier.get());

        toolbarStateHandler.ifPresent(ToolbarStateHandler::enterGridView);
    }

    @Override
    public void unmountNewBoxedExpressionEditor() {
        view.unmountNewBoxedExpressionEditor();
    }

    @Override
    public void setExitCommand(final Command exitCommand) {
        this.exitCommand = Optional.ofNullable(exitCommand);
    }

    @Override
    public void exit() {
        exitCommand.ifPresent(command -> {
            decisionNavigator.clearSelections();
            toolbarStateHandler.ifPresent(ToolbarStateHandler::enterGraphView);
            command.execute();
            exitCommand = Optional.empty();
            if (!dmnDiagramsSession.isGlobalGraphSelected()) {
                drdNameChanger.showDRDNameChanger();
            }
        });
    }

    @Override
    public void handleCanvasElementUpdated(final CanvasElementUpdatedEvent event) {
        final Element<?> element = event.getElement();
        if ((element instanceof Node)) {
            if (element.getContent() instanceof Definition) {
                final Definition definition = (Definition) element.getContent();
                final Optional<Definitions> definitions = Optional.ofNullable(dmnGraphUtils.getModelDefinitions());
                definitions.ifPresent(d -> {
                    if (Objects.equals(d, definition.getDefinition())) {
                        view.setReturnToLinkText(returnToLinkTextSupplier.get());
                    }
                });
                hasExpression.ifPresent(e -> {
                    if (Objects.equals(e.asDMNModelInstrumentedBase(), definition.getDefinition())) {
                        view.setExpressionNameText(Optional.ofNullable((HasName) definition.getDefinition()));
                        view.reloadEditor();
                    }
                });
            }
        }
    }

    @Override
    public boolean isActive() {
        return getExitCommand().isPresent();
    }

    Optional<Command> getExitCommand() {
        return exitCommand;
    }
}
