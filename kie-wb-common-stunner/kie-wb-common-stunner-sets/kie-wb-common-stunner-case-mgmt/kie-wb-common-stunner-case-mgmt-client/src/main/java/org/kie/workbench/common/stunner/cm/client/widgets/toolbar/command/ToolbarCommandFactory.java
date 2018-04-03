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

package org.kie.workbench.common.stunner.cm.client.widgets.toolbar.command;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearStatesToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.CopyToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.CutToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.DeleteSelectionToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToJpgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPdfToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPngToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToSvgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.PasteToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.RedoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SwitchGridToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.UndoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ValidateToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.VisitGraphToolbarCommand;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;

@CaseManagementEditor
@Dependent
public class ToolbarCommandFactory extends org.kie.workbench.common.stunner.client.widgets.toolbar.command.ToolbarCommandFactory {

    public ToolbarCommandFactory() {
        //CDI proxy
    }

    @Inject
    public ToolbarCommandFactory(final ManagedInstance<ClearStatesToolbarCommand> clearStatesCommand,
                                 final ManagedInstance<VisitGraphToolbarCommand> visitGraphCommand,
                                 final ManagedInstance<SwitchGridToolbarCommand> switchGridCommand,
                                 final ManagedInstance<ClearToolbarCommand> clearCommand,
                                 final ManagedInstance<DeleteSelectionToolbarCommand> deleteSelectionCommand,
                                 final ManagedInstance<UndoToolbarCommand> undoCommand,
                                 final ManagedInstance<RedoToolbarCommand> redoCommand,
                                 final ManagedInstance<ValidateToolbarCommand> validateCommand,
                                 final ManagedInstance<ExportToPngToolbarCommand> exportToPngToolbarCommand,
                                 final ManagedInstance<ExportToJpgToolbarCommand> exportToJpgToolbarCommand,
                                 final ManagedInstance<ExportToPdfToolbarCommand> exportToPdfToolbarCommand,
                                 final ManagedInstance<ExportToSvgToolbarCommand> exportToSvgToolbarCommand,
                                 final ManagedInstance<CopyToolbarCommand> copyToolbarCommand,
                                 final ManagedInstance<CutToolbarCommand> cutToolbarCommand,
                                 final @CaseManagementEditor ManagedInstance<PasteToolbarCommand> pasteToolbarCommand) {
        super(clearStatesCommand,
              visitGraphCommand,
              switchGridCommand,
              clearCommand,
              deleteSelectionCommand,
              undoCommand,
              redoCommand,
              validateCommand,
              exportToPngToolbarCommand,
              exportToJpgToolbarCommand,
              exportToSvgToolbarCommand,
              exportToPdfToolbarCommand,
              copyToolbarCommand,
              cutToolbarCommand,
              pasteToolbarCommand);
    }
}
