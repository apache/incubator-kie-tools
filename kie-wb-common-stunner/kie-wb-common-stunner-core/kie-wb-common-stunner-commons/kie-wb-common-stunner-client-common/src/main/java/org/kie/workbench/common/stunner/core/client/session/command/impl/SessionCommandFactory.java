/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class SessionCommandFactory {

    private final Instance<ClearSelectionSessionCommand> clearSelectionCommand;
    private final Instance<VisitGraphSessionCommand> visitGraphCommand;
    private final Instance<SwitchGridSessionCommand> switchGridCommand;
    private final Instance<ClearSessionCommand> clearCommand;
    private final Instance<DeleteSelectionSessionCommand> deleteSelectionCommand;
    private final Instance<UndoSessionCommand> undoCommand;
    private final Instance<ValidateSessionCommand> validateCommand;

    protected SessionCommandFactory() {
        this( null, null, null, null, null, null, null );
    }

    @Inject
    public SessionCommandFactory( final Instance<ClearSelectionSessionCommand> clearSelectionCommand,
                                  final Instance<VisitGraphSessionCommand> visitGraphCommand,
                                  final Instance<SwitchGridSessionCommand> switchGridCommand,
                                  final Instance<ClearSessionCommand> clearCommand,
                                  final Instance<DeleteSelectionSessionCommand> deleteSelectionCommand,
                                  final Instance<UndoSessionCommand> undoCommand,
                                  final Instance<ValidateSessionCommand> validateCommand ) {
        this.clearSelectionCommand = clearSelectionCommand;
        this.visitGraphCommand = visitGraphCommand;
        this.switchGridCommand = switchGridCommand;
        this.clearCommand = clearCommand;
        this.deleteSelectionCommand = deleteSelectionCommand;
        this.undoCommand = undoCommand;
        this.validateCommand = validateCommand;
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

    public ValidateSessionCommand newValidateCommand() {
        return validateCommand.get();
    }

}
