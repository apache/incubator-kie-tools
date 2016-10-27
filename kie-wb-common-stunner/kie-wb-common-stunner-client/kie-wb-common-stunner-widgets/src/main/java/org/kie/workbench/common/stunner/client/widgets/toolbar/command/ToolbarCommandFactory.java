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

package org.kie.workbench.common.stunner.client.widgets.toolbar.command;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Dependent
public class ToolbarCommandFactory {

    private final Instance<ClearSelectionToolbarCommand> clearSelectionCommand;
    private final Instance<VisitGraphToolbarCommand> visitGraphCommand;
    private final Instance<SwitchGridToolbarCommand> switchGridCommand;
    private final Instance<ClearToolbarCommand> clearCommand;
    private final Instance<DeleteSelectionToolbarCommand> deleteSelectionCommand;
    private final Instance<UndoToolbarCommand> undoCommand;
    private final Instance<ValidateToolbarCommand> validateCommand;

    protected ToolbarCommandFactory() {
        this( null, null, null, null, null, null, null );
    }

    @Inject
    public ToolbarCommandFactory( final Instance<ClearSelectionToolbarCommand> clearSelectionCommand,
                                  final Instance<VisitGraphToolbarCommand> visitGraphCommand,
                                  final Instance<SwitchGridToolbarCommand> switchGridCommand,
                                  final Instance<ClearToolbarCommand> clearCommand,
                                  final Instance<DeleteSelectionToolbarCommand> deleteSelectionCommand,
                                  final Instance<UndoToolbarCommand> undoCommand,
                                  final Instance<ValidateToolbarCommand> validateCommand) {
        this.clearSelectionCommand = clearSelectionCommand;
        this.visitGraphCommand = visitGraphCommand;
        this.switchGridCommand = switchGridCommand;
        this.clearCommand = clearCommand;
        this.deleteSelectionCommand = deleteSelectionCommand;
        this.undoCommand = undoCommand;
        this.validateCommand = validateCommand;
    }

    public ClearSelectionToolbarCommand newClearSelectionCommand() {
        return clearSelectionCommand.get();
    }

    public SwitchGridToolbarCommand newSwitchGridCommand() {
        return switchGridCommand.get();
    }

    public VisitGraphToolbarCommand newVisitGraphCommand() {
        return visitGraphCommand.get();
    }

    public ClearToolbarCommand newClearCommand() {
        return clearCommand.get();
    }

    public DeleteSelectionToolbarCommand newDeleteSelectedElementsCommand() {
        return deleteSelectionCommand.get();
    }

    public UndoToolbarCommand newUndoCommand() {
        return undoCommand.get();
    }

    public ValidateToolbarCommand newValidateCommand() {
        return validateCommand.get();
    }

}
