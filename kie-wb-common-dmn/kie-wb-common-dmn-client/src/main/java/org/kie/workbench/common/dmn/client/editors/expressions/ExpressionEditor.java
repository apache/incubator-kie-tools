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

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNEditorToolbar;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.mvp.Command;

@Dependent
public class ExpressionEditor implements ExpressionEditorView.Presenter {

    private ExpressionEditorView view;

    private Optional<Command> exitCommand = Optional.empty();

    private ToolbarCommandStateHandler toolbarCommandStateHandler;

    private DecisionNavigatorPresenter decisionNavigator;

    public ExpressionEditor() {
        //CDI proxy
    }

    @Inject
    @SuppressWarnings("unchecked")
    public ExpressionEditor(final ExpressionEditorView view,
                            final DecisionNavigatorPresenter decisionNavigator) {
        this.view = view;
        this.decisionNavigator = decisionNavigator;
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public ExpressionEditorView getView() {
        return view;
    }

    @Override
    public void init(final SessionPresenter<EditorSession, ?, Diagram> presenter) {
        this.toolbarCommandStateHandler = new ToolbarCommandStateHandler((DMNEditorToolbar) presenter.getToolbar());
    }

    @Override
    public void setExpression(final String nodeUUID,
                              final HasExpression hasExpression,
                              final Optional<HasName> hasName) {
        view.setExpression(nodeUUID,
                           hasExpression,
                           hasName);

        toolbarCommandStateHandler.enter();
    }

    @Override
    public void setExitCommand(final Command exitCommand) {
        this.exitCommand = Optional.ofNullable(exitCommand);
    }

    @Override
    public void exit() {
        exitCommand.ifPresent(command -> {
            decisionNavigator.clearSelections();
            toolbarCommandStateHandler.exit();
            command.execute();
            exitCommand = Optional.empty();
        });
    }

    public void onCanvasFocusedSelectionEvent(@Observes CanvasSelectionEvent event) {
        exit();
    }

    Optional<Command> getExitCommand() {
        return exitCommand;
    }

    //Package-protected for Unit Tests
    ToolbarCommandStateHandler getToolbarCommandStateHandler() {
        return toolbarCommandStateHandler;
    }

    @SuppressWarnings("unchecked")
    static class ToolbarCommandStateHandler {

        private DMNEditorToolbar toolbar;

        //Package-protected for Unit Tests
        boolean visitGraphToolbarCommandEnabled = false;
        boolean clearToolbarCommandEnabled = false;
        boolean deleteSelectionToolbarCommandEnabled = false;
        boolean switchGridToolbarCommandEnabled = false;
        boolean undoToolbarCommandEnabled = false;
        boolean redoToolbarCommandEnabled = false;
        boolean validateToolbarCommandEnabled = false;
        boolean exportToPngToolbarCommandEnabled = false;
        boolean exportToJpgToolbarCommandEnabled = false;
        boolean exportToPdfToolbarCommandEnabled = false;
        boolean copyCommandEnabled = false;
        boolean cutCommandEnabled = false;
        boolean pasteCommandEnabled = false;

        private ToolbarCommandStateHandler(final DMNEditorToolbar toolbar) {
            this.toolbar = toolbar;
        }

        private void enter() {
            this.visitGraphToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getVisitGraphToolbarCommand());
            this.clearToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getClearToolbarCommand());
            this.deleteSelectionToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getDeleteSelectionToolbarCommand());
            this.switchGridToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getSwitchGridToolbarCommand());
            this.undoToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getUndoToolbarCommand());
            this.redoToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getRedoToolbarCommand());
            this.validateToolbarCommandEnabled = toolbar.isEnabled(toolbar.getValidateCommand());
            this.exportToPngToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getExportToPngToolbarCommand());
            this.exportToJpgToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getExportToJpgToolbarCommand());
            this.exportToPdfToolbarCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getExportToPdfToolbarCommand());
            this.copyCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getCopyToolbarCommand());
            this.cutCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getCutToolbarCommand());
            this.pasteCommandEnabled = toolbar.isEnabled((ToolbarCommand) toolbar.getPasteToolbarCommand());

            enableToolbarCommand(toolbar.getVisitGraphToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getClearToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getDeleteSelectionToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getSwitchGridToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getUndoToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getRedoToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getValidateCommand(),
                                 false);
            enableToolbarCommand(toolbar.getExportToPngToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getExportToJpgToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getExportToPdfToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getCopyToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getCutToolbarCommand(),
                                 false);
            enableToolbarCommand(toolbar.getPasteToolbarCommand(),
                                 false);
        }

        private void exit() {
            enableToolbarCommand(toolbar.getVisitGraphToolbarCommand(),
                                 visitGraphToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getClearToolbarCommand(),
                                 clearToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getDeleteSelectionToolbarCommand(),
                                 deleteSelectionToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getSwitchGridToolbarCommand(),
                                 switchGridToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getUndoToolbarCommand(),
                                 undoToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getRedoToolbarCommand(),
                                 redoToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getValidateCommand(),
                                 validateToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getExportToPngToolbarCommand(),
                                 exportToPngToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getExportToJpgToolbarCommand(),
                                 exportToJpgToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getExportToPdfToolbarCommand(),
                                 exportToPdfToolbarCommandEnabled);
            enableToolbarCommand(toolbar.getCopyToolbarCommand(),
                                 copyCommandEnabled);
            enableToolbarCommand(toolbar.getCutToolbarCommand(),
                                 cutCommandEnabled);
            enableToolbarCommand(toolbar.getPasteToolbarCommand(),
                                 pasteCommandEnabled);
        }

        private void enableToolbarCommand(final ToolbarCommand command,
                                          final boolean enabled) {
            if (enabled) {
                toolbar.enable(command);
            } else {
                toolbar.disable(command);
            }
        }
    }
}
