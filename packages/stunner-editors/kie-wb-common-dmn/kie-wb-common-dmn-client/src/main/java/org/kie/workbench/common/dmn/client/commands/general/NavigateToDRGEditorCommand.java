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

package org.kie.workbench.common.dmn.client.commands.general;

import java.util.Optional;

import javax.enterprise.event.Event;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;

public class NavigateToDRGEditorCommand extends BaseNavigateCommand implements VetoUndoCommand {

    public NavigateToDRGEditorCommand(final ExpressionEditorView.Presenter editor,
                                      final SessionPresenter<? extends ClientSession, ?, Diagram> presenter,
                                      final SessionManager sessionManager,
                                      final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                      final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                      final String nodeUUID,
                                      final HasExpression hasExpression,
                                      final Optional<HasName> hasName,
                                      final boolean isOnlyVisualChangeAllowed) {
        super(editor,
              presenter,
              sessionManager,
              sessionCommandManager,
              refreshFormPropertiesEvent,
              nodeUUID,
              hasExpression,
              hasName,
              isOnlyVisualChangeAllowed);
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return NOP_GRAPH_COMMAND;
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
                enableHandlers(true);
                hidePaletteWidget(false);
                unmountNewBoxedExpressionEditor();
                addDRGEditorToCanvasWidget();

                //Ensure Form Properties are updated to reflect the Graph node selection
                refreshFormPropertiesEvent.fire(new RefreshFormPropertiesEvent(sessionManager.getCurrentSession(), nodeUUID));

                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
                enableHandlers(false);
                hidePaletteWidget(true);
                editor.setExpression(nodeUUID,
                                     hasExpression,
                                     hasName,
                                     isOnlyVisualChangeAllowed);
                editor.setExitCommand(() -> navigateToDRGEditor(hasExpression, hasName));
                addExpressionEditorToCanvasWidget();
                return CanvasCommandResultBuilder.SUCCESS;
            }
        };
    }
}
