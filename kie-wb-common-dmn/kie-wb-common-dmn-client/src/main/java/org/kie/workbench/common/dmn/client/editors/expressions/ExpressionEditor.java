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
package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.editors.toolbar.ToolbarStateHandler;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.uberfire.mvp.Command;

public class ExpressionEditor implements ExpressionEditorView.Presenter {

    private ExpressionEditorView view;

    private Optional<Command> exitCommand = Optional.empty();

    private Optional<ToolbarStateHandler> toolbarStateHandler;

    private Optional<HasExpression> hasExpression = Optional.empty();

    private DecisionNavigatorPresenter decisionNavigator;

    private DMNGraphUtils dmnGraphUtils;

    // See DROOLS-3706. This returns the name of the DMN file's Definitions not the name of
    // the DRG or DRD being displayed. At the point DROOLS-3706 was implemented the DMN
    // Editor did not support DRGs so this name was chosen by default.
    private final Supplier<String> returnToLinkTextSupplier = new Supplier<String>() {
        @Override
        public String get() {
            return extractReturnToLinkFromDefinitions();
        }

        private String extractReturnToLinkFromDefinitions() {
            if (Objects.isNull(dmnGraphUtils)) {
                return "";
            }
            final Definitions definitions = dmnGraphUtils.getDefinitions();
            if (Objects.isNull(definitions)) {
                return "";
            }
            final Name name = definitions.getName();
            return Objects.nonNull(name) ? name.getValue() : "";
        }
    };

    @SuppressWarnings("unchecked")
    public ExpressionEditor(final ExpressionEditorView view,
                            final DecisionNavigatorPresenter decisionNavigator,
                            final DMNGraphUtils dmnGraphUtils) {
        this.view = view;
        this.decisionNavigator = decisionNavigator;
        this.dmnGraphUtils = dmnGraphUtils;

        this.view.init(this);
    }

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
        });
    }

    @Override
    public void handleCanvasElementUpdated(final CanvasElementUpdatedEvent event) {
        final Element<?> element = event.getElement();
        if ((element instanceof Node)) {
            if (element.getContent() instanceof Definition) {
                final Definition definition = (Definition) element.getContent();
                final Optional<Definitions> definitions = Optional.ofNullable(dmnGraphUtils.getDefinitions());
                definitions.ifPresent(d -> {
                    if (Objects.equals(d, definition.getDefinition())) {
                        view.setReturnToLinkText(returnToLinkTextSupplier.get());
                    }
                });
                hasExpression.ifPresent(e -> {
                    if (Objects.equals(e.asDMNModelInstrumentedBase(), definition.getDefinition())) {
                        view.setExpressionNameText(Optional.ofNullable((HasName) definition.getDefinition()));
                        view.refresh();
                    }
                });
            }
        }
    }

    Optional<Command> getExitCommand() {
        return exitCommand;
    }

    public boolean isActive() {
        return exitCommand.isPresent();
    }
}
