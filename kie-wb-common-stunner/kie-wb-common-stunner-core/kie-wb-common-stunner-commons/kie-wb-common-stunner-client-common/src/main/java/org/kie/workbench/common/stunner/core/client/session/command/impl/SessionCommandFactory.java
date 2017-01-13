/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;

@ApplicationScoped
public class SessionCommandFactory {

    private final ManagedInstance<ClearSelectionSessionCommand> clearSelectionCommand;
    private final ManagedInstance<VisitGraphSessionCommand> visitGraphCommand;
    private final ManagedInstance<SwitchGridSessionCommand> switchGridCommand;
    private final ManagedInstance<ClearSessionCommand> clearCommand;
    private final ManagedInstance<DeleteSelectionSessionCommand> deleteSelectionCommand;
    private final ManagedInstance<UndoSessionCommand> undoCommand;
    private final ManagedInstance<RedoSessionCommand> redoCommand;
    private final ManagedInstance<ValidateSessionCommand> validateCommand;
    private final ManagedInstance<RefreshSessionCommand> refreshSessionCommand;

    protected SessionCommandFactory() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public SessionCommandFactory(final ManagedInstance<ClearSelectionSessionCommand> clearSelectionCommand,
                                 final ManagedInstance<VisitGraphSessionCommand> visitGraphCommand,
                                 final ManagedInstance<SwitchGridSessionCommand> switchGridCommand,
                                 final ManagedInstance<ClearSessionCommand> clearCommand,
                                 final ManagedInstance<DeleteSelectionSessionCommand> deleteSelectionCommand,
                                 final ManagedInstance<UndoSessionCommand> undoCommand,
                                 final ManagedInstance<RedoSessionCommand> redoCommand,
                                 final ManagedInstance<ValidateSessionCommand> validateCommand,
                                 final ManagedInstance<RefreshSessionCommand> refreshSessionCommand) {
        this.clearSelectionCommand = clearSelectionCommand;
        this.visitGraphCommand = visitGraphCommand;
        this.switchGridCommand = switchGridCommand;
        this.clearCommand = clearCommand;
        this.deleteSelectionCommand = deleteSelectionCommand;
        this.undoCommand = undoCommand;
        this.redoCommand = redoCommand;
        this.validateCommand = validateCommand;
        this.refreshSessionCommand = refreshSessionCommand;
    }

    public ClearSelectionSessionCommand newClearSelectionCommand() {
        return clearSelectionCommand.get();
    }

    public SwitchGridSessionCommand newSwitchGridCommand() {
        return switchGridCommand.get();
    }

    public VisitGraphSessionCommand newVisitGraphCommand() {
        return visitGraphCommand.get();
    }

    public ClearSessionCommand newClearCommand() {
        return clearCommand.get();
    }

    public DeleteSelectionSessionCommand newDeleteSelectedElementsCommand() {
        return deleteSelectionCommand.get();
    }

    public UndoSessionCommand newUndoCommand() {
        return undoCommand.get();
    }

    public RedoSessionCommand newRedoCommand() {
        return redoCommand.get();
    }

    public ValidateSessionCommand newValidateCommand() {
        return validateCommand.get();
    }

    public RefreshSessionCommand newRefreshSessionCommand() {
        return refreshSessionCommand.get();
    }
}
